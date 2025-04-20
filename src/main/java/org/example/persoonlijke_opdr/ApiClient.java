package org.example.persoonlijke_opdr;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {

    public List<GasStation> searchGasStations(String location, int radius) {
        List<GasStation> gasStations = new ArrayList<>();
        try {
            // 1. Geocoding
            String encodedLocation = URLEncoder.encode(location, "UTF-8");
            String geocodeUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedLocation + "&format=json";
            HttpURLConnection connection = (HttpURLConnection) new URL(geocodeUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "GasStationFinderApp/1.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            String lat = extractJsonValue(jsonResponse, "\"lat\":\"", "\"");
            String lon = extractJsonValue(jsonResponse, "\"lon\":\"", "\"");

            if (lat == null || lon == null) {
                System.out.println("Locatie niet gevonden.");
                return gasStations;
            }

            // 2. Overpass query
            String overpassQuery = "[out:json];node[\"amenity\"=\"fuel\"](around:" + radius + "," + lat + "," + lon + ");out;";
            String overpassUrl = "https://overpass-api.de/api/interpreter?data=" + URLEncoder.encode(overpassQuery, "UTF-8");

            HttpURLConnection overpassConnection = (HttpURLConnection) new URL(overpassUrl).openConnection();
            overpassConnection.setRequestMethod("GET");

            BufferedReader overpassReader = new BufferedReader(new InputStreamReader(overpassConnection.getInputStream()));
            StringBuilder overpassResponse = new StringBuilder();
            while ((line = overpassReader.readLine()) != null) {
                overpassResponse.append(line);
            }
            overpassReader.close();

            String overpassJson = overpassResponse.toString();
            JSONObject obj = new JSONObject(overpassJson);
            JSONArray elements = obj.getJSONArray("elements");

            // 3. Parse stations
            for (int i = 0; i < elements.length(); i++) {
                JSONObject element = elements.getJSONObject(i);

                double stationLat = element.getDouble("lat");
                double stationLon = element.getDouble("lon");

                String name = element.has("tags") && element.getJSONObject("tags").has("name") ?
                        element.getJSONObject("tags").getString("name") : "Onbekend tankstation";

                GasStation station = new GasStation(name, stationLat, stationLon);

                // Open/Brandstof dummy voor nu
                station.setHasFuel(true);
                station.setOpen(true);

                // Openingstijden
                if (element.has("tags") && element.getJSONObject("tags").has("opening_hours")) {
                    String opening = element.getJSONObject("tags").getString("opening_hours");
                    station.setOpeningHours(opening);
                } else {
                    station.setOpeningHours("Openingstijden onbekend");
                }

                // Brandstofprijzen ophalen
                JSONObject tags = element.optJSONObject("tags");
                if (tags != null) {
                    if (tags.has("fuel:euro95")) {
                        String euro95Value = tags.getString("fuel:euro95");
                        if (isNumeric(euro95Value)) {
                            station.setEuro95Price(Double.parseDouble(euro95Value));
                        } else {
                            station.setEuro95Price(1.70 + Math.random() * 0.2);
                        }
                    } else {
                        station.setEuro95Price(1.70 + Math.random() * 0.2);
                    }

                    if (tags.has("fuel:diesel")) {
                        String dieselValue = tags.getString("fuel:diesel");
                        if (isNumeric(dieselValue)) {
                            station.setDieselPrice(Double.parseDouble(dieselValue));
                        } else {
                            station.setDieselPrice(1.50 + Math.random() * 0.2);
                        }
                    } else {
                        station.setDieselPrice(1.50 + Math.random() * 0.2);
                    }

                    if (tags.has("fuel:lpg")) {
                        String lpgValue = tags.getString("fuel:lpg");
                        if (isNumeric(lpgValue)) {
                            station.setLpgPrice(Double.parseDouble(lpgValue));
                        } else {
                            station.setLpgPrice(0.80 + Math.random() * 0.2);
                        }
                    } else {
                        station.setLpgPrice(0.80 + Math.random() * 0.2);
                    }

                }

                gasStations.add(station);
            }

            // 4. Afstanden bijwerken
            double userLat = Double.parseDouble(lat);
            double userLon = Double.parseDouble(lon);
            GasStationService.updateDistances(gasStations, userLat, userLon);

        } catch (Exception e) {
            System.out.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        return gasStations;
    }

    private String extractJsonValue(String json, String start, String end) {
        int startIdx = json.indexOf(start);
        if (startIdx == -1) return null;
        startIdx += start.length();
        int endIdx = json.indexOf(end, startIdx);
        return (endIdx == -1) ? null : json.substring(startIdx, endIdx);
    }
    private boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
