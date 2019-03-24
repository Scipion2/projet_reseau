package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.lang.System.exit;

public class Server {

    int port;
    List<File> fil;


    public Server(int port) throws UnknownHostException {
        this.port = port;
        fil=null;
    }

    public void launch() throws IOException {

        //Scanner sc = new Scanner(System.in);
        ServerSocket ss = new ServerSocket(port);
        //Socket socket = new Socket(ss.getInetAddress(), port);
        //String msg = null;
        //String servMsg;
        //BufferedReader in;

       ss.accept();

        new Thread((new Handler(ss.accept()))).start();
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            while (true) {
                (new Handler(ss.accept())).run();
            }
        } catch (IOException ex) {
            System.out.println("Arrêt anormal du serveur.");
            return;
        }
    }

    public static void main(String[] args) {


        if(args.length!=1){
            System.out.println("Erreur d'arguments: @port");
            return;

        }

        try {
            Server s = new Server(Integer.parseInt(args[0]));
            s.launch();
        } catch (IOException e) {
            e.printStackTrace();
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
            File f=new File(this.socket);
            fil.add(f);
        }

        public void run() {
            String tampon;

            String pseudo = null;
            long compteur = 0;


            try {
                /* envoi du message d'accueil */
                // out.println("Bonjour " + hote + "! (vous utilisez le port " + port + ")");

                do {
                    /* Faire echo et logguer */
                    tampon = in.readLine();
                    if (tampon != null) {
                        if (pseudo == null) {
                            pseudo = tampon.substring(8);
                            this.affiche(pseudo+" vient de nous rejoindre");
                        } else {
                            compteur++;
                            /* log */
                            //System.err.println("[" + hote + ":" + port + "]: " + compteur + ":" + tampon);
                            /* echo vers le client */
                            tampon.substring(4);

                            out.println(pseudo + "> " + tampon);
                        }
                        }
                        else{
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

        private void affiche(String s)
        {

            

        }
    }
}