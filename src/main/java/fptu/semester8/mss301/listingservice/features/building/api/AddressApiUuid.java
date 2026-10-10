package fptu.semester8.mss301.listingservice.features.building.api;

import fptu.exe202.signify.apiresponse.exception.BadRequestException;

import java.util.UUID;

final class AddressApiUuid {
    private AddressApiUuid() {
    }

    static UUID parse(String value, String field) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Invalid UUID for " + field + ".");
        }
    }

    static UUID parseOptional(String value, String field) {
        return value == null ? null : parse(value, field);
    }
}
