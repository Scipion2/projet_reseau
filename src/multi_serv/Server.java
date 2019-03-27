package multi_serv;

import server.File;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

        try {
                ss.setReuseAddress(true);
            while (true) {
                new Thread((new Handler(ss.accept()))).start();
            }
        } catch (IOException ex) {
            System.out.println("ss.accept got exception");
            ex.printStackTrace();
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
            File f=new File(socket);
            fil.add(f);
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
                        for(int i=0;i<fil.size();++i)
                        {

                            if(fil.get(i).isSocket(socket))
                                fil.get(i).threading();

                        }
                        message(pseudo+" vient de nous rejoindre",null);

                    }else if(tampon.length()>=3 && tampon.substring(0,3).equals("MSG"))
                    {

                        message(pseudo+"> "+tampon.substring(4),socket);

                    }else if(tampon.equals("EXIT"))
                       break;


                    tampon=null;


                } while (true);

                System.out.println("closed");

                for(int i=0;i<fil.size();++i)
                {

                    if(fil.get(i).isSocket(socket))
                    {

                        fil.remove(fil.get(i));
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
}