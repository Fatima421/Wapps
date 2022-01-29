package com.catrenat.wapps.Movies.RecyclerView.Pelis;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.Movies.MoviesDetailsFragment;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class PelisRecyclerViewAdapter extends RecyclerView.Adapter<PelisRecyclerViewAdapter.PelisViewHolder> {
    private List<Pelis> pelis;
    private Context context;
    private String selectedPlatform;

    public PelisRecyclerViewAdapter(List<Pelis> pelis, Context context, String selectedPlatform){
        this.pelis = pelis;
        this.context = context;
        this.selectedPlatform = selectedPlatform;
    }

    @NonNull
    @Override
    public PelisRecyclerViewAdapter.PelisViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pelis, parent, false);
        PelisRecyclerViewAdapter.PelisViewHolder holder = new PelisRecyclerViewAdapter.PelisViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PelisRecyclerViewAdapter.PelisViewHolder holder, int position) {
        // Peli in the current position
        Pelis peli = pelis.get(position);

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!pelis.get(position).getImagePath().isEmpty() && pelis.get(position).getImagePath() != null) {
            storageReference.child(pelis.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.pelisImage);
                }
            });
        }

        holder.pelisImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity app = (AppCompatActivity) view.getContext();
                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoviesDetailsFragment(peli, selectedPlatform), "moviesDetailsFragment").addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pelis.size();
    }

    public class PelisViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView pelisImage;

        public PelisViewHolder(@NonNull View itemView) {
            super(itemView);
            pelisImage = itemView.findViewById(R.id.pelisImage);
        }
    }
}
