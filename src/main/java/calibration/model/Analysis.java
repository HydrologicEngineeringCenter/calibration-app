package calibration.model;

import calibration.statistics.*;
import hec.heclib.dss.HecDataManager;
import hec.heclib.util.HecTime;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.hecmath.TimeSeriesMath;
import hec.io.TimeSeriesContainer;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;

@XmlRootElement
public class Analysis {

    StringProperty observedDssFile = new SimpleStringProperty();
    StringProperty observedDssPath = new SimpleStringProperty();
    StringProperty computedDssFile = new SimpleStringProperty();
    StringProperty computedDssPath = new SimpleStringProperty();
    StringProperty startDate = new SimpleStringProperty();
    StringProperty startTime = new SimpleStringProperty();
    StringProperty endDate = new SimpleStringProperty();
    StringProperty endTime = new SimpleStringProperty();
    StringProperty intervalProperty = new SimpleStringProperty();
    TimeInterval interval;
    StringBinding binding;

    private String parameter;
    private String units;
    private boolean valid;
    private boolean current;
    private boolean processed;
    int errors;
    private String errorMessages;
    private TimeSeriesContainer observedTsc;
    private TimeSeriesContainer computedTsc;

    private ObservableList<Statistic> statistics = FXCollections.observableArrayList();
    private ObservableList<XYChart.Series<String,Number>> monthlyFlow = FXCollections.observableArrayList();

    public Analysis(){
        initialize();
    }

    public Analysis(File file){
        try {
            JAXBContext context = JAXBContext.newInstance(Analysis.class);
            Unmarshaller um = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            this.observedDssFile = ((Analysis) um.unmarshal(file)).observedDssFile;
            this.observedDssPath = ((Analysis) um.unmarshal(file)).observedDssPath;
            this.computedDssFile = ((Analysis) um.unmarshal(file)).computedDssFile;
            this.computedDssPath = ((Analysis) um.unmarshal(file)).computedDssPath;
            this.startDate = ((Analysis) um.unmarshal(file)).startDate;
            this.startTime = ((Analysis) um.unmarshal(file)).startTime;
            this.endDate = ((Analysis) um.unmarshal(file)).endDate;
            this.endTime = ((Analysis) um.unmarshal(file)).endTime;
            this.interval = ((Analysis) um.unmarshal(file)).interval;

        } catch (Exception e) { // catches ANY exception
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not load data");
            alert.setContentText("Could not load data from file:\n" + file.getPath());

            alert.showAndWait();
        }
        initialize();
    }

    private void initialize() {
        processed = false;
        valid = false;
        bindProperties();
    }

