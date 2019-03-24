package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.lang.System.exit;

public class Server
{

    String serv;
    int port;
   // List


    public Server(String serv, int port) throws UnknownHostException {

        this.serv = serv;
        this.port = port;

    }

    public void sendPack() throws IOException {

        Scanner sc = new Scanner(System.in);
        ServerSocket ss=new ServerSocket();
        Socket socket = new Socket(serv, port);
        String msg = null;
        String servMsg;
        BufferedReader in;

        ss.accept();

        new Thread((new Handler(ss.accept()))).start();

       for(;;)
       {



       }


    }



    class Handler implements Runnable {

        Socket socket;
        PrintWriter out;
        BufferedReader in;
        InetAddress hote;
        int port;

        Handler(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            hote = socket.getInetAddress();
            port = socket.getPort();
        }

        public void run() {
            String tampon;
            long compteur = 0;

            try {
                /* envoi du message d'accueil */
                out.println("Bonjour " + hote + "! (vous utilisez le port " + port + ")");

                do {
                    /* Faire echo et logguer */
                    tampon = in.readLine();
                    if (tampon != null) {
                        compteur++;
                        /* log */
                        //System.err.println("[" + hote + ":" + port + "]: " + compteur + ":" + tampon);
                        /* echo vers le client */
                        //tampon.
                        out.println("> " + tampon);
                    } else {
                        break;
                    }
                } while (true);

                /* le correspondant a quitté */
                in.close();
                out.println("Au revoir...");
                out.close();
                socket.close();

                System.err.println("[" + hote + ":" + port + "]: Terminé...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
