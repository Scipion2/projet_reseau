package threads;

import multi_serv.QueueF;
import multi_serv.ServerF;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnecToServ implements Runnable
{

    private ServerF server;

    ConnecToServ(ServerF serversrc)
    {

        server=serversrc;

    }

    @Override
    public void run()
    {

        try {

            List<Socket> listConnect=getAllserv();
            for(int i=0;i<listConnect.size();++i)
            {

                QueueF messageList=new QueueF(listConnect.get(i),true);
                server.addQueue(messageList);
                PrintWriter out=new PrintWriter(listConnect.get(i).getOutputStream(),true);
                out.println("SERVERCONNECT");
                new Thread(new ReceivedFromServ(new BufferedReader(new InputStreamReader(listConnect.get(i).getInputStream())),server,listConnect.get(i)));
                new Thread(new SendToClient(messageList)).start();
                //server.addServer(new PrintWriter(listConnect.get(i).getOutputStream()));

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<Socket> getAllserv() throws IOException
    {

        File servList=new File(server.filePath);
        List<Socket> toFill=new ArrayList<>();
        String tampon;
        for (BufferedReader br = new BufferedReader(new FileReader(servList)); (tampon=br.readLine())!=null;)
        {

            if(!(tampon.contains(String.valueOf(server.ss.getLocalPort()))))
            {

                //InetAddress adr=InetAddress.getByName(tampon.substring(7,tampon.lastIndexOf(' ')));
                int port=Integer.parseInt(tampon.substring(tampon.lastIndexOf(' ')+1));
                toFill.add(new Socket("localhost",port));

            }
        }

        return toFill;

    }

}
