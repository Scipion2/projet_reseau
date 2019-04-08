package threads;

import multi_serv.ServerF;

import java.io.File;
import java.util.Scanner;

public class ServerCommand implements Runnable
{

    private ServerF st;

    public ServerCommand(ServerF st)
    {

        this.st=st;

    }

    @Override
    public void run()
    {

        String command="";

        for(Scanner sc = new Scanner(System.in);!command.equals("exit"); command=sc.nextLine())
        {

            switch(command)
            {

                case "port":
                    st.displayServerSide("this server has "+st.ss.getLocalPort()+" for port number");
                    break;

                case "adress":
                    st.displayServerSide("this server has "+st.ss.getInetAddress().getHostAddress()+" for adress");
                    break;

                case "ss state":
                    if(st.ss==null)
                        st.displayServerSide("ss is closed");
                    else st.displayServerSide("ss is on");
                    break;

                case "serverconnect":
                    new Thread(new ConnecToServ(st)).start();
                    break;

            }

        }

        new File(st.filePath).deleteOnExit();
        closeThreads();

    }

    private void closeThreads()
    {

        Thread[] list=new Thread[Thread.activeCount()];
        for(int count=Thread.enumerate(list);count>0;count--)
        {

            st.displayServerSide("le nom du Thread n°"+count+" est :");
            st.displayServerSide(list[count-1].getName());

        }

    }

}
