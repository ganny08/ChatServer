/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kirill
 */
public class ChatSocketClient {
    ChatServerHelper serverHelper;
    Socket client;
    InputStream inStream;
    OutputStream outStream;
    String authToken;

    public ChatSocketClient(ChatSocketServer server, Socket client) {
        this.client = client;
        serverHelper = new ChatServerHelper(server);
        try {    
            inStream = client.getInputStream(); // создаем поток на чтение
            outStream = client.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(ChatSocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //authToken = "Guest" + ChatServerHelper.getGuest();
        //server.chatServerHelper.clients.put(authToken, this); // добавляем клиента в список клиентов с гостевым ключом
        
        readAsync();
    }
    
    public void setAuthToken(String token) {
        authToken = token;
    }
    
    public void readAsync() {
        ChatSocketClient localClient = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                int countReadByte = 0;
                byte[] tempBuffer = new byte[4096]; // буффер для чтения с сокета
                while (true) {
                    if (inStream != null) {
                        byte[] buffer; // конечный буффер прочитанных байт нужной размерности
                        try {
                            countReadByte = inStream.read(tempBuffer, 0, tempBuffer.length);
                            buffer = Arrays.copyOf(tempBuffer, countReadByte);
                            //System.out.println(buffer.toString());
                            serverHelper.parsingPackage(buffer, authToken);
                        } catch (Exception ex) {
                            //System.out.println("Ошибка чтения с сокета: " + ex);
                            break;
                        }
                    }
                    else {
                        break;
                    }
                }
                try {
                    client.close();
                    serverHelper.server.clients.remove(authToken); // удаляем клиента из списка
                } catch (Exception ex) {
                    System.out.println("Ошибка");
                }
            }
        }).start();
    }
    
    public void writeAsync(byte[] buffer) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                try {
                    outStream.write(buffer, 0, buffer.length);
                } catch (IOException ex) {
                    Logger.getLogger(ChatSocketClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }
}
