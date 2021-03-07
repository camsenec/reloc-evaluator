package FileIO;

import ClientSide.ClientApp;
import ClientSide.ManagementServiceForClient;
import EdgeServer.ManagementServiceForServer;
import EdgeServer.MecHost;
import Field.Point2D;
import Logger.TxLog;
import Result.Result;
import Utility.Tuple;
import Result.Metric;

import java.io.*;
import java.util.ArrayList;

import static Constants.Constants.DEBUG;
import Config.Config;

public class FileFactory {


    /**
     * read from local file
     */
    public static void loadLogFile(String fileName){

        /*---------read from local file---------*/
        try (BufferedReader reader = new BufferedReader(new FileReader("./Log/" + fileName))){
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                line = line.replace("\"[", "");
                line = line.replace("]\"", "");
                line = line.replace(" ", "");
                String[] data = line.split(",", -1);
                ArrayList<Integer> sendToSec = new ArrayList<>();
                int client_id = Integer.parseInt(data[1]);
                for (int i = 2; i < data.length; i++) {
                    sendToSec.add(Integer.parseInt(data[i]));
                }
                TxLog.txLogSec.put(client_id, sendToSec);

                ArrayList<Integer> groupMember = new ArrayList<>();
                int rep_id = Integer.parseInt(data[1]);
                for (int i = 1; i < data.length; i++) {
                    groupMember.add(Integer.parseInt(data[i]));
                }
                for(int member_id : groupMember){
                    int sender_id = member_id;
                    ArrayList<Integer> sendTo = new ArrayList<>();
                    for(int receiver_id: groupMember){
                        if(receiver_id != sender_id){
                            sendTo.add(receiver_id);
                        }
                    }
                    TxLog.txLog.put(new Tuple<Integer, Integer>(rep_id, sender_id), sendTo);
                }
                count++;
            }
            System.out.println(count + " Transactions were loaded");

        } catch (IOException e) {
            e.printStackTrace();
        }

