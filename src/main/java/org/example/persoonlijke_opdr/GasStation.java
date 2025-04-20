
package org.example.persoonlijke_opdr;



public class GasStation implements Locatie {
    private String name;
    private double latitude;
    private double longitude;
    private boolean isOpen;
    private boolean hasFuel;
    private double distance; // in meters
    private String openingHours;

    // Brandstofprijzen
    private double euro95Price;
    private double dieselPrice;
    private double lpgPrice;

    // Constructor
    public GasStation(String name, double lat, double lon) {
        this.name = name;
        this.latitude = lat;
        this.longitude = lon;
    }

    // Getters
    public String getName() { return name; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getDistance() { return distance; }
    public boolean isOpen() { return isOpen; }
    public boolean hasFuel() { return hasFuel; }
    public String getOpeningHours() { return openingHours; }
    public double getEuro95Price() { return euro95Price; }
    public double getDieselPrice() { return dieselPrice; }
    public double getLpgPrice() { return lpgPrice; }

    // Setters
    public void setDistance(double distance) { this.distance = distance; }
    public void setOpen(boolean open) { this.isOpen = open; }
    public void setHasFuel(boolean hasFuel) { this.hasFuel = hasFuel; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }
    public void setEuro95Price(double euro95Price) { this.euro95Price = euro95Price; }
    public void setDieselPrice(double dieselPrice) { this.dieselPrice = dieselPrice; }
    public void setLpgPrice(double lpgPrice) { this.lpgPrice = lpgPrice; }

    @Override
    public String toString() {
        return name + " - " + Math.round(distance) + "m - €" + String.format("%.2f", euro95Price) + " (Euro95)";
    }
}
