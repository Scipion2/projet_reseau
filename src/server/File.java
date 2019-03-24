package server;

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

}
