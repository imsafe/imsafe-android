package systems.imsafe.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.models.Image;
import systems.imsafe.restapi.RestApi;
import systems.imsafe.restapi.ServiceGenerator;

public class ImageSendActivity extends AppCompatActivity {

    Button selectImage, sendImage;
    ImageView iv_image;
    EditText et_name, et_description, et_password;
    Bitmap bitmap;
    public Uri path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_send);
        initialize();

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send();
            }
        });
    }


    public void initialize() {
        selectImage = findViewById(R.id.selectImage);
        sendImage = findViewById(R.id.sendImage);
        iv_image = findViewById(R.id.image);
        et_name = findViewById(R.id.name);
        et_description = findViewById(R.id.description);
        et_password = findViewById(R.id.password);
    }

    public void showImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 777);
    }

    public void send() {
        //String image = encodeImage();
        File file = new File(path.getPath());
        RequestBody requestBody = RequestBody.create(file, MediaType.parse("*/*"));
        MultipartBody.Part image = MultipartBody.Part.createFormData("name", file.getName(), requestBody);
//        String description = et_description.getText().toString();
//        String password = et_password.getText().toString();
//        LoginActivity loginActivity = new LoginActivity();
//        RestApi restApi = loginActivity.submitData();
        Bundle b = getIntent().getExtras();
        String enteredUsername = b.getString("username");
        String enteredPassword = b.getString("password");
        final RestApi restApi =
                ServiceGenerator.createService(RestApi.class, enteredUsername, enteredPassword);

        //RequestBody image = RequestBody.create(file, MediaType.parse("application/octet-stream"));
        RequestBody name = RequestBody.create(et_name.getText().toString(), MediaType.parse("text/plain"));
        RequestBody description = RequestBody.create(et_description.getText().toString(), MediaType.parse("text/plain"));
        RequestBody password = RequestBody.create(et_password.getText().toString(), MediaType.parse("text/plain"));
        //Call<Image> call = restApi.sendImage(image, name, description, password);


        //RequestBody requestFile = RequestBody.create(getRealPathFromURI(path, this), MediaType.parse("multipart/form-data"));
//        RequestBody requestFile = RequestBody.create(file, MediaType.parse("multipart/form-data"));
//        MultipartBody.Part multipartBody = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
//        Call<ResponseBody> call = restApi.sendImage(multipartBody, name, description, password);
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("filePart", file.getName(), requestFile);

        //Call<ResponseBody> call = restApi.sendImage(filePart, name, description, password);
        Call<ResponseBody> call = restApi.sendImage(image, name, description, password);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 777 && resultCode == RESULT_OK && data != null) {
            path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                iv_image.setImageBitmap(bitmap);
                iv_image.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String encodeImage() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public String getRealPathFromURI(Uri contentURI, Activity context) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }
}
