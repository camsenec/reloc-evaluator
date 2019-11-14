package Model;

public class ClientModel {

    int applicationId;
    int clientId;
    float x;
    float y;
    int home;

    public ClientModel(int application_id, int clientId, float x, float y, int home) {
        this.applicationId = application_id;
        this.clientId = clientId;
        this.x = x;
        this.y = y;
        this.home = home;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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

    public int getHome() {
        return home;
    }

    public void setHome(int home) {
        this.home = home;
    }
}
