package test;

import model.logic.MyServer;
import java.io.IOException;
import java.net.Socket;



public class MyServerTest {

    public static void serverTest() {

        // Create the server
        MyServer server = new MyServer(1234);

        // Start the server
        server.start();

        // Connect five clients
        for(int i = 0; i < 5; i++) {
            connectClient("localhost", 1234);
        }

        // Wait for a while to allow communication between the clients and server
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Stop the server
        try {
            server.ServerClose();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void connectClient(String host, int port) {
        // Connect a client to the server
        try {
            Socket clientSocket = new Socket(host, port);
            System.out.println("Connected to server: " + clientSocket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}









////package test;
////
////
////import java.io.IOException;
////import java.io.InputStream;
////import java.io.OutputStream;
////import java.io.PrintWriter;
////import java.net.Socket;
////import java.net.UnknownHostException;
////import java.util.Scanner;
////
////public class MainTrain {
////
////	public static class ClientHandler1 implements ClientHandler {
////		PrintWriter out;
////		Scanner in;
////		@Override
////		public void handleClient(InputStream inFromclient, OutputStream outToClient) {
////			out=new PrintWriter(outToClient);
////			in=new Scanner(inFromclient);
////			String text = in.next();
////			out.println(new StringBuilder(text).reverse().toString());
////			out.flush();
////		}
////
////		@Override
////		public void close() {
////			in.close();
////			out.close();
////		}
////
////	}
////
////	public static void main(String[] args) {
////
////		// create a new server with the specified port and client handler
////		int port = 12345; // set the port number to listen on
////		ClientHandler handler = new ClientHandler1(); // replace MyClientHandler with your own implementation
////		MyServer server = new MyServer(port, handler);
////
////		// start the server
////		server.start();
////
////		// connect two clients to the server
////		try {
////			Socket client1 = new Socket("localhost", port);
////			Socket client2 = new Socket("localhost", port);
////
////			// handle client1
////			handler.handleClient(client1.getInputStream(), client1.getOutputStream());
////			client1.close();
////
////			// handle client2
////			handler.handleClient(client2.getInputStream(), client2.getOutputStream());
////			client2.close();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
////
////		// wait for user input to stop the server
////		Scanner scanner = new Scanner(System.in);
////		System.out.println("Server started on port " + port + ". Press enter to stop the server.");
////		scanner.nextLine();
////
////		// stop the server
////		server.close();
////	}
////
////}
//package test;
//
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.io.PrintWriter;
//import java.net.Socket;
//import java.util.Random;
//import java.util.Scanner;
//
//public class MainTrain {
//
//	public static class ClientHandler1 implements ClientHandler {
//
//		PrintWriter out;
//		Scanner in;
//
//		@Override
//		public void handleClient(InputStream inFromClient, OutputStream outToClient) {
//
//			out = new PrintWriter(outToClient);
//			in = new Scanner(inFromClient);
//			String text = in.next();
//			out.println(new StringBuilder(text).reverse());
//			out.flush();
//		}
//
//		@Override
//		public void close() {
//
//			in.close();
//			out.close();
//		}
//
//	}
//
//
//	public static void client1(int port) throws Exception {
//
//		Socket server=new Socket("localhost", port);
//		Random r=new Random();
//		String text = ""+(1000+r.nextInt(100000));
//		String rev=new StringBuilder(text).reverse().toString();
//		PrintWriter outToServer=new PrintWriter(server.getOutputStream());
//		Scanner in=new Scanner(server.getInputStream());
//		outToServer.println(text);
//		outToServer.flush();
//		String response=in.next();
//		if(response==null || !response.equals(rev))
//			System.out.println("problem getting the right response from your server, cannot continue the test (-100)");
//		in.close();
//		outToServer.println(text);
//		outToServer.close();
//		server.close();
//	}
//
//	public static boolean testServer() {
//
//		boolean ok = true;
//		Random r = new Random();
//		int port = 6000+r.nextInt(1000);
//		MyServer s=new MyServer(port, new ClientHandler1());
//		int c = Thread.activeCount();
//		s.start(); // runs in the background
//		try {
//			client1(port);
//		} catch(Exception e) {
//			System.out.println("some exception was thrown while testing your server, cannot continue the test (-100)");
//			ok = false;
//		}
//		s.close();
//
//		try {Thread.sleep(2000);} catch (InterruptedException ignored) {}
//
//		if (Thread.activeCount()!=c) {
//			System.out.println("you have a thread open after calling close method (-100)");
//			ok=false;
//		}
//		return ok;
//	}
//
//
//	public static String[] writeFile(String name) {
//
//		Random r=new Random();
//		String[] txt =new String[10];
//		for(int i=0;i<txt.length;i++)
//			txt[i]=""+(10000+r.nextInt(10000));
//
//		try {
//			PrintWriter out=new PrintWriter(new FileWriter(name));
//			for(String s : txt) {
//				out.print(s+" ");
//			}
//			out.println();
//			out.close();
//		}catch(Exception ignored) {}
//
//		return txt;
//	}
//
//
//	public static void runClient(int port,String query,boolean result) {
//		try {
//			Socket server=new Socket("localhost",port);
//			PrintWriter out=new PrintWriter(server.getOutputStream());
//			Scanner in=new Scanner(server.getInputStream());
//			out.println(query);
//			out.flush();
//			String res=in.next();
//			if((result && !res.equals("true")) || (!result && !res.equals("false")))
//				System.out.println("problem getting the right answer from the server (-10)");
//			in.close();
//			out.close();
//			server.close();
//		} catch (IOException e) {
//			System.out.println("your code ran into an IOException (-10)");
//		}
//	}
//
//	public static void testBSCH() {
//		String[] s1 =writeFile("s1.txt");
//		String[] s2;
//		s2 = writeFile("s2.txt");
//
//		Random r=new Random();
//		int port=6000+r.nextInt(1000);
//		MyServer s = new MyServer(port, new BookScrabbleHandler());
//		s.start();
//		runClient(port, "Q,s1.txt,s2.txt,"+s1[1], true);
//		runClient(port, "Q,s1.txt,s2.txt,"+s2[4], true);
//		runClient(port, "Q,s1.txt,s2.txt,2"+s1[1], false);
//		runClient(port, "Q,s1.txt,s2.txt,3"+s2[4], false);
//		runClient(port, "C,s1.txt,s2.txt,"+s1[9], true);
//		runClient(port, "C,s1.txt,s2.txt,#"+s2[1], false);
//		s.close();
//	}
//
//	public static void main(String[] args) {
//		if(testServer()) {
//			testBSCH();
//		}
//		System.out.println("done");
//	}
//
//}


