package calibration;

import calibration.model.Analysis;
import calibration.model.StatisticWriter;
import calibration.statistics.Statistic;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;

public class BatchProcessScript {
    public static void main(String[] args) throws IOException {
        //String directoryPath = args[0];
        String directoryPath = "D:\\FFRD\\CalibrationAppStudy\\";
        File directory = new File(directoryPath);
        File[] files = directory.listFiles();

        StatisticWriter writer = new StatisticWriter(directoryPath + "calibrationsValues.csv",
                directoryPath + "calibrationsRatings.csv");
        for (File file : files) {
            if(file.getAbsolutePath().endsWith(".xml")){
                Analysis analysis = new Analysis(file);
                analysis.validate();
                analysis.process();
                String name = file.getName();
                ObservableList<Statistic> stats = analysis.getStatistics();
                writer.write(name, stats);
            }

        }

    }
}
