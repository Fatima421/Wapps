package com.catrenat.wapps.Movies.RecyclerView.Documentals;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
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
import com.catrenat.wapps.Movies.MoviesDetailsFragment;
import com.catrenat.wapps.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class DocusRecyclerViewAdapter extends RecyclerView.Adapter<DocusRecyclerViewAdapter.DocusViewHolder> {
    private List<Documental> documentals;
    private Context context;
    private String selectedPlatform;

    public DocusRecyclerViewAdapter(List<Documental> documentals, Context context, String selectedPlatform){
        this.documentals = documentals;
        this.context = context;
        this.selectedPlatform = selectedPlatform;
    }

    @NonNull
    @Override
    public DocusRecyclerViewAdapter.DocusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_docus, parent, false);
        DocusRecyclerViewAdapter.DocusViewHolder holder = new DocusRecyclerViewAdapter.DocusViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DocusRecyclerViewAdapter.DocusViewHolder holder, int position) {
        // Documental in the current position
        Documental documental = documentals.get(position);

        // Image loader from firebase using glide (Asks firebase for image hosted url using imagePath to storage)
        StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
        if(!documentals.get(position).getImagePath().isEmpty() && documentals.get(position).getImagePath() != null) {
            storageReference.child(documentals.get(position).getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Load image with glide
                    Glide.with(context) // Context from getContext() in HomeFragment
                            .load(uri.toString())
                            .into(holder.docusImage);
                }
            });
        }

        holder.docusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppCompatActivity app = (AppCompatActivity) view.getContext();
                app.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoviesDetailsFragment(documental, selectedPlatform), "moviesDetailsFragment").addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return documentals.size();
    }

    public class DocusViewHolder extends RecyclerView.ViewHolder{
        // View Elements
        ImageView docusImage;

        public DocusViewHolder(@NonNull View itemView) {
            super(itemView);
            docusImage = itemView.findViewById(R.id.docusImage);
        }
    }
}
