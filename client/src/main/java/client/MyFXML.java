/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package client;

import client.utils.Language;
import com.google.inject.Injector;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.util.Builder;
import javafx.util.BuilderFactory;
import javafx.util.Callback;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

public class MyFXML {

    private Injector injector;

    /**
     * Constructor for MyFXML class.
     *
     * @param injector for dependency injection
     */
    public MyFXML(Injector injector) {
        this.injector = injector;
    }

    /**
     * Loads an FXML file and its associated controller class.
     *
     * @param <T>   the type parameter representing the controller class
     * @param c     the class object of the controller
     * @param parts the parts of the FXML file location
     * @return a Pair containing the loaded controller object
     * @throws RuntimeException if an IOException occurs during the loading process
     */
    @SuppressWarnings("deprecation")
    public <T> Pair<T, Parent> load(Class<T> c, String... parts) {
        try {
            var path = Paths.get(System.getProperty("configPath")).toAbsolutePath();
            var stream = Files.newBufferedReader(path);
            var properties = new Properties();
            properties.load(stream);
            String language = properties.getProperty("language");
            Locale locale = new Locale(language);
            Language.setLanguage(locale);
            ResourceBundle bundle = ResourceBundle.getBundle("language", locale);
            var loader = new FXMLLoader(getLocation(parts), bundle, null,
                    new MyFactory(), StandardCharsets.UTF_8);
            Parent parent = loader.load();
            T ctrl = loader.getController();

            return new Pair<>(ctrl, parent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private URL getLocation(String... parts) {
        var path = Path.of("", parts).toString();
        return MyFXML.class.getClassLoader().getResource(path);
    }

    private class MyFactory implements BuilderFactory, Callback<Class<?>, Object> {

        @Override
        @SuppressWarnings("rawtypes")
        public Builder<?> getBuilder(Class<?> type) {
            return new Builder() {
                @Override
                public Object build() {
                    return injector.getInstance(type);
                }
            };
        }

        @Override
        public Object call(Class<?> type) {
            return injector.getInstance(type);
        }
    }
}