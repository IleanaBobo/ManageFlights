package org.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFlightManager {
    private static String url = "jdbc:mysql://localhost:3306/clock";
    private static String user = "root";
    private static String password = "root";
    private Connection connection;

    public DatabaseFlightManager() throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.connection = DriverManager.getConnection(url, user, password);
        this.connection.setAutoCommit(false);
    }

    public List<Flight> searchFlights(String departureRegion, String arrivalRegion, LocalDate departureTime, LocalDate arrivalTime) {
        List<Flight> flights = new ArrayList<>();
        String sql = "SELECT * FROM Flights WHERE DepartureRegion = ? AND ArrivalRegion = ?AND DepartureTime >= ? AND ArrivalTime <= ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, departureRegion);
            pstmt.setString(2, arrivalRegion);
            pstmt.setDate(3, Date.valueOf(departureTime));
            pstmt.setDate(4, Date.valueOf(arrivalTime));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int ID = rs.getInt("ID");
                String depRegion = rs.getString("DepartureRegion");
                String arrRegion = rs.getString("ArrivalRegion");
                LocalDate depTime = rs.getDate("DepartureTime").toLocalDate();
                LocalDate arrTime = rs.getDate("ArrivalTime").toLocalDate();

                flights.add(new Flight(ID, depRegion, arrRegion, depTime, arrTime));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flights;
    }

    public void registerUser(String username, String password, String email) throws SQLException {
        String checkUserQuery = "SELECT COUNT(*) FROM users WHERE username = ? OR email = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkUserQuery)) {
            checkStmt.setString(1, username);
            checkStmt.setString(2, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new SQLException("Username or email already exists.");
                }
            }
        }

        String insertUserQuery = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertUserQuery)) {
            insertStmt.setString(1, username);
            insertStmt.setString(2, email);
            insertStmt.setString(3, password);
            insertStmt.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }

    }

    public User authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("userID"), rs.getString("username"), rs.getString("email"), rs.getString("password"));
                } else {
                    System.out.println("Authentication failed for user: " + username);
                    return null;
                }
            }
        }
    }

    public void registerPassengers(int flightId, List<Passenger> passengers) throws SQLException {
        String query = "INSERT INTO passengers (flightId, name, age, type, phoneNumber) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (Passenger passenger : passengers) {
                stmt.setInt(1, flightId);
                stmt.setString(2, passenger.getName());
                stmt.setInt(3, passenger.getAge());
                stmt.setString(4, passenger.getType());
                stmt.setString(5, passenger.getPhoneNumber());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public boolean processPayment(int userId, int ID, double totalCost, String cardNumber, String expirationDate, String cvv) throws SQLException {
        if (cardNumber == null || cardNumber.isEmpty()) {
            System.out.println("Payment failed: Invalid card details.");
            return false;
        }

        String query = "INSERT INTO payments (userId, ID, totalCost, cardNumber, expirationDate, cvv) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, ID);
            stmt.setDouble(3, totalCost);
            stmt.setString(4, cardNumber);
            stmt.setString(5, expirationDate);
            stmt.setString(6, cvv);
            stmt.executeUpdate();
        }

        return true;
    }

    public void close() throws SQLException {
        if (connection != null) connection.close();
    }
}

