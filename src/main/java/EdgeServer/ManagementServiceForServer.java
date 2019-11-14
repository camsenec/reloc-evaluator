package EdgeServer;

import Data.Document;
import Model.EdgeServerModel;
import Retrofit.EdgeServerAPI;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

public class ManagementServiceForServer {
    //<application_id -> document_idで指定*/
    private final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Document>> collection = new ConcurrentHashMap<>();

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
    public void registerToServer(MecHost server, double locationX, double locationY, double capacity){

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(locationX));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(locationY));

        RequestBody capacity_str = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(capacity));


        Call<EdgeServerModel> call = service.postServer(
                server.getApplicationId(),
                x,
                y,
                capacity_str);

        call.enqueue(new Callback<EdgeServerModel>() {
            @Override
            public void onResponse(Call<EdgeServerModel> call,
                                   Response<EdgeServerModel> response) {
                EdgeServerModel responseBody = response.body();
                try {
                    //idのセット
                    server.setServerId(responseBody.getServerId());
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<EdgeServerModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


}
