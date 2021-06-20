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
        String word;
        try {
            word = in.readLine();
            String newClientMessage = String.format("(%s) Новый участник подключился к чату. Количество активных участников: %d", getCurrentTime(), Server.serverList.size());
            System.out.println(newClientMessage);
            for (Connection connection : Server.serverList) {
                connection.sendMessageToAll(newClientMessage);
            }
            try {
                out.write(word + "\n");
                out.flush();
            } catch (IOException ignored) {}
            try {
                while (true) {
                    word = in.readLine();
                    if(word.equals("stop")) {
                        for (Connection connection : Server.serverList) {
                            connection.sendMessageToAll("Участник вышел из чата");
                        }
                        this.downService();
                        break;
                    }
                    System.out.println("Сообщение: " + word);
                    Server.story.addStoryMessage(word);
                    for (Connection connection : Server.serverList) {
                        connection.sendMessageToAll(word);
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

    private void sendMessageToAll(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (Connection connection : Server.serverList) {
                    if(connection.equals(this)) connection.interrupt();
                    Server.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}
