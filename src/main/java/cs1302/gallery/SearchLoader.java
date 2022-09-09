package cs1302.gallery;

import javafx.geometry.Pos;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.io.IOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import javafx.util.Duration;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.control.ProgressBar;
import javafx.application.Platform;

/**
 * A custom component that reduces the overall containment heirarchy
 * for {@link cs1302.gallery.GalleryApp} by creating a {@code SearchLoader}
 * which extends {@link javafx.scene.layout.VBox}.
 */
public class SearchLoader extends VBox {

    /* List of protected static instances variables that are used in Search Loader Class */
    protected static final int DEF_HEIGHT = 150;
    protected static final int DEF_WIDTH = 150;

    /* List of protected instances variables that are used in Search Loader Class */
    protected HBox toolBar;
    protected MenuLoader menuLoader;
    protected Button pausePlayButton;
    protected Label searchLabel;
    protected TextField searchField;
    protected Button updateImagesButton;
    protected InputStreamReader jsonReader;
    protected List<String> unusedImageURLs = new ArrayList<String>();
    protected TilePane tile;
    protected String[] usedImageURLs = new String[20];
    protected ImageView[] imageViewsOnScreen = new ImageView[20];
    protected HBox progressTab;
    protected ProgressBar progressBar;
    protected Label courtesyLabel;
    protected KeyFrame keyFrame;
    protected Timeline timeline;
    protected double currentProgress;

    /**
     * Default constructor for {@code SearchLoader} which explicitly calls super(), instantiates
     * the nodes in the {@code SearchLoader} sub-graph, and adds them to the {@code SearchLoader}.
     */
    public SearchLoader() {
         /* Call to VBox Constructor */
        super();

        tile = new TilePane();
        tile.setPrefColumns(5);
        tile.setPrefRows(4);

        createProgressTab();
        updateImages("Pop");
        createToolBar();

        /* Creates a seperate thread for randomly replacing images when the
           gallery app first opens. */
        runNow(() -> playMode(true));

        /* Event Handler for pressing the pause/play button. When the pause/play button
           is pressed, the the image views currently on the gallery app either start
           randomly start or stop randomly replacing images based on the the boolean
           value of enablePlayMode and text in the search field text. */
        pausePlayButton.setOnAction(e -> {
            boolean enablePlayMode;
            if (pausePlayButton.getText().equals("Pause")) {
                pausePlayButton.setText("Play");
                enablePlayMode = false;
            } else {
                pausePlayButton.setText("Pause");
                enablePlayMode = true;
            }
            /* Creates another seperate thread for randomly replacing images when the
               pause/play button is pressed. */
            runNow(() -> playMode(enablePlayMode));
        });


        /* Event Handler for pressing the update button. When the update image, a set
           of images from the new search query are gathered. */
        updateImagesButton.setOnAction(e -> {
            /* Creates a seperate thread for updating images. */
            runNow(() -> updateImages(searchField.getText()));
        });

        this.getChildren().addAll(toolBar, tile, progressTab);
    }

    /**
     * Recieves the user's indicated {@code searchQuery} and uses the iTunes
     * Search API to download the search's query JSON File as a
unusedImageURLs     * {@code InputStreamReader}.
     *
     * @param searchQuery the search query that the user entered in the search
     *                    textfield
     * @return reader an {@code InputStreamReader} for the search query's JSON
     *                File
     *
     */
    public InputStreamReader getJSONResponseQuery (String searchQuery) {
        String searchURL = "https://itunes.apple.com/search?term=";;
        String urlEncodedValue = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
        URL fullQueryURL = null;
        InputStreamReader reader = null;

        searchURL += urlEncodedValue + "&limit=150";

        /* A try catch statement that throws an exception and creates an alert
           if the specified search query url has a malformed exception or if the
           input stream reader has an input/output operation exception. */
        try {
            fullQueryURL = new URL(searchURL);
            reader = new InputStreamReader(fullQueryURL.openStream());
        } catch (MalformedURLException e) {
            createAlert("Malformed URL Exception Error", e.getMessage());
        } catch (IOException e) {
            createAlert("IO Exception Error", e.getMessage());
        }

        return reader;
    }

