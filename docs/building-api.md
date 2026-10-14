# Building API

## Create a building

Creates a building record.

- **Method:** `POST`
- **Path:** `/api/v1/buildings`
- **Content-Type:** `application/json`
- **Authorization:** Write access is expected to be enforced by the API gateway.

### Request body

| Field | Type | Required | Validation |
| --- | --- | --- | --- |
| `name` | string | Yes | Must contain non-whitespace characters; maximum 255 characters. Leading and trailing whitespace is trimmed. |
| `address` | string | Yes | Must contain non-whitespace characters; maximum 500 characters. Leading and trailing whitespace is trimmed. |
| `status` | string | No | Not accepted from clients. New buildings are created with `ACTIVE` status. |
| `latitude` | decimal number | No | Between -90 and 90, inclusive; at most 2 integer digits and 8 fractional digits. |
| `longitude` | decimal number | No | Between -180 and 180, inclusive; at most 3 integer digits and 8 fractional digits. |

Coordinates may be `null` or omitted. `address` is the building's full address; province and ward are no longer stored separately. Coordinates accept decimal values, for example `10.762622` and `106.660172`.

Example:

```json
{
  "name": "Sunrise Apartment",
  "address": "123 Nguyen Hue, Ben Nghe",
  "latitude": 10.762622,
  "longitude": 106.660172
}
```

### Success response

Returns the shared `ApiResponse` success envelope with a `BuildingDetailResponse` as its data. The server generates `id`, `createdAt`, and `updatedAt` (Unix epoch milliseconds).

```json
{
  "success": true,
  "data": {
    "id": 751324529014636546,
    "name": "Sunrise Apartment",
    "address": "123 Nguyen Hue, Ben Nghe",
    "status": "ACTIVE",
    "latitude": 10.762622,
    "longitude": 106.660172,
    "createdAt": 1791964800000,
    "updatedAt": 1791964800000
  }
}
```

### Validation and lookup errors

- Missing, blank, or too-long `name`/`address`, malformed JSON, and out-of-range coordinates fail request validation and return the application's standard validation error response.

The database also enforces latitude and longitude ranges. Set `LISTING_SNOWFLAKE_WORKER_ID` to a unique value from 1 to 1023 for each running service instance; the default is 1 for a single local instance.
