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
    @PUT("api/v1/manager/server/update_used/")
    Call<EdgeServerModel> updateServerStatus(@Query("application_id") int application_id,
                                             @Query("server_id") int server_id,
                                             @Part("capacity") RequestBody capacity,
                                             @Part("used") RequestBody used);



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
                                         @Query("client_id") int client_id);

    @Multipart
    @PUT("api/v1/manager/area/update_number_of_coopserver/")
    Call<ClientModel> updateNumOfCoopServer(@Part("number_of_coopserver") RequestBody numOfCluster);




}
