package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;

public class File
{

    Socket client;
    ArrayBlockingQueue<String> file;

    public File(Socket src)
    {

        this.client=src;
        this.file=new ArrayBlockingQueue<String>(100);
        //new Thread((new Handler())).start();
        System.out.println("thread launched");

    }

    public void add(String msg,Socket src)
    {

        if(this.isSocket(src))
            return;

        file.add(msg);

    }

    private boolean isSocket(Socket src)
    {

        return src==this.client ? true:false;

    }

    public void send()throws IOException
    {

        PrintWriter out=new PrintWriter(client.getOutputStream(),true);
        String msg=file.peek();
        out.println(msg);
        System.out.println("msg= "+msg);
        file.remove(msg);
        out.close();

        System.out.println("sent");

    }

    class Handler implements Runnable
    {

        Handler()
        {}

        public void run()
        {

            System.out.println("dans le thread");

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
