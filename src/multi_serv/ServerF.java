package multi_serv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class ServerF {

    int port;
    List<Queue> allQueue;
    boolean serverconnect;


    public ServerF(int port){
        this.port = port;
        allQueue =new ArrayList<>();
        allQueue.add(new Queue(null));//ce sera la file principale qui contient tout les messages du chat
        this.serverconnect=false;
    }

    public void launch() throws IOException {

       ServerSocket ss = new ServerSocket(port);

        try {
                ss.setReuseAddress(true);
            while (true) {
                new Thread((new gestionMSG(ss.accept()))).start();
            }
        } catch (IOException ex) {
            System.out.println("ss.accept got exception");
            ex.printStackTrace();
            return;
        }
    }

    public void message(String s,Socket src)
    {

        for(int i = 0; i< allQueue.size(); ++i)
        {

            Queue temp= allQueue.get(i);
            temp.add(s,src);
            allQueue.set(i,temp);


        }

    }

    public void connecServ(int port) {

        Socket socket;
        try {
            socket = new Socket("localhost",port);

            System.out.println("Serveur connecté à un autre serveur");
            this.serverconnect=true;
            new Thread(new serverConnection(socket)).start();
            new Thread(new receiveFromServ(socket)).start();

        } catch(ConnectException c){
            System.err.println("Pas de 2eme serveur");
            this.serverconnect=false;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendToServ(Socket socket) throws IOException{

        PrintWriter out= new PrintWriter(socket.getOutputStream(), true);//peut etre input plutot que output

        out.println("SERVERCONNECT");

        String toSend;

        while (true) {

            toSend = allQueue.get(0).queue.peek();
            allQueue.get(0).queue.remove(toSend);
            String tmp = "MSG " + toSend;
            out.println(tmp);

        }


    }


    public static void main(String[] args) {


        if(args.length!=1){
            System.out.println("Erreur d'arguments: @port");
            return;

        }

        try {
            ServerF s = new ServerF(Integer.parseInt(args[0]));
            s.connecServ(Integer.parseInt(args[0]));
            s.launch();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    class gestionMSG implements Runnable {

        Socket socket;
        PrintWriter out;
        BufferedReader in;
        InetAddress hote;
        int port;
        boolean server;

        gestionMSG(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            hote = socket.getInetAddress();
            port = socket.getPort();
            Queue f=new Queue(socket);
            allQueue.add(f);
        }

        public void run() {
            String tampon;

            String pseudo = null;

            try {

                do {

                    tampon = in.readLine();
                    if(tampon==null)
                        break;

                    System.out.println(tampon);

                    if(tampon.length()>=7 && tampon.substring(0,7).equals("CONNECT"))
                    {

                        pseudo = tampon.substring(8);
                        for(int i = 0; i< allQueue.size(); ++i)
                        {

                            if(allQueue.get(i).isSocket(socket))
                                allQueue.get(i).threading();

                        }
                        message(pseudo+" vient de nous rejoindre",null);
                        server=false;

                    }else if(tampon.length()>=13 && tampon.substring(0,13).equals("SERVERCONNECT"))
                    {

                        server=true;

                    }else if(tampon.length()>=3 && tampon.substring(0,3).equals("MSG"))
                    {

                        message(pseudo+"> "+tampon.substring(4),socket);

                    }else if(tampon.equals("EXIT"))
                       break;



                } while (true);

                System.out.println("closed");

                for(int i = 0; i< allQueue.size(); ++i)
                {
                    if(allQueue.get(i).isSocket(socket))
                    {
                        allQueue.remove(allQueue.get(i));
                        break;
                    }
                }

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

    class receiveFromServ implements Runnable{

        BufferedReader in;
        Socket socket;

        public receiveFromServ(Socket socket) throws IOException {

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socket=socket;

        }
        @Override
        public void run() {

            String received=null;

            while (true) {
                try {
                    received = in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (received == null)
                    break;
                message(received,null);
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    class serverConnection implements Runnable{

        Socket socket;

        public serverConnection(Socket s){

            socket=s;
        }

        @Override
        public void run() {

            try {
                sendToServ(socket);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


}