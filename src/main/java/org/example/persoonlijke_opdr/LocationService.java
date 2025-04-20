package org.example.persoonlijke_opdr;

import java.net.URLEncoder;

public class LocationService {

    public static boolean isValidCityName(String input) {
        return input.matches("^[a-zA-Z\\s-]+$"); // Alleen letters en spaties
    }

    public static String encodeCity(String cityName) {
        try {
            return URLEncoder.encode(cityName, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }
}
