/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/*
* Номера функций: 
* 1 - Регистрация (Sign UP)
* 2 - Вход (Login)
* 3 - Выход (Logout)
* 6 - Аутентификация (Auth)
* 10 - Сообщение
*/


public class ChatServerHelper {
    //public ConcurrentHashMap<String,ChatSocketClient> clients;
    private static int sGuestCount = 0;
    private static int sAuthTokenLength = 6;
    ChatSocketServer server;
    XmlParser xmlParser;
    
    public static int getGuest() {
        return ++sGuestCount;
    }
        
    public ChatServerHelper(ChatSocketServer server) {
        this.server = server;
        //clients = new ConcurrentHashMap<String, ChatSocketClient>();
        xmlParser = new XmlParser("E:\\NetBeans\\ChatServer\\DataBase.xml");
    }
        
    public void parsingPackage(byte[] buffer, String token) {
        switch (buffer[0]) {
            case 1: { // регистрация
                regUser(buffer); // регистрация клиента в базе
                System.out.println("Клиент зарегистрировался");
                break;
            }
            case 2: { // вход
                String newToken = loginUser(buffer); // получаем токен по логину
                if(newToken != "") {
                    ChatSocketClient client = server.clients.get(token); // получаем клиента по гостевому логину
                    server.clients.remove(token); // удаляем гостевой токен из списка клиентов
                    server.clients.put(newToken, client); // добавляем клиента с новым токеном
                    server.clients.get(newToken).authToken = newToken;
                    createAndSendAuthPackage(newToken);
                    System.out.println("Клиент авторизировался");
                }
                break;
            }
            case 3: { // выход 
                String localToken = getAuthToken(buffer);
                ChatSocketClient client = server.clients.get(localToken);
                String newToken = "Guest" + getGuest();
                client.authToken = newToken; // переприсваиваем клиенту гостевой токен
                server.clients.remove(localToken); // удаляем токен из списка клиентов
                server.clients.put(newToken, client); // добавляем гостевого клиента
                client.writeAsync(logoutUser());
                break;
            }
            case 10: {
                String localToken = getAuthToken(buffer);
                for(String item : server.clients.keySet()) { // цикл по всем клиентам
                    if(!item.equals(localToken)) { // если итем не совпадает с текущим клинетом, то отправить сообщение
                        server.clients.get(item).writeAsync(
                                sendMsg(xmlParser.getLoginFromToken(localToken), buffer));
                    }
                }
                break;
            }
            default: {
                System.out.println(new String(buffer, Charset.forName("cp866")));
            }
        }
    }
    
    private byte[] sendMsg(String login,byte[] buffer) {
        byte[] byteLogin = login.getBytes();
        byte loginLength = (byte)byteLogin.length;
        byte[] msg = new byte[byteLogin.length + buffer.length + 2 - 6];
        msg[0] = 10;
        msg[1] = loginLength;
        System.arraycopy(byteLogin, 0, msg, 2, byteLogin.length);
        System.arraycopy(buffer, 7, msg, 2 + byteLogin.length, buffer.length-7);
        return msg;
    }
    
    private String getAuthToken(byte[] buffer) {
        String token = new String(buffer,1,sAuthTokenLength);
        return token;
    }
    
    public void regUser(byte[] buffer) {
        String loginPass = new String(buffer,1,buffer.length - 1);
        String[] user = loginPass.split(":");
        xmlParser.addRow(user[0], user[1], newToken(user[0], user[1]));
    }
    
    public String loginUser(byte[] buffer) { 
        String loginPass = new String(buffer,1,buffer.length - 1);
        String[] user = loginPass.split(":");
        if (xmlParser.loginSuccess(user[0], user[1])) {
            return xmlParser.getTokenFromLogin(user[0]);
        }
        return "";
    }
    
    public byte[] logoutUser() {
        return new byte[] {3};
    }
    
    public String newToken(String login, String pass) {
        String token = login.length() + 
                       "T" +
                       pass.charAt(pass.length() - 1) +
                       login.charAt(login.length() - 1) +
                       "N" +
                       pass.length();
        return token;
    }
    
    public void createAndSendAuthPackage(String token) {
        String data = token;
        byte[] string = data.getBytes(Charset.forName("cp866"));
        byte[] bytePackage = new byte[1 + string.length]; // размер пакета 1 (номер функции) + размер токена
        bytePackage[0] = 6;
        System.arraycopy(string, 0, bytePackage, 1, string.length);
        server.clients.get(token).writeAsync(bytePackage);
    }
}

