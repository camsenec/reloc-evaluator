package ClientSide;

import MetaServer.FieldManager;
import EdgeServer.EdgeServer;
import Field.Point2D;
import MetaServer.ServerManager;

import java.util.Random;

public class Client {
    int id;
    Point2D location;
    EdgeServer nearestServer;

    public Client(int id, Point2D location) {
        this.id = id;
        this.location = location;
    }

    //暫定実装
    //client can move following gaussian distribution
    public void updateLocation(){
        Random random = new Random();
        this.location.setX(this.location.getX()
                + random.nextGaussian() % (FieldManager.MAX_X - FieldManager.MIN_X) + FieldManager.MIN_X);
        this.location.setY(this.location.getY()
                + random.nextGaussian() % (FieldManager.MAX_Y - FieldManager.MIN_Y) + FieldManager.MIN_Y);

    }

    //run at regular intervals
    public void updateNearestServer(){
        int id = ServerManager.findNearestServer(this.location);
        this.nearestServer = ServerManager.serverMap.get(id);
    }


    //serverに対してHTTPRequestをかける
    public void HTTPRequest(){

    }


}
