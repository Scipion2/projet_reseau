package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientStress extends Client {
    String pseudo;
    public ClientStress(String adr, int port) throws IOException {
        super(adr, port);
    }

    void launch(String pseudo) throws IOException
    {

        Socket client=this.connect();

        PrintWriter out = new PrintWriter(client.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));

        out.println("CONNECT "+ pseudo);

    }


    public static void main(String[] args){

        if(args.length!=3){
            System.out.println("Erreur d'argument: @adr @port @nobmre de client à connecter sur le serveur");
            return;
        }

        for(int i=0;i<Integer.parseInt(args[2]);i++){

            try {
                ClientStress c=new ClientStress(args[0],Integer.parseInt(args[1]));
                c.pseudo="ClientStress"+i;
                c.launch(c.pseudo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while(true){

        }
    }
}
