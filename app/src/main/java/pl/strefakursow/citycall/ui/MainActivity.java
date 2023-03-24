package pl.strefakursow.citycall.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.databinding.ActivityMainBinding;
import pl.strefakursow.citycall.fragments.EventsFragment;
import pl.strefakursow.citycall.fragments.MapsFragment;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Constants.checkApp(this);

        binding.bottomNavigationView.setOnNavigationItemSelectedListener(this);
        binding.bottomNavigationView.setSelectedItemId(R.id.home_nav);

        binding.addNewEventFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddEventActivity.class));
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout , new MapsFragment()).commit();
                return true;

            case R.id.events_nav:
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new EventsFragment()).commit();
                return true;

        }
        return false;
    }
}