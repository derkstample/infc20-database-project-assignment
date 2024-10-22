module infc20.database.project.assignment {
    exports se.lu.ics;

    opens se.lu.ics.controllers to javafx.fxml;
    opens se.lu.ics.models to javafx.base;

    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires transitive javafx.graphics;

    opens se.lu.ics to javafx.fxml;
}
