param(
    [string]$OutputDirectory = (Join-Path $PSScriptRoot "..\src\main\resources\db\data")
)

$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Net.Http
$baseUrl = "https://provinces.open-api.vn/api/v2"
$uuidNamespace = "6ba7b8119dad11d180b400c04fd430c8" # UUIDv5 DNS namespace
$httpClient = New-Object System.Net.Http.HttpClient

function Get-ApiJson([string]$Uri) {
    for ($attempt = 1; $attempt -le 3; $attempt++) {
        try {
            $bytes = $httpClient.GetByteArrayAsync($Uri).GetAwaiter().GetResult()
            $json = [Text.Encoding]::UTF8.GetString($bytes)
            $data = ConvertFrom-Json -InputObject $json
            return ,$data
        } catch {
            if ($attempt -eq 3) { throw }
            Start-Sleep -Seconds $attempt
        }
    }
}

function Get-DeterministicUuid([string]$Name) {
    $namespaceBytes = for ($i = 0; $i -lt $uuidNamespace.Length; $i += 2) {
        [Convert]::ToByte($uuidNamespace.Substring($i, 2), 16)
    }
    $nameBytes = [Text.Encoding]::UTF8.GetBytes($Name)
    $inputBytes = [byte[]]::new($namespaceBytes.Length + $nameBytes.Length)
    [Array]::Copy($namespaceBytes, 0, $inputBytes, 0, $namespaceBytes.Length)
    [Array]::Copy($nameBytes, 0, $inputBytes, $namespaceBytes.Length, $nameBytes.Length)

    $sha1 = [Security.Cryptography.SHA1]::Create()
    try { $hash = $sha1.ComputeHash($inputBytes) } finally { $sha1.Dispose() }
    $hash[6] = ($hash[6] -band 0x0f) -bor 0x50
    $hash[8] = ($hash[8] -band 0x3f) -bor 0x80
    $hex = [BitConverter]::ToString($hash, 0, 16).Replace("-", "").ToLowerInvariant()
    return "{0}-{1}-{2}-{3}-{4}" -f $hex.Substring(0,8), $hex.Substring(8,4), $hex.Substring(12,4), $hex.Substring(16,4), $hex.Substring(20,12)
}

$provinces = Get-ApiJson "$baseUrl/p/"
if ($provinces.Count -eq 0) { throw "Province endpoint returned no data." }
foreach ($province in $provinces) {
    foreach ($field in @("code", "name", "codename", "division_type")) {
        if ($null -eq $province.$field -or ($field -eq "name" -and [string]::IsNullOrWhiteSpace($province.$field))) {
            throw "Province record is missing required field '$field': $($province | ConvertTo-Json -Compress)"
        }
    }
}
$provinceCodeGroups = @($provinces | Group-Object { [string]$_.code })
if (@($provinceCodeGroups | Where-Object Count -gt 1).Count -gt 0) { throw "Duplicate province code in API response." }

$wards = [Collections.Generic.List[object]]::new()
foreach ($province in $provinces) {
    $provinceWards = Get-ApiJson "$baseUrl/w/?province=$($province.code)"
    if ($provinceWards.Count -eq 0) { throw "No wards returned for province code $($province.code). Refusing to generate a partial dataset." }
    foreach ($ward in $provinceWards) {
        foreach ($field in @("code", "name", "codename", "division_type", "province_code")) {
            if ($null -eq $ward.$field -or ($field -eq "name" -and [string]::IsNullOrWhiteSpace($ward.$field))) {
                throw "Ward record is missing required field '$field': $($ward | ConvertTo-Json -Compress)"
            }
        }
        if ([int]$ward.province_code -ne [int]$province.code) {
            throw "Ward $($ward.code) reports province_code $($ward.province_code), expected $($province.code)."
        }
        $wards.Add($ward)
    }
}
$wardCodeGroups = @($wards | Group-Object { [string]$_.code })
if (@($wardCodeGroups | Where-Object Count -gt 1).Count -gt 0) { throw "Duplicate ward code across API responses." }