        if(DEBUG) {
            for (Tuple<Integer, Integer> key : TxLog.txLog.keySet()) {
                ArrayList<Integer> tmp = TxLog.txLog.get(key);
                System.out.print(key + ":");
                for (int sendto : tmp) {
                    System.out.print(sendto);
                    System.out.print(" ");

                }
                System.out.print("\n");
            }
            System.out.println(TxLog.txLog.size());
        }

    }

    public static void loadServerState(String fileName, int capacityOfServer){
        try (BufferedReader reader = new BufferedReader(new FileReader("./Cache/" + fileName))){
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                int application_id = Integer.parseInt(data[0]);
                int server_id = Integer.parseInt(data[1]);
                double x = Double.parseDouble(data[2]);
                double y = Double.parseDouble(data[3]);
                Point2D location = new Point2D(x,y);
                int clusterId = Integer.parseInt(data[4]);

                MecHost server = new MecHost(application_id);
                server.setServerId(server_id);
                server.setLocation(location);
                server.setCapacity(capacityOfServer);
                server.setClusterId(clusterId);
                ManagementServiceForServer.serverMap.put(server.getServerId(), server);
                count++;
            }
            System.out.println(count + " Servers were loaded");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadClientState(String fileName){
        try (BufferedReader reader = new BufferedReader(new FileReader("./Cache/" + fileName))){
            String line;
            int count = 0;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                int application_id = Integer.parseInt(data[0]);
                int client_id = Integer.parseInt(data[1]);
                double x = Double.parseDouble(data[2]);
                double y = Double.parseDouble(data[3]);
                int home = Integer.parseInt(data[4]);
                Point2D location = new Point2D(x,y);
                ClientApp client = new ClientApp(application_id, client_id);
                client.setLocation(location);
                client.setHomeServerId(home);
                ManagementServiceForClient.clientMap.put(client.getClientId(), client);
                count++;
            }
            System.out.println(count + " Clients were loaded");
            Result.numberOfClient = count;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveServerState(){
        try {
            FileWriter f = new FileWriter("./Cache/serverCache.csv", false);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));

            for(int serverId : ManagementServiceForServer.serverMap.keySet()){
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                p.print(server.getApplicationId());
                p.print(",");
                p.print(server.getServerId());
                p.print(",");
                p.print(server.getLocation().getX());
                p.print(",");
                p.print(server.getLocation().getY());
                p.print(",");
                p.print(server.getClusterId());
                p.println();
                
            }
            p.close();

            System.out.println("Server states are saved to file！");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveClientState(){
        try {

            FileWriter f = new FileWriter("./Cache/clientCache.csv", false);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));
            for(int clientId : ManagementServiceForClient.clientMap.keySet()){
                ClientApp client = ManagementServiceForClient.clientMap.get(clientId);
                p.print(client.getApplicationId());
                p.print(",");
                p.print(client.getClientId());
                p.print(",");
                p.print(client.getLocation().getX());
                p.print(",");
                p.print(client.getLocation().getY());
                p.print(",");
                p.print(client.getHomeServerId());
                p.println();
            }
            p.close();

            System.out.println("Client states are saved to file！");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void saveResult(){
        try {
            FileWriter f = new FileWriter("./Result/result.csv", true);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));
            
            p.printf("%d",Metric.MET_1);
            p.print(",");
            p.printf("%d",Metric.MET_2);
            p.print(",");
            p.printf("%d",Metric.MET_3);
            p.print(",");
            p.printf("%d",Metric.MET_4);
            p.print(",");
            p.printf("%s",Metric.MET_5);
            p.print(",");
            p.print(Result.numberOfSenders);
            p.print(",");
            p.print(Result.numberOfClient);
            p.print(",");
            p.print(Result.publishedDocument);
            p.print(",");
            p.print(Result.numberOfCachedDocument);
            p.print(",");
            p.printf("%.2f",Result.meanOfUsed);
            p.print(",");
            p.print(Result.minOfUsed);
            p.print(",");
            p.print(Result.maxOfUsed);
            p.print(",");
            p.print(Result.saved);
            p.print(",");
            p.printf("%.10f", Result.rateOfSaved);

            p.println();
            p.close();

            System.out.println("Result are saved to file！");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveServerResult(){
       try {
            String filename = String.format("server-group-%d-doc-%d-loc-%d-cluster-%d-method-%s.csv", 
                Result.numberOfGroups, Config.numberOfDocsPerClients, Config.locality, 
                Config.numOfServersInCluster, Config.method);
            FileWriter f = new FileWriter("./Result/" + filename, false);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));

            for(int serverId : ManagementServiceForServer.serverMap.keySet()){
                MecHost server = ManagementServiceForServer.serverMap.get(serverId);
                p.printf("%d",Metric.MET_1);
                p.print(",");
                p.printf("%d",Metric.MET_2);
                p.print(",");
                p.printf("%d",Metric.MET_3);
                p.print(",");
                p.printf("%d",Metric.MET_4);
                p.print(",");
                p.printf("%s",Metric.MET_5);
                p.print(",");
                p.print(server.getApplicationId());
                p.print(",");
                p.print(server.getServerId());
                p.print(",");
                p.print(server.getLocation().getX());
                p.print(",");
                p.print(server.getLocation().getY());
                p.print(",");
                p.print(server.getCapacity());
                p.print(",");
                p.printf("%.4f", server.getUsed());
                p.print(",");
                p.print(server.getConnection());
                p.print(",");
                p.printf("%.4f", server.getCp());
                p.print(",");
                p.print(server.getMPmap().size());
                p.print(",");
                p.print(server.getClusterId());
                p.print(",");
                p.print(Result.aMap.get(serverId));
                p.print(",");
                p.print(Result.bMap.get(serverId));
                p.print(",");
                p.print(Result.distanceMap.get(serverId));
                p.print(",");
                p.println();
            }
            p.close();

            System.out.println("Server results are saved to file！");

        } catch (IOException e) {
            e.printStackTrace();
        } 
    }

    public static void saveClientResult(){
        try {

            String filename = String.format("client-group-%d-doc-%d-loc-%d-cluster-%d-method-%s.csv", 
                Result.numberOfGroups, Config.numberOfDocsPerClients, Config.locality, 
                Config.numOfServersInCluster, Config.method);
            FileWriter f = new FileWriter("./Result/" + filename, false);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));
            for(int clientId : ManagementServiceForClient.clientMap.keySet()){
                ClientApp client = ManagementServiceForClient.clientMap.get(clientId);
                p.printf("%d",Metric.MET_1);
                p.print(",");
                p.printf("%d",Metric.MET_2);
                p.print(",");
                p.printf("%d",Metric.MET_3);
                p.print(",");
                p.printf("%d",Metric.MET_4);
                p.print(",");
                p.printf("%s",Metric.MET_5);
                p.print(",");
                p.print(client.getApplicationId());
                p.print(",");
                p.print(client.getClientId());
                p.print(",");
                p.print(client.getLocation().getX());
                p.print(",");
                p.print(client.getLocation().getY());
                p.print(",");
                p.print(client.getHomeServerId());
                p.println();
            }
            p.close();

            System.out.println("Client results are saved to file！");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveMetric(){
        try {
            FileWriter f = new FileWriter("./Result/metrics.csv", true);
            PrintWriter p = new PrintWriter(new BufferedWriter(f));

            p.printf("%d",Metric.MET_1);
            p.print(",");
            p.printf("%d",Metric.MET_2);
            p.print(",");
            p.printf("%d",Metric.MET_3);
            p.print(",");
            p.printf("%d",Metric.MET_4);
            p.print(",");
            p.printf("%s",Metric.MET_5);
            p.print(",");
            p.printf("%.6f",Metric.MET_6);

            p.println();
            p.close();

            System.out.println("Result are saved to file！");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


