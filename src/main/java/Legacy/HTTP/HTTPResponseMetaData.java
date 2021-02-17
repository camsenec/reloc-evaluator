package Legacy.HTTP;

public class HTTPResponseMetaData {
    private double responseTime;
    private double transmissionCost;

    public HTTPResponseMetaData(double responseTime, double transmissionCost) {
        this.responseTime = responseTime;
        this.transmissionCost = transmissionCost;
    }

    public String toLogFile() {
        return String.format("%10.2f\t%10.2f", responseTime, transmissionCost);
    }
}
