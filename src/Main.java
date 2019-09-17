import CentralServer.FieldManager;
import EdgeServer.EdgeServer;

public class Main {
    public static void main(String args[]){
        if(args.length < 3){
            System.err.println("You need to set commandline argument");
        }

        int numberOfServer = Integer.parseInt(args[0]);

        /* Initialize */
        for(int i = 0; i < numberOfServer; i++){
            EdgeServer edgeServer = new EdgeServer(i, 100);
            FieldManager.serverList.add(edgeServer);
        }









    }
}
