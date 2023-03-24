package pl.strefakursow.citycall.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.adapters.EventsAdapter;
import pl.strefakursow.citycall.adapters.EventsProAdapter;
import pl.strefakursow.citycall.databinding.ActivityProfileBinding;
import pl.strefakursow.citycall.models.EventsModel;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    ArrayList<EventsModel> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        Constants.databaseReference().collection("users").document(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    binding.emailTv.setText(documentSnapshot.getString("email"));
                    binding.nameTv.setText(documentSnapshot.getString("name"));
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
}