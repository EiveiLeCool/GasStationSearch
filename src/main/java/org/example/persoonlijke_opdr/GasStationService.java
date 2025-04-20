package org.example.persoonlijke_opdr;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GasStationService {

    public static List<GasStation> filterByRadius(List<GasStation> stations, double radiusMeters) {
        return stations.stream()
                .filter(gs -> gs.getDistance() <= radiusMeters)
                .collect(Collectors.toList());
    }

    public static List<GasStation> sortBy(List<GasStation> stations, String sortType, String fuelType) {
        switch (sortType.toLowerCase()) {
            case "afstand":
                stations.sort(Comparator.comparingDouble(GasStation::getDistance));
                break;
            case "prijs":
                switch (fuelType.toLowerCase()) {
                    case "euro95":
                        stations.sort(Comparator.comparingDouble(GasStation::getEuro95Price));
                        break;
                    case "diesel":
                        stations.sort(Comparator.comparingDouble(GasStation::getDieselPrice));
                        break;
                    case "lpg":
                        stations.sort(Comparator.comparingDouble(GasStation::getLpgPrice));
                        break;
                    default:
                        stations.sort(Comparator.comparingDouble(GasStation::getEuro95Price));
                        break;
                }
                break;
            case "afstand+prijs":
                stations.sort(Comparator.comparingDouble(gs -> gs.getDistance() * 0.7 + gs.getEuro95Price() * 100));
                break;
            default:
                stations.sort(Comparator.comparingDouble(GasStation::getDistance));
                break;
        }
        return stations;
   }public static void updateDistances(List<GasStation> stations, double userLat, double userLon) {
        for (GasStation gs : stations) {
            double distance = calculateDistance(userLat, userLon, gs.getLatitude(), gs.getLongitude());
            gs.setDistance(distance);
        }
    }
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}