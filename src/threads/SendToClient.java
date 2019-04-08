package threads;

import multi_serv.QueueF;

import java.io.IOException;

public class SendToClient implements Runnable
{

    private QueueF messageList;

    SendToClient(QueueF messageListsrc)
    {

        messageList=messageListsrc;

    }

    @Override
    public void run()
    {

        try{
            for(;messageList!=null;messageList.send());
        }catch(IOException e)
        {

            e.printStackTrace();

        }

    }

}
