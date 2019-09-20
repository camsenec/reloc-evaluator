import ClientSide.Client;
import HTTP.HTTPResponseMetaData;
import MetaServer.ClientManager;
import Constants.Constants;
import EdgeServer.EdgeServer;
import MetaServer.ServerManager;

import java.util.HashMap;

public class Main {
    public static int MAX_T = 10000;

    public static void main(String args[]){
        if(args.length < 3){
            System.err.println("You need to set commandline argument");
        }

        /* read command line argument */
        int numberOfServers = Integer.parseInt(args[0]);
        int capacityOfServers = Integer.parseInt(args[1]);
        int numberOfClients = Integer.parseInt(args[2]);

        /* construct edge server on the field */
        ServerManager.setnumberOfServers(numberOfServers);
        ServerManager.createServers(capacityOfServers);

        if(Constants.DEBUG) {
            HashMap<Integer, EdgeServer> serverMap = ServerManager.serverMap;
            for (int serverId : ServerManager.serverMap.keySet()) {
                System.out.println(serverMap.get(serverId));
            }
        }

        /* create clients */
        ClientManager.setNumberOfClients(numberOfClients);
        ClientManager.createClients();

        /* create Document */
        /* Simulatorの要件 */
        /* [Simulation 1] 逐次的なシミュレーション */
        /** ある戦略に沿ってキャッシングしておく **/
        /** POSTするときに, どのサーバーにドキュメントを置くか **/
        /* このとき, メモリスペースがどれだけ圧迫されているか（各エッジサーバーのデータの均等度　
         容量がなくなってしまっているサーバーは存在しないか. 有るエッジサーバーにおける容量が圧迫されたときに適切に
         ドキュメントの移行, 減少が行われているか*/
        /*
        1. GET しようとしたときにどれだけのHR, ADL, ATCが得られるか.
        2. POSTしようとしたときに, レコードがADLを減らすようにきちんと配置されるか.
        ※
         */

        /**残りで作り込まないといけないのは,
         * ・POSTするときの, 配置手法（大事）-> 暫定
         * ・定期的な再配置をするときのその手法（大事）-> 暫定
         * ・Documentに対して優先度を設定 -> 暫定
         * ・メトリクス -> 暫定(HRだけとかでもOK)
         * ・サーバーのグルーピング手法(暫定) -> 未実装
         */
        /** 余裕があれば可視化までできるとなお良い(python scriptあたり？)*/


        /* [Simulation 2] POSTとGET(PATCH, PUT)を同時並行で行う
        -> きちんとはかるにはちょっと工夫がいりそう*/
        /* ここは単純に評価のため*/
        /* HTTPRequestをすると, METADATA(latency, transmissioncost)が帰ってくる*/
        /* それをログに保持しておく -> あとで合算すれば良い*/
        Client client;
        for(int t = 0; t < MAX_T; t++) {
            HashMap<Integer, Client> clientMap = ClientManager.clientMap;
            for (int clientId : clientMap.keySet()) {
                client = clientMap.get(clientId);
                HTTPResponseMetaData response = client.HTTPRequest();
            }
        }

    }
}
