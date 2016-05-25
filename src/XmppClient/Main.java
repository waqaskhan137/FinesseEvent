package XmppClient;

public class Main {
    public static void main(String arg[]) {

        System.out.println("XMPP Events Capture 1 !  ");
        try {
            //Thread One
            Runnable r = new FinesseXMPPClient("555", "123456", "user-555.log");
            new Thread(r).start();
            System.out.println("Thread -1 started ");


            //Thread Two
            Runnable r1 = new FinesseXMPPClient("509", "123456", "user-509.log");
            new Thread(r1).start();
            System.out.println("Thread-2 started");

        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
}
