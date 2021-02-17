package ClientSide;

import Constants.Constants;
import Data.Document;
import EdgeServer.MecHost;
import Field.Point2D;
import MetaServer.HostResolver;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

public class ClientApp {

    private int applicationId;
    private int clientId; //認証情報
    private Point2D location = new Point2D(); //GPSによって取得
    private int homeServerId; //<application_id, server_id>
    private static final ManagementServiceForClient service = new ManagementServiceForClient();

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    private int weight = 0;


    public ClientApp(int applicationId, int clientId) {
        this.applicationId = applicationId;
        this.clientId = clientId;
    }

    /**
     * 以下, シミュレーターのため（実機では必要ないメソッド群）
     * Must be called when creating client instance
     */

    public void addWeight(int added){
        this.weight += added;
    }

    public void initialize(){
        //set location
        initializeLocation();
        //register to server
        service.registerToServerWithId(this);
    }

    public void initialize_loc(double locationX, double locationY){
        this.location.setX(locationX);
        this.location.setY(locationY);
        //register to server
        service.registerToServerWithId(this);
    }

    public void relocate(){
        service.getHomeServerId(this);
    }

    private void initializeLocation(){
        //Math.random()
        Random random = new Random();
        double areaLengthX = Constants.MAX_X - Constants.MIN_X;
        double areaLengthY = Constants.MAX_Y - Constants.MIN_Y;

        double locationX = Constants.MIN_X + random.nextDouble() * areaLengthX;
        double locationY = Constants.MIN_Y + random.nextDouble() * areaLengthY;
        this.location.setX(locationX);
        this.location.setY(locationY);
    }

    public void update(){
        updateLocation();
        updateHome();
    }

    private void updateLocation() {
        Random random = new Random();
        double areaLengthX = Constants.MAX_X - Constants.MIN_X;
        double areaLengthY = Constants.MAX_Y - Constants.MIN_Y;

        this.location.setX((location.getX()
                + random.nextGaussian()) % areaLengthX + Constants.MIN_X);
        this.location.setY((location.getY()
                + random.nextGaussian()) % areaLengthY + Constants.MIN_Y);

        if(location.getX() < 0) this.location.setX(location.getX() + areaLengthX);
        if(location.getY() < 0) this.location.setY(location.getY() + areaLengthY);

        service.registerLocationToServer(this);
    }

    private void updateHome(){
        //set homeServerId
        service.getHomeServerId(this);
    }


    /**
     * Documentのcachedフィールドに値を設定する
     */

    /*
    public Document createDocument(ArrayList<Integer> cached){
        UUID uuid = UUID.randomUUID();
        Document document = new Document(applicationId);
        document.setCachedServer(cached);
        return document;

    }
    */


    /**
     * cached serverにdocumentをputする
     */

    /*
    public void post(ArrayList<Integer> cached) {
        Document document = createDocument(cached);
        MecHost dest = HostResolver.hosts.get(applicationId).get(homeServerId);
        UUID uuid = document.getDocumentId();
        dest.getCollection().put(uuid, document);

        for(int serverId : cached){
            dest = HostResolver.hosts.get(applicationId).get(serverId);
            dest.getCollection().put(uuid, document);
        }
    }
    */


    public int getApplicationId() {
        return applicationId;
    }

    public int getClientId() {
        return clientId;
    }

    public Point2D getLocation() {
        return location;
    }

    public int getHomeServerId() {
        return homeServerId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }


    public void setClientId(int clientId) {
        this.clientId = clientId;
    }


    public void setLocation(Point2D location) {
        this.location = location;
    }

    public void setHomeServerId(int homeServerId) {
        this.homeServerId = homeServerId;
    }

    /*
        Legacy
        public void updateNearestServer(int application_id) {
            int clientId = ServerManager.findNearestServer(this.location);
            this.homeServer.put(application_id, ServerManager.serverMap.get(clientId);
            //System.out.println("location : " +  this.location + "  NSid : " + homeServer.getServer_id() + "  NSloc : " + homeServer.getLocation());
        }
     */

    /*
     Only metadata is enough for return value on simulation
     */

    /*
    public void GET(int documentId) {
        MecHost src = HostResolver.hosts.get(applicationId).get(homeServerId);
        Document response = src.getCollection().get(documentId);

        double responseTime = 0.0;
        double transmissionCost = 0.0;

        if (responseBody == null) {
            responseTime += 0.1;
            transmissionCost += 0.1;

            return new HTTPResponseMetaData(responseTime, transmissionCost);
        } else {
            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }
    }
    */

}
