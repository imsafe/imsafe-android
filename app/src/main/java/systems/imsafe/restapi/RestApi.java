package systems.imsafe.restapi;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import systems.imsafe.models.Image;

public interface RestApi {
    @GET("images/")
    Call<List<Image>> basicLogin();

//    @FormUrlEncoded
//    @POST("images/")
//    Call<Image> sendImage(@Field("image") String image, @Field("name") String name, @Field("description") String description, @Field("password") String password);

    @Multipart
    @POST("images/")
    Call<ResponseBody> sendImage(@Part MultipartBody.Part image, @Part("name") RequestBody name, @Part("description") RequestBody description, @Part("password") RequestBody password);

}