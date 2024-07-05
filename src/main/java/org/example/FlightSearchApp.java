package org.example;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FlightSearchApp {
    private int numAdults;
    private int numChildren;
    private int numInfants;

    private Scanner scanner;
    private DatabaseFlightManager dbManager;

    public FlightSearchApp() throws SQLException, ClassNotFoundException {
        scanner = new Scanner(System.in);
        dbManager = new DatabaseFlightManager();
    }

    public void run() {
        try {
            User user = authenticateUser();

            boolean continueSearch = true;
            while (continueSearch) {
                List<Flight> flights = searchForFlights();

                if (flights.isEmpty()) {
                    continueSearch = askContinueSearch();
                } else {
                    Flight chosenFlight = chooseFlight(flights);
                    if (chosenFlight != null) {
                        choosePassengers();
                        double totalCost = displayTotalCostWithTaxes();
                        processBooking(user, chosenFlight, totalCost);
                        continueSearch = askContinueSearchForAnotherFlight();
                    } else {
                        System.out.println("Invalid Flight ID. Please try again.");
                    }

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources();
        }
    }

    private User authenticateUser() throws SQLException {
        User user = null;

        while (user == null) {
            System.out.print("Select the option (1. Registration, 2. Log in): ");
            int option = Integer.parseInt(scanner.nextLine());

            if (option == 1) {
                boolean registered = registerUser();
                if (registered) {
                    System.out.println("User registered successfully. Please log in.");
                }
            } else if (option == 2) {
                user = loginUser();
                if (user != null) {
                    System.out.println("Login successful. Welcome " + user.getUsername() + "!");
                } else {
                    System.out.println("Authentication failed. No record found for this user, please register.");
                }
            } else {
                System.out.println("Authentication failed. Please try again.");
            }
        }

        return user;
    }

    private boolean registerUser() {
        while (true) {
            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            System.out.print("Enter your email: ");
            String email = scanner.nextLine();
            System.out.print("Enter your password: ");
            String password = scanner.nextLine();

            try {
                dbManager.registerUser(username, email, password);
                return true;
            } catch (SQLException e) {
                System.out.println("Username or email already exists, please login.");
                System.out.println("Please try again or log in with existing credentials.");
                return false;
            }
        }
    }

    private User loginUser() throws SQLException {
        System.out.print("Enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Enter your password: ");
        String password = scanner.nextLine();
        User user = dbManager.authenticateUser(username, password);
        return user;
    }

    private List<Flight> searchForFlights() throws SQLException {
        System.out.print("Enter your departure region: ");
        String departureRegion = scanner.nextLine();

        LocalDate departureTime = readDate("Enter the date of departure (yyyy-MM-dd): ");

        System.out.print("Enter the region of arrival: ");
        String arrivalRegion = scanner.nextLine();

        LocalDate arrivalTime = readDate("Enter the date of arrival (yyyy-MM-dd): ");

        List<Flight> flights = dbManager.searchFlights(departureRegion, arrivalRegion, departureTime, arrivalTime);
        if (!flights.isEmpty()) {
            System.out.println("Available flights:");
            for (Flight flight : flights) {
                System.out.println(flight);
            }
        }
        return flights;
    }

    private Flight chooseFlight(List<Flight> flights) {
        System.out.print("Choose a flight by Flight ID: ");
        int chosenFlightID = Integer.parseInt(scanner.nextLine());

        for (Flight flight : flights) {
            if (flight.getFlightID() == chosenFlightID) {
                return flight;
            }
        }
        return null;
    }

    private boolean askContinueSearch() {
        System.out.print("Do you want to continue? (Yes/No): ");
        String response = scanner.nextLine();
        return !response.equalsIgnoreCase("No");
    }

    private boolean askContinueSearchForAnotherFlight() {
        System.out.print("Do you want to continue for searching another flight? (Yes/No): ");
        while (true) {
            if (scanner.hasNextLine()) {
                String response = scanner.nextLine();
                return response.equalsIgnoreCase("Yes");
            } else {
                System.out.println("No input found. Please try again.");
            }
        }
    }

    private void processBooking(User user, Flight chosenFlight, double totalCost) throws SQLException {
        System.out.print("Do you want to continue with booking? (Yes/No): ");
        String bookResponse = scanner.nextLine();

        if (bookResponse.equalsIgnoreCase("Yes")) {
            registerPassengers(chosenFlight.getFlightID());
            processPayment(user, chosenFlight, totalCost);
        } else {
            System.out.println("Booking cancelled.");
        }
    }

    private void choosePassengers() {
        System.out.println("Choose how many passengers: ");
        System.out.print("Enter the number of adults: ");
        numAdults = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter the number of children: ");
        numChildren = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter the number of babies: ");
        numInfants = Integer.parseInt(scanner.nextLine());
    }

    private double displayTotalCostWithTaxes() {
        double costPerAdult = 100.0;
        double costPerChild = 50.0;
        double costPerInfant = 0.0;
        double taxesPerPassenger = 20.0;

        double baseCost = (numAdults * costPerAdult) + (numChildren * costPerChild) + (numInfants * costPerInfant);
        double totalTaxes = (numAdults + numChildren + numInfants) * taxesPerPassenger;
        double totalCost = totalTaxes + baseCost;
        System.out.println("The total cost with all taxes included is: " + totalCost + " RON");
        return totalCost;
    }

    private void processPayment(User user, Flight chosenFlight, double totalCost) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your payment card details (in the format: 0000-0000-0000): ");
        String paymentCardDetails = scanner.nextLine();

        try {
            System.out.print("Enter card expiration date (MM/YYYY): ");
            String expirationDate = scanner.nextLine();

            System.out.print("Enter CVV: ");
            String cvv = scanner.nextLine();

            boolean paymentSuccessful = dbManager.processPayment(user.getUserID(), chosenFlight.getFlightID(), totalCost, paymentCardDetails, expirationDate, cvv);

            if (paymentSuccessful) {
                System.out.println("Payment processed successfully!");
                System.out.println("Please check your email to see the flight tickets.");
            } else {
                System.out.println("Payment failed. Please try again.");
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void registerPassengers(int flightId) {
        List<Passenger> passengers = new ArrayList<>();

        for (int i = 0; i < numAdults; i++) {
            boolean validPhoneNumber = false;
            String name = "";
            String phoneNumber = "";

            while (!validPhoneNumber) {
                System.out.print("Enter contact details for adult " + (i + 1) + ": ");
                System.out.print("Full Name: ");
                name = scanner.nextLine();
                System.out.print("Phone Number: ");
                phoneNumber = scanner.nextLine();
                if (isValidPhoneNumber(phoneNumber)) {
                    validPhoneNumber = true;
                } else {
                    System.out.println("Invalid phone number format. Please enter a 9-digit number.");
                }
            }
            passengers.add(new Passenger(name, 18, "Adult", flightId, formatPhoneNumber(phoneNumber)));
        }

        for (int i = 0; i < numChildren; i++) {
            System.out.print("Enter contact details for child " + (i + 1) + ": ");
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            passengers.add(new Passenger(name, 12, "Child", flightId, null));
        }

        for (int i = 0; i < numInfants; i++) {
            System.out.print("Enter contact details for infant " + (i + 1) + ": ");
            System.out.print("Full Name: ");
            String name = scanner.nextLine();
            passengers.add(new Passenger(name, 1, "Infant", flightId, null));
        }

        try {
            dbManager.registerPassengers(flightId, passengers);
        } catch (SQLException e) {
            System.out.println("Error registering passengers: " + e.getMessage());
        }
    }

    public void closeResources() {
        try {
            dbManager.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        scanner.close();
    }

    private LocalDate readDate(String prompt) {
        LocalDate dateTime = null;
        while (dateTime == null) {
            System.out.print(prompt);

            try {
                String dateString = scanner.nextLine();


                dateTime = LocalDate.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException e) {
                System.out.println("The date and time format is invalid. Try again.");
            }
        }
        return dateTime;
    }

    private String formatPhoneNumber(String phoneNumber) {
        if (!phoneNumber.startsWith("+40")) {
            return "+40" + phoneNumber;
        } else {
            return phoneNumber;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("\\d{9}");
    }

    private boolean isValidCardFormat(String cardNumber) {
        return cardNumber.matches("\\d{4}-\\d{4}-\\d{4}-\\d{4}");
    }

}

