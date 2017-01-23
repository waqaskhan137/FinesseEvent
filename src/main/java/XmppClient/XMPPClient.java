package XmppClient;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ResourceBundle;

import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * This class is called with each new agent created and added to the list, to
 * create it's XMPP client that will listen to Finesse
 */
class XMPPClient implements Runnable {

    private static ResourceBundle properties;
    private int i = 0;
    private XMPPConnection connection;
    private String id;
    private String password;
    private String fileName;
    private String path;

    XMPPClient(String id, String password, String fileName) {
        this.id = id;
        this.password = password;
        this.fileName = fileName;
        properties = ResourceBundle.getBundle("config.config");
        path = properties.getString("EVENT_FILE_LOCATION");
    }

    private void login(String userName, String password) throws XMPPException {

        try {
            //    ChatLogger.LogInfo("XMPPClient","userName = [" + userName + "], password = [" + password + "]");
            String serverAddress = properties.getString("SERVER_ADDRESS");
            int port = Integer.parseInt(properties.getString("SERVER_PORT"));

            ConnectionConfiguration config = new ConnectionConfiguration(serverAddress, port, "");
            connection = new XMPPConnection(config);
            connection.connect();
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            connection.login(userName, password);

            //initializing chat for testing
            initChat("test", "Waqas");


        } catch (XMPPException xmppEX) {
            ChatLogger.LogInfo("XMPPClient","xmppEX.getMessage() = " + xmppEX.getMessage());

        } catch (Exception xmppEX) {
            ChatLogger.LogInfo("XMPPClient","xmppEX = " + xmppEX.getMessage());

        }

        PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
        PacketListener myPacketListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                String prettyXML = PrettifyXML(packet.toXML());
                writeXMLFile(prettyXML);
                ChatLogger.LogInfo("XMPPClient", prettyXML);
            }
        };


        try {
            while (true) {

                try {
                    connection.addPacketListener(myPacketListener, filter);
                } catch (Exception e) {
                    ChatLogger.LogInfo("XMPPClient","e.getMessage() = " + e.getMessage());
                }
            }
        } catch (Exception e) {
            ChatLogger.LogInfo("XMPPClient","e.getMessage() = " + e.getMessage());
        }
    }

    private void initChat(String chatRoomName, String chatUserName) {

        try {
            MultiUserChat chatRoom = new MultiUserChat(connection, chatRoomName);

            chatRoom.join(chatUserName);
            for (int i = 0; i < 10; i++) {
                chatRoom.sendMessage("Hello World" + chatUserName);
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

    private void writeXMLFile(String xml) {
        String packetDivider = "<==============================Event " + i++ + " Capture=============================>\n";
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream(new File(fileName), true /* append = true */));
            pw.append(xml);
            pw.append(packetDivider);
            pw.close();
        } catch (FileNotFoundException e) {
            ChatLogger.LogInfo("XMPPClient","e.getMessage() = " + e.getMessage());
        }

    }

    private String PrettifyXML(String _source) {
        try {
            final InputSource src = new InputSource(new StringReader(_source));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            // Set this to true if the output needs to be beautified.
            writer.getDomConfig().setParameter("format-pretty-print", true);

            // Set this to true if the declaration is needed to be outputted.
            writer.getDomConfig().setParameter("xml-declaration", _source.startsWith("<?xml"));

            return writer.writeToString(document);

        } catch (ParserConfigurationException ex) {
            ChatLogger.LogInfo("XMPPClient","ex.getMessage() = " + ex.getMessage());

        } catch (SAXException ex) {
            ChatLogger.LogInfo("XMPPClient","ex.getMessage() = " + ex.getMessage());
        } catch (IOException ex) {
            ChatLogger.LogInfo("XMPPClient","ex.getMessage() = " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            ChatLogger.LogInfo("XMPPClient","ex.getMessage() = " + ex.getMessage());
        } catch (InstantiationException ex) {
            ChatLogger.LogInfo("XMPPClient","ex.getMessage() = " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            ChatLogger.LogInfo("XMPPClient","ex.getMessage() = " + ex.getMessage());
        } catch (ClassCastException ex) {
            ChatLogger.LogInfo("XMPPClient","ex.getMessage() = " + ex.getMessage());
        }

        return _source;
    }

    @Override
    public void run() {

        try {
            login(id, password);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
