import FileIO.FileDownloader;
import FileIO.FileFactory;

public class test {

    public static void main(String[] args) {

        FileDownloader.downlaodLogFile("http://localhost:8000/simulation/out/txLog.csv");
        FileFactory.loadLogFile("txLog.csv");
    }
}
