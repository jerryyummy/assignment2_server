package org.example;

import org.example.queue.Consumer;

public class Main extends Thread{

    public static void main(String[] args) throws Exception {
        Consumer consumer = new Consumer();
        consumer.consume();
    }
}