package EdgeServer;

import ClientSide.ManagementServiceForClient;
import Constants.Constants;
import Data.Document;
import Field.Point2D;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

/*
    サーバー上で動くサーバプログラムを想定
    サーバープログラムに組み込まなければならないものは,
    残り容量（もしくは差分）を取得することと,
    Managementサーバーに送信すること
    これは, ディベロッパーに任せる

    残り容量の取得は基本的にどのプログラミング言語でも可能であろうし,
    Managementサーバーに送信することも容易

    'サーバーレス'とかであれば, そのプラットフォームの提供者が組み込むことを想定
    なんにしろ, サーバーマシンの保有者が利用することを想定
 */

public class MecHost {
    private int applicationId;
    private int serverId;
    private Point2D location = new Point2D();
    private int remain;
    private int capacity;
    private HashMap<Integer, Document> collection = new HashMap<>();

    private static final ManagementServiceForServer service = new ManagementServiceForServer();

    /**
     * Default Constructor
     */
    public MecHost(int applicationId){
        this.applicationId = applicationId;
    }

    public void initialize(int capacity){
        //set location
        initializeLocation();
        //set capacity
        this.capacity = capacity;
        //set serverId
        service.registerToServer(this);

    }

    private void initializeLocation(){
        double areaLengthX = Constants.MAX_X - Constants.MIN_X;
        double areaLengthY = Constants.MAX_Y - Constants.MIN_Y;

        Random random = new Random();
        double locationX = Constants.MIN_X + random.nextDouble() * areaLengthX;
        double locationY = Constants.MIN_Y + random.nextDouble() * areaLengthY;
        this.location.setX(locationX);
        this.location.setY(locationY);
    }

    public void update(){
        updateRemain();
    }

    public void updateState(int sizeOfDoc){
        this.remain+=sizeOfDoc;
        this.capacity-=sizeOfDoc;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getRemain() {
        return remain;
    }

    public void setRemain(int remain) {
        this.remain = remain;
    }

    public Point2D getLocation() {
        return location;
    }

    public int getCapacity() {
        return capacity;
    }

    public ConcurrentHashMap<UUID, Document> getCollection() {
        return collection;
    }

    @Override
    public String toString() {
        return String.format("serverId : %d\t\tlocation : (%6.2f, %6.2f)", serverId);
    }
}
