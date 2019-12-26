import ClientSide.ClientApp;
import ClientSide.ManagementServiceForClient;
import Data.DataBase;
import Data.Document;
import EdgeServer.ManagementServiceForServer;
import EdgeServer.MecHost;
import Constants.Constants;
import FileIO.FileDownloader;
import FileIO.FileFactory;

import java.util.ArrayList;
import java.util.List;
import Result.Result;
import Config.Config;

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
        Constants.notFirst();

        FileDownloader.downlaodLogFile(BASE_URL + "simulation/out/tx_log.csv");
        FileFactory.loadLogFile("txLog.csv");



        /*
         /*
            1. インスタンスの作成
            2. 初期化
            3. シミュレーション用のコレクションに追加
         */

        if(Constants.UPLOAD) {
            /* 手順1 : サーバーの管理サーバーへの登録(同期実行)*/
            for (int i = 0; i < Config.numberOfServers; i++) {
                MecHost host = new MecHost(Config.application_id);
                host.initialize(Config.capacityOfServers);
                ManagementServiceForServer.serverMap.put(host.getServerId(), host);
            }


            /* 手順2 : クライアントの管理サーバーへの登録(同期実行)*/
            ClientApp client;
            ClientApp isExist;
            for (Integer client_id : txLog.keySet()) {
                client = new ClientApp(Config.application_id, client_id);
                client.initialize();
                isExist = ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);

                if(isExist == null){
                    Result.numberOfClient++;
                }

                ArrayList<Integer> clientList = txLog.get(client_id);
                for (int client_id_2 : clientList) {
                    client = new ClientApp(Config.application_id, client_id_2);
                    client.initialize();
                    isExist = ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);
                }

                if(isExist == null){
                    Result.numberOfClient++;
                }
            }
        }else{
            FileFactory.loadServerState("serverCache.csv", Config.capacityOfServers);
            FileFactory.loadClientState("clientCache.csv");
        }

        if(Constants.LOG){
            for(int serverId : ManagementServiceForServer.serverMap.keySet()){
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                System.out.println(server);
            }
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
                ArrayList<Integer> documentList = txLogDocs.get(sendFromId);

                /*
                   home serverはclient登録時に適当に割り当てられている
                */
                /* [ドキュメントの生成]
                   1.あるドキュメントに対して[x個のドキュメント]
                   2.指定した宛先[固定]に送信（サーバー操作）
                */

                for(int documentId : documentList){
                    Document document = DataBase.dataBase.get(documentId);

                    List<Integer> sendToList = txLog.get(sendFromId);
                    for(int sendToId : sendToList) {
                        /* 送り先クライアントの home serverを取得*/
                        ClientApp client = ManagementServiceForClient.clientMap.get(sendToId);
                        int homeId = client.getHomeServerId();
                        MecHost server = ManagementServiceForServer.serverMap.get(homeId);
                        //重複の場合はコレクションに追加しない(同じところをhomeとしていれば重複が発生)
                        Document isExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);

                        Result.numberOfCachedDocument++;

                        //新規にドキュメントをおいた場合は, serverのstateを更新
                        if(isExist == null) {
                            server.updateState(document.getSize());
                        }else{
                            Result.saved++;
                            System.out.println("this document has already been stored!");
                        }
                    }
                }
            }
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

        if(Constants.RESULT){
            int sumOfUsed = 0;
            Result.minOfUsed = Constants.INF;
            Result.maxOfUsed = Constants.INF * (-1);

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
