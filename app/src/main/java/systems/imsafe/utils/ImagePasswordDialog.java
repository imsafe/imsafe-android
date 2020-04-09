package systems.imsafe.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

import systems.imsafe.R;

public class ImagePasswordDialog extends AppCompatDialogFragment {

    private EditText et_decrypt_password;
    private ImagePasswordDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.layout_dialog, null);
        builder.setView(view)
                .setTitle("Decrypt Image")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String password = et_decrypt_password.getText().toString();
                        if(TextUtils.isEmpty(password)) {
                            Toast.makeText(getContext(),"Please fill out password field.",Toast.LENGTH_LONG).show();
                            return;
                        }
                        listener.applyText(password);
                    }
                });

        et_decrypt_password = view.findViewById(R.id.et_decrypt_password);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (ImagePasswordDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "must implement ImagePasswordDialogListener");
        }
    }

    public interface ImagePasswordDialogListener {
        void applyText(String password);
    }
}
