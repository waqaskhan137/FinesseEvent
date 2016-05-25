package XmppClient;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
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

/**
 * This class is called with each new agent created and added to the list, to
 * create it's XMPP client that will listen to Finesse
 */
public class FinesseXMPPClient implements Runnable {

    public static ResourceBundle properties;
    XMPPConnection connection;
    String id;
    String password;
    String fileName;
    String path;

    public FinesseXMPPClient(String id, String password, String fileName) {
        this.id = id;
        this.password = password;
        this.fileName = fileName;
        properties = ResourceBundle.getBundle("config.config");
        path = properties.getString("EVENT_LOCATION");
    }

    public void login(String userName, String password) throws XMPPException {


        try {
            String serverAddress = properties.getString("SERVER_ADDRESS");
            int port = Integer.parseInt(properties.getString("SERVER_PORT"));

            ConnectionConfiguration config = new ConnectionConfiguration(serverAddress, port, "");
            connection = new XMPPConnection(config);
            connection.connect();
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            connection.login(userName, password);

        } catch (XMPPException xmppEX) {
            System.out.println("xmppEX.getMessage() = " + xmppEX.getMessage());

        } catch (Exception xmppEX) {
            System.out.println("xmppEX = " + xmppEX.getMessage());

        }
        PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
        PacketListener myPacketListener = packet -> {
            String prettyXML = PrettifyXML(packet.toXML());
            writeXMLFile(prettyXML);
        };
        while (true) {
            connection.addPacketListener(myPacketListener, filter);
        }
    }

    private void writeXMLFile(String xml) {

        try {
//            PrintWriter pw = new PrintWriter(new FileOutputStream(new File(path + File.pathSeparator + fileName), true /* append = true */));
            PrintWriter pw = new PrintWriter(new FileOutputStream(new File(fileName), true /* append = true */));
            pw.append(xml);
            pw.close();
        } catch (FileNotFoundException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
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
            System.out.println("ex.getMessage() = " + ex.getMessage());

        } catch (SAXException ex) {
            System.out.println("ex.getMessage() = " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("ex.getMessage() = " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println("ex.getMessage() = " + ex.getMessage());
        } catch (InstantiationException ex) {
            System.out.println("ex.getMessage() = " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.out.println("ex.getMessage() = " + ex.getMessage());
        } catch (ClassCastException ex) {
            System.out.println("ex.getMessage() = " + ex.getMessage());
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
