package com.chat.client;

public class Client {

    public static String ip = "localhost";
    public static int port = 8080;

    public static void main(String[] args) {
        new ClientInstance(ip, port);
    }
}
