package multi_serv;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class Queue
{

    Socket client;
    ArrayBlockingQueue<String> queue;

    public Queue(Socket src)
    {

        this.client=src;
        this.queue =new ArrayBlockingQueue<String>(100);


    }

    public void threading()
    {

        new Thread((new Handler())).start();

    }

    public void add(String msg,Socket src)
    {

        if(this.isSocket(src) && src!=null)
            return;

        queue.add(msg);

    }

    public boolean isSocket(Socket src)
    {

        return src==this.client ? true:false;

    }

    public void send()throws IOException
    {

        if(queue.isEmpty())
            return;

        PrintWriter out=new PrintWriter(client.getOutputStream(),true);
        String msg= queue.peek();
        out.println(msg);
        queue.remove(msg);


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
