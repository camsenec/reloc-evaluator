package Plugin;

import java.util.HashMap;

import Config.Config;
import MP.MessageProcessor;
import Data.Document;
import EdgeServer.ManagementServiceForServer;
import EdgeServer.MecHost;
import ClientSide.ClientApp;

public class Cooporation {

    public static void reallocateMP(ClientApp sender, MecHost senderHome){
        if(senderHome.getUsed() > Config.capacityOfServers * 0.8
            || senderHome.getCp() > Config.cpLimit * 0.8){
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
            System.out.printf("client %d's home server is updated from server %d to server %d\n", sender.getClientId(), preHome.getServerId(), newHome.getServerId());
            
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
                    mpcp.getClientMap().put(sender.getClientId(), sender);
                    newHome.getMPmap().put(copiedMPId, mpcp);
                }else{
                    MessageProcessor mpcp = newHome.getMPmap().get(copiedMPId);
                    mpcp.getClientMap().put(sender.getClientId(), sender);
                    newHome.getMPmap().put(copiedMPId,mpcp);
                }
                newHome.updateCP();
            }

            for(int copiedMPId: copiedMPMap.keySet()){
                MessageProcessor mpcp = preHome.getMPmap().get(copiedMPId);
                if(mpcp == null){
                    System.out.println("SenderWarning");
                    continue;
                }
                mpcp.getClientMap().remove(sender.getClientId());
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
                preHome.addConnection(-1);
                preHome.updateCP();
            }
        }               
    }
}
