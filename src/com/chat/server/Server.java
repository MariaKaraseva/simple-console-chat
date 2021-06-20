package com.chat.server;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class Server {

    public static final int PORT = 8080;
    public static LinkedList<Connection> serverList = new LinkedList<>();
    public static Story story;

    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        story = new Story();
        System.out.println("Сервер запущен");
        try {
            while (true) {
                Socket socket = server.accept();
                try {
                    serverList.add(new Connection(socket));
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            server.close();
        }
    }
}