package main;


import client.Client;

import java.io.IOException;

public class app {

    public static void main(String[] args){

        if(args.length!=2){
            System.out.println("Erreur d'arguments: @adr @port");
            return;
        }

        try {
            Client c = new Client(args[0], Integer.parseInt(args[1]));

        }catch (IOException e){
            e.printStackTrace();
        }

    }


}
