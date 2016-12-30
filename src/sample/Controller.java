package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.*;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static sample.Main.stage;

public class Controller implements Initializable {
    private static VBox container;
    public TextField ipText;
    public TextField messageText;
    public ScrollPane con;

    private double xOffset;
    private double yOffset;
    private Receiver receiver;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        container = new VBox();
        con.setContent(container);
        container.setStyle("-fx-background-color: white;");

        con.vvalueProperty().bind(container.heightProperty());

        receiver = new Receiver(4444);
        Main.thread = new Thread(receiver);
        Main.thread.setDaemon(true);
        Main.thread.start();
    }

    public void sendMessage() throws IOException {
        String message = messageText.getText();

        if (message.isEmpty())
            return;

        InetAddress address = InetAddress.getByName(ipText.getText());

        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, address, 4444);
        DatagramSocket datagramSocket = new DatagramSocket();

        datagramSocket.send(packet);

        addUserItem(message);

        messageText.setText("");
    }

    private void addUserItem(String message) {
        HBox box = new HBox();
        box.setStyle("-fx-alignment: center-right; -fx-opacity: 0.0;");
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: linear-gradient(#eceff1, #cfd8dc); -fx-background-radius: 10; -fx-padding: 10; -fx-alignment: center-right; -fx-effect: dropshadow(one-pass-box, gray, 2, 0, 0, 1)");
        Label content = new Label(message);
        content.setStyle("-fx-font-size: 20; -fx-wrap-text: true;");
        Label date = new Label(String.format("%02d:%02d", LocalDateTime.now().getHour(), LocalDateTime.now().getMinute()));
        date.setStyle("-fx-font-size: 12;");
        gridPane.add(content, 0, 0);
        gridPane.add(date, 0, 1);
        box.getChildren().add(gridPane);
        HBox.setMargin(gridPane, new Insets(5, 10, 5, 20));
        container.getChildren().add(box);
        animateMessage(box, true);
    }

    static void addSenderItem(String message) {
        HBox box = new HBox();
        box.setStyle("-fx-alignment: center-left; -fx-opacity: 0.0;");
        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color: linear-gradient(#eeeeee, #e0e0e0); -fx-background-radius: 10; -fx-padding: 10; -fx-alignment: center-right; -fx-effect: dropshadow(one-pass-box, gray, 2, 0, 0, 1)");
        Label content = new Label(message);
        content.setStyle("-fx-font-size: 20; -fx-wrap-text: true;");
        Label date = new Label(String.format("%02d:%02d", LocalDateTime.now().getHour(), LocalDateTime.now().getMinute()));
        date.setStyle("-fx-font-size: 12;");
        gridPane.add(content, 0, 0);
        gridPane.add(date, 0, 1);
        box.getChildren().add(gridPane);
        HBox.setMargin(gridPane, new Insets(5, 20, 5, 10));
        try {
            container.getChildren().add(box);
        } catch (Exception ignored) {
            System.out.println(ignored.getMessage());
        }
        animateMessage(box, false);
    }

    public void mousePressed(MouseEvent mouseEvent) {
        xOffset = stage.getX() - mouseEvent.getScreenX();
        yOffset = stage.getY() - mouseEvent.getScreenY();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        stage.setX(mouseEvent.getScreenX() + xOffset);
        stage.setY(mouseEvent.getScreenY() + yOffset);
    }

    public void closeWindow() {
        stage.hide();
    }

    public void minimizeWindow() {
        stage.setIconified(true);
    }

    private static void animateMessage(Node item, boolean isUser) {
        Timeline animation = new Timeline(new KeyFrame(new Duration(1), event -> animate(item, isUser)));
        animation.setOnFinished(event -> {
            item.setOpacity(1.0);
        });
        animation.setCycleCount(100);
        animation.play();
    }

    private static void animate(Node node, boolean isUser) {
        if (node.getScaleX() == 1.0) {
            if (isUser) {
                node.setScaleX(2.0);
                node.setScaleY(2.0);
            } else {
                node.setScaleX(0.01);
                node.setScaleY(0.01);
            }
        }

        if (isUser) {
            node.setScaleX(node.getScaleX() - 0.01);
            node.setScaleY(node.getScaleY() - 0.01);
        } else {
            node.setScaleX(node.getScaleX() + 0.01);
            node.setScaleY(node.getScaleY() + 0.01);
        }

        node.setOpacity(node.getOpacity() + 0.01);
    }

    public void showChildren() {
        for (Node child : container.getChildren())
            child.setOpacity(1.0);
    }
}