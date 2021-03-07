import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Random;

import Utility.Tuple;


public class test {

    public static void main(String[] args) throws IOException, InterruptedException {
       //HashMap<Tuple<Integer, Integer>, Integer> a = new HashMap<>();
       //a.put(new Tuple<Integer,Integer>(1,2), 1);
       //a.put(new Tuple<Integer,Integer>(2,1), 1);
       //a.put(new Tuple<Integer,Integer>(3,2), 1);
       //a.put(new Tuple<Integer,Integer>(2,3), 1);
       //System.out.println(a);
       String curl = "curl --request DELETE 'http://127.0.0.1:8000/api/v1/manager/server/delete_all/?application_id=1";
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(curl);
            System.out.println(curl);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream())); //BufferedReader to read the output
            StringBuilder sb = new StringBuilder(); //What will hold the entire console output
            String line = ""; //What will hold the text for a line of the output
            while ((line = reader.readLine()) != null) { //While there is still text to be read, read it
                sb.append(line + "\n"); //Append the line to the StringBuilder
            }
            System.out.println(sb); //Print out the full output
        } catch (Exception e) {
            e.printStackTrace();

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
