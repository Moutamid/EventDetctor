package pl.strefakursow.citycall.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fxn.stash.Stash;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import pl.strefakursow.citycall.Constants;
import pl.strefakursow.citycall.R;
import pl.strefakursow.citycall.models.EventsModel;
import pl.strefakursow.citycall.ui.EventDetailActivity;

public class EventsProAdapter extends RecyclerView.Adapter<EventsProAdapter.EventVH> {
    Context context;
    ArrayList<EventsModel> list;

    public EventsProAdapter(Context context, ArrayList<EventsModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public EventVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.event_per, parent, false);
        return new EventsProAdapter.EventVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventVH holder, int position) {
        EventsModel model = list.get(holder.getAdapterPosition());
        holder.desc.setText("Description: " + model.getDesc());
        holder.category.setText("Category: " + model.getCategory());
        holder.rating.setText("Rating : ");

        Glide.with(context).load(model.getImageUri()).into(holder.imageView);
        double average = (
                (5*model.getStar5()) + (4*model.getStar4()) + (3*model.getStar3()) + (2*model.getStar2()) + model.getStar1()
        ) / (model.getStar1() + model.getStar2() + model.getStar3() + model.getStar4() + model.getStar5());

        if (average>=0.0 && average<=1.5){
            holder.star1.setVisibility(View.VISIBLE);
        } else if (average>=1.5 && average<=2.5){
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
        } else if (average>=2.5 && average<=3.5){
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
            holder.star3.setVisibility(View.VISIBLE);
        } else if (average>=3.5 && average<=4){
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
            holder.star3.setVisibility(View.VISIBLE);
            holder.star4.setVisibility(View.VISIBLE);
        } else if (average>=4){
            holder.star1.setVisibility(View.VISIBLE);
            holder.star2.setVisibility(View.VISIBLE);
            holder.star3.setVisibility(View.VISIBLE);
            holder.star4.setVisibility(View.VISIBLE);
            holder.star5.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            context.startActivity(new Intent(context, EventDetailActivity.class));
            Stash.put(Constants.EVENT, model);
        });

        holder.delete.setOnClickListener(v -> {
            new AlertDialog.Builder(context).setCancelable(true).setTitle("Confirm your action").setMessage("Do you want to delete this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        Constants.databaseReference().collection("events").document("E").collection("events")
                                .document(model.getId()).delete().addOnSuccessListener(unused -> {
                                    dialog.dismiss();
                                    Toast.makeText(context, "Event Deleted", Toast.LENGTH_SHORT).show();
                                    notifyItemRemoved(holder.getAdapterPosition());
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }).setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class EventVH extends RecyclerView.ViewHolder{
        MaterialTextView category, rating, desc;
        ShapeableImageView imageView;
        ImageView star1,star2,star3,star4,star5;
        ImageButton delete;

        public EventVH(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.categoryTv);
            rating = itemView.findViewById(R.id.reactionNumberTv);
            desc = itemView.findViewById(R.id.incidentDescTv);
            imageView = itemView.findViewById(R.id.incidentImg);

            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);

            delete = itemView.findViewById(R.id.delete);

        }
    }

}
