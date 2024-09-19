package calibration.view;

import calibration.CalibrationApp;
import calibration.model.Analysis;
import calibration.model.TimeInterval;
import calibration.statistics.Statistic;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class RootLayoutController implements Initializable {
	
	private CalibrationApp app;

	@FXML private Button simulationDssFileBrowse;
	@FXML private Button simulationDssPathBrowse;
	@FXML private Button observationDssFileBrowse;
	@FXML private Button observationDssPathBrowse;
	
	@FXML private TextField computedDssTextField;
	@FXML private TextField computedDssPathTextField;
	@FXML private TextField observedDssTextField;
	@FXML private TextField observedDssPathTextField;
	@FXML private TextField startDateTextField;
	@FXML private TextField startTimeTextField;
	@FXML private TextField endDateTextField;
	@FXML private TextField endTimeTextField;
	@FXML private ComboBox<TimeInterval> timeIntervalComboBox;
	
	@FXML TableView<Statistic> statisticsTable = new TableView<>();
	@FXML TableColumn<Statistic, String> statisticCol;
	@FXML TableColumn<Statistic, Double> valueCol;
	@FXML TableColumn<Statistic, Double> ratingCol;
	
	@FXML private MenuItem export;
	
	CategoryAxis xAxis = new CategoryAxis();
	NumberAxis yAxis = new NumberAxis();
	@FXML LineChart<String,Number> cyclicMonthlyChart = new LineChart<String, Number>(xAxis,yAxis);
	
	StringBinding binding;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {	
		
		binding = new StringBinding() {
			{
				super.bind(computedDssTextField.textProperty(),
						computedDssPathTextField.textProperty(),
						observedDssTextField.textProperty(),
						observedDssPathTextField.textProperty(),
						startDateTextField.textProperty(),
						startTimeTextField.textProperty(),
						endDateTextField.textProperty(),
						endTimeTextField.textProperty(),
						timeIntervalComboBox.getSelectionModel().selectedItemProperty());
			}

			@Override
			protected String computeValue() {
				return (computedDssTextField.textProperty().get()
				+ computedDssPathTextField.textProperty().get()
				+ observedDssTextField.textProperty().get()
				+ observedDssPathTextField.textProperty().get()
				+ startDateTextField.textProperty().get()
				+ startTimeTextField.textProperty().get()
				+ endDateTextField.textProperty().get()
				+ endTimeTextField.textProperty().get()
				+ timeIntervalComboBox.getSelectionModel().selectedItemProperty().getValue());
			}
		};
		
		binding.addListener(observable -> {
			clearStatisticsTable();
			cyclicMonthlyChart.setVisible(false);
			clearMonthlyFlowChart();
			export.setDisable(true);
		});
		
		export.setDisable(true);

		Image browseFiles = new Image(getClass().getResourceAsStream("/Open16.gif"));
		Image browseDss = new Image(getClass().getResourceAsStream("/graph.png"));
		simulationDssFileBrowse.setGraphic(new ImageView(browseFiles));
		simulationDssPathBrowse.setGraphic(new ImageView(browseDss));
		observationDssFileBrowse.setGraphic(new ImageView(browseFiles));
		observationDssPathBrowse.setGraphic(new ImageView(browseDss));
		
		timeIntervalComboBox.getItems().setAll(TimeInterval.values());
		
    	statisticCol = new TableColumn<>("Statistic");
    	statisticCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    	statisticCol.prefWidthProperty().bind(statisticsTable.widthProperty().divide(2));
 
        valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(new PropertyValueFactory<>("value"));
        valueCol.prefWidthProperty().bind(statisticsTable.widthProperty().divide(4));
        
        ratingCol = new TableColumn<>("Moriasi Rating");
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        ratingCol.prefWidthProperty().bind(statisticsTable.widthProperty().divide(4));
        
        statisticsTable.setVisible(false);

        statisticsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    	statisticsTable.getColumns().addAll(statisticCol, valueCol, ratingCol);
    	statisticsTable.setPlaceholder(new Label("Results not computed"));

    	cyclicMonthlyChart.setVisible(false);
    	//cyclicMonthlyChart.setTitle("Monthly Average");
    	cyclicMonthlyChart.getXAxis().setLabel("Month");
	}
		
    public void add(CalibrationApp app) {
        this.app = app;
    }
    
    @FXML 
    private void handleComputedDssBrowse() {
    	FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "DSS files (*.dss)", "*.dss");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(app.getPrimaryStage());

        if (file != null) {
        	computedDssTextField.setText(file.toString());
        }
    }
    
    @FXML 
    private void handleObservedDssBrowse() {
    	FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "DSS files (*.dss)", "*.dss");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showOpenDialog(app.getPrimaryStage());

        if (file != null) {
        	observedDssTextField.setText(file.toString());
        }
    }

    @FXML
    private void handleComputedDssPathBrowse(){
		BrowseDss hmsBrowseDSS = new BrowseDss(computedDssTextField.getText());
		hmsBrowseDSS.setVisible(true);
		String selection = hmsBrowseDSS.getSelection();
		if (selection != null) {
			computedDssPathTextField.setText(selection);
		}
	}

	@FXML
	private void handleObservedDssPathBrowse(){
		BrowseDss hmsBrowseDSS = new BrowseDss(observedDssTextField.getText());
		hmsBrowseDSS.setVisible(true);
		String selection = hmsBrowseDSS.getSelection();
		if (selection != null) {
			observedDssPathTextField.setText(selection);
		}
	}

    
	@FXML 
	private void handleCompute() {
    	  	
    	setAnalysis();
    	
    	app.getAnalysis().validate();
    	if (app.getAnalysis().isValid()){
    		try {	
	        	app.getAnalysis().process();
	        	binding.getValue();
	        	updateStatisticsTable();
	        	updateCyclicMonthlyChart();
	        	export.setDisable(false);
    		} catch (Exception e) {
    			System.out.println(e);
    		}        	
    	} else {
    		
    		Alert alert = new Alert(AlertType.ERROR);
    		alert.setTitle("Error");
    		int errorCount = app.getAnalysis().getNumberErrors();
    		if (errorCount == 1) {
    			alert.setHeaderText("There was 1 error:");
    		} else {
    			alert.setHeaderText(String.format("There were %d errors:", errorCount));
    		}
    		alert.setContentText(app.getAnalysis().getErrorMessages());
    		alert.initModality(Modality.APPLICATION_MODAL);
    		alert.initOwner(app.getPrimaryStage());
    		alert.showAndWait();
    		
    	}
    };
    
	void updateStatisticsTable() {
		clearStatisticsTable();
    	statisticsTable.setEditable(false);
    	statisticsTable.setPlaceholder(new Label("Results invalid, recompute"));
    	       
    	statisticsTable.setItems(app.getAnalysis().getStatistics());
        statisticsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    	statisticsTable.getColumns().addAll(statisticCol, valueCol, ratingCol);
        statisticsTable.getSelectionModel().setCellSelectionEnabled(true);
        statisticsTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	statisticsTable.setVisible(true);
    	
    }
	
	void clearStatisticsTable() {
    	statisticsTable.getItems().clear();
    	statisticsTable.getColumns().clear();
	}
	
	void updateCyclicMonthlyChart() {
		clearMonthlyFlowChart();
    	String yLabel = app.getAnalysis().getParameter() + " (" + app.getAnalysis().getUnits() + ")";
    	cyclicMonthlyChart.getYAxis().setLabel(yLabel);
    	cyclicMonthlyChart.setTitle("Monthly Average " + yLabel);
    	ObservableList<Series<String, Number>> monthlyFlows = app.getAnalysis().getMonthlyFlows();
		app.getAnalysis().getMonthlyFlows().stream().forEach(
				series -> cyclicMonthlyChart.getData().add(series)
				);
		cyclicMonthlyChart.setVisible(true);
		
    }
	
	void clearMonthlyFlowChart() {
		cyclicMonthlyChart.getData().clear();
	}
    
    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        Optional<File> initialFilePath = Optional.ofNullable(app.getPersistedFile());
        if (initialFilePath.isPresent()) {
        	File filePath = initialFilePath.get();
        	if (filePath.exists()) {
				fileChooser.setInitialDirectory(initialFilePath.get().getParentFile());
			}
        }

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
		File file = null;
		try {
			file = fileChooser.showOpenDialog(app.getPrimaryStage());
		} catch (Exception e){
			e.printStackTrace();
		}

        if (file != null) {
            app.loadAnalysisFromFile(file, false);
            computedDssTextField.setText(app.getAnalysis().getComputedDssFile());
        	computedDssPathTextField.setText(app.getAnalysis().getComputedDssPath());
        	observedDssTextField.setText(app.getAnalysis().getObservedDssFile());
        	observedDssPathTextField.setText(app.getAnalysis().getObservedDssPath());
        	startDateTextField.setText(app.getAnalysis().getStartDate());
        	startTimeTextField.setText(app.getAnalysis().getStartTime());
        	endDateTextField.setText(app.getAnalysis().getEndDate());
        	endTimeTextField.setText(app.getAnalysis().getEndTime());
        	timeIntervalComboBox.setValue(app.getAnalysis().getInterval());      
        }
    }
    
    /**
     * Saves the file to the analysis file that is currently open. If there is no
     * open file, the "save as" dialog is shown.
     */
    @FXML private void handleSave() {
    	
    	setAnalysis();
    	
        Optional<File> analysisFile = app.getAnalysisFile();
        if (analysisFile.isPresent()) {
            app.saveAnalysisToFile(analysisFile);
        } else {
            handleSaveAs();
        }
    }
    
    /**
     * Opens a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {

    	setAnalysis();
    	
        FileChooser fileChooser = new FileChooser();
        Optional<File> initialFilePath = Optional.ofNullable(app.getPersistedFile());
        if (initialFilePath.isPresent()) {
        	fileChooser.setInitialDirectory(initialFilePath.get().getParentFile());
        }

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        Optional<File> file = Optional.ofNullable(fileChooser.showSaveDialog(app.getPrimaryStage()));

        if (file.isPresent()) {
            // Make sure it has the correct extension
            if (!file.get().getPath().endsWith(".xml")) {
                file = Optional.of(new File(file.get().getPath() + ".xml"));
            }
            app.saveAnalysisToFile(file);
            app.setAnalysisFile(file);
        }
    }
    
    /**
     * Opens a FileChooser to let the user select a file to export to.
     */
    @FXML
    private void handleExport() {
    	FileChooser fileChooser = new FileChooser();
        Optional<File> initialFilePath = Optional.ofNullable(app.getPersistedFile());
        if (initialFilePath.isPresent()) {
        	fileChooser.setInitialDirectory(initialFilePath.get().getParentFile());
        }

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
        		"TEXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        File file = fileChooser.showSaveDialog(app.getPrimaryStage());

        if (file != null) {
            // Make sure it has the correct extension
            if (!file.getPath().endsWith(".txt")) {
                file = new File(file.getPath() + ".txt");
            }
            app.exportStatisticsToFile(file);
        }
    }
    
    /**
     * Opens an about dialog.
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Continuous Calibration Aid");
        alert.setHeaderText("About");
        alert.setContentText("Contact: Tom Brauer");
		alert.initModality(Modality.APPLICATION_MODAL);
		alert.initOwner(app.getPrimaryStage());

        alert.showAndWait();
    }
    
    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
    
    private void setAnalysis() {
    	Analysis analysis = new Analysis();
    	analysis.setComputedDssFile(computedDssTextField.getText());
    	analysis.setComputedDssPath(computedDssPathTextField.getText());
    	analysis.setObservedDssFile(observedDssTextField.getText());
    	analysis.setObservedDssPath(observedDssPathTextField.getText());
    	analysis.setStartDate(startDateTextField.getText());
    	analysis.setStartTime(startTimeTextField.getText());
    	analysis.setEndDate(endDateTextField.getText());
    	analysis.setEndTime(endTimeTextField.getText());
    	analysis.setInterval(timeIntervalComboBox.getValue());
    	
    	app.setAnalysis(analysis);
    }

}


