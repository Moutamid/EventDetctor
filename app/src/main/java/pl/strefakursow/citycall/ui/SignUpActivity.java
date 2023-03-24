package pl.strefakursow.citycall.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.databinding.ActivitySignUpBinding;
import pl.strefakursow.citycall.models.UserModel;

public class SignUpActivity extends AppCompatActivity {

    ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.registerBtn.setOnClickListener(v -> {
            if (valid()) {
                Constants.auth().createUserWithEmailAndPassword(
                        binding.registerEmailTv.getText().toString(),
                        binding.registerPassTv.getText().toString()
                ).addOnSuccessListener(authResult -> {
                    UserModel userModel = new UserModel(authResult.getUser().getUid(),
                            binding.registerNameTv.getText().toString(),
                            binding.registerEmailTv.getText().toString());
                    Constants.databaseReference().collection("users").document(authResult.getUser().getUid()).set(userModel).addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "User Created Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                        finish();
                    }).addOnFailureListener(e -> {
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

        binding.loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish();
        });


    }

    private boolean valid() {
        if (binding.registerNameTv.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please provide a valid Name", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (binding.registerEmailTv.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
            return false;
        }
//        if (Patterns.EMAIL_ADDRESS.matcher(binding.registerEmailTv.getText().toString()).matches()) {
//            Toast.makeText(this, "Please provide a valid Email", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (binding.registerPassTv.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please provide a valid Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}