package pl.strefakursow.citycall.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.adapters.EventsAdapter;
import pl.strefakursow.citycall.databinding.FragmentEventsBinding;
import pl.strefakursow.citycall.models.EventsModel;

public class EventsFragment extends Fragment {
    FragmentEventsBinding binding;
    ArrayList<EventsModel> list;
    public EventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventsBinding.inflate(getLayoutInflater(), container, false);

        binding.recycler.setHasFixedSize(false);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));

        list = new ArrayList<>();

        Constants.databaseReference().collection("events").document("E").collection("events")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(requireContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        list.clear();
                        for (QueryDocumentSnapshot documentSnapshot: value){
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

                        EventsAdapter adapter = new EventsAdapter(binding.getRoot().getContext(), list);
                        binding.recycler.setAdapter(adapter);

                    }
                });

        return binding.getRoot();
    }
}