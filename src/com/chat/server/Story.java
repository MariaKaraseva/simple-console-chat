package com.chat.server;


import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

public class Story {

    private LinkedList<String> story = new LinkedList<>();

    public void addStoryMessage(String message) {
        // если сообщений больше 10, удаляем первое и добавляем новое
        // иначе просто добавить
        if (story.size() >= 10) {
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
                writer.write("/...." + "\n");
                writer.flush();
            } catch (IOException ignored) {}

        }

    }
}