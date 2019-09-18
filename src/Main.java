import CentralServer.FieldManager;
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
        int capacity = Integer.parseInt(args[1]);

        /* construct edge server on the field */
        FieldManager.createServers(numberOfServers, capacity);

        if(Constants.DEBUG) {
            for (EdgeServer server : FieldManager.serverList) {
                System.out.println(server);
            }
        }

        /* create clients */


        /* simulation */
        for(int t = 0; t < MAX_T; t++){


        }













    }
}
