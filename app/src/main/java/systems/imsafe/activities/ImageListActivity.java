package systems.imsafe.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
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
import systems.imsafe.restapi.ImSafeService;
import systems.imsafe.restapi.ServiceGenerator;
import systems.imsafe.utils.ImagePasswordDialog;

public class ImageListActivity extends AppCompatActivity implements ImagePasswordDialog.ImagePasswordDialogListener {
    private Toolbar toolbar;
    private ListView lvImageList;
    private ImSafeService service;
    private int selectedImageId;
    private List<Image> images = null;
    private ImageAdapter imageAdapter;
    private ProgressDialog progressDialog;
    private FloatingActionButton fab;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        initialize();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ImageEncryptionActivity.class);
                startActivity(intent);
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getImageList();
                refreshLayout.setRefreshing(false);
            }
        });

        getImageList();
    }

    public void initialize() {
        fab = findViewById(R.id.floatingActionButton);
        lvImageList = findViewById(R.id.lv_image);
        refreshLayout = findViewById(R.id.refreshLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getImageList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            new AlertDialog.Builder(this)
                    .setMessage("Do you really want to log out?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            logout();
                        }
                    })
                    .setNegativeButton("Cancel", null).show();
        } else if (id == R.id.action_search_user) {
            startActivity(new Intent(getApplicationContext(), UserSearchActivity.class));
        }
        return true;
    }

    public void logout() {
        SharedPreferences prefs = getSharedPreferences("File", MODE_PRIVATE);
        prefs.edit().remove("username").remove("password").apply();
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }

    public void getImageList() {
        service = ServiceGenerator.createService(ImSafeService.class);

        Call<List<Image>> call = service.getImageList();
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(@NotNull Call<List<Image>> call, @NotNull Response<List<Image>> response) {
                if (response.isSuccessful()) {
                    images = response.body();
                    assert images != null;

                    ArrayList<Image> imageList = new ArrayList<>(images);
                    Collections.reverse(imageList);
                    imageAdapter = new ImageAdapter(ImageListActivity.this, imageList);
                    lvImageList.setAdapter(imageAdapter);
                    lvImageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            setSelectedImageId(imageAdapter.getItem(position).getId());
                            openDialog();
                        }
                    });
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Image>> call, @NotNull Throwable t) {

            }
        });
    }

    public void openDialog() {
        ImagePasswordDialog imagePasswordDialog = new ImagePasswordDialog();
        imagePasswordDialog.show(getSupportFragmentManager(), "image password dialog");
    }

    public int getSelectedImageId() {
        return this.selectedImageId;
    }

    public void setSelectedImageId(int selectedImageId) {
        this.selectedImageId = selectedImageId;
    }

    @Override
    public void applyText(String password) {
        decrypt(getSelectedImageId(), password);
    }

    public void decrypt(Integer imageId, String imagePassword) {
        RequestBody password = RequestBody.create(imagePassword, MediaType.parse("multipart/form-data"));

        progressDialog = new ProgressDialog(ImageListActivity.this);
        progressDialog.setMessage("Decrypting");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<ImageDecryptionResponse> call = service.decryptImage(imageId.toString(), password);
        call.enqueue(new Callback<ImageDecryptionResponse>() {
            @Override
            public void onResponse(@NotNull Call<ImageDecryptionResponse> call, @NotNull Response<ImageDecryptionResponse> response) {
                if (response.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Decrypted successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), ImageViewActivity.class);
                    assert response.body() != null;
                    intent.putExtra("decryptedImageUrl", response.body().getImage());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(@NotNull Call<ImageDecryptionResponse> call, @NotNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
