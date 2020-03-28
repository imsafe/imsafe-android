package systems.imsafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.models.Image;
import systems.imsafe.restapi.ImSafeService;
import systems.imsafe.restapi.ServiceGenerator;

public class MainActivity extends AppCompatActivity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("File", MODE_PRIVATE);
        String username = prefs.getString("username", "not found");
        String password = prefs.getString("password", "not found");
        Log.e("username", username);
        Log.e("password", password);


        ImSafeService service = ServiceGenerator.createService(ImSafeService.class, username, password);
        Call<List<Image>> call = service.getImageList();

        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                if (response.isSuccessful()) {
                    intent = new Intent(MainActivity.this, ImageListActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
                Log.e("responseSuccess", String.valueOf(response.isSuccessful()));
                Log.e("status", "onResponse response " + response.code());
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
