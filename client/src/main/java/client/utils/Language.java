package client.utils;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.*;

public final class Language {

    /**
     * the current selected language.
     */
    private static final ObjectProperty<Locale> language;

    static {
        language = new SimpleObjectProperty<>(getDefaultLanguage());
        language.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    /**
     * get the supported languages.
     *
     * @return List of Locale objects.
     */
    public static List<Locale> getSupportedLanguages() {
        return new ArrayList<>(Arrays.asList(Locale.ENGLISH, Locale.forLanguageTag("nl"),
                Locale.forLanguageTag("de"), Locale.forLanguageTag("bg")));
    }

    /**
     * get the default language. This is the systems default if contained in the supported
     * language, english otherwise.
     *
     * @return default locale
     */
    public static Locale getDefaultLanguage() {
        Locale sysDefault = Locale.getDefault();
        return getSupportedLanguages().contains(sysDefault) ? sysDefault : Locale.ENGLISH;
    }

    /**
     * get the current selected language.
     *
     * @return current locale
     */
    public static Locale getLanguage() {
        return language.get();
    }

    /**
     * set the current selected language.
     *
     * @param language new locale
     */
    public static void setLanguage(Locale language) {
        localeProperty().set(language);
        Locale.setDefault(language);
    }

    /**
     * get the locale property.
     *
     * @return locale property
     */
    public static ObjectProperty<Locale> localeProperty() {
        return language;
    }

    /**
     * get the localized String for the given message bundle key.
     *
     * @param key  key
     * @param args optional arguments for the message
     * @return localized String
     */
    public static String get(String key, Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("language", getLanguage());
        return MessageFormat.format(bundle.getString(key), args);
    }

    /**
     * creates a String binding to a localized String for the given message bundle key
     *
     * @param key  key
     * @param args optional arguments for the message
     * @return String binding
     */
    public static StringBinding createStringBinding(String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), language);
    }
}