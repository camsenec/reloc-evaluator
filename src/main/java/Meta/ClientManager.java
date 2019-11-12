package Meta;

import ClientSide.Client;
import Field.Point2D;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    //クライアント管理
    public static final ConcurrentHashMap<Integer, Client> clientMap = new ConcurrentHashMap<>();

    private static int NUMBER_OF_CLIENTS = 100;

    /**
     * create clients on the field
     */
    public static void createClients(){

        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

        for(int i = 0; i < NUMBER_OF_CLIENTS; i++) {
            Random random = new Random();
            double locationX = FieldManager.MIN_X + random.nextDouble() * areaLengthX;
            double locationY = FieldManager.MIN_Y + random.nextDouble() * areaLengthY;
            Client client = new Client(new Point2D(locationX, locationY));
            clientMap.put(i, client);
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

    public static void printAllClients(){
        for (int clientId : ClientManager.clientMap.keySet()) {
            System.out.println(clientMap.get(clientId));
        }
    }

    public static void setNumberOfClients(int numberOfClients) {
        NUMBER_OF_CLIENTS = numberOfClients;
    }


}
