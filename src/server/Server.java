package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;


public class Server {

    int port;
    List<File> fil;


    public Server(int port){
        this.port = port;
        fil=null;
    }

    public void launch() throws IOException {

       ServerSocket ss = new ServerSocket(port);
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

    public void message(String s,Socket src)
    {

        for(int i=0;i<fil.size();++i)
        {

            File temp=fil.get(i);
            temp.add(s,src);
            fil.set(i,temp);


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

                do {

                    tampon = in.readLine();
                    if (tampon != null) {
                        if (pseudo == null) {
                            pseudo = tampon.substring(8);
                            message(pseudo+" vient de nous rejoindre",null);
                        } else {
                            compteur++;

                            tampon.substring(4);

                            message(pseudo+"> "+tampon,socket);
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

    }
}