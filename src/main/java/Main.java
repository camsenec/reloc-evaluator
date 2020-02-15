import ClientSide.ClientApp;
import ClientSide.ManagementServiceForClient;
import Data.DataBase;
import Data.Document;
import EdgeServer.ManagementServiceForServer;
import EdgeServer.MecHost;
import Constants.Constants;
import Field.Point2D;
import FileIO.FileDownloader;
import FileIO.FileFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Result.Result;
import Config.Config;
import Result.Metric;

import static Constants.Constants.BASE_URL;
import static Logger.TxLog.txLog;
import static Logger.TxLog.txLogDocs;

public class Main {

    public static void main(String args[]){

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
        Constants.first();

        FileDownloader.downlaodLogFile(BASE_URL + "simulation/out/tx_log.csv");
        FileFactory.loadLogFile("txLog.csv");

         /*
            1. インスタンスの作成
            2. 初期化
            3. シミュレーション用のコレクションに追加
         */

        if(Constants.UPLOAD) {
            /* 手順1 : サーバーの管理サーバーへの登録(同期実行)*/
            for (int i = 0; i < Config.numberOfServers; i++) {
                MecHost host = new MecHost(Config.application_id);
                host.initialize(0);
                ManagementServiceForServer.serverMap.put(host.getServerId(), host);
            }


            /* 手順2 : クライアントの管理サーバーへの登録(同期実行)*/
            ClientApp client;
            ClientApp isExist;
            Random random = new Random();

            for (Integer client_id : txLog.keySet()) {
                client = new ClientApp(Config.application_id, client_id);
                client.initialize();
                client.setWeight(1);
                isExist = ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);
                if(isExist != null){
                    ClientApp existClient = ManagementServiceForClient.clientMap.get(client.getClientId());
                    existClient.addWeight(1);
                }

                ArrayList<Integer> clientList = txLog.get(client_id);
                Point2D baseLocation = client.getLocation();
                for (int client_id_2 : clientList) {
                    client = new ClientApp(Config.application_id, client_id_2);
                    client.setWeight(1);
                    //give locality
                    double locationX = Math.abs((baseLocation.getX() + random.nextGaussian() * 20) % 100);
                    double locationY = Math.abs((baseLocation.getX() + random.nextGaussian() * 20) % 100);
                    client.initialize_loc(locationX, locationY);
                    isExist = ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);
                    if(isExist != null){
                        ClientApp existClient = ManagementServiceForClient.clientMap.get(client.getClientId());
                        existClient.addWeight(1);
                    }
                }

            }
        }else{
            FileFactory.loadServerState("serverCache.csv", 0);
            FileFactory.loadClientState("clientCache.csv");
        }



        if(Constants.RESET) {
            for (Integer serverId : ManagementServiceForServer.serverMap.keySet()) {
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                server.resetState();
            }
        }

        int client_count = 0;
        for(int clientId : ManagementServiceForClient.clientMap.keySet()){
            ClientApp client = ManagementServiceForClient.clientMap.get(clientId);
            client.relocate(); //homeserverのidがセットされる
            MecHost server =  ManagementServiceForServer.serverMap.get(client.getHomeServerId());
            server.updateState(0, true, client.getWeight());
            client_count++;
            System.out.println(client_count);
        }




        /*手順3 : ドキュメントの準備（ローカル）*/
        int document_id = 1;
        for(Integer client_id : txLog.keySet()){
            ArrayList<Integer> docList = new ArrayList<>();
            for(int i = 0; i < Config.numberOfDocsPerClients; i++){
                Document document = new Document(Config.application_id, document_id);
                document.initialize(Config.sizeOfDocs);
                DataBase.dataBase.put(document_id, document);

                docList.add(document_id++);
            }
            txLogDocs.put(client_id, docList);
        }

        if(Constants.SIMULATION) {
            for(int sendFromId : txLog.keySet()){
                //txLogDocs : {client_id, docList}
                ArrayList<Integer> documentList = txLogDocs.get(sendFromId);

                /*
                   home serverはclient登録時に適当に割り当てられている
                */
                /* [ドキュメントの生成]
                   1.あるドキュメントに対して[x個のドキュメント]
                   2.指定した宛先[固定]に送信（サーバー操作）
                */

                for(int documentId : documentList){
                    //database : コンテンツ一覧
                    System.out.println(documentList.size());
                    Document document = DataBase.dataBase.get(documentId);

                    List<Integer> sendToList = txLog.get(sendFromId);
                    ClientApp client = ManagementServiceForClient.clientMap.get(sendFromId);
                    int homeId = client.getHomeServerId();
                    MecHost server = ManagementServiceForServer.serverMap.get(homeId);
                    //重複の場合はコレクションに追加しない(同じところをhomeとしていれば重複が発生)
                    Document isExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);

                    Result.numberOfCachedDocument++;

                    //新規にドキュメントをおいた場合は, serverのstateを更新
                    if(isExist == null) {
                        server.updateState(document.getSize(), false, 0);
                    }else{
                        Result.saved++;
                        System.out.println("this document has already been stored!");
                    }

                    for(int sendToId : sendToList) {
                        /* 送り先クライアントの home serverを取得*/
                        client = ManagementServiceForClient.clientMap.get(sendToId);
                        homeId = client.getHomeServerId();
                        server = ManagementServiceForServer.serverMap.get(homeId);
                        //重複の場合はコレクションに追加しない(同じところをhomeとしていれば重複が発生)
                        isExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);

                        Result.numberOfCachedDocument++;

                        //新規にドキュメントをおいた場合は, serverのstateを更新
                        if(isExist == null) {
                            server.updateState(document.getSize(), false, 0);
                        }else{
                            Result.saved++;
                            System.out.println("this document has already been stored!");
                        }
                    }
                }
            }
        }

        /*
        for(Integer clientId: ManagementServiceForClient.clientMap.keySet()){
            rxLogDocs.put(clientId, new ArrayList<Integer>());
        }

        for(Integer sendFromId : txLogDocs.keySet()){
            ArrayList<Integer> sendToList = txLogDocs.get(sendFromId);
            for(Integer sendToId: sendToList){
                rxLogDocs.get(sendToId).add(sendFromId);
            }
        }
        */

        if(Constants.TEST) {
            //serverごとのクライアント集合, C_l の生成
            HashMap<Integer, ArrayList<Integer>> homeClientMap = new HashMap<>();
            for (Integer serverId : ManagementServiceForServer.serverMap.keySet()) {
                homeClientMap.put(serverId, new ArrayList<>());
            }

            for (Integer clientId : txLog.keySet()) {
                ClientApp client = ManagementServiceForClient.clientMap.get(clientId);
                Integer homeId = client.getHomeServerId();
                homeClientMap.get(homeId).add(clientId);
            }

            for(Integer a : homeClientMap.keySet()){
                System.out.print(a + " : ");
                ArrayList<Integer> b = homeClientMap.get(a);
                for(Integer id: b){
                    System.out.print(id + " ");
                }
            }

            //1. Y_1
            int pubSizeSum;
            HashMap<Integer, Double> rMap = new HashMap<>();
            for (Integer serverId : homeClientMap.keySet()) {
                ArrayList<Integer> C_l = homeClientMap.get(serverId);
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                pubSizeSum = 0;
                for (Integer clientId : C_l) {
                    ArrayList<Integer> publishDocs = txLogDocs.get(clientId); //pubDocsは排他的
                    if(publishDocs!=null) {
                        pubSizeSum += publishDocs.size();
                    }
                }
                if(Config.capacityOfServers>=s_l.getUsed()){
                    rMap.put(serverId, 0.0);
                }else {
                    rMap.put(serverId, 1 - (Config.capacityOfServers / (double)s_l.getUsed()));
                }
            }

            double sum = 0;
            for(Integer serverId:rMap.keySet()){
                sum += rMap.get(serverId);
            }
            Metric.MET_1 = sum / Config.numberOfServers;

            for(double r:rMap.values()){
                System.out.println(r);
            }


            //2.Y_2
            HashMap<Integer, Integer> connectionNumMap = new HashMap<>();
            for (Integer serverId : homeClientMap.keySet()) {
                ArrayList<Integer> C_l = homeClientMap.get(serverId);
                connectionNumMap.put(serverId, C_l.size());
            }

            sum = 0;
            for(Integer serverId: connectionNumMap.keySet()){
                sum += connectionNumMap.get(serverId);
            }
            double ave  = sum / Config.numberOfServers;

            sum = 0;
            for(Integer serverId: connectionNumMap.keySet()){
                sum += (connectionNumMap.get(serverId) - ave) * (connectionNumMap.get(serverId) - ave);
            }
            Metric.MET_2 = Math.sqrt((double)sum/(Config.numberOfServers-1));

            //3.Y_3
            HashMap<Integer, Double> distanceMap = new HashMap<>();
            double distSum;
            int count_tmp = 0;
            for (Integer serverId : homeClientMap.keySet()) {
                ArrayList<Integer> C_l = homeClientMap.get(serverId);
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                distSum = 0;
                for (Integer clientId : C_l) {
                    ClientApp c_m = ManagementServiceForClient.clientMap.get(clientId);
                    double x_dist = Math.abs(c_m.getLocation().getX() - s_l.getLocation().getX());
                    double y_dist = Math.abs(c_m.getLocation().getY() - s_l.getLocation().getY());
                    double dist = Math.sqrt(x_dist * x_dist + y_dist * y_dist);
                    distSum += dist;
                    count_tmp++;
                }
                distanceMap.put(serverId, distSum);
            }
            System.out.println("count" + count_tmp);
            System.out.println("size" + ManagementServiceForClient.clientMap.size());

            sum = 0;
            for(Integer serverId:distanceMap.keySet()){
                sum += distanceMap.get(serverId);
            }
            Metric.MET_3 = sum / txLog.size();

            for(Integer serverId :connectionNumMap.keySet()){
                System.out.println(connectionNumMap.get(serverId));
            }


            //4.Y
            //4.1 Constants
            int A = Config.capacityOfServers;
            int B = 100;
            int t_mn = 5;
            int L = Config.numberOfServers;
            int M = txLog.size();
            int N = 3;
            double alpha = 5;
            double beta = 1;
            double gamma = 0.1;
            double y_0, y_1, y_2, y_3;
            double y;
            y_1 = y_2 = y_3 = 0;

            y_0 = t_mn * N * M;

            for (int serverId : ManagementServiceForServer.serverMap.keySet()) {
                y_1 += rMap.get(serverId) * connectionNumMap.get(serverId);
            }
            y_1 = y_1 * alpha * N * t_mn;

            for (int serverId : ManagementServiceForServer.serverMap.keySet()) {
                int connectionNum = connectionNumMap.get(serverId);
                if (connectionNum > B) {
                    y_2 += connectionNum * (connectionNum - B);
                } else {
                    y_2 += 0;
                }
            }
            y_2 = y_2 * beta * N;

            for (int serverId : ManagementServiceForServer.serverMap.keySet()) {
                y_3 += distanceMap.get(serverId);
            }
            y_3 = y_3 * gamma * N;

            y = y_0 + y_1 + y_2 + y_3;
            Metric.MET_4 = y;
            FileFactory.saveMetric();

            System.out.println(y_0 + " " + y_1 + " " + y_2 + " " + y_3);
        }


        if(Constants.SAVE){
            FileFactory.saveServerState();
            FileFactory.saveClientState();
        }

        if(Constants.LOG) {
            for (int serverId : ManagementServiceForServer.serverMap.keySet()) {
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                System.out.println(server);
            }
        }


        for(Integer clientId: ManagementServiceForClient.clientMap.keySet()){
            ClientApp client = ManagementServiceForClient.clientMap.get(clientId);
            System.out.println("clientId" + clientId + "weight" + client.getWeight());
        }


        if(Constants.RESULT){
            int sumOfUsed = 0;
            Result.minOfUsed = Constants.INF;
            Result.maxOfUsed = Constants.INF * (-1);
            Result.numberOfClient = ManagementServiceForClient.clientMap.size();

            for(int serverId : ManagementServiceForServer.serverMap.keySet()){
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                sumOfUsed += server.getUsed();

                if(server.getUsed() > Result.maxOfUsed){
                    Result.maxOfUsed = server.getUsed();
                }
                if(server.getUsed() < Result.minOfUsed){
                    Result.minOfUsed = server.getUsed();
                }
            }
            Result.meanOfUsed = (double)sumOfUsed / (double)ManagementServiceForServer.serverMap.size();
            Result.numberOfSender = txLog.size();
            Result.kindOfDocument = Result.numberOfSender * Config.numberOfDocsPerClients;
            Result.rateOfSaved = (double)Result.saved / (double)Result.numberOfCachedDocument;
            Result.meanOfCachedDocs = Result.meanOfUsed / Config.sizeOfDocs;

            FileFactory.saveResult();
        }
    }
}