    private void bindProperties() {
        current = true;
        binding = new StringBinding() {
            {
                super.bind(computedDssFile,
                        computedDssPath,
                        observedDssFile,
                        observedDssPath,
                        startDate,
                        startTime,
                        endDate,
                        endTime,
                        intervalProperty);
            }

            @Override
            protected String computeValue() {
                return (computedDssFile.get()
                        + computedDssPath.get()
                        + observedDssFile.get()
                        + observedDssPath.get()
                        + startDate.get()
                        + startTime.get()
                        + endDate.get()
                        + endTime.get()
                        + intervalProperty.get());
            }
        };

        binding.addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                current = false;
                valid = false;
                processed = false;
            }

        });
    }

    public void process() {
        try {
            computeStatistics();
            computeCyclicMonthly();
        } catch (HecMathException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        processed = true;
    }

    public void validate() {
        errors = 0;
        valid = true;
        StringBuilder errorMessages = new StringBuilder();

        Map<String,String> dssRecords = new HashMap<>();
        dssRecords.put(observedDssFile.get(), observedDssPath.get());
        dssRecords.put(computedDssFile.get(), computedDssPath.get());

        dssRecords.forEach((dssFile, dssPath) -> {
            File file = new File(dssFile);
            if (!file.exists()) {
                errors++;
                String errorMessage = String.format("File \"%s\" does not exist\n", dssFile);
                errorMessages.append(errorMessage);
                valid = false;
            } else {
                HecDataManager dataManager = new HecDataManager();
                int status = dataManager.setDSSFileName(dssFile);
                if (status != 0) {
                    errors++;
                    String errorMessage = String.format("Cannot access DSS file: %s\n", dssFile);
                    errorMessages.append(errorMessage);
                    valid = false;
                }
            }
        });

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("ddMMMyyyy")
                .toFormatter();
        LocalDate startDate = LocalDate.MIN;
        try {
            startDate = LocalDate.parse(this.startDate.get(), formatter);
        } catch (DateTimeParseException e) {
            System.out.println(e);
            errors++;
            String errorMessage = "Start date is invalid\n";
            errorMessages.append(errorMessage);
            valid = false;
        }

        LocalDate endDate = LocalDate.MAX;
        try {
            endDate = LocalDate.parse(this.endDate.get(), formatter);
        } catch (DateTimeParseException e) {
            errors++;
            String errorMessage = "End date is invalid\n";
            errorMessages.append(errorMessage);
            valid = false;
        }

        formatter = DateTimeFormatter.ofPattern("HHmm");
        LocalTime startTime = LocalTime.MIN;
        try {
            startTime = LocalTime.parse(this.startTime.get(), formatter);
        } catch (DateTimeParseException e) {
            errors++;
            String errorMessage = "Start time is invalid\n";
            errorMessages.append(errorMessage);
            valid = false;
        }

        LocalTime endTime = LocalTime.MAX;
        try {
            endTime = LocalTime.parse(this.endTime.get(), formatter);
        } catch (DateTimeParseException e) {
            errors++;
            String errorMessage = "End time is invalid\n";
            errorMessages.append(errorMessage);
            valid = false;
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
        if (startDateTime.isAfter(endDateTime)) {
            errors++;
            String errorMessage = "Start time must be before end time\n";
            errorMessages.append(errorMessage);
            valid = false;
        }

        TscRetriever.TscRetrieverBuilder builder = new TscRetriever.TscRetrieverBuilder();
        builder.dssFile(observedDssFile.get());
        builder.dssPath(observedDssPath.get());
        builder.startDate(this.startDate.get());
        builder.startTime(this.startTime.get());
        builder.endDate(this.endDate.get());
        builder.endTime(this.endTime.get());
        TscRetriever retriever = builder.build();
        Optional<TimeSeriesContainer> observedTsc = retriever.retrieve();
        if (observedTsc.isPresent()) {
            this.observedTsc = observedTsc.get();
        } else {
            errors++;
            String errorMessage = "DSS read error\n";
            errorMessages.append(errorMessage);
            valid = false;
        }

        builder = new TscRetriever.TscRetrieverBuilder();
        builder.dssFile(computedDssFile.get());
        builder.dssPath(computedDssPath.get());
        builder.startDate(this.startDate.get());
        builder.startTime(this.startTime.get());
        builder.endDate(this.endDate.get());
        builder.endTime(this.endTime.get());
        retriever = builder.build();
        Optional<TimeSeriesContainer> computedTsc = retriever.retrieve();
        if (computedTsc.isPresent()) {
            this.computedTsc = computedTsc.get();
        } else {
            errors++;
            String errorMessage = "DSS read error\n";
            errorMessages.append(errorMessage);
            valid = false;
        }

        if (observedTsc.isPresent() && computedTsc.isPresent()) {
            if (this.computedTsc.units.equals(this.observedTsc.units)) {
                units = this.computedTsc.units;
            } else {
                errors++;
                String errorMessage = "Computed and observed units are not equivalent\n";
                errorMessages.append(errorMessage);
                valid = false;
            }
        }

        if (observedTsc.isPresent() && computedTsc.isPresent()) {
            String observedParameter = this.observedTsc.parameter.split("-")[0];
            String computedParameter = this.computedTsc.parameter.split("-")[0];

            if (computedParameter.equals(observedParameter)) {
                parameter = observedParameter;
            } else {
                errors++;
                String errorMessage = "Computed and observed parameters are not equivalent\n";
                errorMessages.append(errorMessage);
                valid = false;
            }
        }

        this.errorMessages = errorMessages.toString();

    }

    private void computeStatistics() throws HecMathException {

        final TimeSeriesContainer simulatedFlow = (TimeSeriesContainer)((TimeSeriesMath) HecMath.createInstance(
                computedTsc).transformTimeSeries(interval.toString(),"","AVE")).getData();

        final TimeSeriesContainer observedFlow = (TimeSeriesContainer) HecMath.createInstance(
                observedTsc).transformTimeSeries(interval.toString(),"","AVE").getData();


        statistics.removeAll(statistics);

        List<StatisticComputer> computers = new ArrayList<>();
        computers.add(new NashSutcliffeComputer());
        computers.add(new RMSEStandardDeviationComputer());
        computers.add(new PercentBiasComputer());
        computers.add(new CoefficientOfDeterminationComputer());

        computers.stream().forEach(computer -> statistics.add(computer.computeStatistic(simulatedFlow, observedFlow)));
    }

    private void computeCyclicMonthly() throws HecMathException {

        final TimeSeriesMath simulatedFlow = (TimeSeriesMath) HecMath.createInstance(
                computedTsc).transformTimeSeries("1MONTH","","AVE");

        final TimeSeriesMath observedFlow = (TimeSeriesMath) HecMath.createInstance(
                observedTsc).transformTimeSeries("1MONTH","","AVE");

        monthlyFlow.removeAll(monthlyFlow);

        TimeSeriesContainer monthlyAveSimulated = (TimeSeriesContainer) (simulatedFlow.cyclicAnalysis()[3]).getData();
        TimeSeriesContainer monthlyAveObserved = (TimeSeriesContainer) (observedFlow.cyclicAnalysis()[3].getData());

        HecTime time = new HecTime();
        Locale locale = Locale.getDefault();

        XYChart.Series<String,Number> simulatedSeries = new XYChart.Series<String,Number>();
        simulatedSeries.setName("Simulated");
        for (int i=0; i<monthlyAveSimulated.numberValues; i++) {
            if (monthlyAveSimulated.values[i] >= 0) {
                time.set(monthlyAveSimulated.times[i]);
                //time.showTimeAsBeginningOfDay(true);
                simulatedSeries.getData().add(
                        new XYChart.Data<String,Number>(Month.of(time.month()).getDisplayName(TextStyle.SHORT, locale),
                                monthlyAveSimulated.values[i]));
            }
        }

        XYChart.Series<String,Number> observedSeries = new XYChart.Series<String,Number>();
        observedSeries.setName("Observed");
        for (int i=0; i<monthlyAveObserved.numberValues; i++) {
            if (monthlyAveObserved.values[i] >= 0) {
                time.set(monthlyAveObserved.times[i]);
                observedSeries.getData().add(
                        new XYChart.Data<String,Number>(Month.of(time.month()).getDisplayName(TextStyle.SHORT, locale),
                                monthlyAveObserved.values[i]));
            }
        }

        monthlyFlow.addAll(simulatedSeries, observedSeries);
    }

    void addStatistic (Statistic statistic) {
        statistics.add(statistic);
    }

    public ObservableList <Statistic> getStatistics() {
        if (!valid) {
            validate();
        }
        if (!processed) {
            process();
        }
        if (!current) {
            validate();
            process();
            bindProperties();
        }

        return statistics;
    }

    public ObservableList<XYChart.Series<String,Number>> getMonthlyFlows() {
        return monthlyFlow;
    }

    public String getObservedDssFile() {
        return observedDssFile.get();
    }

    @XmlElement
    public void setObservedDssFile(String observedDssFile) {
        this.observedDssFile.set(observedDssFile);
    }

//	public StringProperty observedDssFileProperty() {
//		return observedDssFile;
//	}

    public String getObservedDssPath( ) {
        return observedDssPath.get();
    }

    @XmlElement
    public void setObservedDssPath(String observedDssPath) {
        this.observedDssPath.set(observedDssPath);
    }

//	public StringProperty observedDssPathProperty() {
//		return observedDssPath;
//	}

    public final String getComputedDssFile() {
        return computedDssFile.get();
    }

    @XmlElement
    public final void setComputedDssFile(String computedDssFile) {
        this.computedDssFile.set(computedDssFile);
    }

//	public StringProperty computedDssFileProperty() {
//		return computedDssFile;
//	}

    public String getComputedDssPath( ) {
        return computedDssPath.get();
    }

    @XmlElement
    public void setComputedDssPath(String computedDssPath) {
        this.computedDssPath.set(computedDssPath);
    }

//	public StringProperty computedDssPathProperty() {
//		return computedDssPath;
//	}

    public String getStartDate( ) {
        return startDate.get();
    }

    @XmlElement
    public void setStartDate(String startDate) {
        this.startDate.set(startDate);
    }

//	public StringProperty startDateProperty() {
//		return startDate;
//	}

    public String getStartTime( ) {
        return startTime.get();
    }

    @XmlElement
    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

//	public StringProperty startTimeProperty() {
//		return startTime;
//	}

    public String getEndDate( ) {
        return endDate.get();
    }

    @XmlElement
    public void setEndDate(String endDate) {
        this.endDate.set(endDate);
    }

//	public StringProperty endDateProperty() {
//		return endDate;
//	}

    public String getEndTime( ) {
        return endTime.get();
    }

    @XmlElement
    public void setEndTime(String endTime) {
        this.endTime.set(endTime);
    }

//	public StringProperty endTimeProperty() {
//		return endTime;
//	}

    public TimeInterval getInterval( ) {
        return interval;
    }

    @XmlElement
    public void setInterval(TimeInterval interval) {
        this.interval = interval;
        this.intervalProperty.set(interval.toString());
    }

    public String getUnits() {
        return units;
    }

    public String getParameter() {
        return parameter;
    }

    public boolean isValid() {
        return valid;
    }

    public int getNumberErrors() {
        return errors;
    }

    public String getErrorMessages() {
        return errorMessages;
    }

}

