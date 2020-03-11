package systems.imsafe.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.models.Image;
import systems.imsafe.restapi.RestApi;
import systems.imsafe.restapi.ServiceGenerator;

public class LoginActivity extends AppCompatActivity {
    RestApi restApi;
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View view) {
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }
                basicAuth();
                submitData();
            }
        });


    }

    public void basicAuth() {
        final String enteredUsername = etUsername.getText().toString();
        final String enteredPassword = etPassword.getText().toString();
        restApi = ServiceGenerator.createService(RestApi.class, enteredUsername, enteredPassword);
    }

    public void submitData() {
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.show();
        
        Call<List<Image>> call = restApi.basicLogin();

        //calling the api
        call.enqueue(new Callback<List<Image>>() {
            @Override
            public void onResponse(Call<List<Image>> call, Response<List<Image>> response) {
                //hiding progress dialog
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "You have successfully logged in", Toast.LENGTH_SHORT).show();
                    List<Image> images = response.body();
                    Intent imageIntent = new Intent(getApplicationContext(), ImageListActivity.class);
                    //Intent i = new Intent(LoginActivity.this, ImageSendActivity.class);
//                    Bundle b = new Bundle();
//                    b.putString("username", enteredUsername);
//                    b.putString("password", enteredPassword);
//                    i.putExtras(b);
                    //startActivity(i);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("images", (Serializable) images);
                    imageIntent.putExtras(bundle);
                    startActivity(imageIntent);
                } else {
                    Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Image>> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

}
