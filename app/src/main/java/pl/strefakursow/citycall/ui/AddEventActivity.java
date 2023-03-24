package pl.strefakursow.citycall.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.github.dhaval2404.imagepicker.ImagePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;
import java.util.UUID;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.databinding.ActivityAddEventBinding;
import pl.strefakursow.citycall.models.EventsModel;
import pl.strefakursow.citycall.models.LangLatModel;

public class AddEventActivity extends AppCompatActivity {
    ActivityAddEventBinding binding;
    String currentDate;
    final Calendar calendarStart = Calendar.getInstance();
    final Calendar calendarEnd = Calendar.getInstance();
    ProgressDialog progressDialog;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddEventBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Event...");

        currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        binding.start.setText(currentDate);
        binding.end.setText(currentDate);

        DatePickerDialog.OnDateSetListener startDate = (datePicker, year, month, day) -> {
            calendarStart.set(Calendar.YEAR, year);
            calendarStart.set(Calendar.MONTH, month);
            calendarStart.set(Calendar.DAY_OF_MONTH, day);
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
            binding.start.setText(dateFormat.format(calendarStart.getTime()));
        };

        DatePickerDialog.OnDateSetListener EndDate = (datePicker, year, month, day) -> {
            calendarEnd.set(Calendar.YEAR, year);
            calendarEnd.set(Calendar.MONTH, month);
            calendarEnd.set(Calendar.DAY_OF_MONTH, day);
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.getDefault());
            binding.end.setText(dateFormat.format(calendarEnd.getTime()));
        };

        binding.start.setOnClickListener(v-> {
            new DatePickerDialog(AddEventActivity.this, startDate, calendarStart.get(Calendar.YEAR), calendarStart.get(Calendar.MONTH), calendarStart.get(Calendar.DAY_OF_MONTH)).show();
        });
        binding.end.setOnClickListener(v-> {
            new DatePickerDialog(AddEventActivity.this, EndDate, calendarEnd.get(Calendar.YEAR), calendarEnd.get(Calendar.MONTH), calendarEnd.get(Calendar.DAY_OF_MONTH)).show();
        });

        binding.addImage.setOnClickListener(v -> {
            ImagePicker.with(this)
                    .crop()
                    .compress(1024)
                    .maxResultSize(1080, 1080)
                    .start();
        });

        binding.pickLocation.setOnClickListener(v -> {
            startActivity(new Intent(AddEventActivity.this, GetLocationActivity.class));
        });

        binding.save.setOnClickListener(v -> {
            if (valid()){
                String id = UUID.randomUUID().toString();
                progressDialog.show();

                Constants.storageReference(id).child("image").putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                                LangLatModel model = (LangLatModel) Stash.getObject(Constants.PICKED_LOCATION, LangLatModel.class);
                                EventsModel eventsModel = new EventsModel(
                                        id,
                                        Constants.auth().getCurrentUser().getUid(),
                                        binding.desc.getText().toString(),
                                        binding.spinCatList.getSelectedItem().toString(),
                                        uri.toString(),
                                        binding.start.getText().toString(),
                                        binding.end.getText().toString(),
                                        0,
                                        model.getLatitude(), model.getLongitude(),
                                        0,0,0,0,0
                                );

                                Constants.databaseReference().collection("events").document("E").collection("events").document(id).set(eventsModel)
                                        .addOnSuccessListener(documentReference -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(this, "Event Created", Toast.LENGTH_SHORT).show();
                                            Stash.clear(Constants.PICKED_LOCATION);
                                            startActivity(new Intent(AddEventActivity.this, MainActivity.class));
                                            finish();
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
        });

    }

    private boolean valid() {
        if (imageUri == null){
            Toast.makeText(this, "Please add an Image", Toast.LENGTH_SHORT).show();
            return false;
        } if (binding.spinCatList.getSelectedItem().equals("Select Category")) {
            Toast.makeText(this, "Please Select A Category", Toast.LENGTH_SHORT).show();
            return false;
        } if (binding.desc.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please add a description", Toast.LENGTH_SHORT).show();
            return false;
        } if (binding.location.getVisibility() == View.GONE) {
            Toast.makeText(this, "Please picked any location", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.check.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        LangLatModel model = (LangLatModel) Stash.getObject(Constants.PICKED_LOCATION, LangLatModel.class);
        if (model != null) {
            String txt = "Picked Location is : " + model.getLatitude() + ", " + model.getLongitude();
            binding.location.setText(txt);
            binding.location.setVisibility(View.VISIBLE);
        }
    }
}