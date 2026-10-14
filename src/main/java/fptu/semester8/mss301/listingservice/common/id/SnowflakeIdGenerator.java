package fptu.semester8.mss301.listingservice.common.id;

import java.time.Instant;

/** Generates signed-long-compatible Snowflake identifiers for this service. */
public final class SnowflakeIdGenerator {
    private static final long CUSTOM_EPOCH = Instant.parse("2024-01-01T00:00:00Z").toEpochMilli();
    private static final long MAX_WORKER_ID = 1023L;
    private static final long MAX_SEQUENCE = 4095L;
    private static final long WORKER_ID = readWorkerId();

    private static long lastTimestamp = -1L;
    private static long sequence;

    private SnowflakeIdGenerator() {
    }

    public static synchronized Long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException("System clock moved backwards; cannot generate a Snowflake ID.");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                timestamp = waitForNextMillisecond(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;
        long elapsed = timestamp - CUSTOM_EPOCH;
        if (elapsed < 0 || elapsed >= (1L << 41)) {
            throw new IllegalStateException("Current time is outside the supported Snowflake epoch range.");
        }

        return (elapsed << 22) | (WORKER_ID << 12) | sequence;
    }

    private static long waitForNextMillisecond(long previousTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= previousTimestamp) {
            Thread.onSpinWait();
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    private static long readWorkerId() {
        String configuredWorkerId = System.getenv("LISTING_SNOWFLAKE_WORKER_ID");
        long workerId = configuredWorkerId == null || configuredWorkerId.isBlank()
            ? 1L
            : Long.parseLong(configuredWorkerId);
        if (workerId < 1L || workerId > MAX_WORKER_ID) {
            throw new IllegalStateException("LISTING_SNOWFLAKE_WORKER_ID must be between 1 and 1023.");
        }
        return workerId;
    }
}
