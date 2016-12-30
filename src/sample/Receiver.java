package sample;

import javafx.application.Platform;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Receiver extends Thread {
    private int port = 4444;

    Receiver(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        runListener();
    }

    private void runListener() {
        try {
            DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[512];

            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while (true) {
                serverSocket.receive(receivePacket);
                String sentence = new String(receivePacket.getData(), 0,
                        receivePacket.getLength());

                new Thread(() -> Platform.runLater(() -> Controller.addSenderItem(sentence))).start();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
