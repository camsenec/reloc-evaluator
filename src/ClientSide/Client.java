package ClientSide;

import Data.Document;
import HTTP.HTTPResponseMetaData;
import MetaServer.DocumentIds;
import MetaServer.FieldManager;
import EdgeServer.EdgeServer;
import Field.Point2D;
import MetaServer.ServerManager;

import java.util.Random;
import java.util.UUID;

public class Client {
    int id;
    Point2D location;
    EdgeServer nearestServer;

    public Client(int id, Point2D location) {
        this.id = id;
        this.location = location;
    }

    //暫定実装
    //client can move following gaussian distribution
    public void updateLocation() {
        Random random = new Random();
        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

        this.location.setX((location.getX()
                + random.nextGaussian()) % areaLengthX + FieldManager.MIN_X);
        this.location.setY((location.getY()
                + random.nextGaussian()) % areaLengthY + FieldManager.MIN_Y);

        if(location.getX() < 0) this.location.setX(location.getX() + areaLengthX);
        if(location.getY() < 0) this.location.setY(location.getY() + areaLengthY);


    }

    //run at regular intervals
    public void updateNearestServer() {
        int id = ServerManager.findNearestServer(this.location);
        this.nearestServer = ServerManager.serverMap.get(id);
        System.out.println("location : " +  this.location + "  NSid : " + nearestServer.getId() + "  NSloc : " + nearestServer.getLocation());
    }

    /*
     Only metadata is enough for return value on simulation
     */
    public HTTPResponseMetaData GET(UUID documentId) {
        /** 暫定実装（最も近いサーバーからデータを取りに行く **/
        Document responseBody = this.nearestServer.getCollection().get(documentId);

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

    public HTTPResponseMetaData POST() {
        /** 暫定実装 **/
        int serverId = this.nearestServer.getId();

        /** マルチスレッドの場合, requested serverに対する排他制御が必要 */
        EdgeServer requestedServer = ServerManager.serverMap.get(serverId);
        UUID uuid = UUID.randomUUID();
        Document document = new Document(uuid, this.id);
        requestedServer.getCollection().put(uuid, document);
        //System.out.println("DEBUG" + " " + serverId + " " + requestedServer.getCollection().size());
        requestedServer.updateRemain(uuid);

        DocumentIds.ids.add(uuid);

        return new HTTPResponseMetaData(0,0);
    }


    @Override
    public String toString() {
        return String.format("id : %d\t\tlocation : (%6.2f, %6.2f)\t\tNS : %d", id, location.getX(), location.getY()
                ,this.nearestServer.getId());
    }

    public EdgeServer getNearestServer() {
        return nearestServer;
    }
}
