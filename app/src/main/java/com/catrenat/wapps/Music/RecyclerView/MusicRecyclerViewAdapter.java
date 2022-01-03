package com.catrenat.wapps.Music.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.catrenat.wapps.Music.MusicDetailsFragment;
import com.catrenat.wapps.R;
import com.catrenat.wapps.Models.Music;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.DefaultPlayerUiController;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.MusicViewHolder>{

    // Properties
    private ArrayList<Music> musicArray;
    private Context context;
    private RecyclerView musicRecyclerView;
    boolean heartPressed = false;
    YouTubePlayerView youTubePlayerView;
    public static Dialog musicDetailsFragmentPopup;


    // Constructor
    public MusicRecyclerViewAdapter(RecyclerView musicRecyclerView, ArrayList<Music> musicArray, Context context, YouTubePlayerView youTubePlayerView){
        this.musicRecyclerView = musicRecyclerView;
        this.musicArray = musicArray;
        this.context = context;
        this.youTubePlayerView = youTubePlayerView;
    }

    // Creating a new onCreateViewHolder
    @NonNull
    @Override
    public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music_fragment, parent, false);
        MusicViewHolder holder = new MusicViewHolder(view);
        return holder;
    }

    // Setting values to every field item by item
    @Override
    public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {
        // Create a music object with the music that is inside the array list
        Music music = musicArray.get(position);

        // Creating the music details fragment
        MusicDetailsFragment musicDetailsFragment = new MusicDetailsFragment();

        // Setting the text for the song name and artist
        holder.songName.setText(music.getSongName());
        holder.songArtist.setText(music.getSongArtist());

        // Play the song in the youtube song player
        holder.youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                String videoId = music.getSong();
                youTubePlayer.cueVideo(videoId, 0);
            }
        });

        // For the favourite button
        holder.favouriteImage.setOnClickListener(view -> {
            AppCompatActivity app = (AppCompatActivity) view.getContext();
            int current = (!heartPressed) ? R.drawable.ic_music_filled_heart : R.drawable.ic_music_heart;
            heartPressed = current != R.drawable.ic_music_heart;
            holder.favouriteImage.setImageResource(current);
        });

        // Music Details Fragment Dialog
        holder.itemView.setOnClickListener(v -> {
            // Create the dialog
            Dialog musicDetailsFragmentPopup = new Dialog(context);

            // Create the window manager to get screen size and set dialog size
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(musicDetailsFragmentPopup.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;

            // Set up the dialog
            musicDetailsFragmentPopup.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
            musicDetailsFragmentPopup.setContentView(R.layout.fragment_music_details);

            // Elements of the dialog
            View dialogDismissView = musicDetailsFragmentPopup.findViewById(R.id.musicDetailsdismissDialogView);
            ImageView favouriteImage = musicDetailsFragmentPopup.findViewById(R.id.musicDetailFavourite);
            ImageView songImage = musicDetailsFragmentPopup.findViewById(R.id.songImage);
            ImageView songImagePlaceholder = musicDetailsFragmentPopup.findViewById(R.id.musicDetailPlaceholderImg);
            TextView songNameDetails = musicDetailsFragmentPopup.findViewById(R.id.detailsSongName);
            TextView songArtistDetails = musicDetailsFragmentPopup.findViewById(R.id.detailsSongArtist);
            ImageView youtubeImage = musicDetailsFragmentPopup.findViewById(R.id.musicDetailsYoutubeImg);
            TextView youtubeText = musicDetailsFragmentPopup.findViewById(R.id.musicDetailYoutubeText);

            // Set song name and artist
            songNameDetails.setText(holder.songName.getText());
            songArtistDetails.setText(holder.songArtist.getText());

            // Image loader from firebase using glide
            StorageReference storageReference = FirebaseStorage.getInstance("gs://catrenat-3e277.appspot.com").getReference();
            if(!music.getSongImageUrl().equals("")) {
                storageReference.child(music.getSongImageUrl()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        songImagePlaceholder.setVisibility(View.INVISIBLE);
                        Glide.with(context)
                                .load(uri.toString())
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(songImage);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("IMAGE", e.toString());
                    }
                });
            } else {
                songImage.setImageResource(R.drawable.music_bg_box);
                songImagePlaceholder.setImageResource(R.drawable.cd);
                songImagePlaceholder.setVisibility(View.VISIBLE);
            }

            // Change dialog favourite image by touching
            favouriteImage.setOnClickListener(view -> {
                int current = (!heartPressed) ? R.drawable.ic_music_details_filled_heart : R.drawable.ic_music_details_heart;
                heartPressed = current != R.drawable.ic_music_details_heart;
                favouriteImage.setImageResource(current);
            });

            // Youtube link
            youtubeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openYoutube();
                }
            });

            youtubeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openYoutube();
                }
            });

            // To be able to dismiss the dialog when touched
            dialogDismissView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // musicDetailsFragmentPopup.dismiss();
                }
            });

            // Show the dialog
            musicDetailsFragmentPopup.setCancelable(true);
            musicDetailsFragmentPopup.setCanceledOnTouchOutside(true);
            musicDetailsFragmentPopup.show();
            musicDetailsFragmentPopup.getWindow().setAttributes(lp);

        });
    }

    // Open youtube function
    private void openYoutube() {
        
    }

    // Counting the items in the music list
    @Override
    public int getItemCount() {
        return musicArray.size();
    }

    //Creating properties and finding them in the view
    public class MusicViewHolder extends RecyclerView.ViewHolder{
        ImageView favouriteImage;
        TextView songName;
        TextView songArtist;
        YouTubePlayerView youTubePlayerView;
        FrameLayout frameLayout;
        ImageView playButton;

        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            favouriteImage = itemView.findViewById(R.id.favouriteImg);
            songName = itemView.findViewById(R.id.songName);
            songArtist = itemView.findViewById(R.id.songArtist);
            playButton = itemView.findViewById(R.id.playMusic);
            playButton.setVisibility(View.INVISIBLE);
            youTubePlayerView = itemView.findViewById(R.id.youtubePlayer);
            frameLayout = itemView.findViewById(R.id.musicItemFrameLayout);
            frameLayout.setClipToOutline(true);

            // Youtube player UI
            IFramePlayerOptions options = new IFramePlayerOptions.Builder()
                    .controls(0)
                    .ccLoadPolicy(0)
                    .ivLoadPolicy(0)
                    .rel(0)
                    .build();

            View customPlayerUi = youTubePlayerView.inflateCustomPlayerUi(R.layout.custom_player_ui);
            YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(context, customPlayerUi, youTubePlayer, youTubePlayerView);
                    youTubePlayer.addListener(customPlayerUiController);
                }
            };

            // Disable iframe ui
            youTubePlayerView.initialize(listener, options);
        }
    }
}
