package systems.imsafe.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.models.Image;
import systems.imsafe.restapi.ImSafeService;
import systems.imsafe.restapi.ServiceGenerator;

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                submitData();
            }
        });
    }

    public void initialize() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
    }

    public void submitData() {
        final String enteredUsername = etUsername.getText().toString();
        final String enteredPassword = etPassword.getText().toString();

        ImSafeService service = ServiceGenerator.createService(ImSafeService.class, enteredUsername, enteredPassword);
        Call<List<Image>> call = service.getImageList();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(@NotNull Call<List<Image>> call, @NotNull Response<List<Image>> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    SharedPreferences prefs = getSharedPreferences("File", MODE_PRIVATE);
                    SharedPreferences.Editor e = prefs.edit();
                    e.putString("username", enteredUsername).putString("password", enteredPassword);

                    e.apply();
                    Toast.makeText(getApplicationContext(), "You have successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent imageIntent = new Intent(getApplicationContext(), ImageListActivity.class);
                    startActivity(imageIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<Image>> call, @NotNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
