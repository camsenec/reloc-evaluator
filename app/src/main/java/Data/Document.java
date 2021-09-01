package Data;


public class Document {
    /** METADATA **/
    private int applicationId;
    private int documentId;
    private double size;

    /*
      private ArrayList<Integer> cachedServer = new ArrayList<>();
    */

    public Document(int applicationId, int documentId) {
        this.applicationId = applicationId;
        this.documentId = documentId;
    }

    public void initialize(double docSize){
        this.size = docSize;
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

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }
}
