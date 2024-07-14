package src.main.java.org.example.server;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    private static int requestCount = 0;
    private static final int SERVER_PORT = 6464;
    private final ServerFrame serverFrame;

    Server() throws IOException {
        System.out.println("Server is started");

        serverFrame = new ServerFrame(new ArrayList<>(), new ArrayList<>());
        serverFrame.setVisible(true);

        ServerSocket serv = new ServerSocket(SERVER_PORT);
        while(true){
            Socket sock=serv.accept();
            System.out.println("Connection accepted from " + sock.getInetAddress());
            processRequest(sock);
            sock.close();
        }
    }

    private void processRequest(Socket sock) throws IOException {
        requestCount++;
        System.out.println(String.format("Received %d client request", requestCount));
        try {
            DataInputStream is = new DataInputStream(new BufferedInputStream(sock.getInputStream()));
            int count = is.readInt();

            ArrayList<Double> sinValues = new ArrayList<>(count);
            ArrayList<Double> cosValues = new ArrayList<>(count);

            for (int i = 0; i < count; i++) {
                sinValues.add(is.readDouble());
                cosValues.add(is.readDouble());
            }

            System.out.println("SinX values: " + sinValues);
            System.out.println("CosX values: " + cosValues);

            SwingUtilities.invokeLater(() -> serverFrame.update(sinValues, cosValues));
        }
        catch (Exception e){
            System.out.println("Exception in processRequest: "+ e);
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }
}
