package calibration.model;

import calibration.statistics.Statistic;
import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;

public class StatisticWriter {
    FileWriter valueWriter;
    FileWriter ratingWriter;
    boolean valueHeadersWritten = false;
    boolean ratingHeadersWritten = false;

    public StatisticWriter(String valuesOutputFile, String moriasiRatingOutputFile) {
        try{
            this.valueWriter = new FileWriter(valuesOutputFile);
            this.ratingWriter = new FileWriter(moriasiRatingOutputFile);
        }
        catch (Exception ex){
            System.out.println(ex);
        }
    }

    public void write(String name, ObservableList<Statistic> stats) throws IOException {
        writeStatisticsValues(name,stats);
        writeStatisticsRatings(name,stats);
    }
    private void writeStatisticsValues(String name, ObservableList<Statistic> stats) throws IOException {
        if(valueHeadersWritten){
            WriteHeader(valueWriter,stats);
            ratingHeadersWritten = true;
        }
        valueWriter.write("\n" + name + ",");
        for(int i = 0; i<stats.size();i++){
            valueWriter.write(stats.get(i).getValue() +",");
        }
    }

    private void writeStatisticsRatings(String name, ObservableList<Statistic> stats) throws IOException {
        if(ratingHeadersWritten){
            WriteHeader(ratingWriter,stats);
            ratingHeadersWritten = true;
        }
        ratingWriter.write("\n" + name + ",");
        for(int i = 0; i<stats.size();i++){
            ratingWriter.write(stats.get(i).getRating() +",");
        }
    }

    private void WriteHeader(FileWriter writer, ObservableList<Statistic> stats) throws IOException {
        if(valueHeadersWritten){
            WriteHeader(ratingWriter,stats);
            ratingHeadersWritten = false;
        }
        writer.write("Name,");
        for(int i = 0; i<stats.size();i++){
            ratingWriter.write(stats.get(i).getName() +",");
        }
    }
}
