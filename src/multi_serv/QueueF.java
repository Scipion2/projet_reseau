package multi_serv;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class QueueF
{

    Socket client;
    ArrayBlockingQueue<String> queue;
    boolean isServer;

    public QueueF(Socket src,boolean serv)
    {

        this.client=src;
        this.queue =new ArrayBlockingQueue<>(100);
        isServer=serv;


    }

    void threading()
    {

        new Thread((new Handler())).start();

    }

    void add(String msg,Socket src,boolean fromServ)
    {

        if(this.isSocket(src) && src!=null)
            return;
        if(fromServ && isServer)
            return;

        queue.add(msg);

    }

    boolean isSocket(Socket src)
    {

        return src == this.client;

    }

    public void send()throws IOException
    {

        if(queue.isEmpty())
            return;

        PrintWriter out=new PrintWriter(client.getOutputStream(),true);
        String msg= queue.poll();
        out.println(msg);


    }

    class Handler implements Runnable
    {

        Handler()
        {}

        public void run()
        {


            for(;;)
            {
                try {
                    send();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }

}
