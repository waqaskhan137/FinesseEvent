package XmppClient;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

// TODO: 6/15/2016 add logger to the application
public class Main {

    private Map<String, String> agentsMap;
    private ResourceBundle properties;
    private String xmlFile;
    private File fXmlFile;

    public Main() {
        agentsMap = new HashMap();
        properties = ResourceBundle.getBundle("config.config");
        xmlFile = properties.getString("AGENT_FILE_LOCATION");
        fXmlFile = new File(xmlFile);
    }

    public static void main(String arg[]) {

        ChatLogger.LogInfo("Main","Capture XMPP is Starting ......\n ");

        Main obj = new Main();
        //get all users from xml
        obj.getAgentFromXML();
        //start XMPP thread of each user
        obj.agentToCapture();
    }


    //Starts the XMPP Threads
    private void agentToCapture() {
        for (Map.Entry<String, String> entry : agentsMap.entrySet()) {
            try {
                new Thread(new XMPPClient(entry.getKey(), entry.getValue(), "user-" + entry.getKey() + ".log")).start();
                ChatLogger.LogInfo("Main",entry.getKey() + " Thread started !");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Get all Users from XML file
    private void getAgentFromXML() {
        try {


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("agent");

            ChatLogger.LogInfo("Main","Total Agents To Subscribe = " + nList.getLength());
            ChatLogger.LogInfo("Main","----------------------------");


            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    String agentID = eElement.getElementsByTagName("userId").item(0).getTextContent();
                    String password = eElement.getElementsByTagName("password").item(0).getTextContent();

                    //for logging purpose
                    ChatLogger.LogInfo("Main","User ID : " + agentID);
                    ChatLogger.LogInfo("Main","Password : " + password);
                    ChatLogger.LogInfo("Main","----------------------------");

                    //Putting agent in agentsMap
                    agentsMap.put(agentID, password);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
