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
    public void updateLocation(){
        Random random = new Random();
        this.location.setX(this.location.getX()
                + random.nextGaussian() % (FieldManager.MAX_X - FieldManager.MIN_X) + FieldManager.MIN_X);
        this.location.setY(this.location.getY()
                + random.nextGaussian() % (FieldManager.MAX_Y - FieldManager.MIN_Y) + FieldManager.MIN_Y);

    }

    //run at regular intervals
    public void updateNearestServer(){
        int id = ServerManager.findNearestServer(this.location);
        this.nearestServer = ServerManager.serverMap.get(id);
    }

    /*
     Only metadata is enough for return value on simulation
     */
    public HTTPResponseMetaData GET(UUID documentId){
        /** 暫定実装 **/
        int serverId = this.nearestServer.getId();
        EdgeServer requestedServer = ServerManager.serverMap.get(serverId);
        Document responseBody = requestedServer.getCollection().get(documentId);
        double responseTime = 0.0;
        double transmissionCost = 0.0;

        if(responseBody == null) {
            /**
             * need an algorithm (can be contribution)
             * documentbase : response = document.cachedServer.HTTPRequestForward()
             **/
            /** 暫定実装 **/

            /* create meta data */
            responseTime += 0.1;
            transmissionCost += 0.1;

            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }else{
            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }

    }

    public HTTPResponseMetaData POST(){
        double responseTime = 0.0;
        double transmissionCost = 0.0;

        /** 暫定実装 **/
        int serverId = this.nearestServer.getId();

        /** マルチスレッドの場合, requested serverに対する排他制御が必要 */

        EdgeServer requestedServer = ServerManager.serverMap.get(serverId);
        UUID uuid = UUID.randomUUID();
        Document document = new Document(uuid, this.id);
        requestedServer.getCollection().put(uuid, document);
        requestedServer.updateRemain(uuid);

        DocumentIds.ids.add(uuid);


        /* create metadata */
        responseTime += 0.1;
        transmissionCost += 0.1;

        return new HTTPResponseMetaData(responseTime, transmissionCost);
    }


}
