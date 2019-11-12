package ClientSide;

import Data.Document;
import HTTP.HTTPResponseMetaData;
import Meta.DocumentIds;
import Meta.FieldManager;
import EdgeServer.EdgeServer;
import Field.Point2D;
import Meta.ServerManager;
import Model.ClientModel;
import jdk.internal.joptsimple.internal.Strings;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import Retrofit.EdgeServerAPI;

public class Client {
    int id;
    Point2D location;
    EdgeServer nearestServer;

    public static String BASE_URL = "http://127.0.0.1:8000/";

    public Client(Point2D location) {
        this.location = location;
    }

    public void postToServer(int application_id){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10000, TimeUnit.SECONDS)
                .readTimeout(10000,TimeUnit.SECONDS)
                .writeTimeout(10000, TimeUnit.SECONDS).build();

        Retrofit retro = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //create api service
        final EdgeServerAPI service = retro.create(EdgeServerAPI.class);

        /*------create request body------*/

        RequestBody x = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(location.getX()));

        RequestBody y = RequestBody.create(MediaType.parse("multipart/form-data"),
                String.valueOf(location.getY()));

        System.out.println("body created");


        Call<ClientModel> call = service.postClient(
                application_id,
                x,
                y);

        call.enqueue(new Callback<ClientModel>() {
            @Override
            public void onResponse(Call<ClientModel> call,
                                   Response<ClientModel> response) {
                ClientModel responseBody = response.body();
                System.out.println(responseBody);
                //id = responseBody.getClient_id();

                System.out.println(responseBody.getClient_id());

            }

            @Override
            public void onFailure(Call<ClientModel> call, Throwable t) {
                System.out.println("failure");
                t.printStackTrace();
            }
        });

    }

    //暫定実装
    public void updateLocation() {
        Random random = new Random();
        double areaLengthX = FieldManager.MAX_X - FieldManager.MIN_X;
        double areaLengthY = FieldManager.MAX_Y - FieldManager.MIN_Y;

        this.location.setX((location.getX()
                + random.nextGaussian()) % areaLengthX + FieldManager.MIN_X);
        this.location.setY((location.getY()
                + random.nextGaussian()) % areaLengthY + FieldManager.MIN_Y);

        if(location.getX() < 0) this.location.setX(location.getX() + areaLengthX);
        if(location.getY() < 0) this.location.setY(location.getY() + areaLengthY);
    }

    //run at regular intervals
    public void updateNearestServer() {
        int id = ServerManager.findNearestServer(this.location);
        this.nearestServer = ServerManager.serverMap.get(id);
        System.out.println("location : " +  this.location + "  NSid : " + nearestServer.getId() + "  NSloc : " + nearestServer.getLocation());
    }

    /*
     Only metadata is enough for return value on simulation
     */
    public HTTPResponseMetaData GET(UUID documentId) {
        /** 暫定実装（最も近いサーバーからデータを取りに行く **/
        Document responseBody = this.nearestServer.getCollection().get(documentId);

        double responseTime = 0.0;
        double transmissionCost = 0.0;

        if (responseBody == null) {
            /**
             * 暫定実装
             * need an algorithm (can be contribution)
             * documentbase : response = document.cachedServer.HTTPRequestForward()
             **/

            /* create meta data */
            responseTime += 0.1;
            transmissionCost += 0.1;

            return new HTTPResponseMetaData(responseTime, transmissionCost);
        } else {
            return new HTTPResponseMetaData(responseTime, transmissionCost);
        }

    }

    public HTTPResponseMetaData POST() {
        /** 暫定実装 **/
        int serverId = this.nearestServer.getId();

        /** マルチスレッドの場合, requested serverに対する排他制御が必要 */
        EdgeServer requestedServer = ServerManager.serverMap.get(serverId);
        UUID uuid = UUID.randomUUID();
        Document document = new Document(uuid, this.id);
        requestedServer.getCollection().put(uuid, document);
        //System.out.println("DEBUG" + " " + serverId + " " + requestedServer.getCollection().size());
        requestedServer.updateRemain(uuid);

        DocumentIds.ids.add(uuid);

        return new HTTPResponseMetaData(0,0);
    }

    public void setId(int id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return String.format("id : %d\t\tlocation : (%6.2f, %6.2f)\t\tNS : %d", id, location.getX(), location.getY()
                ,this.nearestServer.getId());
    }

    public EdgeServer getNearestServer() {
        return nearestServer;
    }
}
