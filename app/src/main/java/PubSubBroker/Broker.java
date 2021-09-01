package PubSubBroker;

import java.util.List;

import ClientSide.ClientApp;
import ClientSide.ManagementServiceForClient;
import Data.Document;
import EdgeServer.ManagementServiceForServer;
import EdgeServer.MecHost;
import Logger.TxLog;
import MP.MessageProcessor;
import Result.Result;
import Plugin.Cooporation;

public class Broker {

    public static void publish(ClientApp sender, MecHost senderHome, Document document, int repId, boolean isCooporationEnabled){
        Document isDocExist = senderHome.getCollection().putIfAbsent(document.getDocumentId(), document);
        boolean isMPExist = senderHome.getMPmap().containsKey(sender.getClientId());
        Result.numberOfCachedDocument++;
        
        if (isDocExist == null && !isMPExist) {
            senderHome.addUsed(document.getSize());
            MessageProcessor mp = new MessageProcessor();
            mp.getDocMap().putIfAbsent(document.getDocumentId(), document);
            mp.getClientMap().putIfAbsent(sender.getClientId(), sender);
            senderHome.getMPmap().putIfAbsent(repId, mp);
            sender.getMPmap().putIfAbsent(repId, mp);
            System.out.printf("\n");
        } else if(isDocExist == null && isMPExist) {
            senderHome.addUsed(document.getSize());
            MessageProcessor mp = senderHome.getMPmap().get(sender.getClientId());
            mp.getDocMap().putIfAbsent(document.getDocumentId(), document);
            mp.getClientMap().putIfAbsent(sender.getClientId(), sender);
            sender.getMPmap().putIfAbsent(repId, mp);
            System.out.printf("\n");
        } else if (isDocExist != null && !isMPExist){
            System.out.format("Unexpected error occured.");
        } else {
            Result.saved++;
            System.out.format("Document %d has already been stored (Capacity saved!)\n", document.getDocumentId());
            MessageProcessor mp = senderHome.getMPmap().get(sender.getClientId());
            mp.getDocMap().putIfAbsent(document.getDocumentId(), document);
            mp.getClientMap().putIfAbsent(sender.getClientId(), sender);
            sender.getMPmap().putIfAbsent(sender.getClientId(), mp);
        }
        senderHome.updateCP();
        
        if(isCooporationEnabled) Cooporation.reallocateMP(sender, senderHome);
        Broker.forward(document, repId, isCooporationEnabled);
    }
    
    private static void forward(Document document, int repId, boolean isCooporationEnabled){

        List<Integer> receivers = TxLog.txLogSec.get(repId);
        for (int receiverId : receivers) {
            ClientApp receiver = ManagementServiceForClient.clientMap.get(receiverId);
            int receiverHomeId = receiver.getHomeServerId();
            MecHost receiverHome = ManagementServiceForServer.serverMap.get(receiverHomeId);
            Document isDocExist = receiverHome.getCollection().putIfAbsent(document.getDocumentId(), document);
            boolean isMPExist = receiverHome.getMPmap().containsKey(repId);
            Result.numberOfCachedDocument++;
            System.out.printf("Forward to receiver %d's home server ", receiverId);

            if (isDocExist == null && !isMPExist) {
                receiverHome.addUsed(document.getSize());
                MessageProcessor mp = new MessageProcessor();
                mp.getDocMap().putIfAbsent(document.getDocumentId(), document);
                mp.getClientMap().putIfAbsent(receiverId, receiver);
                receiverHome.getMPmap().putIfAbsent(repId, mp);
                receiver.getMPmap().putIfAbsent(repId, mp);
                System.out.printf("\n");
            } else if(isDocExist == null && isMPExist) {
                receiverHome.addUsed(document.getSize());
                MessageProcessor mp = receiverHome.getMPmap().get(repId);
                mp.getDocMap().putIfAbsent(document.getDocumentId(), document);
                mp.getClientMap().putIfAbsent(receiverId, receiver);
                receiver.getMPmap().putIfAbsent(repId, mp);
                System.out.printf("\n");
            } else if (isDocExist != null && !isMPExist){
                System.out.println("ReceiverWarning");
            } else { //both Exist
                Result.saved++;
                System.out.format("(Document %d has already been stored [capacity saved!])\n", document.getDocumentId());
                MessageProcessor mp = receiverHome.getMPmap().get(repId);
                mp.getDocMap().putIfAbsent(document.getDocumentId(), document);
                mp.getClientMap().putIfAbsent(receiverId, receiver);
                receiver.getMPmap().putIfAbsent(repId, mp);
            }
            receiverHome.updateCP();
            if(isCooporationEnabled) Cooporation.reallocateMP(receiver, receiverHome);
        }

    }
}
