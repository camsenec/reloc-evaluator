package SimulationHelper;

import ClientSide.ClientApp;
import Constants.Constants;
import Field.Point2D;

import java.util.Random;

public class Helper {

    private static int NUMBER_OF_CLIENTS = 100;

    /**
     * create clients on the field
     */
    public static void createClients(){


            ClientApp client = new ClientApp(new Point2D(locationX, locationY));
        }
    }

    public static void updateLocationOfAllClients(){
        for(int clientId : clientMap.keySet()){
            clientMap.get(clientId).updateLocation();
        }
    }

    public static void setNumberOfClients(int numberOfClients) {
        NUMBER_OF_CLIENTS = numberOfClients;
    }
}
