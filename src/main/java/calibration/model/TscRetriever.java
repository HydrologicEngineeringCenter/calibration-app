package calibration.model;

import hec.heclib.dss.HecTimeSeries;
import hec.heclib.util.HecTime;
import hec.io.TimeSeriesContainer;

import java.util.Optional;

public class TscRetriever {
    Optional<String> dssFile;
    Optional<String> dssPath;
    Optional<String> startDate;
    Optional<String> startTime;
    Optional<String> endDate;
    Optional<String> endTime;
    Optional<String> type;
    Optional<TimeInterval> interval;

    private TscRetriever (TscRetrieverBuilder builder) {
        this.dssFile = builder.dssFile;
        this.dssPath = builder.dssPath;
        this.startDate = builder.startDate;
        this.startTime = builder.startTime;
        this.endDate = builder.endDate;
        this.endTime = builder.endTime;
    }

    public static class TscRetrieverBuilder {
        private Optional<String> dssFile = Optional.empty();
        private Optional<String> dssPath = Optional.empty();
        private Optional<String> startDate = Optional.empty();
        private Optional<String> startTime = Optional.empty();
        private Optional<String> endDate = Optional.empty();
        private Optional<String> endTime = Optional.empty();

        public TscRetrieverBuilder() {}

        public TscRetrieverBuilder dssFile (final String dssFile) {
            this.dssFile = Optional.of(dssFile);
            return this;
        }

        public TscRetrieverBuilder dssPath (final String dssPath) {
            this.dssPath = Optional.of(dssPath);
            return this;
        }

        public TscRetrieverBuilder startDate (final String startDate) {
            this.startDate = Optional.of(startDate);
            return this;
        }

        public TscRetrieverBuilder startTime (final String startTime) {
            this.startTime = Optional.of(startTime);
            return this;
        }

        public TscRetrieverBuilder endDate (final String endDate) {
            this.endDate = Optional.of(endDate);
            return this;
        }

        public TscRetrieverBuilder endTime (final String endTime) {
            this.endTime = Optional.of(endTime);
            return this;
        }

        public TscRetriever build () {
            return new TscRetriever(this);
        }

    };

    public Optional<TimeSeriesContainer> retrieve() {
        TimeSeriesContainer tscRead = new TimeSeriesContainer();
        if (dssPath.isPresent()) {
            tscRead.fullName = dssPath.get();
        } else {
            return Optional.empty();
        }

        if (startDate.isPresent() && startTime.isPresent()) {
            HecTime start = new HecTime(startDate.get(), startTime.get());
            tscRead.startTime = start.value();
        } else {
            tscRead.retrieveAllTimes = true;
        }

        if (endDate.isPresent() && endTime.isPresent()) {
            HecTime end = new HecTime(endDate.get(), endTime.get());
            tscRead.endTime = end.value();
        } else {
            tscRead.retrieveAllTimes = true;
        }

        HecTimeSeries dssTimeSeriesRead = new HecTimeSeries();
        if (dssFile.isPresent()) {
            dssTimeSeriesRead.setDSSFileName(dssFile.get());
        } else {
            return Optional.empty();
        }

        int status = dssTimeSeriesRead.read(tscRead, true);
        dssTimeSeriesRead.done();

        if (status < 0) {
            return Optional.empty();
        } else {
            return Optional.of(tscRead);
        }
    }

}
