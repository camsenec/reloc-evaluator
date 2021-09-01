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
import Logger.TxLog;
import PubSubBroker.Broker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import Result.Result;
import Utility.Tuple;
import Config.Config;
import Result.Metric;

import static Logger.TxLog.txLog;
import static Logger.TxLog.txLogDocs;

public class Main {

    public static void main(String args[]) throws InterruptedException {
        
        Config.read();

        ManagementServiceForClient service = new ManagementServiceForClient();

        FileDownloader.downlaodLogFile(Config.BASE_URL + "simulation/out/tx_log.csv");
        FileFactory.loadLogFile("tx_log.csv");

        service.deleteAll();
        service.updateNumberOfCoopServer(Config.numOfServersInCluster);
        service.updateStrategy(Config.method);
        
        /* Step 1 : Register clients and servers to a management server */
        if (Constants.UPLOAD) {
            
            for (int i = 0; i < Config.numberOfServers; i++) {
                MecHost host = new MecHost(Config.application_id);
                if (Config.numberOfServers == 16)
                    host.initialize(Config.capacityOfServers, i);
                else {
                    host.initialize(Config.capacityOfServers);
                }
                ManagementServiceForServer.serverMap.put(host.getServerId(), host);
            }

            ArrayList<Integer> memoSenders = new ArrayList<>();
            Random random = new Random();
            for (Integer senderId : TxLog.txLogSec.keySet()) {
                memoSenders.add(senderId);
                ClientApp sender = new ClientApp(Config.application_id, senderId);
                ManagementServiceForClient.clientMap.put(sender.getClientId(), sender);
                sender.initialize();
                ManagementServiceForServer.serverMap.get(sender.getHomeServerId()).addConnection(1);
                ArrayList<Integer> receivers = TxLog.txLogSec.get(senderId);
                Point2D baseLocation = sender.getLocation();
                for (int receiverId : receivers) {
                    ClientApp receiver = new ClientApp(Config.application_id, receiverId);
                    ClientApp isExist = ManagementServiceForClient.clientMap.putIfAbsent(receiver.getClientId(), receiver);
                    double locationX, locationY;
                    if(isExist == null){
                        while(true){
                            locationX = baseLocation.getX() + random.nextGaussian() * Config.locality;
                            if(locationX >= 0 && locationX <= Config.MAX_X) break;
                        }
                        while(true){
                            locationY = baseLocation.getY() + random.nextGaussian() * Config.locality;
                            if(locationY >= 0 && locationY <= Config.MAX_Y) break;
                        }
                        receiver.initialize(locationX, locationY);
                        ManagementServiceForServer.serverMap.get(receiver.getHomeServerId()).addConnection(1);
                    }
                }
            }

        } else {
            FileFactory.loadServerState("serverCache.csv", Config.capacityOfServers); 
            FileFactory.loadClientState("clientCache.csv");
        }
        
        
        for (int serverId : ManagementServiceForServer.serverMap.keySet()) {
            MecHost server = ManagementServiceForServer.serverMap.get(serverId);
            server.resetState();
        }
        
        for (int clientId : ManagementServiceForClient.clientMap.keySet()) {
            ClientApp client = ManagementServiceForClient.clientMap.get(clientId);
            client.assignHomeserver();
            ManagementServiceForServer.serverMap.get(client.getHomeServerId()).addConnection(1);
        }


        /* Step 3 : Prepare Document */
        int id = 1;
        for (Tuple<Integer, Integer> key : TxLog.txLog.keySet()) {
            int senderId = key.second;
            ArrayList<Integer> docList = new ArrayList<>();
            for(int i = 0; i < Config.numberOfDocsPerClient; i++){
                Document document = new Document(Config.application_id, id);
                document.initialize(Config.sizeOfDocs);
                DataBase.dataBase.put(id, document);
                docList.add(id++);
            }
            txLogDocs.put(senderId, docList);
        }
        System.out.println(--id + " Spoolers are registered");
        int numOfDocs = id;
        
        /* Step 4 : Publish documents to edge servers */
        if (Constants.SIMULATION) {
            for (Tuple<Integer, Integer> key : TxLog.txLog.keySet()) {
                int repId = key.first;
                int senderId = key.second;
                ArrayList<Integer> publishedDocuments = txLogDocs.get(senderId);

                for (int documentId : publishedDocuments) {
                    Document document = DataBase.dataBase.get(documentId);
                    System.out.format("Publish from client %d [ %d/%d ]\n", senderId, documentId, numOfDocs);
                    
                    if(Config.method != "RELOC" && Config.method != "OTOS"){
                        ClientApp sender = ManagementServiceForClient.clientMap.get(senderId);
                        int senderHomeId = ManagementServiceForClient.clientMap.get(senderId).getHomeServerId();
                        MecHost senderHome = ManagementServiceForServer.serverMap.get(senderHomeId);
                        Broker.publish(sender, senderHome, document, repId, false);
                    }else{
                        ClientApp sender = ManagementServiceForClient.clientMap.get(senderId);
                        int senderHomeId = ManagementServiceForClient.clientMap.get(senderId).getHomeServerId();
                        MecHost senderHome = ManagementServiceForServer.serverMap.get(senderHomeId);
                        Broker.publish(sender, senderHome, document, repId, true);
                    }
                }
            }
        }
        
        /* Step 5: Calculate delivery delay in the conducted publishes*/
        if (Constants.TEST) {
            //Constants
            int A = Config.capacityOfServers;
            int B = Config.cpLimit;
            int dc = 5;
            int N = txLog.size();
            double gamma = 0.1;
            double gamma_2 = 0.1;
        
            Metric.MET_1 = TxLog.txLogSec.size();
            Metric.MET_2 = Config.numberOfDocsPerClient;
            Metric.MET_3 = Config.locality;
            Metric.MET_4 = Config.numOfServersInCluster;
            Metric.MET_5 = Config.method;

            HashMap<Integer, ArrayList<Integer>> homeClientsMap = new HashMap<>();
            for (Integer serverId : ManagementServiceForServer.serverMap.keySet()) {
                homeClientsMap.put(serverId, new ArrayList<>());
            }

            for (int clientId : ManagementServiceForClient.clientMap.keySet()) {
                int homeId = ManagementServiceForClient.clientMap.get(clientId).getHomeServerId();
                homeClientsMap.get(homeId).add(clientId);
            }

            for (Integer serverId : homeClientsMap.keySet()) {
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                Result.aMap.put(serverId, Math.min(1, A / (double) s_l.getUsed()));
            }

            for (Integer serverId : homeClientsMap.keySet()) {
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                Result.bMap.put(serverId, Math.min(1, B / (double) s_l.getCp()));
            }

            HashMap<Integer, Double> distanceSumMap = new HashMap<>();
            for (int serverId : homeClientsMap.keySet()) {
                ArrayList<Integer> C_l = homeClientsMap.get(serverId);
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                double distSum = 0;
                for (Integer clientId : C_l) {
                    ClientApp c_m = ManagementServiceForClient.clientMap.get(clientId);
                    double x_dist = Math.abs(c_m.getLocation().getX() - s_l.getLocation().getX());
                    double y_dist = Math.abs(c_m.getLocation().getY() - s_l.getLocation().getY());
                    double dist = Math.sqrt(x_dist * x_dist + y_dist * y_dist);
                    distSum += dist;
                }
                distanceSumMap.put(serverId, distSum);
                Result.distanceMap.put(serverId, distSum / C_l.size());
            }
            
            double first_sum = 0;
            double second_sum = 0;
            double third_sum = 0;
            double di = 0;
            for(Tuple<Integer, Integer> key: txLog.keySet()){
                int senderId = key.second;

                //publisher side
                ClientApp sender = ManagementServiceForClient.clientMap.get(senderId);
                int senderHomeId = sender.getHomeServerId();
                MecHost senderHome = ManagementServiceForServer.serverMap.get(senderHomeId);
                double x_dist = Math.abs(sender.getLocation().getX() - senderHome.getLocation().getX());
                double y_dist = Math.abs(sender.getLocation().getY() - senderHome.getLocation().getY());
                
                double dml = gamma * Math.sqrt(x_dist * x_dist + y_dist * y_dist);

                double al = Result.aMap.get(senderHomeId);
                double bl = Result.bMap.get(senderHomeId);
                double cd = 2 * dc * (1 - Math.min(al,bl));
                
                ArrayList<Integer> receivers = txLog.get(key);
                double dml_sum = 0;
                double dlm_sum = 0;
                double mid_sum = 0;
                for(int receiverId: receivers){
                    dml_sum += dml;

                    ClientApp receiver = ManagementServiceForClient.clientMap.get(receiverId);
                    int receiverHomeId = receiver.getHomeServerId();
                    MecHost receiverHome = ManagementServiceForServer.serverMap.get(receiverHomeId);
                    x_dist = Math.abs(senderHome.getLocation().getX() - receiverHome.getLocation().getX());
                    y_dist = Math.abs(senderHome.getLocation().getY() - receiverHome.getLocation().getY());
                    double dll = gamma_2 * Math.min(al,bl) * Math.sqrt(x_dist * x_dist + y_dist * y_dist);
                    mid_sum += (dll + cd);

                    //subscriber side
                    x_dist = Math.abs(receiver.getLocation().getX() - receiverHome.getLocation().getX());
                    y_dist = Math.abs(receiver.getLocation().getY() - receiverHome.getLocation().getY());
                
                    double dlm = gamma * Math.sqrt(x_dist * x_dist + y_dist * y_dist);
                    dlm_sum += dlm;
                }
                double first = dml_sum / receivers.size();
                double second = mid_sum / receivers.size();
                double third = dlm_sum / receivers.size();
                first_sum += first;
                second_sum += second;
                third_sum += third;
                di += (first + second + third);
            }
            Metric.MET_6 = first_sum / N;
            Metric.MET_7 = second_sum / N;
            Metric.MET_8 = third_sum / N;
            Metric.MET_9 = di / N;
        }

            

        if (Constants.SAVE) {
            FileFactory.saveServerState();
            FileFactory.saveClientState();
        }

        if (Constants.LOG) {
            for (int serverId : ManagementServiceForServer.serverMap.keySet()) {
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                System.out.println(server);
            }
        }

        if (Constants.RESULT) {
            int sumOfUsed = 0;
            Result.minOfUsed = Constants.INF;
            Result.maxOfUsed = Constants.INF * (-1);
            Result.numberOfClient = ManagementServiceForClient.clientMap.size();

            for (int serverId : ManagementServiceForServer.serverMap.keySet()) {
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                sumOfUsed += server.getUsed();

                if (server.getUsed() > Result.maxOfUsed) {
                    Result.maxOfUsed = server.getUsed();
                }
                if (server.getUsed() < Result.minOfUsed) {
                    Result.minOfUsed = server.getUsed();
                }
            }
            Result.meanOfUsed = (double) sumOfUsed / (double) ManagementServiceForServer.serverMap.size();
            Result.numberOfGroups = TxLog.txLogSec.size();
            Result.numberOfSenders = txLog.size();
            Result.publishedDocument = Result.numberOfSenders * Config.numberOfDocsPerClient;
            Result.rateOfSaved = (double) Result.saved / (double) Result.numberOfCachedDocument;
            Result.meanOfCachedDocs = Result.meanOfUsed / Config.sizeOfDocs;


            FileFactory.saveResult();
            FileFactory.saveMetric();
            FileFactory.saveServerResult();
            FileFactory.saveClientResult();
        }
    }
}
