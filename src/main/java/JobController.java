import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;


public class JobController {

    @FXML private Label title, error_label;
    @FXML private ComboBox<String> languageSelector;
    @FXML private ListView<String> job_titles;
    @FXML private Button fetch_button;

    private static final Map<String, Locale> LANGUAGE_MAP = new LinkedHashMap<>();

    static {
        LANGUAGE_MAP.put("English", Locale.of("en"));
        LANGUAGE_MAP.put("French", Locale.of("fr"));
        LANGUAGE_MAP.put("Spanish", Locale.of("es"));
        LANGUAGE_MAP.put("Chinese", Locale.of("zh"));
    }

    @FXML
    public void initialize() {
        languageSelector.getItems().addAll(LANGUAGE_MAP.keySet());

        Locale defaultLocale = Locale.getDefault();
        languageSelector.setValue(LANGUAGE_MAP.entrySet().stream()
                .filter(entry -> entry.getValue().getLanguage().equals(defaultLocale.getLanguage()))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("English"));

        languageSelector.setOnAction(event -> switchLanguage());
    }

    private void switchLanguage() {
        languageSelector.setOnAction(null);

        String selectedLanguage = languageSelector.getValue();
        Locale newLocale = LANGUAGE_MAP.get(selectedLanguage);

        if (newLocale != null) {
            Main.setLocale(newLocale);
            reloadScene(selectedLanguage);
        }
    }

    private void reloadScene(String selectedLanguage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/job-titles.fxml"));
            loader.setResources(Main.bundle);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) title.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(Main.bundle.getString("title"));

            JobController controller = loader.getController();
            controller.setSelectedLanguage(selectedLanguage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setSelectedLanguage(String selectedLanguage) {
        languageSelector.setOnAction(null);
        languageSelector.setValue(selectedLanguage);
        languageSelector.setOnAction(event -> switchLanguage());
    }

    @FXML
    private void searchJobTitles() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        String dbUsername = properties.getProperty("db.username");
        String dbPassword = properties.getProperty("db.password");

        try {
            Connection connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/otp2", dbUsername, dbPassword);
            String q = "SELECT Key_name, translation_text FROM translations WHERE Language_code = ?";
            PreparedStatement statement = connection.prepareStatement(q);
            statement.setString(1, Main.currentLocale.getLanguage());
            ResultSet resultSet = statement.executeQuery();

            job_titles.getItems().clear();
            while (resultSet.next()) {
                String keyName = resultSet.getString("Key_name");
                String translationText = resultSet.getString("translation_text");
                job_titles.getItems().add(translationText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
