package Data;

import java.util.ArrayList;
import java.util.UUID;

public class Document {
    /** METADATA **/
    private int applicationId;
    private int documentId;
    private ArrayList<Integer> cachedServer = new ArrayList<>();
    private int size = 1;

    public Document(int applicationId, int documentId) {
        this.applicationId = applicationId;
        this.documentId = documentId;
    }


}
