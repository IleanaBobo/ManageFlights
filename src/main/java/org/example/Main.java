package org.example;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        FlightSearchApp app = null;
        try {
            app = new FlightSearchApp();
            app.run();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (app != null) {
                app.closeResources();
            }
        }
    }
}

