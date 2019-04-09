package threads;

import multi_serv.QueueF;
import multi_serv.ServerF;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Message implements Runnable
{

    private Socket client;
    private ServerF server;
    private PrintWriter out;
    private BufferedReader in;

    Message(Socket srcclient, ServerF srcserv) throws IOException {

        client =srcclient;
        server =srcserv;
        out=new PrintWriter(client.getOutputStream(),true);
        in=new BufferedReader(new InputStreamReader(client.getInputStream()));
        server.displayServerSide("socket init");

    }

    @Override
    public void run()
    {

        try {
            server.displayServerSide("ready to check socket type");
            String tampon=in.readLine();
            server.displayServerSide("message recieved is : "+tampon);
            if(tampon.length()>=7 && tampon.substring(0,7).equals("CONNECT"))
            {

                QueueF messageList=new QueueF(client,false);
                server.addQueue(messageList);
                server.displayServerSide("CONNECT received");
                String pseudo = connectClient();
                server.message(pseudo+" vient de nous rejoindre",null,false);
                server.displayServerSide(pseudo+" logged in");
                new Thread(new ReceivedFromClient(in,server, pseudo,client)).start();
                new Thread(new SendToClient(messageList)).start();

            }else if(tampon.length()>=13 && tampon.substring(0,13).equals("SERVERCONNECT"))
            {

                server.displayServerSide("un serveur s'est connecte au reseau");
                QueueF messageList=new QueueF(client,true);
                server.addQueue(messageList);
                new Thread(new ReceivedFromServ(in,server,client)).start();
                new Thread(new SendToClient(messageList));
                //server.addServer(new PrintWriter(client.getOutputStream()));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String connectClient() throws IOException {

        out.println("Bienvenue sur le server ClavardAmu\nveuillez choisir votre pseudo pour cette session\n");
        return in.readLine().substring(4);

    }

}
