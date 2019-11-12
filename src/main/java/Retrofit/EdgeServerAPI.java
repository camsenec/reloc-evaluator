package Retrofit;

import Model.ClientModel;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface EdgeServerAPI {

    @Multipart
    @POST("api/v1/manager/user/post/")
    Call<ClientModel> postClient(@Query("application_id") int application_id,
                                 @Part("x") RequestBody x,
                                 @Part("y") RequestBody y);

    @POST("api/v1/manager/server/post/")
    Call<ClientModel> postServer(@Query("application_id") int application_id,
                                 @Part("x") RequestBody x,
                                 @Part("y") RequestBody y,
                                 @Part("capacity") RequestBody capacity);




}
