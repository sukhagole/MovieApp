package view;

import database.DatabaseManager;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Movie;
import model.MovieTableModel;

import static model.StartupConstants.*;
/**
 * This class provides the User Interface for this application,
 * providing controls and the entry points for creating, loading,
 * viewing movie posters.
 *
 * @author Edward Rodriguez, Raymond Calapatia, Sukharam Gole, Fasih Uddin
 */
public class MovieAppView {
    // LIST OF MOVIES FROM DB
    MovieTableModel movieList;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION GUI
    VBox rootPane;

    // WORKSPACE
    BorderPane maPane;

    // THIS WILL GO AT THE TOP OF SCREEN
    VBox headerPane;
    Label nowPlayingLabel;

    // FILTER PANE AND COMPONENTS
    HBox filterBox;
    Label filterLabel;
    CheckBox allCheckBox;
    CheckBox pgRatingCheckBox;
    CheckBox pg13RatingCheckBox;
    CheckBox rRatingCheckBox;
    CheckBox nc17RatingCheckBox;
    CheckBox gRatingCheckBox;

    // POSTER LISTING SPACE (CENTER)
    FlowPane movieListPane;

    // THIS WILL ENCAPSULATE WORKSPACE TO ALLOW
    // SCROLLABILITY
    ScrollPane scrollPane;

    // MAIN APP UI WINDOW AND SCENE GRAPH
    Stage window;
    Scene primaryScene;

    // COMPONENTS FOR BACKGROUND IMAGE
    Image image;
    double imageWidth;
    double imageHeight;
    BackgroundSize backgroundSize;
    BackgroundImage backgroundImage;
    Background background;

    // WINDOW BUTTONS
    Button minimizeButton;
    Button closeButton;
    HBox windowPane;

    DatabaseManager db;

    public MovieAppView(DatabaseManager db) {
        this.db = db;
        movieList = new MovieTableModel();
        movieList = db.getMovieTableModel();
    }

    private void initMovieListPane(){
        movieListPane = new FlowPane();
        movieListPane.setPrefWrapLength(945); // preferred width = 300
        movieListPane.setPrefHeight(600);
        scrollPane = new ScrollPane();

        // SETUP BACKGROUND IMAGE
        image = new Image("img/blur-blurred-dark-1526.jpg");
        imageWidth = movieListPane.getWidth();
        imageHeight = movieListPane.getHeight();
        backgroundSize = new BackgroundSize(imageWidth, imageHeight, true, true, true, true);
        backgroundImage = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                                                    BackgroundPosition.DEFAULT, backgroundSize);
        background = new Background(backgroundImage);

        // STYLE CLASSES
        movieListPane.getStyleClass().add(CSS_CLASS_FLOW_PANE);
        scrollPane.getStyleClass().add("edge-to-edge");
    }

    private void initTopBarPane() {
        headerPane = new VBox();
        Image logo = new Image("img/logo2.png");
        ImageView logoView = new ImageView(logo);

        // SETUP CUSTOM MIN/CLOSE WINDOW BUTTONS
        Image image = new Image("img/icons8-delete-50.png", 25, 25, false, false);
        closeButton = new Button();
        closeButton.setGraphic(new ImageView(image));
        closeButton.setStyle("-fx-border-color: transparent ");
        Image image2 = new Image("img/icons8-subtract-50.png", 25, 25, false, false);
        minimizeButton = new Button();
        minimizeButton.setGraphic(new ImageView(image2));
        windowPane = new HBox();
        windowPane.getChildren().addAll(minimizeButton, closeButton);

        // FILTER LABEL AND CHECKBOXES
        filterBox = new HBox();
        filterLabel = new Label("Filter by Rating: ");
        allCheckBox = new CheckBox("All");
        pgRatingCheckBox = new CheckBox("PG");
        pg13RatingCheckBox = new CheckBox("PG-13");;
        rRatingCheckBox = new CheckBox("R");;
        nc17RatingCheckBox = new CheckBox("NC-17");;
        gRatingCheckBox = new CheckBox("G");

        // SETUP SPACING AND STYLE CLASSES
        filterBox.getStyleClass().add(CSS_CLASS_FILTER_BOX);
        closeButton.getStyleClass().add(CSS_CLASS_CLOSE_BUTTON);
        headerPane.getStyleClass().add(CSS_CLASS_HEADER_PANE);
        minimizeButton.getStyleClass().add(CSS_CLASS_MINIMIZE_BUTTON);
        windowPane.getStyleClass().add(CSS_CLASS_WINDOW_PANE);

        filterBox.getChildren().addAll(filterLabel, allCheckBox, gRatingCheckBox, pgRatingCheckBox, pg13RatingCheckBox,
                                       rRatingCheckBox, nc17RatingCheckBox);
        headerPane.getChildren().addAll(logoView, filterBox);

    }

    public void startUI(Stage primaryStage, String windowTitle){

        window = primaryStage;
        initTopBarPane();
        initMovieListPane();
        initWindow(windowTitle);
        initEventHandlers();
        reloadMovieListPane();

    }

    public void reloadMovieListPane() {
        for (Movie movie : movieList.getMovies()) {
            MovieView movieEditor = new MovieView(movie, movieList);
            MovieDescription movieEditor1 = new MovieDescription(movie, movieList);
            movieListPane.getChildren().add(movieEditor);

            movieEditor.getImageView().setOnMouseEntered(e ->  {
                primaryScene.setCursor(Cursor.HAND);
            });
            movieEditor.getImageView().setOnMouseExited(e -> {
                primaryScene.setCursor(Cursor.DEFAULT);
            });
            movieEditor.getImageView().setOnMouseClicked(e->{
            	maPane.getChildren().clear();
            	movieListPane = new FlowPane();
            	movieListPane.setPrefWrapLength(945);
            	movieListPane.getChildren().add(movieEditor1);
            	maPane.setCenter(movieListPane);
                primaryScene = new Scene(maPane, 955, 600);
                window.setScene(primaryScene);
                window.show();
               });
        }   
    }

    private void initEventHandlers() {
        minimizeButton.setOnMouseClicked(e -> {
            window.setIconified(true);
        });

        closeButton.setOnAction(e -> {
            Platform.exit();
        });

        allCheckBox.setOnAction(e -> {
            gRatingCheckBox.setSelected(true);
            pg13RatingCheckBox.setSelected(true);
            pgRatingCheckBox.setSelected(true);
            rRatingCheckBox.setSelected(true);
            nc17RatingCheckBox.setSelected(true);
        });
    }

    public boolean allCheckBoxIsSelected() {
        return allCheckBox.isSelected() ? true : false;
    }

    public MovieTableModel getMovieList() {
        return movieList;
    }
    private void initWindow(String windowTitle) {
        window.setTitle(windowTitle);

        maPane = new BorderPane();
        maPane.setTop(headerPane);
        maPane.setCenter(movieListPane);

        scrollPane.setContent(maPane);
        movieListPane.setBackground(background);

        rootPane = new VBox();
        rootPane.getChildren().addAll(windowPane, scrollPane);

        primaryScene = new Scene(rootPane, 965, 600);
        primaryScene.getStylesheets().add("css/movieStyle.css");

        window.setScene(primaryScene);
        window.setResizable(false);
        primaryScene.setFill(Color.TRANSPARENT);
        window.initStyle(StageStyle.TRANSPARENT);
        window.show();
    }
}
