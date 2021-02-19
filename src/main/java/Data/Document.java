package Data;

import Config.Config;


public class Document {
    /** METADATA **/
    private int applicationId;
    private int documentId;
    private int size;

    /*
      private ArrayList<Integer> cachedServer = new ArrayList<>();
    */

    public Document(int applicationId, int documentId) {
        this.applicationId = applicationId;
        this.documentId = documentId;
    }

    public void initialize(int docSize){
        this.size = Config.sizeOfDocs;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
