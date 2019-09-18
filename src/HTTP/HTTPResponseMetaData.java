package HTTP;

public class HTTPResponseMetaData {
    private double responseTime;
    private double transmissionCost;

    public HTTPResponseMetaData(double responseTime, double transmissionCost) {
        this.responseTime = responseTime;
        this.transmissionCost = transmissionCost;
    }
}
