package src.main.java.org.example.client;

import java.util.ArrayList;
import java.util.List;

public class MathFuncs {
    public static List<Double> sinX = new ArrayList<>();
    public static List<Double> cosX = new ArrayList<>();
    public static final int size=50;
    public static void calculateSinArray(double start, double end , List<Double> result) {
        double step = (end - start) / (size - 1);
        result.clear();
        for (int i = 0; i < size; ++i) {
            result.add(Math.sin(start + step * i));
        }
    }

    public static void calculateCosArray(double start, double end, List<Double> result) {
        double step = (end - start) / (size - 1);
        result.clear();
        for (int i = 0; i < size; ++i) {
            result.add(Math.cos(start + step * i));
        }
    }
}
