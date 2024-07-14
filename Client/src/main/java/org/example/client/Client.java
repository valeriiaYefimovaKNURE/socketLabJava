package src.main.java.org.example.client;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Client implements KeySendListener {
    private long kickOffTimeMsec;
    private final static int SEND_TIMEOUT_MSEC = 2 * 1000; // 2 seconds
    private final static int SERVER_PORT = 6464;

    private final ReentrantLock workerLock;
    private final Condition workerCondition;

    @Override
    public void send(){
        workerLock.lock();
        try {
            workerCondition.signal();
        } finally {
            workerLock.unlock();
        }
    }

    Client(){
        workerLock = new ReentrantLock();
        workerCondition = workerLock.newCondition();

        new ClientForm(this).setVisible(true);
        new Thread(this::sendKeyPressedState, "Worker").start();
    }

    private boolean timeExpired() {
        return (new Date().getTime() - kickOffTimeMsec) > SEND_TIMEOUT_MSEC;
    }

    private void sendKeyPressedState(){
        kickOffTimeMsec = new Date().getTime();
        while(true) {
            workerLock.lock();
            try {
                try {
                    workerCondition.await();
                } catch (InterruptedException e) {
                    System.out.println("Interrupted main thread");
                }
                if(timeExpired()) {
                    if(MathFuncs.sinX.isEmpty() && MathFuncs.cosX.isEmpty()) {
                        System.out.println("No values, nothing to send");
                        continue;
                    }
                    System.out.println("Sending keys information to the server");
                    try {
                        sendArraysInfo();
                    } catch (IOException e) {
                        System.err.println("Failed to send keys info");
                    }
                    kickOffTimeMsec = new Date().getTime();
                } else {
                    System.out.println("Wait a bit...");
                }
            } finally {
                workerLock.unlock();
            }
        }
    }

    private void sendArraysInfo() throws IOException {
        try {
            Socket s = new Socket(InetAddress.getLocalHost(), SERVER_PORT);
            DataOutputStream ds = new DataOutputStream(new BufferedOutputStream(s.getOutputStream()));

            ds.writeInt(MathFuncs.sinX.size());
            for (int i = 0; i < MathFuncs.sinX.size(); i++) {
                ds.writeDouble(MathFuncs.sinX.get(i));
                ds.writeDouble(MathFuncs.cosX.get(i));
            }
            ds.flush();
        }
        catch (Exception e){
            System.out.println("Exception in send data: "+ e);
        }
    }

    public static void main(String[] args) {
        new Client();
    }
}
