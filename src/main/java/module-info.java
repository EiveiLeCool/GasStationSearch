
module org.example.persoonlijke_opdr {
    requires org.json;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires java.desktop;


    opens org.example.persoonlijke_opdr to javafx.fxml;
    exports org.example.persoonlijke_opdr;
}

