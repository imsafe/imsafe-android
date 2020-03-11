package systems.imsafe.activities;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.models.ImageDecryptionResponse;
import systems.imsafe.restapi.RestApi;
import systems.imsafe.restapi.ServiceGenerator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class ImageDecryptionActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    EditText et_dec_password;
    RestApi restApi;
    Button btn_dec;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_decryption);
        et_dec_password = findViewById(R.id.et_dec_password);
        btn_dec = findViewById(R.id.btn_dec);

        Intent intent = this.getIntent();
        int id = intent.getIntExtra("id",0);

        btn_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrypt(id);
            }
        });
    }


    public void decrypt(Integer imageId) {
        RequestBody password = RequestBody.create(et_dec_password.getText().toString(), MediaType.parse("multipart/form-data"));


        restApi = ServiceGenerator.createService(RestApi.class);
        progressDialog = new ProgressDialog(ImageDecryptionActivity.this);
        progressDialog.setMessage("Decrypting");
        progressDialog.setCancelable(false);
        progressDialog.show();
        Call<ImageDecryptionResponse> call = restApi.decryptImage(imageId.toString(), password);

        call.enqueue(new Callback<ImageDecryptionResponse>() {
            @Override
            public void onResponse(@NotNull Call<ImageDecryptionResponse> call, @NotNull Response<ImageDecryptionResponse> response) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Decrypted successfully", Toast.LENGTH_LONG).show();
                int statusCode = response.code();
                Log.e("Tag", "onResponse response " + response.isSuccessful());
                Log.e("status", "onResponse response " + statusCode);
                assert response.body() != null;
                //Toast.makeText(getApplicationContext(), response.body().getImage(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),ImageViewActivity.class);
                intent.putExtra("decryptedImageUrl", response.body().getImage());
                //Toast.makeText(getApplicationContext(), response.body().getStatus(), Toast.LENGTH_LONG).show();
                startActivity(intent);
            }

            @Override
            public void onFailure(@NotNull Call<ImageDecryptionResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
