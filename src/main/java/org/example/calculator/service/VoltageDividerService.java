package org.example.calculator.service;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class VoltageDividerService {

    public static class Result {
        private final StringProperty resistors;
        private final StringProperty vout;
        private final StringProperty error;
        private final int count; // количество резисторов

        public Result(String resistors, double vout, double error, int count) {
            this.resistors = new SimpleStringProperty(resistors);
            this.vout = new SimpleStringProperty(String.format("%.3f", vout));
            this.error = new SimpleStringProperty(String.format("%.2f %%", error * 100));
            this.count = count;
        }

        public StringProperty resistorsProperty() { return resistors; }
        public StringProperty voutProperty() { return vout; }
        public StringProperty errorProperty() { return error; }
        public int getCount() { return count; }

        public String getVout() {
            return vout.get();
        }

        public String getResistors() {
            return resistors.get();
        }
    }

    // Ряды резисторов
    private static final double[][] SERIES = {
            {10, 15, 22, 33, 47, 68}, // E6
            {10, 12, 15, 18, 22, 27, 33, 39, 47, 56, 68, 82}, // E12
            {10, 11, 12, 13, 15, 16, 18, 20, 22, 24, 27, 30, 33, 36, 39, 43, 47, 51, 56, 62, 68, 75, 82, 91}, // E24
            {10, 10.2, 10.5, 10.7, 11, 11.3, 11.5, 11.8, 12.1, 12.4, 12.7, 13, 13.3, 13.7, 14, 14.3, 14.7, 15, 15.4, 15.8, 16.2, 16.5, 16.9, 17.4, 17.8, 18.2, 18.7, 19.1, 19.6, 20} // кусочек E96
    };

    private static double[] getSeries(String name) {
        return switch (name) {
            case "E6" -> SERIES[0];
            case "E12" -> SERIES[1];
            case "E24" -> SERIES[2];
            case "E96" -> SERIES[3];
            default -> SERIES[1];
        };
    }

    private static double parallel(double r1, double r2) {
        return (r1 * r2) / (r1 + r2);
    }

    private static double series(double r1, double r2) {
        return r1 + r2;
    }

    public List<Result> calculate(double vin, double voutReq, double tol, String seriesName, int rmin, int rmax) {
        List<Result> results = new ArrayList<>();
        double[] baseSeries = getSeries(seriesName);

        for (int decade = 1; decade <= 1_000_000; decade *= 10) {
            for (double r1Base : baseSeries) {
                double r1 = r1Base * decade;
                if (r1 < rmin || r1 > rmax) continue;

                for (double r2Base : baseSeries) {
                    double r2 = r2Base * decade;
                    if (r2 < rmin || r2 > rmax) continue;

                    // === 2 резистора ===
                    checkAndAdd(results, vin, voutReq, tol, r1, r2, "R1=%.0fΩ, R2=%.0fΩ".formatted(r1, r2), 2);

                    // === 3 резистора ===
                    for (double r3Base : baseSeries) {
                        double r3 = r3Base * decade;
                        if (r3 < rmin || r3 > rmax) continue;

                        // Последовательно/параллельно верхнее плечо
                        checkAndAdd(results, vin, voutReq, tol, series(r1, r3), r2,
                                "R1=%.0fΩ+%.0fΩ, R2=%.0fΩ".formatted(r1, r3, r2), 3);
                        checkAndAdd(results, vin, voutReq, tol, parallel(r1, r3), r2,
                                "R1=(%.0f||%.0f)Ω, R2=%.0fΩ".formatted(r1, r3, r2), 3);

                        // Последовательно/параллельно нижнее плечо
                        checkAndAdd(results, vin, voutReq, tol, r1, series(r2, r3),
                                "R1=%.0fΩ, R2=%.0fΩ+%.0fΩ".formatted(r1, r2, r3), 3);
                        checkAndAdd(results, vin, voutReq, tol, r1, parallel(r2, r3),
                                "R1=%.0fΩ, R2=(%.0f||%.0f)Ω".formatted(r1, r2, r3), 3);

                        // === 4 резистора ===
                        for (double r4Base : baseSeries) {
                            double r4 = r4Base * decade;
                            if (r4 < rmin || r4 > rmax) continue;

                            // оба плеча последовательные
                            checkAndAdd(results, vin, voutReq, tol,
                                    series(r1, r3), series(r2, r4),
                                    "R1=%.0fΩ+%.0fΩ, R2=%.0fΩ+%.0fΩ".formatted(r1, r3, r2, r4), 4);
                            // оба плеча параллельные
                            checkAndAdd(results, vin, voutReq, tol,
                                    parallel(r1, r3), parallel(r2, r4),
                                    "R1=(%.0f||%.0f)Ω, R2=(%.0f||%.0f)Ω".formatted(r1, r3, r2, r4), 4);
                        }
                    }
                }
            }
        }

        return results;
    }

    private void checkAndAdd(List<Result> results,
                             double vin, double voutReq, double tol,
                             double r1, double r2,
                             String description, int count) {
        double vout = vin * r2 / (r1 + r2);
        double error = Math.abs((vout - voutReq) / voutReq);
        if (error <= tol) {
            results.add(new Result(description, vout, error, count));
        }
    }
}
