package FileIO;

import java.net.HttpURLConnection;
import java.net.URL;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public class FileDownloader {

    public static void downlaodLogFile(String src){

        try {

            URL url = new URL(src);
            System.out.println(src);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();
            conn.setAllowUserInteraction(false);
            conn.setInstanceFollowRedirects(true);
            conn.setRequestMethod("GET");
            conn.connect();

            int httpStatusCode = conn.getResponseCode();

            if(httpStatusCode != HttpURLConnection.HTTP_OK){
                throw new Exception();
            }

            // Input Stream
            DataInputStream dataInStream
                    = new DataInputStream(
                    conn.getInputStream());

            // Output Stream
            DataOutputStream dataOutStream
                    = new DataOutputStream(
                        new BufferedOutputStream(
                            new FileOutputStream("./Log/tx_log.csv")));

            // Read Data
            byte[] b = new byte[4096];
            int readByte = 0;

            while(-1 != (readByte = dataInStream.read(b))){
                dataOutStream.write(b, 0, readByte);
            }

            // Close Stream
            dataInStream.close();
            dataOutStream.close();

        } catch (FileNotFoundException | ProtocolException | MalformedURLException e) {
            e.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

}