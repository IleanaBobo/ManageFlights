package org.example;

import java.time.LocalDate;

public class Flight {
    private int flightId;
    private String departureRegion;
    private String arrivalRegion;
    private LocalDate departureTime;
    private LocalDate arrivalTime;

    public Flight(int flightId, String departureRegion, String arrivalRegion, LocalDate departureTime, LocalDate arrivalTime) {
        this.flightId = flightId;
        this.departureRegion = departureRegion;
        this.arrivalRegion = arrivalRegion;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public int getFlightID() {
        return flightId;
    }

    public void setFlightID(int flightID) {
        this.flightId = flightID;
    }

    public String getDepartureRegion() {
        return departureRegion;
    }

    public void setDepartureRegion(String departureRegion) {
        this.departureRegion = departureRegion;
    }

    public String getArrivalRegion() {
        return arrivalRegion;
    }

    public void setArrivalRegion(String arrivalRegion) {
        this.arrivalRegion = arrivalRegion;
    }

    public LocalDate getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDate departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDate getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDate arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    @Override
    public String toString() {
        return "Flight ID: " + flightId + ", Departure: " + departureRegion + ", Arrival: " + arrivalRegion +
                ", Departure Time: " + departureTime + ", Arrival Time: " + arrivalTime;
    }
}
