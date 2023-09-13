package org.server;

public class MessageProcessor {

    private Server server;

    public MessageProcessor(Server server) {
        this.server = server;
    }

    public void processMessage(String message, String nickname) {
        if (message.startsWith("/nick")) {
            processNickCommand(message, nickname);
        } else if (message.startsWith("/quit")) {
            processQuitCommand(nickname);
        } else {
            broadcastMessage(nickname, message);
        }
    }

    private void processNickCommand(String message, String currentNickname) {
        // Parse and handle /nick command
        String[] messageSplit = message.split(" ", 2);
        if (messageSplit.length == 2) {
            String newNickname = messageSplit[1];
            broadcastNicknameChange(currentNickname, newNickname);
        }
    }

    private void processQuitCommand(String nickname) {
        // Handle /quit command
        server.broadcast(nickname + " left the chat!");
    }

    private void broadcastMessage(String nickname, String message) {
        // Broadcast regular message
        server.broadcast(nickname + ": " + message);
    }

    private void broadcastNicknameChange(String oldNickname, String newNickname) {
        // Broadcast nickname change
        server.broadcast(oldNickname + " Renamed themselves to: " + newNickname);
    }
}
