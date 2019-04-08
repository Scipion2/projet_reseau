package multi_serv;

import threads.AcceptConnection;
import threads.ServerCommand;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerF
{

    public ServerSocket ss;
    public String filePath;
    private List<QueueF> allQueue;
    private List<PrintWriter> servers;

    private ServerF(int port)
    {

        allQueue=new ArrayList<>();
        allQueue.add(new QueueF(null,false));
        servers=new ArrayList<>();

        try {
            this.ss=connecServ(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.launch();

    }


    private ServerSocket connecServ(int port) throws IOException {

        ServerSocket ss;
        File adressList;

        if(System.getProperty("os.name").equals("WINDOWS"))
            adressList=new File(System.getProperty("user.dir")+"\\pairs.cfg");
        else
            adressList=new File(System.getProperty("user.dir")+"/pairs.cfg");

        filePath=adressList.getAbsolutePath();

        if(adressList.exists() && adressList.length()!=0)
        {

            ss = getFreePort(adressList);
            if(System.getProperty("os.name").equals("WINDOWS"))
                addServ("\r\npeer","localhost",ss.getLocalPort());
            else
                addServ("\npeer","localhost",ss.getLocalPort());

        }
        else
        {

            ss=new ServerSocket(port);
            addServ("peer","localhost",ss.getLocalPort());

        }

        return ss;

    }

    private ServerSocket getFreePort(File adressList) throws IOException {

        BufferedReader br;
        String all=new String(new BufferedInputStream(new FileInputStream(adressList)).readAllBytes());
        String temp;
        int port=12345;

        for(br=new BufferedReader(new InputStreamReader(new FileInputStream(adressList)));all.contains(String.valueOf(++port));port=Integer.parseInt(temp))
        {

            temp=br.readLine();
            temp=temp.substring(temp.lastIndexOf(' ')+1);

        }

        return new ServerSocket(port);

    }


    private void addServ(String name, String adress, int newPort)
    {

        BufferedOutputStream bos;
        String toWrite=name+" = "+adress+" "+newPort;


        try
        {

            bos=new BufferedOutputStream(new FileOutputStream(new File(filePath),true));
            bos.write(toWrite.getBytes());
            bos.close();

        } catch (IOException e)
        {

            System.out.println("this file does not exist");

        }

    }


    public static void main(String[] args)
    {

        ServerF st;

        if(args.length!=1)
            System.out.println("Erreur d'argument @port");
        else
        {

            st = new ServerF(Integer.parseInt(args[0]));
            //try {
                //st.ss.close();
            //} //catch (IOException e) {
                //e.printStackTrace();
            //}

        }




    }

    private void launch()
    {

        threading();

        //for(;allQueue!=null;);

    }

    private void threading()
    {

        new Thread(new ServerCommand(this)).start();
        new Thread(new AcceptConnection(this)).start();

    }

    public void addQueue(QueueF toAdd)
    {

        allQueue.add(toAdd);

    }

    public boolean isOnline()
    {

        return !(allQueue==null);

    }

    public void message(String toSend, Socket src,boolean serv)
    {

        for (QueueF queue : allQueue) queue.add(toSend, src,serv);
        System.out.println(toSend);

    }

    public void displayServerSide(String toDisp){System.out.println(toDisp);}

    /*public void addServer(PrintWriter servToAdd)
    {

        servers.add(servToAdd);

    }

    public void sendToServ(String msg) throws IOException {

        for (PrintWriter server : servers) {

            server.println(msg);

        }

    }*/

}
