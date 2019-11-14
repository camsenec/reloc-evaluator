package ClientSide;

import Model.ClientModel;
import Retrofit.EdgeServerAPI;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

public class ManagementServiceForClient {

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
     * @param applicationId アプリケーションのid
     */
    public void registerToServer(ClientApp client, double locationX, double locationY){

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(locationX));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(locationY));


        Call<ClientModel> call = service.postClient(
                client.applicationId,
                x,
                y);

        call.enqueue(new Callback<ClientModel>() {
            @Override
            public void onResponse(Call<ClientModel> call,
                                   Response<ClientModel> response) {
                ClientModel responseBody = response.body();
                try {
                    //idのセット
                    client.clientId = responseBody.getClientId();
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ClientModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    /**
     * API Call
     * serverに現在位置を反映させる
     *
     */

    public void registerLocationToServer(ClientApp client, double locationX, double locationY){

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(locationX));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(locationY));

        /*------create call-------*/


        Call<ClientModel> call = service.updateLocationOfClient(
                client.applicationId,
                client.clientId,
                x,
                y);

        call.enqueue(new Callback<ClientModel>() {
            @Override
            public void onResponse(Call<ClientModel> call,
                                   Response<ClientModel> response) {
                ClientModel responseBody = response.body();
            }

            @Override
            public void onFailure(Call<ClientModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    /**
     * API Call
     * serverに現在位置を反映させる
     * @param client APIを呼び出すクライアントのインスタンス
     *
     */

    public void getHomeServerId(ClientApp client){

        /*------create call------*/


        Call<ClientModel> call = service.updateHomeOfClient(
                client.applicationId,
                client.clientId);

        call.enqueue(new Callback<ClientModel>() {
            @Override
            public void onResponse(Call<ClientModel> call,
                                   Response<ClientModel> response) {
                ClientModel responseBody = response.body();
                int home = responseBody.getHome();
                client.homeServerId = home;
            }

            @Override
            public void onFailure(Call<ClientModel> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public EdgeServerAPI getService() {
        return service;
    }


}
