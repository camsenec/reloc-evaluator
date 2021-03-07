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
import MP.MessageProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import Result.Result;
import Utility.Tuple;
import Config.Config;
import Result.Metric;

import static Logger.TxLog.txLog;
import static Logger.TxLog.txLogDocs;

public class Main {

    public static void main(String args[]) throws InterruptedException {
        
        //Config.method = "RA";
        //Config.method = "NS";
        //Config.method = "LCA";
        //Config.method = "RCA";
        //Config.method = "RLCA";
        //Config.method = "LCCA";
        //Config.method = "RCCA";
        //Config.method = "RLCCA";
        boolean FIG = false;
        //Config.distinct = "";
        Config.distinct = "disjoint-";

        ArrayList<String> methods = new ArrayList<>(
            Arrays.asList("RA", "NS", "RCCA","RLCCA") 
        );
        ArrayList<Integer> pubNumList = new ArrayList<>(
            Arrays.asList(2, 4, 6, 8, 10) 
        );
        ArrayList<Integer> localityList = new ArrayList<>(
            Arrays.asList(1, 5, 10, 15, 20) 
        );
        ArrayList<Integer> numOfCoopServerList = new ArrayList<>(
            Arrays.asList(4, 8, 16, 2, 1) 
        );
        
        int epoch = 0;
        for(int locality: localityList){
            Config.numberOfDocsPerClients = pubNumList.get(0);
            Config.locality = locality;
            Config.numOfServersInCluster = numOfCoopServerList.get(0);
        
        for(String method: methods){
            Config.method = method;

        if(Config.method == "RCA"){
            Config.numOfServersInCluster = Config.numberOfServers;
        }else if(Config.method == "RCCA"){
            Config.numOfServersInCluster = Config.numberOfServers;
            Config.method = "OTOS";
        }else{
            Config.numOfServersInCluster = 4;
        }

        ManagementServiceForClient service = new ManagementServiceForClient();

        TxLog.txLog.clear();
        TxLog.txLogSec.clear();
        FileDownloader.downlaodLogFile(Constants.BASE_URL + "simulation/out/tx_log.csv");
        FileFactory.loadLogFile("tx_log.csv");


        /* read command line argument */
        //if(epoch == 0) Constants.first();
        //else Constants.notFirst();
        Constants.first();
        epoch++;
        Result.reset();
        service.updateNumberOfCoopServer(Config.numOfServersInCluster);
        service.updateStrategy(Config.method);

        if (Constants.UPLOAD) {
            ManagementServiceForClient.clientMap.clear();
            ManagementServiceForServer.serverMap.clear();
            service.deleteAll();
            /* Step 1 : Register server to a management server */
            for (int i = 0; i < Config.numberOfServers; i++) {
                MecHost host = new MecHost(Config.application_id);
                host.initialize(Config.capacityOfServers, i);
                ManagementServiceForServer.serverMap.put(host.getServerId(), host);
            }


            /* Step 2 : Register client to a management server*/
            Random random;
            if(FIG) random = new Random(2); //methods&flow:6 //not-dis:8
            else random = new Random();

            ArrayList<Integer> memoSenders = new ArrayList<>();
            for (Integer senderId : TxLog.txLogSec.keySet()) {
                memoSenders.add(senderId);
                ClientApp sender = new ClientApp(Config.application_id, senderId);
                ManagementServiceForClient.clientMap.put(sender.getClientId(), sender);
                double locationX = Constants.MIN_X + random.nextDouble() * (Constants.MAX_X - Constants.MIN_X);
                double locationY = Constants.MIN_Y + random.nextDouble() * (Constants.MAX_Y - Constants.MIN_Y);
                sender.initialize(locationX, locationY);
                ManagementServiceForServer.serverMap.get(sender.getHomeServerId()).addConnection(1);
            

                ArrayList<Integer> receivers = TxLog.txLogSec.get(senderId);
                Point2D baseLocation = sender.getLocation();
                for (int receiverId : receivers) {
                    ClientApp receiver = new ClientApp(Config.application_id, receiverId);
                    ClientApp isExist = ManagementServiceForClient.clientMap.putIfAbsent(receiver.getClientId(), receiver);
                    if(isExist == null){
                        while(true){
                            locationX = baseLocation.getX() + random.nextGaussian() * Config.locality;
                            if(locationX >= 0 && locationX <= Constants.MAX_X) break;
                        }
                        while(true){
                            locationY = baseLocation.getY() + random.nextGaussian() * Config.locality;
                            if(locationY >= 0 && locationY <= Constants.MAX_Y) break;
                        }
                        receiver.initialize(locationX, locationY);
                        ManagementServiceForServer.serverMap.get(receiver.getHomeServerId()).addConnection(1);
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
        for (int repId : TxLog.txLogSec.keySet()) {
            ArrayList<Integer> docList = new ArrayList<>();
            for (int i = 0; i < 1; i++) {
                Document document = new Document(Config.application_id, id);
                document.initialize(1 * TxLog.txLogSec.get(repId).size() * Config.numberOfDocsPerClients);
                DataBase.dataBase.put(id, document);
                docList.add(id++);
            }
            txLogDocs.put(repId, docList);
        }
        System.out.println(--id + " Spoolers are registered");

        if (Constants.SIMULATION) {
            for (Integer repId : TxLog.txLogSec.keySet()) {
                int senderId = repId;
                ArrayList<Integer> publishedDocuments = txLogDocs.get(repId);

                for (int documentId : publishedDocuments) {
                    Document document = DataBase.dataBase.get(documentId);
                    
                    if(Config.method != "RLCCA" && Config.method != "OTOS"){
                        ClientApp sender = ManagementServiceForClient.clientMap.get(senderId);
                        int senderHomeId = ManagementServiceForClient.clientMap.get(senderId).getHomeServerId();
                        MecHost senderHome = ManagementServiceForServer.serverMap.get(senderHomeId);
                        //new document
                        Document isDocExist = senderHome.getCollection().putIfAbsent(document.getDocumentId(), document);
                        boolean isMPExist = senderHome.getMPmap().containsKey(repId);
                        Result.numberOfCachedDocument++;
                        //If a new document is published, update the server state
                        // even if null, the doc was put to the database
                        //System.out.println("EXIST: " + isMPExist);
                        
                        MessageProcessor mp;
                        if (isDocExist == null && !isMPExist) {
                            senderHome.addUsed(document.getSize());
                            mp = new MessageProcessor();
                            mp.getDocMap().putIfAbsent(documentId, document);
                            mp.getClientMap().putIfAbsent(senderId, sender);
                            senderHome.getMPmap().putIfAbsent(repId, mp);
                            sender.getMPmap().putIfAbsent(repId, mp);
                        } else if(isDocExist == null && isMPExist) {
                            senderHome.addUsed(document.getSize());
                            mp = senderHome.getMPmap().get(repId);
                            mp.getDocMap().putIfAbsent(documentId, document);
                            mp.getClientMap().putIfAbsent(senderId, sender);
                            sender.getMPmap().putIfAbsent(repId, mp);
                        } else if (isDocExist != null && !isMPExist){
                            System.out.format("SenderWarning");
                        } else { //both Exist
                            Result.saved++;
                            System.out.format("Document %d has already been stored!\n", documentId);
                            mp = senderHome.getMPmap().get(repId);
                            mp.getDocMap().putIfAbsent(documentId, document);
                            mp.getClientMap().putIfAbsent(senderId, sender);
                            sender.getMPmap().putIfAbsent(repId, mp);
                        }
                        senderHome.updateCP();

                        List<Integer> receivers = TxLog.txLogSec.get(repId);
                        for (int receiverId : receivers) {
                            /* get home server of a receiver*/
                            ClientApp receiver = ManagementServiceForClient.clientMap.get(receiverId);
                            int receiverHomeId = receiver.getHomeServerId();
                            MecHost receiverHome = ManagementServiceForServer.serverMap.get(receiverHomeId);
                            isDocExist = receiverHome.getCollection().putIfAbsent(document.getDocumentId(), document);
                            isMPExist = receiverHome.getMPmap().containsKey(repId);
                            Result.numberOfCachedDocument++;
                            //If a new document is published, update the server state
                            // even if null, the doc was put to the database
            
                            if (isDocExist == null && !isMPExist) {
                                receiverHome.addUsed(document.getSize());
                                mp = new MessageProcessor();
                                mp.getDocMap().putIfAbsent(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, receiver);
                                receiverHome.getMPmap().putIfAbsent(repId, mp);
                                receiver.getMPmap().putIfAbsent(repId, mp);
                            } else if(isDocExist == null && isMPExist) {
                                receiverHome.addUsed(document.getSize());
                                mp = receiverHome.getMPmap().get(repId);
                                mp.getDocMap().putIfAbsent(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, receiver);
                                receiver.getMPmap().putIfAbsent(repId, mp);
                            } else if (isDocExist != null && !isMPExist){
                                System.out.println("ReceiverWarning");
                            } else { //both Exist
                                Result.saved++;
                                System.out.format("Document %d has already been stored!\n", documentId);
                                mp = receiverHome.getMPmap().get(repId);
                                mp.getDocMap().putIfAbsent(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, receiver);
                                receiver.getMPmap().putIfAbsent(repId, mp);
                            }
                            receiverHome.updateCP();
                        }
                    }else{
                        ClientApp sender = ManagementServiceForClient.clientMap.get(senderId);
                        int senderHomeId = ManagementServiceForClient.clientMap.get(senderId).getHomeServerId();
                        MecHost senderHome = ManagementServiceForServer.serverMap.get(senderHomeId);
                        //new document
                        Document isDocExist = senderHome.getCollection().putIfAbsent(document.getDocumentId(), document);
                        boolean isMPExist = senderHome.getMPmap().containsKey(repId);
                        Result.numberOfCachedDocument++;
                        //If a new document is published, update the server state
                        // even if null, the doc was put to the database
                        //System.out.println("EXIST: " + isMPExist);
                        
                        MessageProcessor mp;
                        if (isDocExist == null && !isMPExist) {
                            senderHome.addUsed(document.getSize());
                            mp = new MessageProcessor();
                            mp.getDocMap().putIfAbsent(documentId, document);
                            mp.getClientMap().putIfAbsent(senderId, sender);
                            senderHome.getMPmap().putIfAbsent(repId, mp);
                            sender.getMPmap().putIfAbsent(repId, mp);
                        } else if(isDocExist == null && isMPExist) {
                            senderHome.addUsed(document.getSize());
                            mp = senderHome.getMPmap().get(repId);
                            mp.getDocMap().putIfAbsent(documentId, document);
                            mp.getClientMap().putIfAbsent(senderId, sender);
                            sender.getMPmap().putIfAbsent(repId, mp);
                        } else if (isDocExist != null && !isMPExist){
                            System.out.format("SenderWarning");
                        } else { //both Exist
                            Result.saved++;
                            System.out.format("Document %d has already been stored!\n", documentId);
                            mp = senderHome.getMPmap().get(repId);
                            mp.getDocMap().putIfAbsent(documentId, document);
                            mp.getClientMap().putIfAbsent(senderId, sender);
                            sender.getMPmap().putIfAbsent(repId, mp);
                        }
                        senderHome.updateCP();
                        //add mp to client
                        if(senderHome.getUsed() > Config.capacityOfServers
                            || senderHome.getCp() > Config.cpLimit){
                            System.out.println("Home Updated");
                            MecHost preHome = senderHome;
                            HashMap<Integer, MessageProcessor> copiedMPMap = sender.getMPmap();
                            double copiedSize = 0;
                            double copiedCP = 0;
                            for(MessageProcessor copiedMP: copiedMPMap.values()){
                                double size = 0;
                                for(Document spooler: copiedMP.getDocMap().values()){
                                    size += spooler.getSize();
                                }
                                copiedSize += size;
                                copiedCP += size * 1;
                            }
                            sender.assignHomeserver(copiedCP, copiedSize);
                            MecHost newHome = ManagementServiceForServer.serverMap.get(sender.getHomeServerId());
                            
                            //add movedMp and copiedMP (mp, docs, connection, used) to newHome
                            newHome.addConnection(1);
                            
                            for(int copiedMPId: copiedMPMap.keySet()){
                                MessageProcessor copiedMP = copiedMPMap.get(copiedMPId);
                                boolean isMPCP = newHome.getMPmap().containsKey(copiedMPId);
                                if(!isMPCP){
                                    MessageProcessor mpcp = new MessageProcessor();
                                    double added = 0;
                                    for(Document spooler: copiedMP.getDocMap().values()){
                                        added += spooler.getSize();
                                    }
                                    newHome.addUsed(added);
                                    for(int i: copiedMP.getDocMap().keySet()){
                                        newHome.getCollection().putIfAbsent(i, copiedMP.getDocMap().get(i));
                                    }
                                    mpcp.setDocMap(copiedMP.getDocMap());
                                    mpcp.getClientMap().put(senderId, sender);
                                    newHome.getMPmap().put(copiedMPId, mpcp);
                                }else{
                                    MessageProcessor mpcp = newHome.getMPmap().get(copiedMPId);
                                    mpcp.getClientMap().put(senderId, sender);
                                    newHome.getMPmap().put(copiedMPId,mpcp);
                                }
                                newHome.updateCP();
                            }

                            for(int copiedMPId: copiedMPMap.keySet()){
                                MessageProcessor mpcp = preHome.getMPmap().get(copiedMPId);
                                if(mpcp == null){
                                    System.out.println("SenderWarning");
                                    continue; //Why?
                                }
                                mpcp.getClientMap().remove(senderId);
                                if(mpcp.getClientMap().size()==0){
                                    double added = 0;
                                    for(Document spooler: mpcp.getDocMap().values()){
                                        added += spooler.getSize();
                                    }
                                    preHome.addUsed(-added);
                                    preHome.getMPmap().remove(copiedMPId);
                                    for(int i: mpcp.getDocMap().keySet()){
                                        preHome.getCollection().remove(i);
                                    }
                                }
                            }
                            preHome.addConnection(-1);
                            preHome.updateCP();
                        }                        

                        
                    
                        List<Integer> receivers = TxLog.txLogSec.get(repId);
                        for (int receiverId : receivers) {
                            /* get home server of a receiver*/
                            ClientApp receiver = ManagementServiceForClient.clientMap.get(receiverId);
                            int receiverHomeId = receiver.getHomeServerId();
                            MecHost receiverHome = ManagementServiceForServer.serverMap.get(receiverHomeId);
                            isDocExist = receiverHome.getCollection().putIfAbsent(document.getDocumentId(), document);
                            isMPExist = receiverHome.getMPmap().containsKey(repId);
                            Result.numberOfCachedDocument++;
                            //If a new document is published, update the server state
                            // even if null, the doc was put to the database
            
                            if (isDocExist == null && !isMPExist) {
                                receiverHome.addUsed(document.getSize());
                                mp = new MessageProcessor();
                                mp.getDocMap().putIfAbsent(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, receiver);
                                receiverHome.getMPmap().putIfAbsent(repId, mp);
                                receiver.getMPmap().putIfAbsent(repId, mp);
                            } else if(isDocExist == null && isMPExist) {
                                receiverHome.addUsed(document.getSize());
                                mp = receiverHome.getMPmap().get(repId);
                                mp.getDocMap().putIfAbsent(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, receiver);
                                receiver.getMPmap().putIfAbsent(repId, mp);
                            } else if (isDocExist != null && !isMPExist){
                                System.out.println("ReceiverWarning");
                            } else { //both Exist
                                Result.saved++;
                                System.out.format("Document %d has already been stored!\n", documentId);
                                mp = receiverHome.getMPmap().get(repId);
                                mp.getDocMap().putIfAbsent(documentId, document);
                                mp.getClientMap().putIfAbsent(receiverId, receiver);
                                receiver.getMPmap().putIfAbsent(repId, mp);
                            }
                            receiverHome.updateCP();
                            

                            if(receiverHome.getUsed() > Config.capacityOfServers
                                || receiverHome.getCp() > Config.cpLimit){
                                MecHost preHome = receiverHome;
                                HashMap<Integer, MessageProcessor> copiedMPMap = receiver.getMPmap();
                                double copiedSize = 0;
                                double copiedCP = 0;
                                for(MessageProcessor copiedMP: copiedMPMap.values()){
                                    double size = 0;
                                    for(Document spooler: copiedMP.getDocMap().values()){
                                        size += spooler.getSize();
                                    }
                                    copiedSize += size;
                                    copiedCP += size * 1;
                                }
                                receiver.assignHomeserver(copiedCP, copiedSize);
                                System.out.printf("Home Updated to %d\n", receiver.getHomeServerId());
                                MecHost newHome = ManagementServiceForServer.serverMap.get(receiver.getHomeServerId());
                                
                                //add movedMp and copiedMP (mp, docs, connection, used) to newHome
                                newHome.addConnection(1);
                                newHome.updateCP();
                                
                                for(int copiedMPId: copiedMPMap.keySet()){
                                    MessageProcessor copiedMP = copiedMPMap.get(copiedMPId);
                                    boolean isMPCP = newHome.getMPmap().containsKey(copiedMPId);
                                    if(!isMPCP){
                                        MessageProcessor mpcp = new MessageProcessor();
                                        double added = 0;
                                        for(Document spooler: copiedMP.getDocMap().values()){
                                            added += spooler.getSize();
                                        }
                                        newHome.addUsed(added);
                                        System.out.printf("%f : Added\n", added);
                                        for(int i: copiedMP.getDocMap().keySet()){
                                            newHome.getCollection().putIfAbsent(i, copiedMP.getDocMap().get(i));
                                        }
                                        mpcp.setDocMap(copiedMP.getDocMap());
                                        mpcp.getClientMap().put(receiverId, receiver);
                                        newHome.getMPmap().put(copiedMPId, mpcp);
                                    }else{
                                        MessageProcessor mpcp = newHome.getMPmap().get(copiedMPId);
                                        mpcp.getClientMap().put(receiverId, receiver);
                                        newHome.getMPmap().put(copiedMPId,mpcp);
                                    }
                                }
                                newHome.updateCP();

                                for(int copiedMPId: copiedMPMap.keySet()){
                                    MessageProcessor mpcp = preHome.getMPmap().get(copiedMPId);
                                    if(mpcp==null){
                                        System.out.println("Warning");
                                        continue; //Why?
                                    }
                                    mpcp.getClientMap().remove(receiverId);
                                    if(mpcp.getClientMap().size()==0){
                                         double added = 0;
                                        for(Document spooler: mpcp.getDocMap().values()){
                                            added += spooler.getSize();
                                        }
                                        System.out.printf("%f : Removed\n", added);
                                        preHome.addUsed(-added);
                                        preHome.getMPmap().remove(copiedMPId);
                                        for(int i: mpcp.getDocMap().keySet()){
                                           preHome.getCollection().remove(i);
                                        }
                                    }
                                }
                                preHome.addConnection(-1);
                                preHome.updateCP();
                            }
                        }
                    }
                }
            }
        }

        if (Constants.TEST) {
            //Constants
            int A = Config.capacityOfServers;
            int B = Config.cpLimit;
            int dc = 5;
            int L = Config.numberOfServers;
            int N = txLog.size();
            int M = ManagementServiceForClient.clientMap.size();
            double gamma = 0.1;
            double gamma_2 = 0.1;

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
            for (Integer serverId : homeClientsMap.keySet()) {
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                Result.aMap.put(serverId, Math.min(1, A / (double) s_l.getUsed()));
            }

            double sum = 0;
            for (double a : Result.aMap.values()) {
                sum += a;
            }
            //Metric.MET_1 =  sum / L;

            //2. Y_2
            for (Integer serverId : homeClientsMap.keySet()) {
                MecHost s_l = ManagementServiceForServer.serverMap.get(serverId);
                Result.bMap.put(serverId, Math.min(1, B / (double) s_l.getCp()));
            }

            sum = 0;
            for (double b : Result.bMap.values()){
                sum += b;
            }
            //Metric.MET_2 = sum / L;
            /*
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
            */
            //3.Y_3
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

            sum = 0;
            for (int serverId : distanceSumMap.keySet()) {
                sum += distanceSumMap.get(serverId);
            }
            //Metric.MET_3 = sum / M;


            //4.Y
            //The same data flow with the data flow in txLog
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

        Metric.MET_1 = TxLog.txLogSec.size();
        Metric.MET_2 = Config.numberOfDocsPerClients;
        Metric.MET_3 = Config.locality;
        Metric.MET_4 = Config.numOfServersInCluster;
        Metric.MET_5 = Config.method;
            

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
            Result.publishedDocument = Result.numberOfSenders * Config.numberOfDocsPerClients;
            Result.rateOfSaved = (double) Result.saved / (double) Result.numberOfCachedDocument;
            Result.meanOfCachedDocs = Result.meanOfUsed / Config.sizeOfDocs;


            FileFactory.saveResult();
            FileFactory.saveMetric();
            FileFactory.saveServerResult();
            FileFactory.saveClientResult();
        }
    }
    }
    }
}
