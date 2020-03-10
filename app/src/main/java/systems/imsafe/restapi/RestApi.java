package systems.imsafe.restapi;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import systems.imsafe.models.Image;
import systems.imsafe.models.ImageDecryptionResponse;
import systems.imsafe.models.ImagePostResponse;

public interface RestApi {
    @GET("images/")
    Call<List<Image>> basicLogin();

    @Multipart
    @POST("images/")
    Call<ImagePostResponse> sendImage(@Part MultipartBody.Part image,
                                      @Part("name") RequestBody name,
                                      @Part("description") RequestBody description,
                                      @Part("password") RequestBody password);


    @Multipart
    @POST("images/{id}/decrypt/")
    Call<ImageDecryptionResponse> decryptImage(@Path("id") String id, @Part("password") RequestBody password);

}