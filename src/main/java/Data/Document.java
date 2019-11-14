package Data;

import java.util.ArrayList;
import java.util.UUID;

public class Document {
    /** METADATA **/
    private int applicationId;
    private UUID documentId;
    private ArrayList<Integer> cachedServer = new ArrayList<>();
    private int size = 1;

    public Document(int applicationId, UUID documentId) {
        this.applicationId = applicationId;
        this.documentId = documentId;
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
