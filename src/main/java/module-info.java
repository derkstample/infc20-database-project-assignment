module se.lu.ics {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens se.lu.ics to javafx.fxml;
    exports se.lu.ics;
}
