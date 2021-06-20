package com.chat.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Connection extends Thread {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Server.story.printStory(out);
        start();
    }

    @Override
    public void run() {
        String message;
        try {
            message = in.readLine();
            String newClientMessage = String.format("(%s) Новый участник подключился к чату.", getCurrentTime());
            System.out.println(newClientMessage);
            for (Connection connection : Server.serverList) {
                connection.sendMessageToAllClients(newClientMessage);
            }
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException ignored) {}
            try {
                while (true) {
                    message = in.readLine();
                    if(message.equals("exit")) {
                        this.downService();
                        String removeClientMessage = String.format("(%s) Участник вышел из чата.", getCurrentTime());
                        System.out.println(removeClientMessage);
                        for (Connection connection : Server.serverList) {
                            connection.sendMessageToAllClients(removeClientMessage);
                        }
                        break;
                    }
                    System.out.println(message);
                    Server.story.addStoryMessage(message);
                    for (Connection connection : Server.serverList) {
                        connection.sendMessageToAllClients(message);
                    }
                }
            } catch (NullPointerException ignored) {}


        } catch (IOException e) {
            this.downService();
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
        return dateFormat.format(new Date());
    }

    private void sendMessageToAllClients(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    private void downService() {
        try {
            Server.serverList.remove(this);
            if(!socket.isClosed()) {
                in.close();
                out.close();
                socket.close();
            }
        } catch (IOException ignored) {}
    }
}
