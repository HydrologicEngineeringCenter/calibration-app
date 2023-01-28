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
            ex.printStackTrace();
        }
    }
    public void write(String name, ObservableList<Statistic> stats) throws IOException {
        writeStatisticsValues(name,stats);
        writeStatisticsRatings(name,stats);
    }
    private void writeStatisticsValues(String name, ObservableList<Statistic> stats) throws IOException {
        if(!valueHeadersWritten){
            WriteHeader(valueWriter,stats);
            valueHeadersWritten = true;
        }
        valueWriter.write("\n" + name + ",");
        for (Statistic stat : stats) {
            valueWriter.write(stat.getValue() + ",");
        }
        valueWriter.flush();
    }
    private void writeStatisticsRatings(String name, ObservableList<Statistic> stats) throws IOException {
        if(!ratingHeadersWritten){
            WriteHeader(ratingWriter,stats);
            ratingHeadersWritten = true;
        }
        ratingWriter.write("\n" + name + ",");
        for (Statistic stat : stats) {
            ratingWriter.write(stat.getRating() + ",");
        }
        ratingWriter.flush();
    }
    private void WriteHeader(FileWriter writer, ObservableList<Statistic> stats) throws IOException {
        writer.write("Name,");
        for (Statistic stat : stats) {
            writer.write(stat.getName() + ",");
        }
        writer.flush();
    }
}
