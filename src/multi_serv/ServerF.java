package multi_serv;

import server.Server;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ServerF {

    ServerSocket ss;
    List<Queue> allQueue;
    static String filePath;


    public ServerF(int port)
    {

        try {
            this.ss=connecServ(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        allQueue =new ArrayList<>();
        allQueue.add(new Queue(null));//ce sera la file principale qui contient tout les messages du chat

    }

    public void launch(){

        try {
                ss.setReuseAddress(true);

            while (true) {
                new Thread((new gestionMSG(ss.accept()))).start();
            }
        } catch (IOException ex) {
            System.out.println("ss.accept got exception");
            ex.printStackTrace();
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

    public ServerSocket connecServ(int port)throws IOException {

        Socket socket;
        try {
            socket = new Socket("localhost",port);

            System.out.println("Serveur connecté à un autre serveur");
            PrintWriter out=new PrintWriter(socket.getOutputStream(),true);
            BufferedReader in=new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("SERVERCONNECT");
            ServerSocket ss=new ServerSocket(Integer.parseInt(in.readLine()));
            out.println(ss.getInetAddress());
            this.filePath=in.readLine();

            new Thread(new serverConnection(socket)).start();
            new Thread(new receiveFromServ(socket)).start();

            return ss;


        } catch(ConnectException c){
            System.err.println("Pas de 2eme serveur");

            File adressList;

            if(System.getProperty("os.name").equals("WINDOWS"))
                adressList=new File(System.getProperty("user.dir")+"\\pairs.cfg");
            else
                adressList=new File(System.getProperty("user.dir")+"/pairs.cfg");

            if(adressList.createNewFile())
                System.out.println("adress list created");
            else
                System.out.println("adress list file already exist");

            this.filePath=adressList.getAbsolutePath();
            ServerSocket ss=new ServerSocket(port);
            addServ("master",ss.getLocalPort(),String.valueOf(ss.getInetAddress()));

            return ss;

        }

    }

    public void sendToServ(Socket socket) throws IOException{

        PrintWriter out= new PrintWriter(socket.getOutputStream(), true);

        for(String toSend = "SERVERCONNECT"; toSend!=null; toSend = allQueue.get(0).queue.poll())
        {

            if(toSend.equals("SERVERCONNECT"))
                out.println(toSend);
            else
            {

                String tmp = "MSG " + toSend;
                out.println(tmp);

            }


        }

    }


    public static void main(String[] args) {


        if(args.length!=1){
            System.out.println("Erreur d'arguments: @port");
            return;

        }

        ServerF s = new ServerF(Integer.parseInt(args[0]));
        s.launch();
        File file=new File(filePath);
        if(file.delete())
            System.out.println("file deleted");
        else
            System.out.println("delete c'est de la merde");

    }



    class gestionMSG implements Runnable {

        Socket socket;
        PrintWriter out;
        BufferedReader in;
        InetAddress hote;
        int port;

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

                    }else if(tampon.length()>=13 && tampon.substring(0,13).equals("SERVERCONNECT"))
                    {

                        int newPort=getNewPort();
                        out.println(newPort);
                        addServ("peer",newPort,in.readLine());

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

    private void addServ(String name,int newPort, String adress)
    {

        BufferedOutputStream bos;
        String toWrite=name+" = "+adress+" "+newPort;


        try {

            bos=new BufferedOutputStream(new FileOutputStream(new File(filePath)));
            bos.write(toWrite.getBytes());
            bos.close();
        } catch (IOException e) {
            System.out.println("this file does not exist FDP");
        }

    }


    public int getNewPort()
    {

        BufferedInputStream bis;
        int port=0;
        try {

            bis=new BufferedInputStream(new FileInputStream(new File(filePath)));
            String temp=new String(bis.readAllBytes());
            port=Integer.parseInt(temp.substring(temp.lastIndexOf(' ')+1));
            bis.close();

        } catch (IOException e) {
            System.out.println("this file does not exist FDP");
        }

        return port++;
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