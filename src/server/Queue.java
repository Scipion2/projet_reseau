package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class Queue
{

    Socket client;
    public ArrayBlockingQueue<String> file;

    public Queue(Socket src)
    {

        this.client=src;
        this.file=new ArrayBlockingQueue<String>(100);


    }

    public void threading()
    {

        new Thread((new Handler())).start();

    }

    public void add(String msg,Socket src)
    {

        if(this.isSocket(src))
            return;

        file.add(msg);

    }

    public boolean isSocket(Socket src)
    {

        return src==this.client ? true:false;

    }

    public void send()throws IOException
    {

        if(file.isEmpty())
            return;

        PrintWriter out=new PrintWriter(client.getOutputStream(),true);
        String msg=file.peek();
        out.println(msg);
        file.remove(msg);


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
