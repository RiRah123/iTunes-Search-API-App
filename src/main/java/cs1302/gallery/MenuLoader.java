package cs1302.gallery;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A custom component that reduces the overall containment heirarchy
 * for {@link cs1302.gallery.GalleryApp} by creating a {@code MenuLoader}
 * which extends {@link javafx.scene.control.MenuBar}.
 */
public class MenuLoader extends MenuBar {

    protected final Menu fileMenu;
    protected final Menu helpMenu;

    protected MenuItem exitItem;
    protected MenuItem aboutItem;

    /**
     * Default constructor for {@code MenuLoader} which explicitly calls super(), instantiates
     * the nodes in the {@code MenuLoader} sub-graph, and adds them to the {@code MenuLoader}.
     */
    public MenuLoader () {
        /* Call to MenuBar Constructor */
        super();

        fileMenu = new Menu("File");
        helpMenu = new Menu("Help");

        exitItem = new MenuItem("Exit");

        /* Event Handler for pressing the exit item. When the exit menu item is pressed,
           Platform.exit() is called, allowing the application to exit gracefully. */
        exitItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    Platform.exit();
                }
            });

        aboutItem = new MenuItem("About");

        /* Event Handler for pressing the about item. When the about item is pressed,
           aboutRianRahmanStage() is called, which creates a stage with my image, name,
           email, and version number. */
        aboutItem.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    aboutRianRahmanStage();
                }
            });

        fileMenu.getItems().add(exitItem);
        helpMenu.getItems().add(aboutItem);

        this.getMenus().addAll(fileMenu, helpMenu);
    }

    /**
     * Creates an About Rian Rahman stage consisting of my name, email, and version
     * number. The stage is loaded whenever the user clicks the {@code MenuItem}
     * About under the {@code Menu} Help.
     */
    public void aboutRianRahmanStage() {
        Stage newStage = new Stage();
        VBox newPane = new VBox();
        StackPane holder = new StackPane();
        Scene newScene = new Scene(holder, 310, 420);

        /* Creates an ImageView with my image. */
        String imageURL = "https://i.imgur.com/Sz8j21i.jpg";
        Image myImage = new Image(imageURL, 250, 350, false, false);
        ImageView  myImageView = new ImageView(myImage);

        /* Creates the label with my name, email, and version number for the gallery
           application. Also, sets the font for label to verdana, bold, black, and 12
           size to make it more visually appealing.*/
        String myInformationString = "Name: Rian Rahman\nEmail: rr87004@uga.edu\n" +
            "Version Number: 5.0";
        Text myInformationLabel = new Text(myInformationString);
        myInformationLabel.setFill(Color.BLACK);
        myInformationLabel.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

        /* Adds the ImageView of my image and the previously created label to the tile
           pane. Then adds the tile pane to the a stack pane and sets the background
           color of the stack pane to navajowhite to make it more visually appealing. */
        newPane.getChildren().addAll(myImageView, myInformationLabel);
        newPane.setAlignment(Pos.CENTER);
        holder.getChildren().add(newPane);
        holder.setStyle("-fx-background-color: navajowhite");

        newStage.setMaxWidth(310);
        newStage.setMaxHeight(420);
        newStage.setTitle("About Rian Rahman");
        newStage.setScene(newScene);
        newStage.sizeToScene();
        newStage.showAndWait();
    }
}
