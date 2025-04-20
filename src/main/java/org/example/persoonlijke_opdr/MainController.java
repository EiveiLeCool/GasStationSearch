package org.example.persoonlijke_opdr;

import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class MainController {

    @FXML
    private javafx.scene.control.ListView<GasStation> stationListView;
    private String gekozenSorteerType = "afstand";
    private String gekozenBrandstofType = "euro95";

    @FXML
    // Locatie input veld ding
    private TextField locationInput;
    @FXML
    // Radius input veld ding
    private TextField radiusInput;

    @FXML
    private void onSearchClicked() {
        String location = locationInput.getText();
        String radiusText = radiusInput.getText();
        if (location.isEmpty() || radiusText.isEmpty()) {
            showAlert("Error", "Graag zowel locatie als radius invullen.");
            return;
        }

        try {
            int radius = Integer.parseInt(radiusText);

            if (!LocationService.isValidCityName(location)) {
                showAlert("Error", "Voer een geldige stad in (alleen letters).");
                return;
            }

            ApiClient apiClient = new ApiClient();
            List<GasStation> stations = apiClient.searchGasStations(location, radius);



            if (stations.isEmpty()) {
                showAlert("Geen resultaten", "Geen tankstations gevonden binnen deze radius.");
                return;
            }




            // Sorteer de tankstations
            List<GasStation> sortedStations = GasStationService.sortBy(stations, gekozenSorteerType, gekozenBrandstofType);

            // Vul de lijst in het scherm
            stationListView.getItems().clear();
            stationListView.getItems().addAll(sortedStations);


            // Lijst vullen
            stationListView.getItems().clear();
            stationListView.getItems().addAll(stations);

        } catch (NumberFormatException e) {
            showAlert("Error", "Foute radius, graag alleen een getal.");
        }
    }


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.getDialogPane().setMinHeight(200); // Anders kan alert te klein zijn
        alert.showAndWait();
    }
    @FXML
    private void onSortByAfstandClicked() {
        gekozenSorteerType = "afstand";
        herlaadStations();
    }

    @FXML
    private void onSortByPrijsClicked() {
        gekozenSorteerType = "prijs";
        herlaadStations();
    }

    @FXML
    private void onSortByAfstandPrijsClicked() {
        gekozenSorteerType = "afstand+prijs";
        herlaadStations();
    }

    @FXML
    private void initialize() {
        stationListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Dubbelklik
                GasStation selectedStation = stationListView.getSelectionModel().getSelectedItem();
                if (selectedStation != null) {
                    showStationDetails(selectedStation);
                }
            }
        });
    }
    private void showStationDetails(GasStation station) {
        String mapsLink = "https://www.google.com/maps/dir/?api=1&destination="
                + station.getLatitude() + "," + station.getLongitude();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tankstation Details");
        dialog.setHeaderText(station.getName());

        // Create selectable but not clickable text field with the link
        TextField linkField = new TextField(mapsLink);
        linkField.setEditable(false);
        linkField.setPrefWidth(350);

        Button copyButton = new Button("Kopieer link");
        copyButton.setOnAction(e -> {
            linkField.selectAll();
            linkField.copy();
        });
        Button openRouteButton = new Button("Open Route");
        openRouteButton.setOnAction(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(mapsLink));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        HBox linkBox = new HBox(10, linkField, copyButton, openRouteButton);



        VBox content = new VBox(10);
        content.getChildren().addAll(
                new Label("Afstand: " + Math.round(station.getDistance()) + "m"),
                new Label("Euro95: €" + String.format("%.2f", station.getEuro95Price())),
                new Label("Diesel: €" + String.format("%.2f", station.getDieselPrice())),
                new Label("LPG: €" + String.format("%.2f", station.getLpgPrice())),
                new Label("Open: " + (station.isOpen() ? "Ja" : "Nee")),
                new Label("Brandstof beschikbaar: " + (station.hasFuel() ? "Ja" : "Nee")),
                new Label("Openingstijden: " + station.getOpeningHours()),
                new Label("Plan je route:"),
                linkBox
        );
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private void herlaadStations() {
        String location = locationInput.getText();
        String radiusText = radiusInput.getText();
        if (location.isEmpty() || radiusText.isEmpty()) {
            return;
        }

        try {
            int radius = Integer.parseInt(radiusText);
            ApiClient apiClient = new ApiClient();
            List<GasStation> stations = apiClient.searchGasStations(location, radius);

            List<GasStation> sortedStations = GasStationService.sortBy(stations, gekozenSorteerType, gekozenBrandstofType);
            stationListView.getItems().clear();
            stationListView.getItems().addAll(sortedStations);
        } catch (NumberFormatException e) {
            System.out.println("Foute radius, kan niet herladen.");
        }
    }



}
