import FileIO.FileDownloader;
import FileIO.FileFactory;

public class test {

    public static void main(String[] args) {

        FileDownloader.downlaodLogFile("http://localhost:8000/simulation/out/tx_log.csv");
        FileFactory.readLogFile();
    }
}
