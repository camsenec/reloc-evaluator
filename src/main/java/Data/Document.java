package Data;

import java.util.ArrayList;
import java.util.UUID;

public class Document {
    /** METADATA **/
    private int applicationId;
    private UUID documentId;
    private int size;

    /*
      private ArrayList<Integer> cachedServer = new ArrayList<>();
    */

    public Document(int applicationId, int documentId) {
        this.applicationId = applicationId;
        this.documentId = documentId;
    }

    public void initialize(int docSize){
        this.size = 100;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public UUID getDocumentId() {
        return documentId;
    }

    public void setDocumentId(UUID documentId) {
        this.documentId = documentId;
    }

    public ArrayList<Integer> getCachedServer() {
        return cachedServer;
    }

    public void setCachedServer(ArrayList<Integer> cachedServer) {
        this.cachedServer = cachedServer;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
