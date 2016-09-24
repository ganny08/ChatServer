/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 *
 * @author Kirill
 */
public class ConsoleHelper {
    ChatSocketClient client;
    ChatSocketServer  server;
    Scanner scanner;
    
    public ConsoleHelper(ChatSocketServer server) {
        this.server = server;
        scanner = new Scanner(System.in, "cp866");
    }
    
    public void readFromConsole() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    String msg = scanner.nextLine();
                    switch (msg.toLowerCase()) {
                        case "users": {
                            viewUsers();
                            break;
                        }
                        case "logout user": {
                            System.out.println("Введите логин: ");
                            logoutUser(scanner.nextLine());
                            break;
                        }
                        case "help" : {
                            printMenu();
                            break;
                        }
                        default: {
                            System.out.println("Неизвестная команда");
                        }
                    }
                }
            }
        }).start();
    }
    
    private void printMenu() {
        System.out.println("1 - Users");
        System.out.println("2 - Logout user");
        System.out.println("3 - Help");
    }
    
    private void viewUsers() {
        for(String item : server.clients.keySet()) {
            String login = server.chatServerHelper.xmlParser.getLoginFromToken(item);
            if (login.equals("")) {
                login = item;
            }
            System.out.println(login);
        }
    }
    
    private void logoutUser(String login) {
        String token = server.chatServerHelper.xmlParser.getTokenFromLogin(login);
        if (server.clients.containsKey(token) ) {
            server.clients.get(token).writeAsync(server.chatServerHelper.logoutUser());
        }
    }
}
