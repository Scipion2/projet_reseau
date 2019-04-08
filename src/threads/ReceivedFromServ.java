package threads;

import multi_serv.ServerF;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ReceivedFromServ implements Runnable
{

    private BufferedReader in;
    private ServerF server;
    private Socket client;

    ReceivedFromServ(BufferedReader insrc, ServerF serversrc, Socket clientsrc)
    {

        in=insrc;
        server=serversrc;
        client=clientsrc;

    }

    @Override
    public void run()
    {

        try {
            for (String tampon; !(tampon = in.readLine()).equals("CLOSE CONNECTION");server.message(tampon, client,true)) {


                server.displayServerSide(tampon+" was received from a serv");

            }

        }catch(IOException e)
            {

                e.printStackTrace();

            }

    }

}
