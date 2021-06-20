package com.chat.server;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Story {

    private LinkedList<String> story = new LinkedList<>();
    private int maxStoryMessageCount = 10;

    public void addStoryMessage(String message) {
        if (story.size() >= maxStoryMessageCount) {
            story.removeFirst();
            story.add(message);
        } else {
            story.add(message);
        }
    }

    public void printStory(BufferedWriter writer) {
        if(story.size() > 0) {
            try {
                writer.write("История сообщений" + "\n");
                for (String message : story) {
                    writer.write(message + "\n");
                }
                writer.write("<----->" + "\n");
                writer.flush();
            } catch (IOException ignored) {}

        }

    }
}