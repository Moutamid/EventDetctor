package pl.strefakursow.citycall.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.databinding.FragmentMapsBinding;
import pl.strefakursow.citycall.models.EventsModel;
import pl.strefakursow.citycall.ui.EventDetailActivity;
import pl.strefakursow.citycall.ui.GetLocationActivity;
import pl.strefakursow.citycall.ui.MainActivity;
import pl.strefakursow.citycall.ui.ProfileActivity;

public class MapsFragment extends Fragment {
    SimpleDateFormat dateFormat;
    Date currentDate;
    String current;
    FragmentMapsBinding binding;
    Context context;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    EventsModel model;
    Map<String, Object> marker = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(getLayoutInflater(), container, false);

        dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        currentDate = new Date();

        context = binding.getRoot().getContext();

        current = dateFormat.format(currentDate);

        Constants.databaseReference().collection("users").document(Constants.auth().getCurrentUser().getUid())
                .get().addOnSuccessListener(documentSnapshot -> {
                    Glide.with(context).load(documentSnapshot.getString("image")).placeholder(R.drawable.profile_icon).into(binding.profileImage);
                }).addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });

        binding.profile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ProfileActivity.class));
        });


        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showMap();
    }

    private void showMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
        if (!checkGPSStatus()) {
            Task<Location> task = fusedLocationProviderClient.getLastLocation();
            task.addOnSuccessListener(location -> {
                if (location != null) {
                    currentLocation = location;
                    SupportMapFragment mapFragment =
                            (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(googleMap -> {
                            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                                return;
                            }
                            googleMap.setMyLocationEnabled(true);

                            Constants.databaseReference().collection("events").document("E").collection("events")
                                    .addSnapshotListener((value, error) -> {
                                        if (error != null) {
                                            Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            for (QueryDocumentSnapshot documentSnapshot : value) {
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
                                                Date endDate, cur;
                                                try {
                                                    endDate = dateFormat.parse(model.getEndDate());
                                                    cur = dateFormat.parse(current);
                                                } catch (ParseException e) {
                                                    throw new RuntimeException(e);
                                                }

                                                Geocoder geocoder = new Geocoder(binding.getRoot().getContext(), Locale.getDefault());
                                                String myCity = "";
                                                String markerCity = "";

                                                try {
                                                    List<Address> addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
                                                    if (addresses != null && addresses.size() > 0) {
                                                        myCity = addresses.get(0).getCountryName();
                                                        Log.d("Check123", "myCity " + myCity);
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                try {
                                                    List<Address> addresses = geocoder.getFromLocation(model.getLatitude(), model.getLongitude(), 1);
                                                    if (addresses != null && addresses.size() > 0) {
                                                        markerCity = addresses.get(0).getCountryName();
                                                        Log.d("Check123", "marker " + markerCity);
                                                    }
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                Log.d("Check123", "Lat : " + model.getLatitude() + " long : " + model.getLongitude());

                                                if (endDate.compareTo(cur) >= 0) {
                                                    if (markerCity.equals(myCity)) {
                                                        LatLng mark = new LatLng(model.getLatitude(), model.getLongitude());
                                                        MarkerOptions mo = new MarkerOptions().position(mark).flat(true)
                                                                .rotation(0).title( model.getCategory() + " event Start at " + model.getStartDate());
                                                        Marker mkr = googleMap.addMarker(mo);
                                                        marker.put(mkr.getId(), model);
                                                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mark));
                                                    }
                                                }
                                            }
                                        }
                                    });

                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                            googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(@NonNull Marker mark) {
                                    EventsModel model1 = (EventsModel) marker.get(mark.getId());
                                    startActivity(new Intent(requireContext(), EventDetailActivity.class));
                                    Stash.put(Constants.EVENT, model1);
                                    return false;
                                }
                            });

                        });
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVisible()){
            showMap();
        }
        /*try {
            requireActivity().recreate();
        } catch (Exception e){}*/
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = null;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gps_enabled && !network_enabled) {
            new AlertDialog.Builder(context).setTitle("Open Settings")
                    .setMessage("GPS not enabled")
                    .setCancelable(false)
                    .setPositiveButton("Open Settings", (dialog, which) -> {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }).show();
        }

        return !gps_enabled && !network_enabled;
    }

}