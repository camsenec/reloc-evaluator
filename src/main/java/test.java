import ClientSide.ClientApp;
import Constants.Constants;
import Legacy.ClientManager;

public class test {

    public static void main(String[] args) {

        int numberOfClients = 1;

        ClientManager.setNumberOfClients(numberOfClients);
        ClientManager.createClients();
        ClientManager.updateLocationOfAllClients();
        ClientManager.updateNearestServerOfAllClients();


        ClientApp client = ClientManager.clientMap.get(0);
        System.out.println(client);
        if (Constants.DEBUG) {
            System.out.println("-----------Clients Created-----------");
            ClientManager.printAllClients();
        }
    }
}
