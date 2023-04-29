module productivity{
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;


    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires annotations;

    requires java.sql;
    requires java.desktop;

    opens com;
    exports com;
    exports com.Controller;
    opens com.Controller;
    exports com.Model;
    opens com.Model;
    exports com.Controller.notes;
    opens com.Controller.notes;
}