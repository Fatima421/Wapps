package com.catrenat.wapps.Favourites.MovieFav.MovieFavRecyclerView;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.catrenat.wapps.Models.Documental;
import com.catrenat.wapps.Models.Pelis;
import com.catrenat.wapps.Models.Serie;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MovieFavRecyclerView extends RecyclerView.Adapter<MovieFavRecyclerView.MovieFavViewHolder> {
    private Context context;
    ArrayList<Serie> series;
    ArrayList<Pelis> pelis;
    ArrayList<Documental> documentals;

    public MovieFavRecyclerView(Context context, ArrayList<Serie> series, ArrayList<Pelis> pelis, ArrayList<Documental> documentals) {
        this.context = context;
        this.series = series;
        this.pelis = pelis;
        this.documentals = documentals;
    }

    @NonNull
    @Override
    public MovieFavRecyclerView.MovieFavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie_fav, parent, false);
        MovieFavRecyclerView.MovieFavViewHolder holder = new MovieFavRecyclerView.MovieFavViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieFavRecyclerView.MovieFavViewHolder holder, int position) {

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if (!series.isEmpty()) {
            if(!series.get(position).getImagePath().isEmpty()) {
                storageReference.child(series.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(context) // Context from getContext() in HomeFragment
                                .load(uri.toString())
                                .into(holder.favMovieImage);
                    }
                });
            }
        }

        if (!pelis.isEmpty()) {
            if(!pelis.get(position).getImagePath().isEmpty()) {
                storageReference.child(pelis.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(context) // Context from getContext() in HomeFragment
                                .load(uri.toString())
                                .into(holder.favMovieImage);
                    }
                });
            }
        }

        if (!documentals.isEmpty()) {
            if(!documentals.get(position).getImagePath().isEmpty()) {
                storageReference.child(documentals.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Load image with glide
                        Glide.with(context) // Context from getContext() in HomeFragment
                                .load(uri.toString())
                                .into(holder.favMovieImage);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return series.size();
    }

    public class MovieFavViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView favMovieImage;
        CardView favMovieView;

        public MovieFavViewHolder(@NonNull View itemView) {
            super(itemView);
            favMovieImage = itemView.findViewById(R.id.movieFavImage);
            favMovieView = itemView.findViewById(R.id.movieFavCard);
        }
    }
}
