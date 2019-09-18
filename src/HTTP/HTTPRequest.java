package HTTP;

import CentralServer.FieldManager;
import Data.Document;
import EdgeServer.EdgeServer;

public class HTTPRequest {

    /*
     Only metadata is enough for return value on simulation
     */
    public static HTTPResponseMetaData GET(int serverId, int documentId){
        EdgeServer requestedServer = FieldManager.serverList.get(serverId);
        Document responseBody = requestedServer.getCollection().get(documentId);
        double responseTime = 0.0;
        double transmissionCost = 0.0;

        if(responseBody == null) {
            /**
             * need an algorithm (can be contribution)
             * documentbase : response = document.cachedServer.HTTPRequestForward()
             **/

            responseTime += 0.1;
            transmissionCost += 0.1;

            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }else{
            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }

    }

    public static void POST(EdgeServer nearestServer){

    }
}
