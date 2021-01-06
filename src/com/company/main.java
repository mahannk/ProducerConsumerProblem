/*
Code modified from Producer&Consumer obtained from https://www.udemy.com/course/java-the-complete-java-developer-course
*/

package com.company;

import java.util.LinkedList;
import java.util.Random;

public class main {

    public static void main(String[] args) throws InterruptedException {


        Message message = new Message(100, 10);  // Data Capacity, Buffer Capacity

        Thread w_thread = new Thread(new Writer(message, 100)); // Writer Delay
        Thread r_thread = new Thread(new Reader(message, 300)); // Reader Delay
        w_thread.start();
        r_thread.start();

        w_thread.join();
        r_thread.join();

    }
}

class Message {

    private LinkedList<Integer> data;
    private LinkedList<Integer> buffer;
    private int bufferCapacity;
    protected int dataCapacity;

    public LinkedList<Integer> getData() {
        return data;
    }

    public Message(int dataCapacity, int bufferCapacity) {
        this.dataCapacity = dataCapacity;
        this.bufferCapacity = bufferCapacity;
        buffer = new LinkedList<>();
        data = new LinkedList<>();
        for (int i=0;i<dataCapacity;i++) {
            data.add(i);
        }
    }

    public synchronized void write(int value) {
        while(buffer.size() == bufferCapacity) {
            try {
                System.out.println("Buffer Full");
                wait(5000);
            } catch (InterruptedException e) {
                System.out.println("synced write interrupted " + e.getMessage());
            }
        }

        buffer.add(value);
        System.out.println("W -> " + value);
        notifyAll();
    }

    public synchronized int read() {
        while(buffer.size() == 0) {
            try {
                wait(5000);
            } catch (InterruptedException e) {
                System.out.println("synced read interrupted " + e.getMessage());
            }
        }
        int value = buffer.removeFirst();
        System.out.println("R <- " + value);
        notifyAll();
        return value;
    }
}

class Writer implements Runnable {
    private Message message;
    private int delay;

    public Writer(Message message, int delay) {
        this.message = message;
        this.delay = delay;
    }

    public void run() {
        Random random = new Random();

        for (int value : message.getData()) {
            message.write((int) value);
            try {
                Thread.sleep(random.nextInt(delay));
            } catch (InterruptedException e) {
                System.out.println("WriterClass Error: " + e.getMessage());
            }
        }
    }
}

class Reader implements Runnable {
    private Message message;
    private int delay;

    public Reader(Message message, int delay) {
        this.message = message;
        this.delay = delay;
    }

    public void run() {
        Random random = new Random();
        boolean eof = false;
        while(!eof) {
            int value = message.read();
            System.out.println("Output: " + value);

            if (value == (message.dataCapacity - 1)) {
                eof = true;
            }

            try {
                Thread.sleep(random.nextInt(delay));
            } catch (InterruptedException e) {
                System.out.println("ReaderClass error - " + e.getMessage());
            }
        }
    }
}