    /**
     * Creates the Tool Bar section upon starting the Gallery Application.
     */
    public void createToolBar() {
        /* Creates an HBox for the toolbar, which consists of a Pause Button, Search Query
           Label, and Update Images Button. */
        toolBar = new HBox(8);
        pausePlayButton = new Button("Pause");
        searchLabel = new Label("Search Query:");
        searchField = new TextField("Pop");
        updateImagesButton = new Button("Update Images");

        HBox.setHgrow(searchField, Priority.ALWAYS);

        toolBar.getChildren().addAll(pausePlayButton, searchLabel, searchField, updateImagesButton);
        toolBar.setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * Creates a {@code ProgressBar} upon starting the Gallery Application.
     */
    public void createProgressTab() {
        /* Creates a HBox for the progress bar, which consists of a Progress Bar
           and Courtesy Label. */
        progressTab = new HBox(8);
        progressBar = new ProgressBar(100.0);
        courtesyLabel = new Label("Images provided courtesy of iTunes");
        progressTab.getChildren().addAll(progressBar, courtesyLabel);
        progressTab.setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * Searches through a search query's JSON File, finds all the
     * artwork100 URL attribute/member within the JSON File, and
     * adds them to a {@code List<String>}.
     *
     * @param reader an {@code InputStreamReader} for the search query's JSON
     *               File
     * @return imageURLList an {@code List<String>} containing all the artworkUrl100
     *                      URL for the given JSON File
     */
    public List<String> getURLStrings (InputStreamReader reader) {
        /* Parses the JSON Response to get a JSON Element which represents the root
           the root of the response. */
        JsonElement JSONElement = JsonParser.parseReader(reader);
        /* Converts the root of the JSON Element a JSON Object. */
        JsonObject root = JSONElement.getAsJsonObject();
        /* Retrieves the JSON Array results from the root of the JSON Element. */
        JsonArray results = root.getAsJsonArray("results");
        int numResults = results.size();
        JsonObject result;
        String artworkUrl100;
        List<String> imageURLList = new ArrayList<String>();

        /* A for loop that tranverses through each element in the JSON Array and sees
           if it has a non-null artworkUrl100 attribute/url. If it does, then it stores
           the URL from the artworkUrl100 attribute/url to a list. */
        for (int i = 0; i < numResults; i++) {

            /* Get a the specified element in the JSON Array results */
            result = results.get(i).getAsJsonObject();

            /* Checks to see that the specified element from the JSON Array has a
               artworkUrl100 attribute/member. If it does have one, then it calls
               toString method for artworkUrl100 attribute/member to get the URL
               and store it in a list of URLs. */
            if (result.has("artworkUrl100") && result.get("artworkUrl100") != null) {
                artworkUrl100 = result.get("artworkUrl100").toString();
                artworkUrl100 = artworkUrl100.substring(1, artworkUrl100.length() - 1);
                if (notSameURL(imageURLList, artworkUrl100)) {
                    imageURLList.add(artworkUrl100);
                }
            }
        }

        return imageURLList;
    }

    /**
     * Determines whether a given artworkUrl100 URL in a praticular section
     * of the search query's JSON File is a duplicate by seeing if it is
     * already in the {@code imageURLList}.
     *
     * @param artworkUrl100 an artworkUrl100 URL attribute/member in a praticular
     *                      section of the search query's JSON File
     * @param imageURLList a {@code List<String>} for all the distinct artworkUrl100
     *                     URL that have so far been found in the search query's
     *                     JSON File.
     * @return notSameURL a {@code boolean} indicating if a given artwork100 URL
     *                    in the search query's JSON File is a duplicate
     */
    public boolean notSameURL(List<String> imageURLList, String artworkUrl100) {
        boolean notSameURL = true;
        /* A for loop that tranverses through each index of the current URL list,
           making sure the artworkUrl100 URL found is not already stored in that list.
           If it is already stored in the list, however, then notSameURL is set to
           false. */
        for (int i = 0; i < imageURLList.size() && notSameURL; i++) {
            if (artworkUrl100.equals(imageURLList.get(i))) {
                notSameURL = false;
            }
        }
        return notSameURL;
    }

    /**
     * Creates an {@code Alert} dialogue and displays the appropiate error to the
     * user when one is encountered.
     *
     * @param headerErrorMessage header text for the {@code Alert} dialogue
     * @param contentErrorMessage content text for the {@code Alert} dialogue
     */
    public void createAlert(String headerErrorMessage, String contentErrorMessage) {
        /* Sets the Alert dialogue to an ERROR type dialogue. Also, sets the
           Alert dialogue to an appropiate minimum height and makes sure that
           the Alert dialogue is resizable. */
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);

        alert.setTitle("Error Message!");
        alert.setHeaderText(headerErrorMessage);
        alert.setContentText(contentErrorMessage);
        alert.showAndWait();
    }

    /**
     * Creates an {@code ImageView} for each artwork100 image URL found in the
     * specified search query.
     *
     * @param imageURL an artwork100 image URL in the specified search query
     * @return currentImageView a {@code ImageView} for the specified artwork100
     *                          image URL
     */
    public ImageView createImageView(String imageURL) {
        Image currentImage = new Image(imageURL, DEF_HEIGHT, DEF_WIDTH, false, false);
        ImageView currentImageView = new ImageView(currentImage);
        return currentImageView;
    }

    /**
     * Updates the current images displayed on screen whenever the "Update Image" button
     * is pressed by gathering a new set of images from the specified search query and
     * displaying those images instead.
     *
     * @param searchQuery the search query that the user entered in the search
     *                    textfield
     */
    public void updateImages(String searchQuery) {
        /* Get sthe input stream reader for the search query's JSON File and then uses
           that input stream reader to a get a list of all the distinct artworkUrl100 URLs. */
        jsonReader = getJSONResponseQuery(searchQuery);
        List<String> tempList = getURLStrings(jsonReader);
        /* If less than twenty distinct artwork image URLs were gathered from, then an
           alert dialogue with the appropiate error message is displayed. Otherwise,
           twenty of the gathered images are displayed on the gallery app. */
        if (tempList.size() < 21) {
            String contentText = "The search query that you have entered has less than " +
                "twenty one distinct artwork image URLs. Please enter in a search query " +
                "that has more than twenty one distinct artwork image URLs.";
            Platform.runLater(() -> createAlert("Not Enough Images Error", contentText));
        } else {
            boolean appHasAlreadySetUp = false;
            /* If unusedImageURLs is not null, that means the allery app has been already set up
               and the update image button has been pressed. hus, this clears all the usedImageURLs
               list from the previous search query and resets the progress bar to 0.0. */
            if (unusedImageURLs != null) {
                unusedImageURLs.clear();
                currentProgress = 0.0;
                progressBar.setProgress(currentProgress);
                appHasAlreadySetUp = true;
            }
            /* Transfers all the distinct artworkUrl100 URLs from tempListto unusedImageURL. */
            for (int i = 0; i < tempList.size(); i++) {
                unusedImageURLs.add(tempList.get(i));
            }
            Runnable runnable;
            /* A for loop that generates twenty image view objects using the gathered artworkUrl100
               URLs from specified search query and then updates the progress bar appropiately. */
            for (int i = 0; i < 20; i++) {
                usedImageURLs[i] = unusedImageURLs.get(i);
                imageViewsOnScreen[i] = createImageView(usedImageURLs[i]);
                if (appHasAlreadySetUp) {
                    Platform.runLater(() -> updateProgressBar());
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    Platform.runLater(
                        () -> createAlert("InterruptedException Error", e.getMessage())
                    );
                }
            }
            /* If unusedImageURLs is not null, all the image views on the tile pane is cleared.*/
            if (unusedImageURLs != null) {
                Platform.runLater(() -> tile.getChildren().clear());
            }
            /* A for loop that calls createRunnable to add each of the twenty image objects
               to the current tile pane. */
            for (int i = 0; i < 20; i++) {
                runnable = createRunnable(i);
                Platform.runLater(runnable);
            }
            tile.setPrefWidth(750);
            tile.setPrefHeight(600);
            unusedImageURLs.subList(0, 20).clear();
        }
    }

    /**
     * Updates the {@code ProgressBar} when the "Update Image" button is pressed.
     * As the {@code ProgressBar} updates, it indicates the progress of querying the
     * iTunes Search API.
     */
    public void updateProgressBar() {
        /* Increments the progress bar by 0.05 or 5% each time a new image view object
           is generated using the gathered artworkUrl100 URLs from specified search
           query. */
        currentProgress += 0.05;
        progressBar.setProgress(currentProgress);
    }

    /**
     * Creates a {@code Runnable} when updating the images on a seperate thread.
     *
     * @param i the current index of the {@code imageViewsOnScreen[]} that should
     *          be added onto the {@code tile}
     * @return r {@code Runnable} for updating each {@code imageViewsOnScreen[]}
     *           on a seperate thread
     */
    public Runnable createRunnable(int i) {
        /* Creates a runnable task for updating images by adding the specified index
           in the imageViewOnScreen[] to the tile pane and setting VBox grow priority to
           new the tile pane height. */
        Runnable r = () -> {
            tile.getChildren().add(imageViewsOnScreen[i]);
            VBox.setVgrow(tile, Priority.ALWAYS);
        };
        return r;
    }

    /**
     * Enables or disables play mode for the Gallery Application when the Play/Pause
     * button is clicked. If play mode is enabled, then every 2 seconds a image
     * currently on display is randomly replaced with an image that is not currently
     * dsiplayed.
     *
     * @param enablePlayMode a {@code boolean} representing if play mode should be enabled
     *                       or disabled
     */
    public void playMode(boolean enablePlayMode) {
        /* An if statement that sees if play mode should be enabled or disabled. If play mode
           is enabled, then images start getting randomly replaced. Otherwise, they do not. */
        if (enablePlayMode) {
            /* Uses the Random Class to get an index of a random image URL stored in
               usedImageURLs and a random image URL stored in unusedImageURLs */
            EventHandler<ActionEvent> handler = event -> {
                int randomNonDisplayedImage = new Random().nextInt(unusedImageURLs.size());
                int randomDisplayedImage = new Random().nextInt(usedImageURLs.length);

                /* Adds the index of the random image URL stored in usedImageURLs to the end
                   of the unusedImageURLs list. Then constructs a new Image object using
                   the random image URL recieved in the unusedImageURLs list. */
                unusedImageURLs.add(usedImageURLs[randomDisplayedImage]);
                Image newImage = new Image(unusedImageURLs.get(randomNonDisplayedImage));

                /* Swaps the random image currently displayed with the randomy image that is
                   not currently displayed by setting the image view of random that is currently
                   displayed to the image of the random image that is not currently displayed. */
                imageViewsOnScreen[randomDisplayedImage].setImage(newImage);
                imageViewsOnScreen[randomDisplayedImage].setFitWidth(DEF_WIDTH);
                imageViewsOnScreen[randomDisplayedImage].setFitHeight(DEF_HEIGHT);
                usedImageURLs[randomDisplayedImage] = unusedImageURLs.get(randomNonDisplayedImage);
                unusedImageURLs.remove(randomNonDisplayedImage);
            };
            startRandomlyReplacingImage(handler);
            timeline.play();
        } else {
            timeline.pause();
        }
    }

    /**
     * Starts the timeline process for randomly replacing images through creating
     * {@code KeyFrame} and {@code Timeline}.
     *
     * @param handler the lambda expression which the {@code Timeline} will
     *                handle and perform every two seconds
     */
    public void startRandomlyReplacingImage(EventHandler<ActionEvent> handler) {
        /* Uses KeyFrame and Timeline objects to allow the event handler for
           swapping randomly replaced image to occur every two seconds. */
        keyFrame = new KeyFrame(Duration.seconds(2), handler);
        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(keyFrame);
    }

    /**
     * Creates and immediately starts a new daemon thread that executes
     * {@code target.run()}. This method, which may be called from any thread,
     * will return immediately its the caller. Please note that this section
     * of code was borrowed from the Brief Introduction to Java Threads
     * tutorial.
     *
     * @author Dr. Cotterell and Dr. Barnes
     * @param target the object whose {@code run} method is invoked when this
     *               thread is started
     */
    public static void runNow(Runnable target) {
        /* Creates and starts a new seperate thread. Also, sets the thread to be
           a deamon thread to prevent the thread from delaying program termination
           when the main thread or Java FX Application thread has terminated first. */
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    }
}
