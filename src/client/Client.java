package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class Client
{

    int port;
    String adr;
    String pseudal;

    public Client(String adr,int port)throws IOException
    {

        this.port=port;
        this.adr=adr;
        this.launch();

    }

    Socket connect()throws IOException
    {

        return new Socket(adr,port);

    }

    void launch() throws IOException
    {

        Socket client=this.connect();
        String received;
        String toSend;

        Scanner sc=new Scanner(System.in);
        System.out.println("veuillez choisir votre pseudal");
        pseudal=sc.nextLine();

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        out.println("CONNECT "+pseudal);

        for(;;)
        {

            new Thread(new Handler(client)).start();
            toSend=sc.nextLine();
            String tmp="MSG " + toSend;
            out.println(tmp);

        }

    }

    class Handler implements Runnable{
        Socket socket;
        PrintWriter out;
        BufferedReader in;
        InetAddress hote;
        int port;
        String received;

        Handler(Socket socket) throws IOException {
            this.socket = socket;
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            hote = socket.getInetAddress();
            port = socket.getPort();
        }


        public void run() {

            try {
                received=in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(received);

        }


    }

}
