package XmppClient;

public class Main {

    public static void main(String arg[]) {

        System.out.println("XMPP Events Capture !");

        Main obj = new Main();
        try {
            //Add users as many as you want to capture !
            obj.agentToCapture("111", "Password");
            obj.agentToCapture("555", "123456");

        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }

    }

    private void agentToCapture(String agentID, String password) {
        try {
            new Thread(new XMPPClient(agentID, password, "user-" + agentID + ".log")).start();
            System.out.println(agentID + " Thread started !");
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }
    }
}
