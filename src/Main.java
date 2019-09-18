import MetaServer.FieldManager;
import Constants.Constants;
import EdgeServer.EdgeServer;

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
        FieldManager.setGrainLevel(numberOfServers);
        FieldManager.createServers(capacityOfServers);

        if(Constants.DEBUG) {
            for (EdgeServer server : FieldManager.serverList) {
                System.out.println(server);
            }
        }

        /* create clients */
        for(int i = 0; i < numberOfClients; i++){

        }



        /* simulation */
        for(int t = 0; t < MAX_T; t++){


        }













    }
}
