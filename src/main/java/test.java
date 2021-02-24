import java.util.HashMap;
import java.util.Random;


public class test {

    public static void main(String[] args) {
       Random random = new Random(1);
       for(int i = 0; i < 10; i++){
           System.out.println(random.nextDouble());
       }
    }

        /*
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
        */
}
