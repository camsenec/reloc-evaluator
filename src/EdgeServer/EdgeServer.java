package EdgeServer;

import Data.Document;
import Field.Point2D;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class EdgeServer {
    private int id;
    private Point2D location;
    /*とりあえず3段階 1.弱い, 2. 普通, 3. 強い*/
    private int performanceLevel = 2;

    private int capacity;
    private int remain;
    private HashMap<Integer, Document> collection = new HashMap<>();

    public EdgeServer(int id, int capacity, Point2D location){
        this.id = id;
        this.location = location;
        this.capacity = capacity;
        this.remain = capacity;
    }

    public void HTTPRequestForward(){
        for(int i = 0; i < sameGroupServer.size(); i++){


        }
    }

    public Point2D getLocation() {
        return location;
    }

    public void setLocation(Point2D location) {
        this.location = location;
    }

    public HashMap<Integer, Document> getCollection() {
        return collection;
    }

    @Override
    public String toString() {
        return String.format("id : %d\t\tlocation : (%6.2f, %6.2f)", id, location.getX(), location.getY());
    }
}
