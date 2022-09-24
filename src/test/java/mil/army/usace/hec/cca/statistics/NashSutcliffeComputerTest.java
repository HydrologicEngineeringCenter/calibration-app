package mil.army.usace.hec.cca.statistics;

import calibration.model.TimeInterval;
import calibration.model.TscRetriever;
import calibration.statistics.NashSutcliffeComputer;
import calibration.statistics.Statistic;
import hec.hecmath.HecMath;
import hec.hecmath.HecMathException;
import hec.io.TimeSeriesContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

class NashSutcliffeComputerTest {

    @Test
    //Computed time series is based on a PER-AVER of the SMA compute
    //The expected value was validated using the hydroGOF package in R
    void test1() throws IOException {
        File pathToComputedFile = new File(getClass().getResource("/CapellaComputed.txt").getFile());
        File pathToObservedFile = new File(getClass().getResource("/CapellaObserved.txt").getFile());
        double[] computed = readArrayFromFile(pathToComputedFile);
        double[] observed = readArrayFromFile(pathToObservedFile);
        NashSutcliffeComputer computer = new NashSutcliffeComputer();
        double nse = computer.compute(computed, observed);
        Assertions.assertEquals(0.85418, nse, 0.0001);
    }

    @Test
    //The expected value was validated using the hydroGOF package in R
    void test2() throws HecMathException {
        TimeInterval interval = TimeInterval.ONE_DAY;

        File pathToComputedFile = new File(getClass().getResource("/WY_1951_2010_SMA.dss").getFile());
        TscRetriever.TscRetrieverBuilder builder1 = new TscRetriever.TscRetrieverBuilder();
        builder1.dssFile(pathToComputedFile.getPath());
        builder1.dssPath("//CALPELLA GAGE/FLOW/01SEP1950/1HOUR/RUN:WY 1951-2010 SMA/");
        builder1.startDate("01Oct1950");
        builder1.startTime("0000");
        builder1.endDate("30Sep2010");
        builder1.endTime("0000");
        TscRetriever retriever1 = builder1.build();
        Optional<TimeSeriesContainer> computedTsc = retriever1.retrieve();

        TimeSeriesContainer simulatedFlow = null;
        if (computedTsc.isPresent()) {
            simulatedFlow = (TimeSeriesContainer) (HecMath.createInstance(
                    computedTsc.get()).transformTimeSeries(interval.toString(), "", "AVE")).getData();
        }

        File pathToObservedFile = new File(getClass().getResource("/RussianRiverObserved.dss").getFile());
        TscRetriever.TscRetrieverBuilder builder2 = new TscRetriever.TscRetrieverBuilder();
        builder2.dssFile(pathToObservedFile.getPath());
        builder2.dssPath("/EF RUSSIAN R/CALPELLA CA/FLOW/01Jan1950/1Day/USGS/");
        builder2.startDate("01Oct1950");
        builder2.startTime("0000");
        builder2.endDate("30Sep2010");
        builder2.endTime("0000");
        TscRetriever retriever2 = builder2.build();
        Optional<TimeSeriesContainer> observedTsc = retriever2.retrieve();

        TimeSeriesContainer observedFlow = null;
        if (observedTsc.isPresent()) {
            observedFlow = (TimeSeriesContainer) (HecMath.createInstance(
                    observedTsc.get()).transformTimeSeries(interval.toString(), "", "AVE")).getData();
        }

        NashSutcliffeComputer computer = new NashSutcliffeComputer();
        Statistic nse = computer.computeStatistic(simulatedFlow, observedFlow);

        Assertions.assertEquals(0.85418, nse.getValue(), 0.0001);
    }

    private double[] readArrayFromFile(File file) throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        ArrayList<Double> list = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            try {
                double value = Double.parseDouble(line);
                list.add(value);
            } catch (NullPointerException e) {
                e.printStackTrace();
                //null string
            } catch (NumberFormatException e) {
                e.printStackTrace();
                //no parsable on the current line
            }
        }
        double[] doubles = new double[list.size()];
        for (int i = 0; i < doubles.length; i++) doubles[i] = list.get(i);
        return doubles;
    }

}