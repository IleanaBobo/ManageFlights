package org.example;

public class Passenger {
    private String name;
    private int age;
    private String type;
    private int flightId;
    private String phoneNumber;

    public Passenger(String name, int age, String type, int flightId, String phoneNumber) {
        this.name = name;
        this.age = age;
        this.type = type;
        this.flightId = flightId;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getType() {
        return type;
    }
    public int getFlightId() {
        return flightId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
