package server;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Server {

    int port;
    List<File> fil;


    public Server(int port){
        this.port = port;
        fil=new ArrayList<File>();
    }

    public void launch() throws IOException {

       ServerSocket ss = new ServerSocket(port);
       //ss.accept();

       System.out.println("ss set");

        new Thread((new Handler(ss.accept()))).start();
        System.out.println("first thread launched");

        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            while (true) {
                (new Handler(ss.accept())).run();
            }
        } catch (IOException ex) {
            System.out.println("ss.accept got exception");
            return;
        }
    }

    public void message(String s,Socket src)
    {

        System.out.println("je suis dans message");
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
            System.out.println("init");
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
            File f=new File(socket);
            fil.add(f);
        }

        public void run() {
            String tampon;

            String pseudo = null;


            try {

                do {

                    tampon = in.readLine();
                    if (tampon != null) {
                        if (pseudo == null) {
                            pseudo = tampon.substring(8);
                            System.out.println(pseudo+" on");
                            message(pseudo+" vient de nous rejoindre",null);
                        } else {

                            tampon.substring(4);

                            message(pseudo+"> "+tampon,socket);
                            System.out.println("message send");
                        }
                        }
                        else{
                            break;
                        }

                } while (true);

                System.out.println("closed");

                /* le correspondant a quitté */
                in.close();
                System.out.println("Au revoir...");
                out.close();

                socket.close();

                System.err.println("[" + hote + ":" + port + "]: Terminé...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}