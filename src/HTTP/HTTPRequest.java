package HTTP;

import CentralServer.FieldManager;
import Data.Document;
import EdgeServer.EdgeServer;

public class HTTPRequest {
    public static HTTPResponse GET(int serverId, int documentId){
        EdgeServer requestedServer = FieldManager.serverList.get(serverId);
        Document HTTPResponse = requestedServer.getCollection().get(documentId);

        if(response == null) {
            /**
             * need an algorithm (can be contribution)
             **/
            //response = document.cachedServer.HTTPRequestForward()
            //group内
            return response;
        }else{
            return response;
        }

    }

    public static void POST(EdgeServer nearestServer){

    }
}
