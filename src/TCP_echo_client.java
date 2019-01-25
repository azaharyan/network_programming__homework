import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.KeyStore;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.+
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author N
 */
public class TCP_echo_client {
    public static final int LISTENING_TCP_PORT = 3333;
//    public static final String LISTENING_IP_ADDRESS = "localhost";

    public static void main(String[] args) throws IOException {

        System.out.println("Enter IP address of the server:");
        Scanner in = new Scanner(System.in);
        String ipAddress = in.next();

        SSLSocketFactory factory = null;
        try {
            SSLContext ctx;
            KeyManagerFactory kmf;
            KeyStore ks;
            char[] passphrase = "passphrase".toCharArray();

            ctx = SSLContext.getInstance("TLS");
            kmf = KeyManagerFactory.getInstance("SunX509");
            ks = KeyStore.getInstance("JKS");

            ks.load(new FileInputStream("testkeys"), passphrase);

            kmf.init(ks, passphrase);
            ctx.init(kmf.getKeyManagers(), null, null);

            factory = ctx.getSocketFactory();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }


        try (SSLSocket socket = (SSLSocket)factory.createSocket(ipAddress,LISTENING_TCP_PORT)) {
            System.out.println("Connected to echo server (" + socket.getInetAddress().getHostAddress() + ")");
            socket.startHandshake();

            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader input
                    = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String sentence = reader.readLine();
            while ( !sentence.equals("END") ) {
                out.println(sentence);
                String response = input.readLine();
                System.out.println(response);
                sentence = reader.readLine();
            }
            out.println(sentence);      // send END to the server to notify them to close the cocket
        } catch (SocketException se){
            Logger.getLogger(TCP_echo_client.class.getName()).log(Level.SEVERE, null, se);
        }
    }
}
