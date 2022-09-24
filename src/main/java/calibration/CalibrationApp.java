package calibration;

import calibration.model.Analysis;
import calibration.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

public class CalibrationApp extends Application {
    private static final Logger logger = Logger.getLogger(CalibrationApp.class.getName());

    static {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            logger.warning(e::getMessage);
        }
    }

    Stage primaryStage;
    AnchorPane rootLayout;
    Analysis analysis;
    Optional<File> analysisFile = Optional.empty();

    private static final String WINDOW_POSITION_X = "Window_Position_X";
    private static final String WINDOW_POSITION_Y = "Window_Position_Y";
    private static final String WINDOW_WIDTH = "Window_Width";
    private static final String WINDOW_HEIGHT = "Window_Height";
    private static final double DEFAULT_X = 10;
    private static final double DEFAULT_Y = 10;
    private static final double DEFAULT_WIDTH = 600;
    private static final double DEFAULT_HEIGHT = 600;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Continuous Calibration Aid");

        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            this.primaryStage.setScene(scene);
            scene.getStylesheets().add(
                    getClass().getResource("/CalibrationApp.css").toExternalForm()
            );

            this.primaryStage.getIcons().add(new Image("/sine.png"));
            rootLayout.requestFocus();

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.add(this);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Preferences pref = Preferences.userNodeForPackage(CalibrationApp.class);
        double x = pref.getDouble(WINDOW_POSITION_X, DEFAULT_X);
        double y = pref.getDouble(WINDOW_POSITION_Y, DEFAULT_Y);
        double width = pref.getDouble(WINDOW_WIDTH, DEFAULT_WIDTH);
        double height = pref.getDouble(WINDOW_HEIGHT, DEFAULT_HEIGHT);
        this.primaryStage.setX(x);
        this.primaryStage.setY(y);
        this.primaryStage.setWidth(width);
        this.primaryStage.setHeight(height);

        this.primaryStage.show();

        this.primaryStage.setOnCloseRequest((final WindowEvent event) -> {
            Preferences prefs = Preferences.userNodeForPackage(CalibrationApp.class);
            prefs.putDouble(WINDOW_POSITION_X, this.primaryStage.getX());
            prefs.putDouble(WINDOW_POSITION_Y, this.primaryStage.getY());
            prefs.putDouble(WINDOW_WIDTH, this.primaryStage.getWidth());
            prefs.putDouble(WINDOW_HEIGHT, this.primaryStage.getHeight());
        });

    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    /**
     * Saves the current analysis to the specified file.
     *
     * @param file
     */
    public void saveAnalysisToFile(Optional<File> file) {
        try {
            JAXBContext context = JAXBContext.newInstance(Analysis.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Analysis analysis = getAnalysis();

            // Marshalling and saving XML to the file.
            marshaller.marshal(analysis, file.get());

            // Save the file path to the registry.
            setPersistedFile(file);
        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.get().getPath());

            alert.showAndWait();
        }
    }

    public void setAnalysisFile(Optional<File> file) {
        analysisFile = file;
        if (file.isPresent()) {
            primaryStage.setTitle(file.get().getName() + " - Continuous Calibration Aid");
            setPersistedFile(file);
        } else {
            primaryStage.setTitle("Continuous Calibration Aid");
        }
    }

    public Optional<File> getAnalysisFile() {
        return analysisFile;
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     *
     * @return
     */
    public File getPersistedFile() {
        Preferences prefs = Preferences.userNodeForPackage(CalibrationApp.class);
        String filePath = prefs.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in
     * the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setPersistedFile(Optional<File> file) {
        Preferences prefs = Preferences.userNodeForPackage(CalibrationApp.class);
        if (file.isPresent()) {
            prefs.put("filePath", file.get().getPath());
        } else {
            prefs.remove("filePath");
        }
    }

    /**
     * Loads person data from the specified file. The current data will be replaced.
     *
     * @param file
     */
    public void loadAnalysisFromFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(Analysis.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            this.analysis = (Analysis) um.unmarshal(file);

            // Save the file path to the registry.
            setAnalysisFile(Optional.ofNullable(file));
            setPersistedFile(Optional.ofNullable(file));

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

    public void exportStatisticsToFile(File file) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Statistic\tValue\tMoriasi Rating\n");
            Analysis analysis = getAnalysis();
            analysis.getStatistics().stream().forEach(statistic -> {
                stringBuilder.append(statistic.getName());
                stringBuilder.append("\t");
                stringBuilder.append(statistic.getValue());
                stringBuilder.append("\t");
                stringBuilder.append(statistic.getRating());
                stringBuilder.append("\n");
            });
            Files.write(file.toPath(), stringBuilder.toString().getBytes());
            // Save the file path to the registry.
            setPersistedFile(Optional.ofNullable(file));
        } catch (Exception e) { // catches ANY exception
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not save data");
            alert.setContentText("Could not save data to file:\n" + file.getPath());

            alert.showAndWait();
        }
    }

}

