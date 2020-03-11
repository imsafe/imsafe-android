package systems.imsafe.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.adapters.ImageAdapter;
import systems.imsafe.models.Image;
import systems.imsafe.models.ImageDecryptionResponse;
import systems.imsafe.restapi.RestApi;
import systems.imsafe.restapi.ServiceGenerator;
import systems.imsafe.utils.ImagePasswordDialog;

public class ImageListActivity extends AppCompatActivity implements ImagePasswordDialog.ImagePasswordDialogListener {
    ListView lvImageList;
    RestApi restApi;
    private ImageAdapter imageAdapter;
    private ProgressDialog progressDialog;
    private FloatingActionButton fab;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        fab = findViewById(R.id.floatingActionButton);
        lvImageList = findViewById(R.id.lv_image);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        List<Image> images = null;

        if (bundle != null) {
            images = (List<Image>) bundle.getSerializable("images");
        }
        assert images != null;

        ArrayList<Image> imageList = new ArrayList<>(images);

        imageAdapter = new ImageAdapter(this, imageList);
        lvImageList.setAdapter(imageAdapter);
        lvImageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(ImageList2Activity.this, imageAdapter.getItem(position).getId().toString(), Toast.LENGTH_SHORT).show();
                //openDialog();
                //decrypt(imageAdapter.getItem(position).getId());
                Intent intent = new Intent(getApplicationContext(),ImageDecryptionActivity.class);
                intent.putExtra("id", imageAdapter.getItem(position).getId());
                startActivity(intent);
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageSendActivity.class);
                startActivity(intent);
            }
        });

    }

    public void openDialog() {
        ImagePasswordDialog imagePasswordDialog = new ImagePasswordDialog();
        imagePasswordDialog.show(getSupportFragmentManager(),"image password dialog");
    }

    @Override
    public void applyText(String password) {
        this.password = password;

    }

//    public void decrypt(Integer imageId) {
//        RequestBody password = RequestBody.create("1234448", MediaType.parse("multipart/form-data"));
//
//
//        restApi = ServiceGenerator.createService(RestApi.class);
//        progressDialog = new ProgressDialog(ImageListActivity.this);
//        progressDialog.setMessage("Decrypting");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//        Call<ImageDecryptionResponse> call = restApi.decryptImage(imageId.toString(), password);
//
//        call.enqueue(new Callback<ImageDecryptionResponse>() {
//            @Override
//            public void onResponse(@NotNull Call<ImageDecryptionResponse> call, @NotNull Response<ImageDecryptionResponse> response) {
//                progressDialog.dismiss();
//                Toast.makeText(getApplicationContext(), "Decrypted successfully", Toast.LENGTH_LONG).show();
//                int statusCode = response.code();
//                Log.e("Tag", "onResponse response " + response.isSuccessful());
//                Log.e("status", "onResponse response " + statusCode);
//                Toast.makeText(getApplicationContext(), response.body().getImage(), Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(getApplicationContext(),ImageViewActivity.class);
//                intent.putExtra("decryptedImageUrl", response.body().getImage());
//                //Toast.makeText(getApplicationContext(), response.body().getStatus(), Toast.LENGTH_LONG).show();
//                startActivity(intent);
//            }
//
//            @Override
//            public void onFailure(@NotNull Call<ImageDecryptionResponse> call, @NotNull Throwable t) {
//                t.printStackTrace();
//                Toast.makeText(getApplicationContext(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
}
