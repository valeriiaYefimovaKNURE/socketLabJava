package src.main.java.org.example.server;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Vector;

public class DrawClass extends JPanel {
    private static Polygon sinPolygon;
    private static Polygon cosPolygon;

    public DrawClass() {
        sinPolygon = new Polygon();
        cosPolygon = new Polygon();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(2*10, 2*100, 2*380, 2*100);
        g.drawLine(2*200, 2*30, 2*200, 2*190);

        g.drawLine(2*380, 2*100, 2*370, 2*90);
        g.drawLine(2*380, 2*100, 2*370, 2*110);
        g.drawLine(2*200, 2*30, 2*190, 2*40);
        g.drawLine(2*200, 2*30, 2*210, 2*40);

        g.drawString("X", 2*370, 2*80);
        g.drawString("Y", 2*220, 2*35);

        g.setColor(Color.red);
        g.drawPolyline(sinPolygon.xpoints, sinPolygon.ypoints, sinPolygon.npoints);

        g.setColor(Color.blue);
        g.drawPolyline(cosPolygon.xpoints, cosPolygon.ypoints, cosPolygon.npoints);
    }

    public void updateGraph(ArrayList<Double> sinX, ArrayList<Double> cosX) {
        sinPolygon.reset();
        cosPolygon.reset();

        int yOffset = 200; // Сдвиг по оси Y
        int yScale = 40; // Масштабирование по оси Y
        int xScale = 10; // Масштабирование по оси X, если нужно увеличить график в ширину

        for (int x = 0; x < sinX.size(); x++) {
            sinPolygon.addPoint(xScale * x + 200, (int)(yOffset - yScale * sinX.get(x)));
            cosPolygon.addPoint(xScale * x + 200, (int)(yOffset - yScale * cosX.get(x)));
        }

        repaint();
    }
}
