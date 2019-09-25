package HTTP;

import MetaServer.FieldManager;
import Data.Document;
import EdgeServer.EdgeServer;
import MetaServer.ServerManager;

import java.util.UUID;

public class HTTPRequest {

    /*
     Only metadata is enough for return value on simulation
     */
    public static HTTPResponseMetaData GET(int serverId, int documentId){
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


            responseTime += 0.1;
            transmissionCost += 0.1;

            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }else{
            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }

    }

    public static void POST(int serverId){
        EdgeServer requestedServer = ServerManager.serverMap.get(serverId);
        UUID uuid = UUID.randomUUID();
        Document document = new Document(uuid, )
        requestedServer.getCollection().put(document)

    }
}
