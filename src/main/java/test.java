import ClientSide.Client;
import Constants.Constants;
import Meta.ClientManager;

public class test {

    public static void main(String[] args) {

        int numberOfClients = 1;

        ClientManager.setNumberOfClients(numberOfClients);
        ClientManager.createClients();
        ClientManager.updateLocationOfAllClients();
        ClientManager.updateNearestServerOfAllClients();


        Client client = ClientManager.clientMap.get(0);
        System.out.println(client);
        if (Constants.DEBUG) {
            System.out.println("-----------Clients Created-----------");
            ClientManager.printAllClients();
        }
    }
}
