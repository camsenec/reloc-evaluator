package ClientSide;

import CentralServer.FieldManager;
import EdgeServer.EdgeServer;
import Field.Point2D;

import java.util.PriorityQueue;

public class Client {
    Point2D location;
    EdgeServer nearestServer;

    private void move(){

    }

    public void update(){

    }

    public void updateNearestServer(){
        int id = FieldManager.findNearestServer(this.location);
        this.nearestServer = FieldManager.serverList.get(id);
    }


}
