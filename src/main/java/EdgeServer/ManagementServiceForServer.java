package EdgeServer;

import Data.Document;
import Model.EdgeServerModel;
import Retrofit.EdgeServerAPI;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

public class ManagementServiceForServer {
    public static final HashMap<Integer, Document> collection = new HashMap<>();
    public static final HashMap<Integer, MecHost> serverMap = new HashMap<>();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000,TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS).build();

    private final Retrofit retro = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    //create api service
    private final EdgeServerAPI service = retro.create(EdgeServerAPI.class);

    /**
     * API Call
     * @param host Instance of MecHost
     */
    public void registerToServer(MecHost host){


        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(host.getLocation().getX()));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(host.getLocation().getY()));

        RequestBody capacity_body = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(host.getCapacity()));


        Call<EdgeServerModel> call = service.postServer(
                host.getApplicationId(),
                x,
                y,
                capacity_body);

        try {
            Response<EdgeServerModel> response = call.execute();
            System.out.println(response.body());
            host.setServerId(response.body().getServer_id());
            System.out.println("Server " + host.getServerId() + " was registered");
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    public void updateState(MecHost server){

        RequestBody used = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(server.getUsed()));
        
        RequestBody connection = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(server.getConnection()));


        Call<EdgeServerModel> call = service.updateServerStatus(
                server.getApplicationId(),
                server.getServerId(),
                used,
                connection);

        try {
            call.execute();
        }catch(IOException e){
            e.printStackTrace();
        }

    }


}
