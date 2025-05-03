package org.novalearn.client;

import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;

    public Client(Socket socket) {
        try{
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        }catch(IOException e){
            System.out.println("Error creating Client!");
            e.printStackTrace();
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){
        try{
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessageToServer(String messageToServer) {
        if (socket != null && !socket.isClosed()) {
            try {
                bufferedWriter.write(messageToServer);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                System.out.println("Message sent to server: " + messageToServer);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error sending message to the Server!");
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        } else {
            System.out.println("Connection to server is closed.");
        }
    }



    public void receiveMessageFromServer(VBox vbox_messages) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(socket.isConnected()) {
                    try {
                        String messageFromServer = bufferedReader.readLine();
                        if (messageFromServer != null) {
                            ClientController.addLabel(messageFromServer, vbox_messages);
                        } else {
                            System.out.println("Server closed the connection");
                            break;  // Quitte la boucle si le serveur ferme la connexion
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Error receiving message from the Server!");
                        closeEverything(socket, bufferedReader, bufferedWriter);
                        break;
                    }
                }
            }
        }).start();
    }


}