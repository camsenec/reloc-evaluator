import ClientSide.ClientApp;
import ClientSide.ManagementServiceForClient;
import Data.DataBase;
import Data.Document;
import EdgeServer.ManagementServiceForServer;
import EdgeServer.MecHost;
import Constants.Constants;
import Field.Point2D;
import FileIO.FileFactory;

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

    public static void main(String args[]) {

        ManagementServiceForClient service = new ManagementServiceForClient();

        /* read command line argument */
        for (int t = 1; t <= 100; t++) {
            Result.reset();
            if (t == 1) Constants.first();
            else Constants.notFirst();
            service.updateNumberOfCoopServer(t);

            FileFactory.loadLogFile("txLog.csv");

            if (Constants.UPLOAD) {
                /* Step 1 : Register server to a management server */
                for (int i = 0; i < Config.numberOfServers; i++) {
                    MecHost host = new MecHost(Config.application_id);
                    host.initialize(Config.capacityOfServers); //why is it 0?
                    ManagementServiceForServer.serverMap.put(host.getServerId(), host);
                }


                /* Step 2 : Register client to a management server*/
                ClientApp client;
                Random random = new Random();

                for (Integer sender_id : txLog.keySet()) {
                    client = new ClientApp(Config.application_id, sender_id);
                    ManagementServiceForClient.clientMap.putIfAbsent(client.getClientId(), client);
                    double areaLengthX = Constants.MAX_X - Constants.MIN_X;
                    double locationX = Constants.MIN_X + random.nextDouble() * (Constants.MAX_X - Constants.MIN_X);
                    double locationY = Constants.MIN_Y + random.nextDouble() * (Constants.MAX_Y - Constants.MIN_Y);
                    client.initialize(locationX, locationY);

                    ArrayList<Integer> receivers = txLog.get(sender_id);
                    Point2D baseLocation = client.getLocation();
                    for (int receiver : receivers) {
                        client = new ClientApp(Config.application_id, receiver);
                        while(true){
                            locationX = baseLocation.getX() + random.nextGaussian() * Config.locality;
                            if(locationX >= 0 && locationX <= Constants.MAX_X) break;
                        }
                        while(true){
                            locationY = baseLocation.getY() + random.nextGaussian() * Config.locality;
                            if(locationY >= 0 && locationY <= Constants.MAX_Y) break;
                        }
                        client.initialize(locationX, locationY);
                    }
                }

            } else {
                FileFactory.loadServerState("serverCache.csv", Config.capacityOfServers); //Why is it
                FileFactory.loadClientState("clientCache.csv");
            }


            for (Integer serverId : ManagementServiceForServer.serverMap.keySet()) {
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                server.resetState();
            }

            for (int clientId : ManagementServiceForClient.clientMap.keySet()) {
                ClientApp client = ManagementServiceForClient.clientMap.get(clientId);
                client.assignHomeserver();
                ManagementServiceForServer.serverMap.get(client.getHomeServerId()).addConnection();
            }


            /*Step 3 : Prepare Document */
            int document_id = 1;
            for (Integer sender_id : txLog.keySet()) {
                ArrayList<Integer> docList = new ArrayList<>();
                for (int i = 0; i < Config.numberOfDocsPerClients; i++) {
                    Document document = new Document(Config.application_id, document_id);
                    document.initialize(Config.sizeOfDocs);
                    DataBase.dataBase.put(document_id, document);
                    docList.add(document_id++);
                }
                txLogDocs.put(sender_id, docList);
            }

            if (Constants.SIMULATION) {
                for (int sender_id : txLog.keySet()) {
                    ArrayList<Integer> publishedDocuments = txLogDocs.get(sender_id);

                    for (int documentId : publishedDocuments) {
                        Document document = DataBase.dataBase.get(documentId);

                        List<Integer> receivers = txLog.get(sender_id);
                        int homeId = ManagementServiceForClient.clientMap.get(sender_id).getHomeServerId();
                        MecHost server = ManagementServiceForServer.serverMap.get(homeId);
                        Document isExist = server.getCollection().putIfAbsent(document.getDocumentId(), document);
                        Result.numberOfCachedDocument++;

                        //If a new document is published, update the server state
                        if (isExist == null) {
                            server.addUsed(document.getSize());
                        } else {
                            Result.saved++;
                            System.out.format("Document %d has already been stored!", documentId);
                        }

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
                                System.out.format("Document %d has already been stored!", documentId);
                            }
                        }
                    }
                }
            }

            if (Constants.TEST) {
                HashMap<Integer, ArrayList<Integer>> homeClientMap = new HashMap<>();
                for (Integer serverId : ManagementServiceForServer.serverMap.keySet()) {
                    homeClientMap.put(serverId, new ArrayList<>());
                }

                for (Integer sender : txLog.keySet()) {
                    int homeId = ManagementServiceForClient.clientMap.get(sender).getHomeServerId();
                    homeClientMap.get(homeId).add(sender);
                }
                
                if(Constants.DEBUG){
                    for (Integer a : homeClientMap.keySet()) {
                        System.out.print(a + " : ");
                        ArrayList<Integer> b = homeClientMap.get(a);
                        for (Integer id : b) {
                            System.out.print(id + " ");
                        }
                    }
                }

                //1. Y_1
                HashMap<Integer, Double> rMap = new HashMap<>();
                for (Integer serverId : homeClientMap.keySet()) {
                    MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                    if (Config.capacityOfServers >= s_l.getUsed()) {
                        rMap.put(serverId, 0.0);
                    } else {
                        rMap.put(serverId, 1 - (Config.capacityOfServers / (double) s_l.getUsed()));
                    }
                }

                double sum = 0;
                for (double r : rMap.values()) {
                    sum += r;
                }
                Metric.MET_1 = sum / Config.numberOfServers;

                //2. Y_2
                sum = 0;
                for (MecHost server : ManagementServiceForServer.serverMap.values()) {
                    sum += server.getConnection();
                }
                double ave = sum / Config.numberOfServers;

                sum = 0;
                for (MecHost server : ManagementServiceForServer.serverMap.values()) {
                    sum += (server.getConnection() - ave) * (server.getConnection() - ave);
                }
                Metric.MET_2 = Math.sqrt((double) sum / (Config.numberOfServers - 1));

                //3.Y_3
                HashMap<Integer, Double> distanceMap = new HashMap<>();
                for (Integer serverId : homeClientMap.keySet()) {
                    ArrayList<Integer> C_l = homeClientMap.get(serverId);
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
                for (Integer serverId : distanceMap.keySet()) {
                    sum += distanceMap.get(serverId);
                }
                Metric.MET_3 = sum / txLog.size();


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

                System.out.println(y_0 + " " + y_1 + " " + y_2 + " " + y_3);
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
            }
        }
    }
}
