package Model;

public class EdgeServerModel {
    int applicationId;
    int serverId;
    float x;
    float y;
    float capacity;
    float remain;
    int clusterId;

    public EdgeServerModel(int applicationId, int serverId, float x, float y, float capacity, float remain, int clusterId) {
        this.applicationId = applicationId;
        this.serverId = serverId;
        this.x = x;
        this.y = y;
        this.capacity = capacity;
        this.remain = remain;
        this.clusterId = clusterId;
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

    public float getRemain() {
        return remain;
    }

    public void setRemain(float remain) {
        this.remain = remain;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }
}
