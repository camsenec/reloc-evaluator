package EdgeServer;

import Data.Document;
import Field.Point2D;
import Utility.Range;

import java.util.HashMap;

public class EdgeServer {
    private int id;
    private Point2D location;
    private Range sameGroupServers;

    /*とりあえず3段階 1.弱い, 2. 普通, 3. 強い*/
    private int performanceLevel = 2;
    private int capacity;
    private int remain;

    /* {key : value}  = { id : Document } */
    private final static HashMap<Integer, Document> collection = new HashMap<>();


    public EdgeServer(int id, int capacity, Point2D location){
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.remain = capacity;
    }

    public void HTTPRequestForward(){
    }

    public int getId() {
        return id;
    }

    public Point2D getLocation() {
        return location;
    }

    public HashMap<Integer, Document> getCollection() {
        return collection;
    }

    public void setSameGroupServers(Range sameGroupServers) {
        this.sameGroupServers = sameGroupServers;
    }

    @Override
    public String toString() {
        return String.format("id : %d\t\tlocation : (%6.2f, %6.2f)", id, location.getX(), location.getY());
    }
}
