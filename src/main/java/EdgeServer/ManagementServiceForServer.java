package EdgeServer;

import Data.Document;
import Model.EdgeServerModel;
import Retrofit.EdgeServerAPI;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

public class ManagementServiceForServer {
    //<application_id -> document_idで指定*/
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Document>> collection = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, MecHost>> serverMap = new ConcurrentHashMap<>();

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
     * @param server MECHostのインスタンス
     */
    public void registerToServer(MecHost server){

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(server.getLocation().getX()));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(server.getLocation().getY()));

        RequestBody capacity_body = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(server.getCapacity()));

        System.out.println(x);


        Call<EdgeServerModel> call = service.postServer(
                server.getApplicationId(),
                x,
                y,
                capacity_body);

        try {
            call.execute();
        }catch(IOException e){
            e.printStackTrace();
        }

    }


}
