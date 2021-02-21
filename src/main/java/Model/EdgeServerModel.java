package Model;

public class EdgeServerModel {
    private int application_id;
    private int serverId;
    private float x;
    private float y;
    private float capacity;
    private float used;
    private int clusterId;

    public EdgeServerModel(int application_id, int serverId, float x, float y, float capacity, float used, int cluster_id) {
        this.application_id = application_id;
        this.serverId = serverId;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.used = used;
        this.clusterId = cluster_id;
    }

    public int getApplication_id() {
        return application_id;
    }

    public void setApplication_id(int application_id) {
        this.application_id = application_id;
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int server_id) {
        this.serverId = server_id;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getCapacity() {
        return capacity;
    }

    public void setCapacity(float capacity) {
        this.capacity = capacity;
    }

    public float getUsed() {
        return used;
    }

    public void setUsed(float used) {
        this.used = used;
    }

    public int getCluster_id() {
        return clusterId;
    }

    public void setCluster_id(int cluster_id) {
        this.clusterId = cluster_id;
    }

    @Override
    public String toString() {
        return "EdgeServerModel{" +
                "application_id=" + application_id +
                ", server_id=" + serverId +
                ", x=" + x +
                ", y=" + y +
                ", capacity=" + capacity +
                ", used=" + used +
                ", cluster_id=" + clusterId +
                '}';
    }
}
