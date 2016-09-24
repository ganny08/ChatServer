/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kirill
 */
public class ChatSocketServer {
    
    ServerSocket server;
    public ConcurrentHashMap<String,ChatSocketClient> clients;
    volatile ChatServerHelper chatServerHelper;
    
    public ChatSocketServer(int port) {
        try {
            clients = new ConcurrentHashMap<String, ChatSocketClient>();
            server = new ServerSocket(port);
            chatServerHelper = new ChatServerHelper(this);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public void acceptAsync() {
        ChatSocketServer serverForClient = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
            try {
                ChatSocketClient client = new ChatSocketClient(serverForClient, server.accept());
                String authToken = "Guest" + ChatServerHelper.getGuest();
                clients.put(authToken, client);
                client.setAuthToken(authToken);
                System.out.println("Подключился клиент: " + 
                        client.client.getInetAddress() + ": " + client.client.getPort() );
            } catch (IOException ex) {
                Logger.getLogger(ChatSocketServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
            }
        }).start();
        
    }
    
    
}
