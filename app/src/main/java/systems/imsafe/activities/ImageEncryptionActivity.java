package systems.imsafe.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import systems.imsafe.R;
import systems.imsafe.models.ImageEncryptionResponse;
import systems.imsafe.restapi.ImSafeService;
import systems.imsafe.restapi.ServiceGenerator;

public class ImageEncryptionActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private Button selectImage, sendImage;
    private ImageView iv_image;
    private EditText et_name, et_description, et_password;
    private String imagePath;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_encryption);
        initialize();

        checkPermissionREAD_EXTERNAL_STORAGE(ImageEncryptionActivity.this);

        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encrypt();
            }
        });
    }


    public void initialize() {
        selectImage = findViewById(R.id.btn_select_image);
        sendImage = findViewById(R.id.btn_encrypt);
        iv_image = findViewById(R.id.imageView);
        et_name = findViewById(R.id.et_image_name);
        et_description = findViewById(R.id.et_image_description);
        et_password = findViewById(R.id.et_image_password);
    }

    public void showImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 777);
    }

    public void encrypt() {
        File file = new File(imagePath);

        RequestBody requestBody = RequestBody.create(file, MediaType.parse("multipart/form-data"));
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        RequestBody name = RequestBody.create(et_name.getText().toString(), MediaType.parse("multipart/form-data"));
        RequestBody description = RequestBody.create(et_description.getText().toString(), MediaType.parse("multipart/form-data"));
        RequestBody password = RequestBody.create(et_password.getText().toString(), MediaType.parse("multipart/form-data"));

        progressDialog = new ProgressDialog(ImageEncryptionActivity.this);
        progressDialog.setMessage("Encrypting");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImSafeService service = ServiceGenerator.createService(ImSafeService.class);
        Call<ImageEncryptionResponse> call = service.encryptImage(image, name, description, password);


        call.enqueue(new Callback<ImageEncryptionResponse>() {
            @Override
            public void onResponse(Call<ImageEncryptionResponse> call, Response<ImageEncryptionResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Encrypted successfully", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ImageEncryptionResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(getApplicationContext(), "ERROR: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 777 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            imagePath = getRealPath(imageUri);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                iv_image.setImageBitmap(bitmap);
                iv_image.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getRealPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_idx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_idx);
        cursor.close();
        return result;
    }

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context, final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{permission},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImage();
                } else {
                    Toast.makeText(ImageEncryptionActivity.this, "Error: Permission Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

}