$provinceByCode = @{}
foreach ($province in $provinces) { $provinceByCode[[int]$province.code] = $province }
foreach ($ward in $wards) {
    if (-not $provinceByCode.ContainsKey([int]$ward.province_code)) { throw "Ward $($ward.code) refers to missing province $($ward.province_code)." }
}

$timestamp = [DateTimeOffset]::UtcNow.ToUnixTimeMilliseconds()
$provinceRows = @($provinces | Sort-Object { [int]$_.code } | ForEach-Object {
    [pscustomobject][ordered]@{
        id = Get-DeterministicUuid "vn-provinces-api-v2:province:$($_.code)"
        code = [int]$_.code
        name = [string]$_.name
        codename = [string]$_.codename
        division_type = [string]$_.division_type
        created_at = $timestamp
        updated_at = $timestamp
    }
})
$provinceIdByCode = @{}
foreach ($row in $provinceRows) { $provinceIdByCode[$row.code] = $row.id }
$wardRows = @($wards | Sort-Object { [int]$_.code } | ForEach-Object {
    [pscustomobject][ordered]@{
        id = Get-DeterministicUuid "vn-provinces-api-v2:ward:$($_.code)"
        province_id = $provinceIdByCode[[int]$_.province_code]
        code = [int]$_.code
        name = [string]$_.name
        codename = [string]$_.codename
        division_type = [string]$_.division_type
        created_at = $timestamp
        updated_at = $timestamp
    }
})

if (@($provinceRows | Group-Object id | Where-Object Count -gt 1).Count -gt 0) { throw "UUID collision in province UUIDv5 output." }
if (@($wardRows | Group-Object id | Where-Object Count -gt 1).Count -gt 0) { throw "UUID collision in ward UUIDv5 output." }

New-Item -ItemType Directory -Path $OutputDirectory -Force | Out-Null
$provinceCsvPath = Join-Path $OutputDirectory "province-v2.csv"
$wardCsvPath = Join-Path $OutputDirectory "ward-v2.csv"
$provinceCsv = ($provinceRows | ConvertTo-Csv -NoTypeInformation) -join "`n"
$wardCsv = ($wardRows | ConvertTo-Csv -NoTypeInformation) -join "`n"
[IO.File]::WriteAllText($provinceCsvPath, $provinceCsv + "`n", [Text.UTF8Encoding]::new($false))
[IO.File]::WriteAllText($wardCsvPath, $wardCsv + "`n", [Text.UTF8Encoding]::new($false))
if (-not [IO.File]::ReadAllText($provinceCsvPath, [Text.Encoding]::UTF8).Contains($provinceRows[0].name)) {
    throw "Province CSV UTF-8 verification failed."
}
if (-not [IO.File]::ReadAllText($wardCsvPath, [Text.Encoding]::UTF8).Contains($wardRows[0].name)) {
    throw "Ward CSV UTF-8 verification failed."
}

$manifest = [ordered]@{
    source = $baseUrl
    retrieved_at_utc = [DateTimeOffset]::UtcNow.ToString("o")
    uuid_version = 5
    uuid_namespace = "6ba7b811-9dad-11d1-80b4-00c04fd430c8"
    uuid_name_prefixes = @{ province = "vn-provinces-api-v2:province:"; ward = "vn-provinces-api-v2:ward:" }
    province_count = $provinceRows.Count
    ward_count = $wardRows.Count
    missing_province_relations = 0
    duplicate_province_codes = 0
    duplicate_ward_codes = 0
    province_csv_sha256 = (Get-FileHash -Algorithm SHA256 $provinceCsvPath).Hash.ToLowerInvariant()
    ward_csv_sha256 = (Get-FileHash -Algorithm SHA256 $wardCsvPath).Hash.ToLowerInvariant()
}
[IO.File]::WriteAllText((Join-Path $OutputDirectory "vietnam-admin-v2-manifest.json"), ($manifest | ConvertTo-Json -Depth 5), [Text.UTF8Encoding]::new($false))

Write-Output "Generated $($provinceRows.Count) provinces and $($wardRows.Count) wards."
Write-Output "Province CSV SHA-256: $($manifest.province_csv_sha256)"
Write-Output "Ward CSV SHA-256: $($manifest.ward_csv_sha256)"
$httpClient.Dispose()
