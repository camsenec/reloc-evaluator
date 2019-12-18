import ClientSide.ClientApp;
import EdgeServer.MecHost;
import Constants.Constants;
import FileIO.FileDownloader;
import FileIO.FileFactory;
import Logger.TxLog;

import static Constants.Constants.BASE_URL;
import static Logger.TxLog.tx_log;

public class Main {
    public static int MAX_T = 10;

    public static void main(String args[]){

        int application_id = 1;
        int numberOfServers;
        int capacityOfServers;

        /**
         *
         * 手続き、変数
         * ・シミュレーター系
         * ・クライアント側
         * ・エッジサーバー側
         *
         * コマンドライン引数系は, シミュレーター側で設定する変数
         *
         **/

        /* read command line argument */
        if(args.length != 2){
            numberOfServers = 100;
            capacityOfServers = 10000;
        }else {
            numberOfServers = Integer.parseInt(args[0]);
            capacityOfServers = Integer.parseInt(args[1]);
        }

        FileDownloader.downlaodLogFile(BASE_URL + "simulation/out/tx_log.csv");
        FileFactory.readLogFile();

        if(Constants.CREATE) {

            /* 手順1 : サーバーの管理サーバーへの登録(同期実行)*/
            for (int i = 0; i < numberOfServers; i++) {
                MecHost host = new MecHost(application_id);
                host.initialize(capacityOfServers);
            }


            /* 手順2 : クライアントの管理サーバーへの登録(同期実行)*/
            int count = 0;
            for(Integer client_id : tx_log.keySet()) {
                ClientApp client = new ClientApp(application_id);
                client.setClientId(client_id);
                client.initialize();
                ++count;
                if(count > 10){
                    break;
                }
            }
        }

        if(Constants.SIMULATION) {

            /* create Document */
            ClientApp client;
            for (int i = 1; i <= 100; i++) {

            }

            if (Constants.DEBUG) {
                System.out.println("-----------Collection on EachServers Created-----------");
                //ServerManager.printCollectionSizeOfAllServers();
            }
        }

    }
}
