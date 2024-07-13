package client.utils;

import client.scenes.MainCtrl;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public interface LanguageSelector {
    Map<String, Image> flags = new HashMap<>();

    final double flagWidth = 100;
    final double flagHeight = 50;

    /**
     * Returns the main controller
     *
     * @return the main controller
     */
    MainCtrl getMainCtrl();

    /**
     * Returns the language indicator
     *
     * @return the language indicator
     */
    MenuButton getLanguageIndicator();

    /**
     * Returns the menu item for the English language
     *
     * @return the menu item for the English language
     */
    MenuItem getEnglish();

    /**
     * Returns the menu item for the Dutch language
     *
     * @return the menu item for the Dutch language
     */
    MenuItem getDutch();

    /**
     * Returns the menu item for the Bulgarian language
     *
     * @return the menu item for the Bulgarian language
     */
    MenuItem getBulgarian();

    /**
     * Returns the menu item for the German language
     *
     * @return the menu item for the German language
     */
    MenuItem getGerman();

    /**
     * Returns the menu item for contributing a new language
     *
     * @return the menu item for contributing a new language
     */
    MenuItem getContribute();

    /**
     * Sets the flags for the language menu items
     */
    default void setFlags() {
        setLanguageMenuItem(getEnglish(), "murica", "en");
        setLanguageMenuItem(getDutch(), "netherlands", "nl");
        setLanguageMenuItem(getBulgarian(), "bulgaria", "bg");
        setLanguageMenuItem(getGerman(), "germany", "de");
        setAddLanguageMenuItem(getContribute(), "un");
        setLanguageButtonGraphic();
    }

    /**
     * Sets the language menu item with the specified flag and language
     *
     * @param menuItem the menu item to set
     * @param flag     the flag to set
     * @param language the language to set
     */
    default void setLanguageMenuItem(MenuItem menuItem, String flag, String language) {
        Image flagImage = new Image("/images/" + flag + ".png");
        Pane pane = setFlag(flagImage);
        menuItem.setGraphic(pane);
        menuItem.setStyle("-fx-font: 14px Arial; -fx-font-weight: bold");
        menuItem.textProperty().bind(Language.createStringBinding(language));
        flags.put(language, flagImage);
        menuItem.setOnAction(event -> getMainCtrl().updateLanguage(Locale.of(language)));
    }

    /**
     * Sets the menu item for contributing a new language with the specified flag
     *
     * @param menuItem the menu item to set
     * @param path     the path to set
     */
    default void setAddLanguageMenuItem(MenuItem menuItem, String path) {
        Image flagImage = new Image("/images/" + path + ".png");
        Pane pane = setFlag(flagImage);
        menuItem.setGraphic(pane);
        menuItem.setStyle("-fx-font: 14px Arial; -fx-font-weight: bold");
        menuItem.textProperty().bind(Language.createStringBinding("eventOverviewContribute"));
        menuItem.setOnAction(event -> generateLanguageTemplate());
    }

    /**
     * Sets the graphic for the language button
     */
    default void setLanguageButtonGraphic() {
        String currentLanguage = Language.getLanguage().getLanguage();
        Pane pane = setFlag(flags.get(currentLanguage));
        Circle border = new Circle(flagWidth / 3, flagHeight / 2, flagHeight / 2 - 2);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Paint.valueOf("#ececec"));
        border.setStrokeWidth(2);
        pane.getChildren().add(border);
        getLanguageIndicator().setGraphic(pane);
        getLanguageIndicator().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                border.setStroke(Color.LIGHTBLUE);
            } else {
                border.setStroke(Paint.valueOf("#ececec"));
            }
        });
    }

    /**
     * Sets the flag for the specified image
     *
     * @param flagImage the flag image to set
     * @return the pane with the flag image
     */
    default Pane setFlag(Image flagImage) {
        return getPane(flagImage, flagWidth, flagHeight);
    }

    /**
     * Returns the pane with the flag image
     *
     * @param flagImage  the flag image to set
     * @param flagWidth  the flag width to set
     * @param flagHeight the flag height to set
     * @return the pane with the flag image
     */
    default Pane getPane(Image flagImage, double flagWidth, double flagHeight) {
        ImageView flagImageView = new ImageView(flagImage);
        flagImageView.setFitWidth(flagWidth);
        flagImageView.setFitHeight(flagHeight);

        Circle clip = new Circle(flagWidth / 3, flagHeight / 2, flagHeight / 2 - 2);
        flagImageView.setClip(clip);

        Pane pane = new Pane();
        pane.getChildren().add(flagImageView);
        pane.setMaxSize(flagWidth / 2 + 10, flagWidth / 2 + 10);
        return pane;
    }

    /**
     * Generates a language template
     */
    default void generateLanguageTemplate() {
        String configPath = System.getProperty("configPath");
        Path path = Paths.get(configPath);
        Path parentDir = path.getParent();
        Path languagePropertiesPath = parentDir.resolve("language_en.properties");
        Path newLanguageFilePath = parentDir.resolve("language_NEW.properties");

        Properties prop = new Properties();

        try (InputStream input = new FileInputStream(languagePropertiesPath.toString())) {
            prop.load(input);
        } catch (IOException ignored) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error creating language template");
            alert.setContentText("An error occurred while creating the language template. " +
                    "Please try again.");
            alert.showAndWait();
        }

        Properties emptyProp = new Properties();
        for (String key : prop.stringPropertyNames()) {
            emptyProp.setProperty(key, "");
        }

        try (OutputStream output = new FileOutputStream(newLanguageFilePath.toFile())) {
            emptyProp.store(output, null);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Language template created");
            alert.setContentText("Please fill in the translations in the file " +
                    "language.properties and add the country code to the file name.");
            alert.showAndWait();
        } catch (IOException exception) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Error creating language template");
            alert.setContentText("An error occurred while creating the language template. " +
                    "Please try again.");
            alert.showAndWait();
        }
    }

}
