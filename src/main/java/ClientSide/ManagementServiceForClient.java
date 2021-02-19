package ClientSide;

import Model.ClientModel;
import Retrofit.EdgeServerAPI;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static Constants.Constants.BASE_URL;

public class ManagementServiceForClient {

    public static final HashMap<Integer, ClientApp> clientMap = new HashMap<>();

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
     * @param client Instance of ClientApp
     */
    public void registerToServer(ClientApp client){

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
            client.setHomeServerId(response.body().getHome());
            System.out.println("Client " + client.getClientId() + " registered");
        }catch(EOFException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    } 

    /**
     * API Call
     * register location to management server
     **/

    public void registerLocationToServer(ClientApp client){

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getX()));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(client.getLocation().getY()));

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
     * get home server id
     * @param client instance of client
     *
     */

    public void getHomeServerId(ClientApp client){

        Call<ClientModel> call = service.updateHomeOfClient(
                client.getApplicationId(),
                client.getClientId());

        try {
            Response<ClientModel> response = call.execute();
            client.setHomeServerId(response.body().getHome());
            System.out.println("Response: " + response.body());
            System.out.println("Client " + client.getClientId() + " 's Home was obtained");
        }catch(EOFException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }


    public void updateNumberOfCoopServer(int number_of_coopserver){

        /*------create request body------*/
        RequestBody numOfCoopServer = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(number_of_coopserver));


        Call<ClientModel> call = service.updateNumOfCoopServer(numOfCoopServer);

        try {
            call.execute();
        }catch(EOFException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }



}
