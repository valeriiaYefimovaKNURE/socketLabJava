package src.main.java.org.example.server;

import javafx.scene.chart.XYChart;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Vector;

public class ServerFrame extends JFrame{
    public static ArrayList<Double> sinValues;
    public static ArrayList<Double> cosValues;
    private final DrawClass drawClass;
    private static final int width=800;
    private static final int height=500;
    ServerFrame(ArrayList<Double> sinX, ArrayList<Double> cosX){
        sinValues=sinX;
        cosValues=cosX;
        this.drawClass=new DrawClass();
        setTitle("Server");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(width,height);

        setLayout(new BorderLayout());

        add(drawClass,BorderLayout.CENTER);
    }
    public void update(ArrayList<Double> sinX, ArrayList<Double> cosX){
        System.out.println("Update graphic");
        SwingUtilities.invokeLater(() -> {
            drawClass.updateGraph(sinX, cosX);
        });
    }
}
