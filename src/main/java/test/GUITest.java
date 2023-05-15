package test;


public class GUITest {

    private static GUITest singleGUI = null;
    String ip, port;

    private GUITest() {

        ip = "12.34.56";
        port = "3000";
    }

    public static GUITest getGuiTest() {

        if (singleGUI == null)
            singleGUI = new GUITest();

        return singleGUI;
    }

    public String getIp() { return ip != null? ip : "0.0.0.0"; }

    public String getPort() { return port != null? port : "0000"; }
}
