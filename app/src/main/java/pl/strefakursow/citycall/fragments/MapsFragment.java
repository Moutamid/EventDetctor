package pl.strefakursow.citycall.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.databinding.FragmentMapsBinding;
import pl.strefakursow.citycall.models.EventsModel;
import pl.strefakursow.citycall.ui.EventDetailActivity;
import pl.strefakursow.citycall.ui.GetLocationActivity;
import pl.strefakursow.citycall.ui.ProfileActivity;

public class MapsFragment extends Fragment {

    FragmentMapsBinding binding;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    EventsModel model;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION },1);
                return;
            }
            googleMap.setMyLocationEnabled(true);

            Constants.databaseReference().collection("events").document("E").collection("events")
                    .addSnapshotListener((value, error) -> {
                        if (error != null){
                            Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            for (QueryDocumentSnapshot documentSnapshot: value){
                                model = new EventsModel(
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
                                LatLng sydney = new LatLng(model.getLatitude(), model.getLongitude());
                                googleMap.addMarker(new MarkerOptions().position(sydney).title("Event Start at " + model.getStartDate() + " \n End At " + model.getEndDate()));

                            }

                            Task<Location> task = fusedLocationProviderClient.getLastLocation();
                            task.addOnSuccessListener(location -> {
                                if (location!=null){
                                    currentLocation = location;
                                    LatLng sydney = new LatLng(location.getLatitude(), location.getLongitude());
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                                    //googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                                }
                            });

                        }
                    });

            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            /*googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(@NonNull Marker marker) {
                    startActivity(new Intent(requireContext(), EventDetailActivity.class));
                    Stash.put(Constants.EVENT, model);
                    return false;
                }
            });*/

        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(getLayoutInflater(), container, false);

        binding.profile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ProfileActivity.class));
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}