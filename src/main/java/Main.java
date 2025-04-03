import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

// Store preferred locale in a configuration file or database
// Use AI-assisted translation to pre-fill localized .properties files
// Adopt ICU MessageFormat (for pluralization, gender handling)
// ResourceBundles can include strings, images, configuration data

public class Main extends Application {

    public static Locale currentLocale = Locale.getDefault();
    public static ResourceBundle bundle = ResourceBundle.getBundle("messages", currentLocale);

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/job-titles.fxml"), bundle);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle(bundle.getString("title"));
        stage.setScene(scene);
        stage.show();
    }

    public static void setLocale(Locale locale) {
        currentLocale = locale;
        bundle = ResourceBundle.getBundle("messages", currentLocale, new ResourceBundle.Control() {
        @Override
        public long getTimeToLive(String baseName, Locale locale){
            return TTL_DONT_CACHE;
        }
        });
    }

    public static void main(String[] args) {
        //Locale locale = Locale.ENGLISH;
        //Locale locale = Locale.FRENCH;
        //Locale locale = Locale.of("es", "ES");
        //Locale locale = Locale.CHINESE;

        //System.out.println(getMessage("labels", "app.title", locale));
        //System.out.println(getMessage("messages", "welcome.message", locale));
        //System.out.println(getMessage("errors", "error.notFound", locale));

        System.setProperty("prism.order", "sw");
        launch(args);
    }

    public static void displayMessages(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
        System.out.println("Locale: " + locale);
        System.out.println(bundle.getString("greeting"));

        MessageFormat format = new MessageFormat(bundle.getString("items"));
        System.out.println(format.format(new Object[]{3}));
    }

    public static String getMessage(String bundleName, String key, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        return bundle.getString(key);
    }
}
