package ClientSide;

import Constants.Constants;
import Data.Document;
import EdgeServer.MecHost;
import HTTP.HTTPResponseMetaData;
import Data.DistDataBase;
import Field.Point2D;
import Legacy.ServerManager;

import java.util.Random;
import java.util.UUID;

public class ClientApp {

    private int applicationId;
    private int clientId; //認証情報
    private Point2D location; //GPSによって取得
    private int homeServerId; //<application_id, server_id>
    private static final ManagementServiceForClient service = new ManagementServiceForClient();




    /**
     * 以下, シミュレーターのため（実機では必要ないメソッド群）
     * Must be called when creating client instance
     */

    public void initialize(){
        //set location
        initializeLocation();
        //set clientId
        service.registerToServer(this, location.getX(), location.getY());
        //set homeServerId
        service.getHomeServerId(this);
    }

    private void initializeLocation(){
        double areaLengthX = Constants.MAX_X - Constants.MIN_X;
        double areaLengthY = Constants.MAX_Y - Constants.MIN_Y;

        Random random = new Random();
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

        service.registerLocationToServer(this, location.getX(), location.getY());
    }

    private void updateHome(){
        //set homeServerId
        service.getHomeServerId(this);
    }

    /*
     Only metadata is enough for return value on simulation
     */
    public HTTPResponseMetaData GET(UUID documentId) {
        /** 暫定実装（最も近いサーバーからデータを取りに行く **/
        Document responseBody = this.homeServer.getCollection().get(documentId);

        double responseTime = 0.0;
        double transmissionCost = 0.0;

        if (responseBody == null) {
            /**
             * 暫定実装
             * need an algorithm (can be contribution)
             * documentbase : response = document.cachedServer.HTTPRequestForward()
             **/

            /* create meta data */
            responseTime += 0.1;
            transmissionCost += 0.1;

            return new HTTPResponseMetaData(responseTime, transmissionCost);
        } else {
            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }

    }

    public HTTPResponseMetaData POST(int applicationId) {
        /** home serverを取得**/
        int serverId = this.homeServer.get(applicationId);

        /*
          ※マルチスレッドの場合, requested serverに対する排他制御が必要
        */
        MecHost requestedServer = ServerManager.serverMap.get(serverId);

        Document document = new Document(, this.clientId);
        requestedServer.getCollection().put(uuid, document);
        //System.out.println("DEBUG" + " " + serverId + " " + requestedServer.getCollection().size());
        requestedServer.updateRemain(uuid);

        DistDataBase.ids.add(uuid);

        return new HTTPResponseMetaData(0,0);
    }


    @Override
    public String toString() {
        return String.format("clientId : %d\t\tlocation : (%6.2f, %6.2f)\t\tNS : %d", clientId, location.getX(), location.getY()
                ,this.homeServer.getId());
    }

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
            //System.out.println("location : " +  this.location + "  NSid : " + homeServer.getServerId() + "  NSloc : " + homeServer.getLocation());
        }
     */

}
