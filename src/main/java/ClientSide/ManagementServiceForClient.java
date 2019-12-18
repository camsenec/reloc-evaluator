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

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

public class ManagementServiceForClient {

    public static ArrayList<ClientApp> clientList = new ArrayList<>();

    private final OkHttpClient okhttp = new OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .readTimeout(10000,TimeUnit.SECONDS)
            .writeTimeout(10000, TimeUnit.SECONDS).build();

    private final Retrofit retro = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okhttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    //create api service
    private final EdgeServerAPI service = retro.create(EdgeServerAPI.class);


    /**
     * API Call
     * @param client クライアントのインスタンス
     */
    public void registerToServer(ClientApp client){

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getX()));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getY()));


        Call<ClientModel> call = service.postClient(
                client.getApplicationId(),
                x,
                y);

        try {
            Response<ClientModel> response = call.execute();
            System.out.println(response);
            client.setClientId(response.body().getClientId());
        }catch(IOException e){
            e.printStackTrace();
        }



    }

    /**
     * API Call
     * serverに現在位置を反映させる
     *
     */

    public void registerLocationToServer(ClientApp client){

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getX()));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getY()));

        /*------create call-------*/


        Call<ClientModel> call = service.updateLocationOfClient(
                client.getApplicationId(),
                client.getClientId(),
                x,
                y);

        try {
            call.execute();
        }catch(IOException e){
            e.printStackTrace();
        }
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
                client.getApplicationId(),
                client.getClientId());

        try {
            Response<ClientModel> response = call.execute();
            System.out.println(response);
            client.setHomeServerId(response.body().getHome());
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    /**
     * API Call
     * @param client クライアントのインスタンス
     */
    public void registerToServerWithId(ClientApp client){

        /*------create request body------*/
        RequestBody client_id = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getClientId()));

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getX()));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getY()));


        Call<ClientModel> call = service.postClientWithId(
                client.getApplicationId(),
                client_id,
                x,
                y);

        try {
            Response<ClientModel> response = call.execute();
            System.out.println(response);
            client.setHomeServerId(response.body().getHome());
        }catch(IOException e){
            e.printStackTrace();
        }

    }


}
