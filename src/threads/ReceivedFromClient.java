package threads;

import multi_serv.ServerF;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ReceivedFromClient implements Runnable
{

    private BufferedReader in;
    private ServerF server;
    private String pseudal;
    private Socket client;

    ReceivedFromClient(BufferedReader insrc, ServerF serversrc, String pseudalsrc, Socket clientsrc)
    {

        in=insrc;
        server=serversrc;
        pseudal=pseudalsrc;
        client=clientsrc;

    }

    @Override
    public void run()
    {

        try {

            for (String tampon; !(tampon = in.readLine()).equals("EXIT") && server.isOnline(); ) {

                if(tampon.length()>=3 && tampon.substring(0,3).equals("MSG"))
                {

                    String toSend=pseudal+"> "+tampon.substring(4);
                    server.message(toSend, client,false);
                    //server.sendToServ(toSend);

                }

            }

        }catch(IOException e)
        {

            e.printStackTrace();

        }

    }


}
