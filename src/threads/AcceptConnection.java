package threads;

import multi_serv.ServerF;

import java.io.IOException;

public class AcceptConnection implements Runnable
{

    ServerF st;

    public AcceptConnection(ServerF st)
    {

        this.st=st;
        st.displayServerSide("ready to accept connection");

    }

    @Override
    public void run()
    {

        try {
            for(st.ss.setReuseAddress(true);st.ss!=null;new Thread(new Message(st.ss.accept(),st)).start()) {}
        } catch (IOException e) {
            e.printStackTrace();
            st.displayServerSide("socket was not accepted");
        }

    }

}
