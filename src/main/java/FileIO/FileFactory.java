package FileIO;
import Logger.TxLog;

import java.io.*;
import java.util.ArrayList;

import static Constants.Constants.DEBUG;

public class FileFactory {

    private static double EPS = 1e-5;

    /**
     * fileName : データの読み書きに用いるファイル
     * customView : モデルを参照する先のビュー
     */

    /**
     * read from local file
     */
    public static void readLogFile(){

        /*---------read from local file---------*/
        try (BufferedReader reader = new BufferedReader(new FileReader("./Log/txLog.csv"));){
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.replace("\"[", "");
                line = line.replace("]\"", "");
                line = line.replace(" ", "");
                String[] data = line.split(",", -1);

                ArrayList<Integer> sendTo = new ArrayList<>();
                int client_id = Integer.parseInt(data[1]);
                for (int i = 2; i < data.length; i++) {
                    sendTo.add(Integer.parseInt(data[i]));
                }
                TxLog.txLog.put(client_id, sendTo);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(DEBUG) {
            for (Integer key : TxLog.txLog.keySet()) {
                ArrayList tmp = TxLog.txLog.get(key);
                System.out.print(key + ":");
                for (Object sendto : tmp) {
                    System.out.print(sendto);
                    System.out.print(" ");

                }
                System.out.print("\n");
            }
            System.out.println(TxLog.txLog.size());
        }

    }

    /**
     * write to local file
     */

    /*
    public static void writer(Context context, ArrayList<MyTriangle> triangles){

        try {

            System.out.println(context.getFilesDir().getPath());
            FileOutputStream fileStream = context.openFileOutput(
                    fileNameOutput, Context.MODE_PRIVATE);
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(fileStream,"UTF-8"));

            float JCoupling;

            for(MyTriangle triangleI : triangles){
                for(MyTriangle triangleJ : triangles){
                    if(triangleI.getNextTriangles().contains(triangleJ)) {
                        JCoupling = -1;
                    }else {
                        JCoupling = 0;
                    }

                    if(!triangleI.equals(triangleJ)) {

                        pw.write(String.format(Locale.US,
                                "%d,%d,%d,%d,%f\n",
                                triangleI.getSiteX(),
                                triangleI.getSiteY(),
                                triangleJ.getSiteX(),
                                triangleJ.getSiteY(),
                                JCoupling
                        ));



                    if(Math.abs(JCoupling - (-1)) < EPS){
                        System.out.println(String.format(Locale.US,
                                "%d,%d,%d,%d,%f",
                                triangleI.getSiteX(),
                                triangleI.getSiteY(),
                                triangleJ.getSiteX(),
                                triangleJ.getSiteY(),
                                JCoupling));
                    }

                    }
                }
            }
            System.out.println("WRITE DONE");

            pw.close();

        } catch(IOException e){
            e.printStackTrace();
        }
    }
    */

}
