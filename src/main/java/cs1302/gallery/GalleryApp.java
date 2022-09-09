package cs1302.gallery;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cs1302.gallery.MenuLoader;
import javafx.scene.layout.Priority;

/**
 * Represents an iTunes GalleryApp.
 */
public class GalleryApp extends Application {

    protected Stage stage;

    /**
     * The entry point for the gallery application.
     *
     * <p>
     * {@inheritdoc}
     */
    @Override
    public void start(Stage newStage) {
        stage = newStage;
        VBox pane = new VBox();
        Scene scene = new Scene(pane);

        MenuLoader menuLoader = new MenuLoader();
        SearchLoader searchLoader = new SearchLoader();

        /* Adds Search Loader and Menu Loader custom compotent
           to the scene graph. */
        pane.setVgrow(searchLoader, Priority.ALWAYS);
        pane.getChildren().addAll(menuLoader, searchLoader);

        stage.setMaxWidth(752);
        stage.setMaxHeight(700);
        stage.setTitle("GalleryApp!");
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    } // start

} // GalleryApp
