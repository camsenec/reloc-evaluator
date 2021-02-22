package MP;

import java.util.HashMap;

import ClientSide.ClientApp;
import Data.Document;

public class MessageProcessor {
    private HashMap<Integer, Document> docMap = new HashMap<>();
    private HashMap<Integer, ClientApp> clientMap = new HashMap<>();

    public MessageProcessor(){};

    public MessageProcessor(HashMap<Integer, Document> docMap, HashMap<Integer, ClientApp> clientMap) {
      this.docMap = docMap;
      this.clientMap = clientMap;
    }

    public HashMap<Integer, Document> getDocMap() {
      return docMap;
    }

    public void setDocMap(HashMap<Integer, Document> docMap) {
      this.docMap = docMap;
    }

    public HashMap<Integer, ClientApp> getClientMap() {
      return clientMap;
    }

    public void setClientMap(HashMap<Integer, ClientApp> clientMap) {
      this.clientMap = clientMap;
    }

    
}
