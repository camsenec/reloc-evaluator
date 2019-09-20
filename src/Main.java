import MetaServer.ClientManager;
import MetaServer.FieldManager;
import Constants.Constants;
import EdgeServer.EdgeServer;
import MetaServer.ServerManager;

import java.util.HashMap;

import static java.lang.Math.log;

public class Main {
    public static int MAX_T = 10000;

    public static void main(String args[]){
        if(args.length < 3){
            System.err.println("You need to set commandline argument");
        }

        /* read command line argument */
        int numberOfServers = Integer.parseInt(args[0]);
        int capacityOfServers = Integer.parseInt(args[1]);
        int numberOfClients = Integer.parseInt(args[2]);

        /* construct edge server on the field */
        ServerManager.setnumberOfServers(numberOfServers);
        ServerManager.createServers(capacityOfServers);

        if(Constants.DEBUG) {
            HashMap<Integer, EdgeServer> serverMap = ServerManager.serverMap;
            for (int serverId : ServerManager.serverMap.keySet()) {
                System.out.println(serverMap.get(serverId));
            }
        }

        /* create clients */
        ClientManager.setNumberOfClients(numberOfClients);
        ClientManager.createClients();

        /* simulation */
        for(int t = 0; t < MAX_T; t++){


        }













    }
}
