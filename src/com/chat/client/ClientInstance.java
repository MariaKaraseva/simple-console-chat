package com.chat.client;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientInstance {

    private Socket socket;
    private BufferedReader in; // поток чтения из сокета
    private BufferedWriter out; // поток чтения в сокет
    private BufferedReader inputUser; // поток чтения с консоли
    private String ip; // ip адрес клиента
    private int port; // порт соединения
    private String nickname; // имя клиента

    public ClientInstance(String ip, int port) {
        this.ip = ip;
        this.port = port;
        try {
            this.socket = new Socket(ip, port);
        } catch (IOException e) {
            System.err.println("Сервер не запущен, попробуйте позже");
        }
        try {
            inputUser = new BufferedReader(new InputStreamReader(System.in));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.pressNickname();
            new ReadMsg().start();
            new WriteMsg().start();
        } catch (IOException e) {
            ClientInstance.this.downService();
        }
    }

    private void pressNickname() {
        System.out.print("Введите свой ник: ");
        try {
            nickname = inputUser.readLine();
            out.write("Добро пожаловать, " + nickname + "\n");
            out.flush();
        } catch (IOException ignored) {
        }
    }

    private void downService() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    private class ReadMsg extends Thread {
        @Override
        public void run() {

            String str;
            try {
                while (true) {
                    str = in.readLine();
                    System.out.println(str);
                }
            } catch (IOException e) {
                ClientInstance.this.downService();
            }
        }
    }

    public class WriteMsg extends Thread {

        @Override
        public void run() {
            while (true) {
                String userWord;
                try {
                    if (inputUser.equals(null)) {
                        break;
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy HH:mm:ss");
                    String time = dateFormat.format(new Date());
                    userWord = inputUser.readLine();
                    if(userWord.equals("")) {
                        continue;
                    }
                    if (userWord.equals("exit")) {
                        out.write("exit, " + nickname + "\n");
                        /*Если убрать две строки ниже, то будет работать информирование об удалении соединения, но не будет нормального закрытия клиента*/
                        ClientInstance.this.downService();
                        break;
                    } else {
                        out.write("(" + time + ", " + nickname + "): " + userWord + "\n");
                    }
                    out.flush();
                } catch (IOException e) {
                    ClientInstance.this.downService();
                }

            }
        }
    }
}