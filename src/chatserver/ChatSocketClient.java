/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kirill
 */
public class ChatSocketClient {
    ChatSocketServer server;
    Socket client;
    InputStream inStream;
    OutputStream outStream;

    public ChatSocketClient(ChatSocketServer server, Socket client) {
        this.client = client;
        this.server = server;
        try {    
            inStream = client.getInputStream(); // создаем поток на чтение
            outStream = client.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(ChatSocketClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        server.clients.add(this); // добавляем клиента в список клиентов на сервере
        readAsync();
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
                            System.out.println(new String(buffer));
                            for(ChatSocketClient item : server.clients) { // цикл по всем клиентам
                                if(item != localClient) { // если итем не совпадает с текущим клинетом, то отправить сообщение
                                    item.writeAsync(buffer);
                                }
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(ChatSocketClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    else {
                        break;
                    }
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
