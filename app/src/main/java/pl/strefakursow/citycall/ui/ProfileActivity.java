package pl.strefakursow.citycall.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.adapters.EventsAdapter;
import pl.strefakursow.citycall.adapters.EventsProAdapter;
import pl.strefakursow.citycall.databinding.ActivityProfileBinding;
import pl.strefakursow.citycall.models.EventsModel;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    ArrayList<EventsModel> list;
    ProgressDialog progressDialog;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Profile");
        progressDialog.setCancelable(false);

        binding.recyclerView.setHasFixedSize(false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(ProfileActivity.this));

        list = new ArrayList<>();

        binding.backbtn.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.logoutBtn.setOnClickListener(v -> {
            Constants.auth().signOut();
            startActivity(new Intent(this, SplashScreenActivity.class));
            finish();
        });

        binding.edit.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .cropSquare()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });

        Constants.databaseReference().collection("users").document(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    binding.emailTv.setText(documentSnapshot.getString("email"));
                    binding.nameTv.setText(documentSnapshot.getString("name"));
                    Glide.with(ProfileActivity.this).load(documentSnapshot.getString("image")).placeholder(R.drawable.profile_icon).into(binding.profilePhotoImg);
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

        Constants.databaseReference().collection("events").document("E").collection("events")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(ProfileActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        list.clear();
                        for (QueryDocumentSnapshot documentSnapshot: value){
                            if (documentSnapshot.getString("userID").equals(Constants.auth().getCurrentUser().getUid())){
                                EventsModel model = new EventsModel(
                                        documentSnapshot.getString("id"),
                                        documentSnapshot.getString("userID"),
                                        documentSnapshot.getString("desc"),
                                        documentSnapshot.getString("category"),
                                        documentSnapshot.getString("imageUri"),
                                        documentSnapshot.getString("startDate"),
                                        documentSnapshot.getString("endDate"),
                                        documentSnapshot.getDouble("reactions"),
                                        documentSnapshot.getDouble("latitude"),
                                        documentSnapshot.getDouble("longitude"),
                                        documentSnapshot.getDouble("star1"),
                                        documentSnapshot.getDouble("star2"),
                                        documentSnapshot.getDouble("star3"),
                                        documentSnapshot.getDouble("star4"),
                                        documentSnapshot.getDouble("star5")
                                );
                                list.add(model);
                            }
                        }

                        EventsProAdapter adapter = new EventsProAdapter(ProfileActivity.this, list);
                        binding.recyclerView.setAdapter(adapter);

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Glide.with(ProfileActivity.this).load(imageUri).placeholder(R.drawable.profile_icon).into(binding.profilePhotoImg);
            progressDialog.show();
            Constants.storageReference(Constants.auth().getCurrentUser().getUid()).child("usersLogo").putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            Map<String, Object> map = new HashMap<>();
                            map.put("image", uri.toString());
                            Constants.databaseReference().collection("users").document(Constants.auth().getCurrentUser().getUid())
                                    .update(map).addOnSuccessListener(unused -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }).addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

}