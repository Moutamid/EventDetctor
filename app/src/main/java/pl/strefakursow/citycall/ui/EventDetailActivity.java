package pl.strefakursow.citycall.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.adapters.EventsAdapter;
import pl.strefakursow.citycall.databinding.ActivityEventDetailBinding;
import pl.strefakursow.citycall.models.EventsModel;

public class EventDetailActivity extends FragmentActivity implements OnMapReadyCallback {

    ActivityEventDetailBinding binding;
    EventsModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        model = (EventsModel) Stash.getObject(Constants.EVENT, EventsModel.class);

        binding.category.setText("Category : " + model.getCategory());
        binding.start.setText(model.getStartDate());
        binding.end.setText(model.getEndDate());
        binding.desc.setText(model.getDesc());

        double average = (
                (5*model.getStar5()) + (4*model.getStar4()) + (3*model.getStar3()) + (2*model.getStar2()) + model.getStar1()
                ) / (model.getStar1() + model.getStar2() + model.getStar3() + model.getStar4() + model.getStar5());

        if (average>=0.0 && average<=1.5){
            binding.star1.setVisibility(View.VISIBLE);
        } else if (average>=1.5 && average<=2.5){
            binding.star1.setVisibility(View.VISIBLE);
            binding.star2.setVisibility(View.VISIBLE);
        } else if (average>=2.5 && average<=3.5){
            binding.star1.setVisibility(View.VISIBLE);
            binding.star2.setVisibility(View.VISIBLE);
            binding.star3.setVisibility(View.VISIBLE);
        } else if (average>=3.5 && average<=4){
            binding.star1.setVisibility(View.VISIBLE);
            binding.star2.setVisibility(View.VISIBLE);
            binding.star3.setVisibility(View.VISIBLE);
            binding.star4.setVisibility(View.VISIBLE);
        } else if (average>=4){
            binding.star1.setVisibility(View.VISIBLE);
            binding.star2.setVisibility(View.VISIBLE);
            binding.star3.setVisibility(View.VISIBLE);
            binding.star4.setVisibility(View.VISIBLE);
            binding.star5.setVisibility(View.VISIBLE);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        binding.close.setOnClickListener(v -> {
            Stash.clear(Constants.EVENT);
            finish();
        });

        binding.rate.setOnClickListener(v -> {
            showDialog();
        });

    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rating_layout);

        AtomicInteger starCount = new AtomicInteger();
        starCount.set(0);

        ImageView star1,star2,star3,star4,star5;

        star1 = dialog.findViewById(R.id.star1);
        star2 = dialog.findViewById(R.id.star2);
        star3 = dialog.findViewById(R.id.star3);
        star4 = dialog.findViewById(R.id.star4);
        star5 = dialog.findViewById(R.id.star5);

        Button rateBtn = dialog.findViewById(R.id.rateBTN);

        star1.setOnClickListener(v -> {
            star1.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star2.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            star3.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            star4.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            star5.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            starCount.set(1);
        });
        star2.setOnClickListener(v -> {
            star1.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star2.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star3.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            star4.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            star5.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            starCount.set(2);
        });
        star3.setOnClickListener(v -> {
            star1.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star2.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star3.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star4.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            star5.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            starCount.set(3);
        });
        star4.setOnClickListener(v -> {
            star1.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star2.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star3.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star4.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star5.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_border_24));
            starCount.set(4);
        });
        star5.setOnClickListener(v -> {
            star1.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star2.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star3.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star4.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            star5.setImageDrawable(getResources().getDrawable(R.drawable.baseline_star_rate_24));
            starCount.set(5);
        });


        rateBtn.setOnClickListener(v -> {
            Map<String, Object> rating = new HashMap<>();
            if (starCount.get()==0){
                Toast.makeText(this, "Please select number of stars", Toast.LENGTH_SHORT).show();
            } else {
                if (starCount.get()==5){
                    rating.put("star5", (model.getStar5()+1));
                } else if (starCount.get()==4){
                    rating.put("star4", (model.getStar4()+1));
                } else if (starCount.get()==3){
                    rating.put("star3", (model.getStar3()+1));
                } else if (starCount.get()==2){
                    rating.put("star2", (model.getStar2()+1));
                } else if (starCount.get()==1){
                    rating.put("star1", (model.getStar1()+1));
                }

                Constants.databaseReference().collection("events").document("E").collection("events").document(model.getId())
                        .update(rating).addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Thanks for your time", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }).addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });

            }



        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng sydney = new LatLng(model.getLatitude(), model.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(sydney));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}