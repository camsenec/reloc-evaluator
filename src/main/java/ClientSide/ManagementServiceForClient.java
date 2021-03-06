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
            //System.out.println(response.body());
            client.setHomeServerId(response.body().getHome());
            //System.out.println("Client " + client.getClientId() + " registered");
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

        RequestBody plus_connection_body = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(1));

        RequestBody plus_used_body = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(0));

        Call<ClientModel> call = service.updateHomeOfClient(
                client.getApplicationId(),
                client.getClientId(),
                plus_connection_body,
                plus_used_body);

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

    public void getHomeServerId(ClientApp client, double plus_cp, double plus_used){
        RequestBody plus_cp_body = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(plus_cp));

        RequestBody plus_used_body = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(plus_used));

        Call<ClientModel> call = service.updateHomeOfClient(
                client.getApplicationId(),
                client.getClientId(),
                plus_cp_body,
                plus_used_body);

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

    public void updateState(ClientApp client, int newHomeId){
        RequestBody newHomeIdBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(newHomeId));

      Call<ClientModel> call = service.updateState(
                client.getApplicationId(),
                client.getClientId(),
                newHomeIdBody);

        try {
            Response<ClientModel> response = call.execute();
            client.setHomeServerId(response.body().getHome());
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

    public void updateStrategy(String strategy){

        /*------create request body------*/
        RequestBody strategyBody = RequestBody.create(MediaType.parse("multipart/form-data"),
                strategy);


        Call<ClientModel> call = service.updateStrategy(strategyBody);

        try {
            call.execute();
        }catch(EOFException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }

    }



}
