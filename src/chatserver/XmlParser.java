/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Kirill
 */
public class XmlParser {
    String fileName;
    Document doc;
    Element table;
    
    public XmlParser(String fileName) {
        this.fileName = fileName;
    }
    
    public void getDocument()
    {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBilder = dbf.newDocumentBuilder();
            doc = docBilder.parse(new File(fileName));
            Element dataBase = doc.getDocumentElement();
            //System.out.println(dataBase.getNodeName());
            for(int i=0; i<dataBase.getChildNodes().getLength(); i++) {
                if (dataBase.getChildNodes().item(i) instanceof Element) {
                    table = (Element) dataBase.getChildNodes().item(i);
                    //System.out.println(table.getNodeName());
                }
            }          
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public String getLoginFromToken(String token) {
        Element r = null;
        getDocument();
        try {
            for(int i=0;i<table.getChildNodes().getLength();i++) {
                if (table.getChildNodes().item(i) instanceof Element){ 
                    Element row = (Element) table.getChildNodes().item(i);
                    //System.out.println(row.getTagName());
                    for(int k=0;k<row.getChildNodes().getLength();k++) {
                        if (row.getChildNodes().item(k) instanceof Element){ 
                            Element node = (Element) row.getChildNodes().item(k);
                            //System.out.println(node.getNodeName());
                            if(node.getNodeName().equals("token")) {
                                //System.out.println(node.getTextContent());
                                if(node.getTextContent().equals(token)) {
                                    r = row;
                                    break;
                                }
                            }
                        }
                    }
                    if(r != null)
                        break;
                }
            }
            if(r != null) {
                for(int k=0;k<r.getChildNodes().getLength();k++) {
                    if (r.getChildNodes().item(k) instanceof Element){ 
                        Element node = (Element) r.getChildNodes().item(k);
                        //System.out.println(node.getNodeName());
                        if(node.getNodeName().equals("login")) {
                            return node.getTextContent();
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }
    
    public String getTokenFromLogin(String login) {
        Element r = null;
        getDocument();
        try {
            for(int i=0;i<table.getChildNodes().getLength();i++) {
                if (table.getChildNodes().item(i) instanceof Element){ 
                    Element row = (Element) table.getChildNodes().item(i);
                    //System.out.println(row.getTagName());
                    for(int k=0;k<row.getChildNodes().getLength();k++) {
                        if (row.getChildNodes().item(k) instanceof Element){ 
                            Element node = (Element) row.getChildNodes().item(k);
                            //System.out.println(node.getNodeName());
                            if(node.getNodeName().equals("login")) {
                                //System.out.println(node.getTextContent());
                                if(node.getTextContent().equals(login)) {
                                    r = row;
                                    break;
                                }
                            }
                        }
                    }
                    if(r != null)
                        break;
                }
            }
            if (r != null) {
                for(int k=0;k<r.getChildNodes().getLength();k++) {
                    if (r.getChildNodes().item(k) instanceof Element){ 
                        Element node = (Element) r.getChildNodes().item(k);
                        //System.out.println(node.getNodeName());
                        if(node.getNodeName().equals("token")) {
                            return node.getTextContent();
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return "";
    }
    
    public void addRow(String login, String password, String token) {
        getDocument();
        
        Element row = doc.createElement("Row");
        table.appendChild(row);
        
        Element eLogin = doc.createElement("login");
        eLogin.setTextContent(login);
        row.appendChild(eLogin);
        
        Element ePassword = doc.createElement("password");
        ePassword.setTextContent(password);
        row.appendChild(ePassword);
        
        Element eToken = doc.createElement("token");
        eToken.setTextContent(token);
        row.appendChild(eToken);
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(fileName));
            transformer.transform(domSource, streamResult);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public boolean loginSuccess(String login, String password) {
        getDocument();
        boolean lSuccess = false;
        boolean pSuccess = false;
        try {
            for(int i=0;i<table.getChildNodes().getLength();i++) {
                if (table.getChildNodes().item(i) instanceof Element){ 
                    Element row = (Element) table.getChildNodes().item(i);
                    //System.out.println(row.getTagName());
                    for(int k=0;k<row.getChildNodes().getLength();k++) {
                        if (row.getChildNodes().item(k) instanceof Element){ 
                            Element node = (Element) row.getChildNodes().item(k);
                            //System.out.println(node.getNodeName());
                            if(node.getNodeName().equals("login")) {
                                //System.out.println(node.getTextContent());
                                if(node.getTextContent().equals(login)) {
                                    lSuccess = true;
                                }
                            }
                            if(node.getNodeName().equals("password")) {
                                if(node.getTextContent().equals(password)){
                                    pSuccess = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return (lSuccess && pSuccess);
    }
}
