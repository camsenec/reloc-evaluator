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
import MP.MessageProcessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Result.Result;
import Config.Config;
import Result.Metric;

import static Logger.TxLog.txLog;
import static Logger.TxLog.txLogDocs;

public class Main {

    public static void main(String args[]) throws InterruptedException {
        boolean RLCCA = false;
        boolean FIG = false;

        ManagementServiceForClient service = new ManagementServiceForClient();

        FileDownloader.downlaodLogFile(Constants.BASE_URL + "simulation/out/tx_log.csv");
        FileFactory.loadLogFile("tx_log.csv");


        /* read command line argument */
        
        Result.reset();
        Constants.first();
        //Constants.notFirst();
        service.updateNumberOfCoopServer(10);

        if (Constants.UPLOAD) {
            /* Step 1 : Register server to a management server */
            for (int i = 0; i < Config.numberOfServers; i++) {
                MecHost host = new MecHost(Config.application_id);
                host.initialize(Config.capacityOfServers);
                ManagementServiceForServer.serverMap.put(host.getServerId(), host);
            }


            /* Step 2 : Register client to a management server*/
            ClientApp client;
            Random random;
            if(FIG) random = new Random(1);
            else random = new Random();

            for (int senderId : txLog.keySet()) {
                client = new ClientApp(Config.application_id, senderId);
                ClientApp isExist = ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);
                if(isExist==null){
                    double locationX = Constants.MIN_X + random.nextDouble() * (Constants.MAX_X - Constants.MIN_X);
                    double locationY = Constants.MIN_Y + random.nextDouble() * (Constants.MAX_Y - Constants.MIN_Y);
                    client.initialize(locationX, locationY);
                    ManagementServiceForServer.serverMap.get(client.getHomeServerId()).addConnection(1);
                }

                ArrayList<Integer> receivers = txLog.get(senderId);
                Point2D baseLocation = client.getLocation();
                for (int receiverId : receivers) {
                    client = new ClientApp(Config.application_id, receiverId);
                    isExist = ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);
                    if(isExist == null){
                    double locationX, locationY;
                    while(true){
                        locationX = baseLocation.getX() + random.nextGaussian() * Config.locality;
                        if(locationX >= 0 && locationX <= Constants.MAX_X) break;
                    }
                    while(true){
                        locationY = baseLocation.getY() + random.nextGaussian() * Config.locality;
                        if(locationY >= 0 && locationY <= Constants.MAX_Y) break;
                    }
                    client.initialize(locationX, locationY);
                    ManagementServiceForServer.serverMap.get(client.getHomeServerId()).addConnection(1);
                    }
                    
                }
            }

        } else {
            FileFactory.loadServerState("serverCache.csv", Config.capacityOfServers); //Why is it
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


        /*Step 3 : Prepare Document */
        int id = 1;
        for (int senderId : txLog.keySet()) {
            ArrayList<Integer> docList = new ArrayList<>();
            for (int i = 0; i < Config.numberOfDocsPerClients; i++) {
                Document document = new Document(Config.application_id, id);
                document.initialize(Config.sizeOfDocs);
                DataBase.dataBase.put(id, document);
                docList.add(id++);
            }
            txLogDocs.put(senderId, docList);
        }

        if (Constants.SIMULATION) {
            for (int senderId : txLog.keySet()) {
                ArrayList<Integer> publishedDocuments = txLogDocs.get(senderId);

                for (int documentId : publishedDocuments) {
                    Document document = DataBase.dataBase.get(documentId);
                    
                    if(!RLCCA){
                        int homeId = ManagementServiceForClient.clientMap.get(senderId).getHomeServerId();
                        MecHost server = ManagementServiceForServer.serverMap.get(homeId);
                        Document isExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);
                        Result.numberOfCachedDocument++;

                        //If a new document is published, update the server state
                        server.addUsed(document.getSize());

                        List<Integer> receivers = txLog.get(senderId);
                        for (int receiverId : receivers) {
                            /* get home server of a receiver*/
                            homeId = ManagementServiceForClient.clientMap.get(receiverId).getHomeServerId();
                            server = ManagementServiceForServer.serverMap.get(homeId);
                            isExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);
                            Result.numberOfCachedDocument++;

                            //If a new document is published, update the server state
                            if (isExist == null) {
                                server.addUsed(document.getSize());
                            } else {
                                Result.saved++;
                                System.out.format("Document %d has already been stored!\n", documentId);
                            }
                        }
                    }else{
                        int homeId = ManagementServiceForClient.clientMap.get(senderId).getHomeServerId();
                        MecHost server = ManagementServiceForServer.serverMap.get(homeId);
                        Document isDocExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);
                        Result.numberOfCachedDocument++;

                        //If a new document is published, update the server state
                        server.addUsed(document.getSize());
                        MessageProcessor mp = new MessageProcessor();
                        mp.getDocMap().putIfAbsent(documentId, document);
                        mp.getClientMap().putIfAbsent(senderId, ManagementServiceForClient.clientMap.get(senderId));
                        server.getMPmap().putIfAbsent(senderId, mp);
                    
                        List<Integer> receivers = txLog.get(senderId);
                        for (int receiverId : receivers) {
                            /* get home server of a receiver*/
                            ClientApp receiver = ManagementServiceForClient.clientMap.get(receiverId);
                            homeId = ManagementServiceForClient.clientMap.get(receiverId).getHomeServerId();
                            server = ManagementServiceForServer.serverMap.get(homeId);
                            isDocExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);
                            Result.numberOfCachedDocument++;

                            boolean isMPExist = server.getMPmap().containsKey(senderId);
                            //If a new document is published, update the server state
                            // even if null, the doc was put to the database
                            //System.out.println("EXIST: " + isMPExist);
                            
                            if (isDocExist == null && !isMPExist) {
                                server.addUsed(document.getSize());
                                mp = new MessageProcessor();
                                mp.getDocMap().put(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, ManagementServiceForClient.clientMap.get(receiverId));
                                server.getMPmap().putIfAbsent(senderId, mp);
                            } else if(isDocExist == null && isMPExist) {
                                server.addUsed(document.getSize());
                                mp.getDocMap().put(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, ManagementServiceForClient.clientMap.get(receiverId));
                                server.getMPmap().putIfAbsent(senderId, mp);
                            } else {
                                Result.saved++;
                                System.out.format("Document %d has already been stored!\n", documentId);
                            }

                            if(server.getUsed() >= Config.capacityOfServers * 0.8
                                || server.getConnection() >= Config.connectionLimit * 0.8){
                                MessageProcessor movedMP = server.getMPmap().get(senderId);
                                MecHost preHome = ManagementServiceForServer.serverMap.get(receiver.getHomeServerId());
                                receiver.assignHomeserver(movedMP.getClientMap().size(), movedMP.getDocMap().size());
                                HashMap<Integer, ClientApp> clients = movedMP.getClientMap();
                                for(ClientApp client : clients.values()){
                                    client.updateState(receiver.getHomeServerId());
                                }
                                MecHost newHome =  ManagementServiceForServer.serverMap.get(receiver.getHomeServerId());
                                System.out.println("HOME UPDATED");
                                newHome.getMPmap().put(senderId, movedMP);
                                newHome.addUsed(Config.sizeOfDocs * movedMP.getDocMap().size());
                                newHome.addConnection(movedMP.getClientMap().size());
                                for(int i: movedMP.getDocMap().keySet()){
                                  newHome.getCollection().putIfAbsent(i, movedMP.getDocMap().get(i));
                                }
                                preHome.getMPmap().remove(senderId);
                                preHome.addUsed(-Config.sizeOfDocs * movedMP.getDocMap().size());
                                preHome.addConnection(-movedMP.getClientMap().size());
                                for(int i: movedMP.getDocMap().keySet()){
                                  preHome.getCollection().remove(i);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (Constants.TEST) {
            //Constants
            int A = Config.capacityOfServers;
            int B = Config.connectionLimit;
            int dc = 5;
            int L = Config.numberOfServers;
            int N = txLog.size();
            int M = ManagementServiceForClient.clientMap.size();
            double beta = 1;
            double gamma = 0.1;
            double gamma_2 = 0.001;

            HashMap<Integer, ArrayList<Integer>> homeClientsMap = new HashMap<>();
            for (Integer serverId : ManagementServiceForServer.serverMap.keySet()) {
                homeClientsMap.put(serverId, new ArrayList<>());
            }

            for (int clientId : ManagementServiceForClient.clientMap.keySet()) {
                int homeId = ManagementServiceForClient.clientMap.get(clientId).getHomeServerId();
                homeClientsMap.get(homeId).add(clientId);
            }
            
            if(Constants.DEBUG){
                for (Integer a : homeClientsMap.keySet()) {
                    System.out.print(a + " : ");
                    ArrayList<Integer> b = homeClientsMap.get(a);
                    for (int i : b) {
                        System.out.print(i + " ");
                    }
                }
            }

            //1. Y_1
            HashMap<Integer, Double> rMap = new HashMap<>();
            for (Integer serverId : homeClientsMap.keySet()) {
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                if (A >= s_l.getUsed()) {
                    rMap.put(serverId, 0.0);
                } else {
                    rMap.put(serverId, 1 - (A / (double) s_l.getUsed()));
                }
            }

            double sum = 0;
            for (double r : rMap.values()) {
                sum += r;
            }
            Metric.MET_1 = sum / L;

            //2. Y_2
            sum = 0;
            for (MecHost server : ManagementServiceForServer.serverMap.values()) {
                sum += server.getConnection();
            }
            double ave = sum / L;

            sum = 0;
            for (MecHost server : ManagementServiceForServer.serverMap.values()) {
                sum += (server.getConnection() - ave) * (server.getConnection() - ave);
            }
            Metric.MET_2 = Math.sqrt((double) sum / (L - 1));

            //3.Y_3
            HashMap<Integer, Double> distanceMap = new HashMap<>();
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
                distanceMap.put(serverId, distSum);
            }

            sum = 0;
            for (int serverId : distanceMap.keySet()) {
                sum += distanceMap.get(serverId);
            }
            Metric.MET_3 = sum / M;


            //4.Y
            //The same data flow with the data flow in txLog
            double di = 0;
            for(int senderId: txLog.keySet()){
                ClientApp sender = ManagementServiceForClient.clientMap.get(senderId);
                int senderHomeId = sender.getHomeServerId();
                MecHost senderHome = ManagementServiceForServer.serverMap.get(senderHomeId);
                double x_dist = Math.abs(sender.getLocation().getX() - senderHome.getLocation().getX());
                double y_dist = Math.abs(sender.getLocation().getY() - senderHome.getLocation().getY());
                
                double dl1h = beta * Math.max(homeClientsMap.get(senderHomeId).size() - B, 0);
                double dl2h = gamma * Math.sqrt(x_dist * x_dist + y_dist * y_dist);
                double dlh = dl1h + dl2h;
                double dmp = dlh + rMap.get(senderHomeId) * dc;
                
                ArrayList<Integer> receivers = txLog.get(senderId); 
                double dms_sum = 0;
                for(int receiverId: receivers){
                    ClientApp receiver = ManagementServiceForClient.clientMap.get(receiverId);
                    int receiverHomeId = receiver.getHomeServerId();
                    MecHost receiverHome = ManagementServiceForServer.serverMap.get(receiverHomeId);
                    x_dist = Math.abs(receiver.getLocation().getX() - receiverHome.getLocation().getX());
                    y_dist = Math.abs(receiver.getLocation().getY() - receiverHome.getLocation().getY());
                
                    dl1h = beta * Math.max(homeClientsMap.get(receiverHomeId).size() - B, 0);
                    dl2h = gamma * Math.sqrt(x_dist * x_dist + y_dist * y_dist);
                    dlh = dl1h + dl2h;
                    dms_sum += dlh + rMap.get(receiverHomeId) * dc;
                }
                double dms = dms_sum / receivers.size();
                di += (dmp + dms);
            }
            Metric.MET_4 = di / N;
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
            Result.numberOfSender = txLog.size();
            Result.kindOfDocument = Result.numberOfSender * Config.numberOfDocsPerClients;
            Result.rateOfSaved = (double) Result.saved / (double) Result.numberOfCachedDocument;
            Result.meanOfCachedDocs = Result.meanOfUsed / Config.sizeOfDocs;

            FileFactory.saveResult();
            FileFactory.saveMetric();
            FileFactory.saveServerResult();
        }
    }
}
