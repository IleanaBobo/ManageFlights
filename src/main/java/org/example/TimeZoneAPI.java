package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TimeZoneAPI {
    private static final String API_KEY = "62WFMJBU0S03";
    private static final String API_URL = "http://api.timezonedb.com";

    public String getTimeZones(String country) throws IOException {
        String urlStr = API_URL + "?key=" + API_KEY + "&format=xml&by=zone&zone=" + country;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        StringBuilder response = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }
        public Clock parseTimeZoneInfo(String xml) {
            Clock clock = null;

            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new java.io.ByteArrayInputStream(xml.getBytes()));

                NodeList zoneNodes = doc.getElementsByTagName("result");
                if (zoneNodes.getLength() > 0) {
                    Element zoneElement = (Element) zoneNodes.item(0);
                    Integer zoneName = Integer.valueOf(zoneElement.getElementsByTagName("Id").item(0).getTextContent());
                    clock = new Clock(" ", 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return clock;
        }
    }

