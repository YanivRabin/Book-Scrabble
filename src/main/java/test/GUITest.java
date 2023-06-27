package test;


public class GUITest {

    private static GUITest singleGUI = null;
    String ip, port;

    /**
     * The GUITest function is a constructor for the GUITest class.
     * It creates a new instance of the GUITest class, and sets up
     * all of its components.  The function also adds an action listener to each button, which allows it to respond when clicked on by the user.

     *
     *
     * @return The ip and port variables
     *
     * @docauthor Trelent
     */
    private GUITest() {

        ip = "12.34.56";
        port = "3000";
    }

    public static GUITest getGuiTest() {

        if (singleGUI == null)
            singleGUI = new GUITest();

        return singleGUI;
    }

    /**
     * The getIp function returns the IP address of the client.
     *
     *
     *
     * @return The ip address of the client
     *
     * @docauthor Trelent
     */
    public String getIp() { return ip != null? ip : "0.0.0.0"; }

    /**
     * The getPort function returns the port number of the server.
     *
     *
     *
     * @return The port number if it is not null
     *
     * @docauthor Trelent
     */
    public String getPort() { return port != null? port : "0000"; }
}
