package multi_serv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import static java.lang.System.exit;

public class ClientF {

    protected int port;
    protected String adr;

    protected ClientF(String adr, int port)
    {

        this.port=port;
        this.adr=adr;

    }

    protected Socket connect()throws IOException
    {

        return new Socket(adr,port);

    }

    protected void launch() throws IOException
    {

        Socket client=this.connect();
        String toSend;

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        Scanner sc=new Scanner(System.in);

        out.println("CONNECT");

        new Thread(new Handler(client)).start();

        while (true) {

            toSend = sc.nextLine();
            String tmp = "MSG " + toSend;
            out.println(tmp);

        }
    }


    public static void main(String[] args)
    {

        if(args.length!=2)
        {
            System.out.println("Erreur d'arguments: @adr @port");
            return;
        }

        try {

            ClientF c = new ClientF(args[0], Integer.parseInt(args[1]));
            c.launch();

        } catch (IOException e) {
            e.printStackTrace();
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


        public void run()
        {

            for(;;)
            {

                try {
                    received=in.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if(received==null)
                    break;
                System.out.println(received);
            }

            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            exit(0);
        }
    }
}
