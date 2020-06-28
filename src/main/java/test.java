import ClientSide.ManagementServiceForClient;
import FileIO.FileDownloader;
import FileIO.FileFactory;

public class test {

    public static void main(String[] args) {

        ManagementServiceForClient service = new ManagementServiceForClient();
        service.update_number_of_coopserver(20);
    }
}
