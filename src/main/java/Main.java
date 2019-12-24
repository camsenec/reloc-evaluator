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

import static Constants.Constants.BASE_URL;
import static Logger.TxLog.txLog;
import static Logger.TxLog.txLogDocs;

public class Main {

    public static void main(String args[]){

        int application_id = 1;
        int numberOfServers;
        int capacityOfServers;
        int numberOfDocsPerClients;
        int sizeOfDocs;

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
        if(args.length != 4){
            numberOfServers = 100;
            capacityOfServers = 10000;
            numberOfDocsPerClients = 3;
            sizeOfDocs = 100;
        }else {
            numberOfServers = Integer.parseInt(args[0]);
            capacityOfServers = Integer.parseInt(args[1]);
            numberOfDocsPerClients = Integer.parseInt(args[2]);
            sizeOfDocs = Integer.parseInt(args[3]);
        }

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
            for (int i = 0; i < numberOfServers; i++) {
                MecHost host = new MecHost(application_id);
                host.initialize(capacityOfServers);
                ManagementServiceForServer.serverMap.put(host.getServerId(), host);
            }


            /* 手順2 : クライアントの管理サーバーへの登録(同期実行)*/
            ClientApp client;
            for (Integer client_id : txLog.keySet()) {
                client = new ClientApp(application_id, client_id);
                client.initialize();
                ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);

                ArrayList<Integer> clientList = txLog.get(client_id);
                for (int client_id_2 : clientList) {
                    client = new ClientApp(application_id, client_id_2);
                    client.initialize();
                    ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);
                }
            }
        }else{
            FileFactory.loadServerState("serverCache.csv", capacityOfServers);
            FileFactory.loadClientState("clientCache.csv");
        }

        /*手順3 : ドキュメントの生成（ローカル）*/
        int document_id = 1;
        for(Integer client_id : txLog.keySet()){
            ArrayList<Integer> docList = new ArrayList<>();
            for(int i = 0; i < numberOfDocsPerClients; i++){
                Document document = new Document(application_id, document_id);
                document.initialize(sizeOfDocs);
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
                /*
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
                        //重複の場合はコレクションに追加しない
                        Document isExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);
                        //新規にドキュメントをおいた場合は, serverのstateを更新
                        if(isExist == null) {
                            server.updateState(document.getSize());
                        }else{
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

        if(Constants.LOG){
            for(int serverId : ManagementServiceForServer.serverMap.keySet()){
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                System.out.println(server);
            }
        }

    }
}
