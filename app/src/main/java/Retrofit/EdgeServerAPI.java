package Retrofit;

import Model.ClientModel;
import Model.EdgeServerModel;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface EdgeServerAPI {

    @Multipart
    @POST("api/v1/manager/server/post/")
    Call<EdgeServerModel> postServer(@Query("application_id") int application_id,
                                     @Part("x") RequestBody x,
                                     @Part("y") RequestBody y,
                                     @Part("capacity") RequestBody capacity);

    @Multipart
    @PUT("api/v1/manager/server/update_state/")
    Call<EdgeServerModel> updateServerStatus(@Query("application_id") int application_id,
                                             @Query("server_id") int server_id,
                                             @Part("used") RequestBody used,
                                             @Part("connection") RequestBody connection,
                                             @Part("cp") RequestBody cp);


    @Multipart
    @POST("api/v1/manager/user/post/")
    Call<ClientModel> postClient(@Query("application_id") int application_id,
                                 @Part("x") RequestBody x,
                                 @Part("y") RequestBody y);

    @Multipart
    @POST("api/v1/manager/user/post_from_simulator/")
    Call<ClientModel> postClientWithId(@Query("application_id") int application_id,
                                 @Part("client_id") RequestBody client_id,
                                 @Part("x") RequestBody x,
                                 @Part("y") RequestBody y);

    @Multipart
    @PUT("api/v1/manager/user/update_location/")
    Call<ClientModel> updateLocationOfClient(@Query("application_id") int application_id,
                                 @Query("client_id") int client_id,
                                 @Part("x") RequestBody x,
                                 @Part("y") RequestBody y);

    @Multipart
    @PUT("api/v1/manager/user/update_home/")
    Call<ClientModel> updateHomeOfClient(@Query("application_id") int application_id,
                                         @Query("client_id") int client_id,
                                         @Part("plus_cp") RequestBody plus_cp,
                                         @Part("plus_used") RequestBody plus_used);

    @Multipart
    @PUT("api/v1/manager/user/update_state/")
    Call<ClientModel> updateState(@Query("application_id") int application_id,
                                  @Query("client_id") int client_id,
                                  @Part("home") RequestBody newHomeId);
    

    @Multipart
    @PUT("api/v1/manager/area/update_number_of_coopserver/")
    Call<ClientModel> updateNumOfCoopServer(@Part("number_of_coopserver") RequestBody numOfCluster);

    @Multipart
    @PUT("api/v1/manager/area/update_strategy/")
    Call<ClientModel> updateStrategy(@Part("strategy") RequestBody strategy);

    
    @DELETE("api/v1/manager/user/delete_all/")
    Call<Void> deleteClient(@Query("application_id") int application_id);

    @DELETE("api/v1/manager/server/delete_all/")
    Call<Void> deleteServer(@Query("application_id") int application_id);

    @DELETE("api/v1/manager/cluster/delete_all/")
    Call<Void> deleteCluster(@Query("application_id") int application_id);
    





}
