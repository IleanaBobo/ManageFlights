package org.example;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Clock {
    private Integer Id;
    private String region;

    /**
     * Constructs a Clock with a specific region and time zone ID.
     * @param region  the region of the clock
     * @param Id  the time zone ID as a string
     */
    public Clock(String region, Integer Id) {
        if (Id == null || Id.equals(0)) {
            throw new IllegalArgumentException("zoneIdString cannot be null or empty");
        }
        this.region = region;
        this.Id = Id;
    }

    /**
     * Gets the current time in the clock's time zone.
     * @return  the current time as a formatted string
     */
    public String getTime() {
        ZonedDateTime zonedDateTime = ZonedDateTime.now();
        return DateTimeFormatter.ofPattern("HH:mm:ss").format(zonedDateTime);
    }

    public String getRegion() {
        return this.region;
    }

    @Override
    public String toString() {
        return "Clock{" +
                "Id=" + Id +
                ", region='" + region + '\'' +
                '}';
    }

}



