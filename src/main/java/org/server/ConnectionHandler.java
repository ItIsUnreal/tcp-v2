package org.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {

    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private String nickname;
    private Server server;
    private MessageProcessor messageProcessor; // Add MessageProcessor

    public ConnectionHandler(Socket client, Server server) {
        this.client = client;
        this.server = server;
        this.messageProcessor = new MessageProcessor(server); // Initialize MessageProcessor
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out.println("Please enter your nickname: ");
            nickname = in.readLine();
            LoggerUtil.logInfo(nickname + " connected!"); // Log connection info
            server.broadcast(nickname + " Joined the chat!");
            String message;
            while ((message = in.readLine()) != null) {
                messageProcessor.processMessage(message, nickname); // Process messages
            }
        } catch (IOException e) {
            LoggerUtil.logError("Error in ConnectionHandler run: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void shutdown() {
        try {
            server.removeConnection(this);
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            LoggerUtil.logError("Error in ConnectionHandler shutdown: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
