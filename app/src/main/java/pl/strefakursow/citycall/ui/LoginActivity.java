package pl.strefakursow.citycall.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please wait");

        binding.registerTv.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        binding.loginUserBtn.setOnClickListener(v -> {
            if (valid()) {
                progressDialog.show();
                Constants.auth().signInWithEmailAndPassword(
                        binding.loginEmailEt.getText().toString(),
                        binding.loginPassEt.getText().toString()
                ).addOnSuccessListener(authResult -> {
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });


    }

    private boolean valid() {

        if (binding.loginEmailEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (Patterns.EMAIL_ADDRESS.matcher(binding.loginEmailEt.getText().toString()).matches()) {
//            Toast.makeText(this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (binding.loginPassEt.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please provide a valid Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}