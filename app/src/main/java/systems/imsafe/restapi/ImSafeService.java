package systems.imsafe.restapi;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import systems.imsafe.models.Image;
import systems.imsafe.models.ImageDecryptionResponse;
import systems.imsafe.models.ImageEncryptionResponse;
import systems.imsafe.models.User;

public interface ImSafeService {

    @GET("images/")
    Call<List<Image>> getImageList();

    @Multipart
    @POST("images/")
    Call<ImageEncryptionResponse> encryptImage(@Part MultipartBody.Part image,
                                               @Part("name") RequestBody name,
                                               @Part("description") RequestBody description,
                                               @Part("password") RequestBody password);

    @Multipart
    @POST("images/{id}/decrypt/")
    Call<ImageDecryptionResponse> decryptImage(@Path("id") String id, @Part("password") RequestBody password);

    @Multipart
    @POST("users/")
    Call<ResponseBody> createAccount(@Part("username") RequestBody username,
                                     @Part("email") RequestBody email,
                                     @Part("password") RequestBody password,
                                     @Part("first_name") RequestBody firstName,
                                     @Part("last_name") RequestBody lastName);

    @Multipart
    @POST("search-user/")
    Call<ArrayList<User>> search(@Part("name") RequestBody name);

    @GET("followings/")
    Call<ArrayList<User>> getFollowingList();

    @GET("followers/")
    Call<ArrayList<User>> getFollowerList();

    @Multipart
    @POST("follow/")
    Call<ResponseBody> follow(@Part("user_id") RequestBody userId);

    @Multipart
    @POST("unfollow/")
    Call<ResponseBody> unfollow(@Part("user_id") RequestBody userId);
}