package Log;

import HTTP.HTTPResponseMetaData;

import java.io.FileWriter;
import java.io.IOException;

public class Log {
    /**最後に出力が必要なもの
     * 1. 各サーバーの容量
     * 2. ATC
     * 3. レイテンシ
     *
     * 1はEdgeServerのフィールド
     * 2,3 はHTTPResponseMetaDataから生成
     */

    public static FileWriter fw = null;


    public static void openLogfile(){
        try{
            fw = new FileWriter("log.txt", true);
        }catch(IOException e){
            System.err.println("cannot open/create logfile");
        }
    }

    public static void outputResponseData(HTTPResponseMetaData response){
        try{
            fw.write(response.toLogFile());
        }catch(IOException e){
            System.err.println("cannot write to logfile");
        }
    }

    public static void closeLogFile(){
        try{
            fw.close();
        }catch(IOException e){
            System.err.println("cannot close logfile");
        }
    }


}
