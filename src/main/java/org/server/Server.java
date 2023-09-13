package org.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done;
    private ExecutorService pool;

    public Server() {
        connections = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(9989);
            pool = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client, this);
                connections.add(handler);
                pool.execute(handler);
            }
        } catch (Exception e) {
            LoggerUtil.logError("Error in server run: " + e.getMessage(), e);
            shutdown();
        }
    }


    public void broadcast(String message) {
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public void removeConnection(ConnectionHandler handler) {
        connections.remove(handler);
    }

    public void shutdown() {
        try {
            done = true;
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
            pool.shutdown();
        } catch (IOException e) {
            LoggerUtil.logError("Error in server shutdown: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
