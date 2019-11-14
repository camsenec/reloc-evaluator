import ClientSide.ClientApp;
import HTTP.HTTPResponseMetaData;
import Log.Log;
import Legacy.ClientManager;
import Constants.Constants;
import Data.DistDataBase;
import Legacy.ServerManager;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    public static int MAX_T = 10;

    public static void main(String args[]){

        int application_id = 1;

        int numberOfServers;
        int capacityOfServers;
        int groupSize;
        int numberOfClients;

        /**
         *
         * 手続き、変数
         * ・シミュレーター系
         * ・クライアント側
         * ・エッジサーバー側
         *
         * コマンドライン引数系は, シミュレーター側で設定する変数
         *
         */
        /* read command line argument */
        if(args.length != 4){
            numberOfServers = 16;
            capacityOfServers = 10000;
            numberOfClients = 1;
        }else {
            numberOfServers = Integer.parseInt(args[0]);
            capacityOfServers = Integer.parseInt(args[1]);
            numberOfClients = Integer.parseInt(args[2]);
        }

        /* construct edge server on the field */
        ServerManager.setNumberOfServers(numberOfServers);
        ServerManager.createServers(capacityOfServers);

        if(Constants.DEBUG) {
            System.out.println("-----------Servers Created-----------");
            //ServerManager.printAllServers();
        }


        /* create clients and register to server*/
        ClientManager.setNumberOfClients(numberOfClients);
        ClientManager.createClients();
        ClientManager.updateLocationOfAllClients();

        if(Constants.DEBUG) {
            System.out.println("-----------Clients Created-----------");
            ClientManager.printAllClients();
        }

        try {
            Thread.sleep(10000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }

        System.exit(0);

        /* create Document */
        /* 一人あたり100個のドキュメントを生成*/
        ClientApp client;
        for(int i = 1;  i <= 100; i++) {
            ConcurrentHashMap<Integer, ClientApp> clientMap = ClientManager.clientMap;
            for (int clientId : clientMap.keySet()) {
                client = clientMap.get(clientId);
                /*homeサーバーにクライアントがデータをPOSTする*/
                HTTPResponseMetaData response = client.POST();
                //Log.outputResponseData(response);
            }
            if(i % 3 == 0) {
                ClientManager.updateLocationOfAllClients();
            }
        }

        if(Constants.DEBUG) {
            System.out.println("-----------Collection on EachServers Created-----------");
            //ServerManager.printCollectionSizeOfAllServers();
        }

        /*GETによる評価*/
        Log.openLogfile();
        Random random = new Random();

        for(int t = 0; t < MAX_T; t++) {
            ConcurrentHashMap<Integer, ClientApp> clientMap = ClientManager.clientMap;
            for (int clientId : clientMap.keySet()) {
                client = clientMap.get(clientId);
                //最も近いサーバーからGETをする（しようとする）
                UUID documentId = DistDataBase.ids.get(random.nextInt(DistDataBase.ids.size()));
                HTTPResponseMetaData response = client.GET(documentId);
                Log.outputResponseData(response);
            }

            if(t % 3 == 0) {
                ClientManager.updateLocationOfAllClients();
                ClientManager.updateNearestServerOfAllClients();
            }
        }

        if(Constants.DEBUG) {
            System.out.println("-----------Remain of Each MecHost-----------");
            ServerManager.printRemainOfAllServers();
        }

    }


    /* Simulatorの要件 */
    /* [Simulation 1] 逐次的なシミュレーション */
    /** ある戦略に沿ってキャッシングしておく **/
    /** POSTするときに, どのサーバーにドキュメントを置くか **/
    /*
    このとき, メモリスペースがどれだけ圧迫されているか（各エッジサーバーのデータの均等度　
    容量がなくなってしまっているサーバーは存在しないか. 有るエッジサーバーにおける容量が圧迫されたときに適切に
    ドキュメントの移行, 減少が行われているか
    */
    /*
    1. GET しようとしたときにどれだけのHR, ADL, ATCが得られるか.
    2. POSTしようとしたときに, レコードがADLを減らすようにきちんと配置されるか.
    */

    /**
     * 残りで作り込まないといけないのは,
     * ・POSTするときの, 配置手法（大事）-> 暫定(OK)
     * ・定期的な再配置をするときのその手法（大事）-> 暫定(行わない, group内で追加実装)
     * ・Documentに対して優先度を設定 -> 暫定(OK)
     * ・メトリクス -> 暫定(HRだけとかでもOK)
     * ・サーバーのグルーピング手法(暫定) -> OK
     **/
    /** 余裕があれば可視化までできるとなお良い(python scriptあたり？)*/


    /* [Simulation 2] POSTとGET(PATCH, PUT)を同時並行で行う
     -> きちんとはかるにはちょっと工夫がいりそう
     */
    /* ここは単純に評価のため */
    /* HTTPRequestをすると, METADATA(latency, transmissioncost)が帰ってくる*/
    /* それをログに保持しておく -> あとで合算すれば良い*/
}
