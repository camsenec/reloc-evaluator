package MetaServer;

import ClientSide.Client;
import Field.Point2D;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    public static final ConcurrentHashMap<Integer, Client> clientMap = new ConcurrentHashMap<>();

    private static int NUMBER_OF_CLIENTS = 100;

    /**
     * create clients on the field
     */
    public static void createClients(){

        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

        for(int clientId = 0; clientId <= NUMBER_OF_CLIENTS; clientId++) {
            Random random = new Random();
            double locationX = FieldManager.MIN_X + random.nextDouble() * areaLengthX;
            double locationY = FieldManager.MIN_Y + random.nextDouble() * areaLengthY;
            Client client = new Client(clientId, new Point2D(locationX, locationY));
            clientMap.put(clientId, client);
        }

    }

    public static void updateLocationOfAllClients(){
        for(int clientId : clientMap.keySet()){
            clientMap.get(clientId).updateLocation();
        }
    }

    public static void updateNearestServerOfAllClients(){
        for(int clientId : clientMap.keySet()){
            clientMap.get(clientId).updateNearestServer();
        }
    }

    public static void setNumberOfClients(int numberOfClients) {
        NUMBER_OF_CLIENTS = numberOfClients;
    }
}
