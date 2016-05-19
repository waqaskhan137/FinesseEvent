
import java.io.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
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

/**
 * This class is called with each new agent created and added to the list, to
 * create it's XMPP client that will listen to Finesse
 *
 */
public class FinesseXMPPClient {
    File file = null;
    PrintWriter pw;
    XMPPConnection connection;
    static String name = "FinesseXMPPClient";

    public FinesseXMPPClient() {

        file = new File("finesseEvent1.txt");
//        pw = new PrintWriter(file);
    }
    public void login(String userName, String password) throws XMPPException {

        try {

            //.LogDebug(name, "XMPP Client Login ID " + userName);
            String serverAddress = "192.168.200.82";
            String domainName = "";
            //XMPPConnection.DEBUG_ENABLED = false;

            ConnectionConfiguration config = new ConnectionConfiguration(serverAddress, 5222, "");
            connection = new XMPPConnection(config);
            connection.connect();
            SASLAuthentication.supportSASLMechanism("PLAIN", 0);
            connection.login(userName, password);
            //.LogTrace(name, "XMPP Client Login  successful ID " + userName);

        } catch (XMPPException xmppEX) {
            //.LogError(name, xmppEX.getXMPPError().toString());
            //.LogError(name, xmppEX.getMessage());
            System.out.println("XMPP Exception:");
            System.out.println(xmppEX);

        } catch (Exception xmppEX) {
            //.LogError(name, "Error in Finesse XMPP Client login");
            //.LogError(name, xmppEX.getMessage());
            System.out.println("Exception:");
            System.out.println(xmppEX);

        }
        PacketFilter filter = new MessageTypeFilter(Message.Type.normal);
        PacketListener myPacketListener = new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                //.LogTrace(name, "Received Event From Finesse: \n" + PrettifyXML(packet.toXML()));
//                Parser.parseEvent(packet.toXML());

                System.out.println(PrettifyXML(packet.toXML()));
                try{

//                    pw.write(packet.toXML().toString());
                }catch (Exception ex){
                    System.out.print("Error"+ex);
                }
            }
        };
        while(true){
            connection.addPacketListener(myPacketListener, filter);
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
            //.LogError(name, ex.getMessage());
        } catch (SAXException ex) {
            //.LogError(name, ex.getMessage());
        } catch (IOException ex) {
            //.LogError(name, ex.getMessage());
        } catch (ClassNotFoundException ex) {
            //.LogError(name, ex.getMessage());
        } catch (InstantiationException ex) {
            //.LogError(name, ex.getMessage());
        } catch (IllegalAccessException ex) {
            //.LogError(name, ex.getMessage());
        } catch (ClassCastException ex) {
            //.LogError(name, ex.getMessage());
        }

        //.LogError(name, "Received Event From Finesse: Error Prettifying.");
        return _source;
    }
}
