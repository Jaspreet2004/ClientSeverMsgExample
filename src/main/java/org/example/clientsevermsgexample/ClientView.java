package org.example.clientsevermsgexample;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class ClientView implements Initializable {

    @FXML private TextField tf_message;
    @FXML private Button   button_send;
    @FXML private VBox     vbox_messages;

    private Socket          socket;
    private DataInputStream dis;
    private DataOutputStream dos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) connect to server
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 5555);
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());

                // read incoming
                while (true) {
                    String msg = dis.readUTF();
                    Platform.runLater(() -> addLabel(msg, Pos.TOP_LEFT));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // 2) wire Send button
        button_send.setOnAction(evt -> {
            try {
                String msg = tf_message.getText().trim();
                if (!msg.isEmpty()) {
                    dos.writeUTF(msg);
                    addLabel(msg, Pos.TOP_RIGHT);
                    tf_message.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addLabel(String msg, Pos alignment) {
        HBox h = new HBox();
        h.setAlignment(alignment);
        Label lbl = new Label(msg);
        lbl.setStyle("-fx-background-color: lightgreen; -fx-padding: 5; -fx-border-radius: 5;");
        h.getChildren().add(lbl);
        vbox_messages.getChildren().add(h);
    }
}
