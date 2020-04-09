package systems.imsafe.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.restapi.ImSafeService;
import systems.imsafe.restapi.ServiceGenerator;

public class RegistrationActivity extends AppCompatActivity {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,7}$", Pattern.CASE_INSENSITIVE);
    private Button btnCreate;
    private EditText et_username, et_first_name, et_last_name, et_email, et_password, et_repeat_password;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initialize();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    public void initialize() {
        btnCreate = findViewById(R.id.btn_create);
        et_username = findViewById(R.id.et_register_username);
        et_first_name = findViewById(R.id.et_register_first_name);
        et_last_name = findViewById(R.id.et_register_last_name);
        et_email = findViewById(R.id.et_register_email);
        et_password = findViewById(R.id.et_register_password);
        et_repeat_password = findViewById(R.id.et_register_repeat_password);
    }

    private boolean validate(String enteredEmail, String enteredPassword, String enteredRepeatPassword) {
        boolean isValid = true;

        if (!VALID_EMAIL_ADDRESS_REGEX.matcher(enteredEmail).matches()) {
            Toast.makeText(RegistrationActivity.this, "Invalid E-mail Address", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (!enteredPassword.equals(enteredRepeatPassword)) {
            Toast.makeText(RegistrationActivity.this, "Entered passwords do not match.", Toast.LENGTH_SHORT).show();
            isValid = false;
        }
        return isValid;
    }

    public void createAccount() {
        final String enteredUsername = et_username.getText().toString();
        final String enteredFirstName = et_first_name.getText().toString();
        final String enteredLastName = et_last_name.getText().toString();
        final String enteredEmail = et_email.getText().toString();
        final String enteredPassword = et_password.getText().toString();
        final String enteredRepeatPassword = et_repeat_password.getText().toString();

        if (checkFormInputs(enteredUsername, enteredFirstName, enteredLastName, enteredEmail, enteredPassword, enteredRepeatPassword)) {
            return;
        }

        if (!validate(enteredEmail, enteredPassword, enteredRepeatPassword)) {
            return;
        }

        progressDialog = new ProgressDialog(RegistrationActivity.this);
        progressDialog.setMessage("Creating");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestBody username = RequestBody.create(enteredUsername, MediaType.parse("multipart/form-data"));
        RequestBody firstName = RequestBody.create(enteredFirstName, MediaType.parse("multipart/form-data"));
        RequestBody lastName = RequestBody.create(enteredLastName, MediaType.parse("multipart/form-data"));
        RequestBody email = RequestBody.create(enteredEmail, MediaType.parse("multipart/form-data"));
        RequestBody password = RequestBody.create(enteredPassword, MediaType.parse("multipart/form-data"));

        ImSafeService service = ServiceGenerator.createService(ImSafeService.class);
        Call<ResponseBody> call = service.createAccount(username, email, password, firstName, lastName);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Account created successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
                int statusCode = response.code();
                Log.e("Tag", "onResponse response " + response.isSuccessful());
                Log.e("status", "onResponse response " + statusCode);
            }

            @Override
            public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean checkFormInputs(String enteredUsername, String enteredFirstName,
                                   String enteredLastName, String enteredEmail,
                                   String enteredPassword, String enteredRepeatPassword) {
        boolean isEmpty = false;
        if (TextUtils.isEmpty(enteredUsername)) {
            et_username.setError("Please fill out this field.");
            isEmpty = true;
        } else if (TextUtils.isEmpty(enteredFirstName)) {
            et_first_name.setError("Please fill out this field.");
            isEmpty = true;
        } else if (TextUtils.isEmpty(enteredLastName)) {
            et_last_name.setError("Please fill out this field.");
            isEmpty = true;
        } else if (TextUtils.isEmpty(enteredEmail)) {
            et_email.setError("Please fill out this field.");
            isEmpty = true;
        } else if (TextUtils.isEmpty(enteredPassword)) {
            et_password.setError("Please fill out this field.");
            isEmpty = true;
        } else if (TextUtils.isEmpty(enteredRepeatPassword)) {
            et_repeat_password.setError("Please fill out this field.");
            isEmpty = true;
        }
        return isEmpty;
    }
}
