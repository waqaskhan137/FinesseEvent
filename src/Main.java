public class Main {
    public static void main(String arg[]) {

        System.out.println("XMPP Events Capture 1 !  ");
        FinesseXMPPClient fxc = new FinesseXMPPClient();
        try {
            fxc.login("111", "Password");
        } catch (Exception ex) {
            System.out.print(ex);
        }
    }
}
