package net.aldar.cramello.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import net.aldar.cramello.R;
import net.aldar.cramello.model.response.Video;

import java.util.List;

public class VideoRVAdapter extends RecyclerView.Adapter<VideoRVAdapter.mViewHolder> {


    public static YouTubePlayerView youTubePlayerView;
    List<Video> Videos;
    Context context;

    public VideoRVAdapter(List<Video> videos, Context context) {
        Videos = videos;
        this.context = context;
    }

    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_raw, parent, false);
        return new VideoRVAdapter.mViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {

        Video video = Videos.get(position);
        int index;
        String videoID;
        if (video.getVideo().contains("embed/")) {
            index = video.getVideo().indexOf("embed/");
            videoID = video.getVideo().substring(index + 6);
            Log.d("videoID", videoID);

            youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
                @Override
                public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                    // String videoId = "9KDSI_L_A8w";
                    youTubePlayer.cueVideo(videoID, 0);

                }
            });

        }


    }

    @Override
    public int getItemCount() {
        return Videos.size();
    }

    class mViewHolder extends RecyclerView.ViewHolder {

        mViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubePlayerView = itemView.findViewById(R.id.youtube_player_view);
        }
    }
}
