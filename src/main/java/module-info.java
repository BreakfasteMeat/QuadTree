module com.example.quadtree {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.quadtree to javafx.fxml;
    exports com.example.quadtree;
}