package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static java.lang.System.exit;

public class Server
{

    String serv;
    int port;
    List


    public Server(String serv, int port) throws UnknownHostException {

        this.serv = serv;
        this.port = port;

    }

    public void sendPack() throws IOException {

        Scanner sc = new Scanner(System.in);
        ServerSocket ss=new ServerSocket();
        Socket socket = new Socket(serv, port);
        String msg = null;
        String servMsg;
        BufferedReader in;


        ss.accept();

       for(;;)
       {



       }


    }


}
