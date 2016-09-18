/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.util.Scanner;

/**
 *
 * @author Kirill
 */
public class ChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.print("Введите порт сервера -> ");
        int port = new Scanner(System.in, "UTF-8").nextInt();
        ChatSocketServer server = new ChatSocketServer(port);
        readFromConsole();
        server.acceptAsync();
    }
    
    public static void readFromConsole() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(System.in);
                while(true) {
                    String command = scanner.nextLine();
                    System.out.println("Введено: " + command);
                }
            }
        }).start();
    }
    
}
