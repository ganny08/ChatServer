/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Kirill
 */
public class ChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Test");
        readFromConsole();
        ChatSocketServer server = new ChatSocketServer();
        server.acceptAsync();
    }
    
    public static void readFromConsole() {
        Scanner scanner = new Scanner(System.in);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    String command = scanner.next();
                    System.out.println("Введено: " + command);
                }
            }
        }).start();
    }
    
}
