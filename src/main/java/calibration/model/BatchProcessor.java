package calibration.model;

import calibration.statistics.Statistic;
import hec.lang.Observable;
import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class BatchProcessor {

    String interval;
    String computedDSSFile;
    String observedDSSFile;
    ArrayList<String> startDate;
    ArrayList<String> endDate;
    ArrayList<String> startTime;
    ArrayList<String> endTime;
    ArrayList<String> computedDSSPaths;
    ArrayList<String> observedDSSPaths;
    ArrayList<String> names;
    FileWriter writer;
    Boolean firstWrite = true;


    public BatchProcessor(ArrayList<String> computedDSSPaths, ArrayList<String> observedDSSPaths,
                          String computedDSSFile, String observedDSSFile,
                          ArrayList<String> startDate, ArrayList<String> endDate,
                          ArrayList<String> startTime, ArrayList<String> endTime,
                          String interval, ArrayList<String> names, String outputFile)
    {
        this.computedDSSPaths = computedDSSPaths;
        this.observedDSSPaths = observedDSSPaths;
        this.computedDSSFile = computedDSSFile;
        this.observedDSSFile = observedDSSFile;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
        this.names = names;
        try{
            writer = new FileWriter(outputFile);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    public void GenerateStatistics() throws IOException {
        Analysis myAnalysis = new Analysis();
        myAnalysis.setComputedDssFile(computedDSSFile);
        myAnalysis.setObservedDssFile(observedDSSFile);
        myAnalysis.setInterval(TimeInterval.valueOf(interval));
        for( int i = 0; i < names.size(); i++){
            myAnalysis.setStartDate(startDate.get(i));
            myAnalysis.setEndDate(endDate.get(i));
            myAnalysis.setStartTime(startTime.get(i));
            myAnalysis.setEndTime(endTime.get(i));
            myAnalysis.setComputedDssPath(computedDSSPaths.get(i));
            myAnalysis.setObservedDssPath(observedDSSPaths.get(i));
            myAnalysis.process();
            ObservableList<Statistic> stats = myAnalysis.getStatistics();
            writeStatistics(names.get(i),stats);
        }
    }

    private void writeStatistics(String name, ObservableList<Statistic> stats) throws IOException {
        if(firstWrite){
            writer.write("name");
            for(int i = 0; i<stats.size();i++){
                writer.write(","+stats.get(i).getName());
            }
            writer.write("\n");
            firstWrite = false;
        }
        writer.write(name + ",");
        for(int i = 0; i<stats.size();i++){
            writer.write(String.valueOf(stats.get(i).getValue()));
            writer.write(",");
        }
        writer.write("\n");
    }


}
