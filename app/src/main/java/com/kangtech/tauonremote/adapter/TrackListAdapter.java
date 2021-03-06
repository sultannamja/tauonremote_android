package com.kangtech.tauonremote.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.huhx0015.hxaudio.audio.HXMusic;
import com.kangtech.tauonremote.R;
import com.kangtech.tauonremote.api.ApiServiceInterface;
import com.kangtech.tauonremote.model.track.TrackListModel;
import com.kangtech.tauonremote.model.track.TrackModel;
import com.kangtech.tauonremote.util.Server;
import com.kangtech.tauonremote.util.SharedPreferencesUtils;
import com.kangtech.tauonremote.view.MainActivity;
import com.kangtech.tauonremote.view.fragment.track.TrackFragment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static android.content.Context.MODE_PRIVATE;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> implements Filterable {

    private TrackListModel trackListModels;
    private TrackListModel getTrackListModelsFiltered;
    private final Context context;
    private final String playlistID;

    private SharedPreferences.Editor editor;

    public TrackListAdapter(Context context, TrackListModel trackListModels, String playlistID) {
        this.context = context;
        this.trackListModels = trackListModels;
        this.getTrackListModelsFiltered = trackListModels;
        this.playlistID = playlistID;
    }

    @NonNull
    @Override
    public TrackListAdapter.TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_trackall, parent, false);
        return new TrackListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListAdapter.TrackListViewHolder holder, int position) {
        holder.title.setText(trackListModels.tracks.get(position).title);
        holder.artist.setText(trackListModels.tracks.get(position).artist);

        Glide.with(context)
                .load("http://" + SharedPreferencesUtils.getString("ip", "127.0.0.1") + ":7814/api1/pic/small/" + trackListModels.tracks.get(position).id)
                .centerCrop()
                .placeholder(R.drawable.ic_round_music_note_24)
                .dontAnimate()
                .into(holder.ivCover);

        if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
            if (playlistID.equals(SharedPreferencesUtils.getString("playlistID", "0"))) {
                if (trackListModels.tracks.get(position).id == SharedPreferencesUtils.getInt("TrackID", 0)) {
                    holder.llTrack.setBackgroundColor(context.getResources().getColor(R.color.rose_bg_seekbar1));
                } else {
                    holder.llTrack.setBackgroundColor(context.getResources().getColor(R.color.rose_bg_list));
                }
            }
        } else {
            if (playlistID.equals(SharedPreferencesUtils.getString("playlist_stream", "0"))) {
                if (trackListModels.tracks.get(position).id == SharedPreferencesUtils.getInt("trackId_stream", 0)) {
                    holder.llTrack.setBackgroundColor(context.getResources().getColor(R.color.rose_bg_seekbar1));
                } else {
                    holder.llTrack.setBackgroundColor(context.getResources().getColor(R.color.rose_bg_list));
                }
            }
        }

        holder.llTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SharedPreferencesUtils.getBoolean("is_stream_mode", true)) {
                    if (playlistID.equals(SharedPreferencesUtils.getString("playlistID", "0"))) {
                        holder.apiServiceInterface.start(SharedPreferencesUtils.getString("playlistID", "0"), trackListModels.tracks.get(position).position)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ResponseBody>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                        notifyDataSetChanged();

                    } else {
                        holder.apiServiceInterface.start(playlistID, trackListModels.tracks.get(position).position)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<ResponseBody>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onNext(@io.reactivex.annotations.NonNull ResponseBody responseBody) {

                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });

                        notifyDataSetChanged();
                    }
                } else {
                    if (playlistID.equals(SharedPreferencesUtils.getString("playlist_stream", "0"))) {
                        if (HXMusic.isPlaying()) {
                            HXMusic.stop();
                        }

                        initStream(trackListModels.tracks.get(position).id);

                        editor = context.getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                        editor.putInt("trackPosition_stream", trackListModels.tracks.get(position).position);
                        editor.putInt("trackId_stream", trackListModels.tracks.get(position).id);
                        editor.apply();

                        notifyDataSetChanged();
                    } else {
                        if (HXMusic.isPlaying()) {
                            HXMusic.stop();
                        }

                        initStream(trackListModels.tracks.get(position).id);

                        editor = context.getSharedPreferences("tauon_remote", MODE_PRIVATE).edit();
                        editor.putString("playlist_stream", playlistID);
                        editor.putInt("trackPosition_stream", trackListModels.tracks.get(position).position);
                        editor.putInt("trackId_stream", trackListModels.tracks.get(position).id);
                        editor.apply();

                        notifyDataSetChanged();
                    }
                }

                //MainActivity.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                //MainActivity.navController.navigate(R.id.nav_album);
            }
        });
    }

    private void initStream(int trackId) {
        HXMusic.music()
                .load("http://" + Server.BASE_URL + ":7814/api1/file/" + trackId)
                .looped(false)
                .play(context);
    }

    @Override
    public int getItemCount() {
        return trackListModels.tracks.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                if(charSequence.toString().isEmpty()){
                    getTrackListModelsFiltered.tracks = trackListModels.tracks;

                }else{
                    String searchChr = charSequence.toString().toLowerCase();

                    List<TrackModel> resultData = new ArrayList<>();

                    for(TrackModel userModel: trackListModels.tracks){
                        if(userModel.title.toLowerCase().contains(searchChr) || userModel.artist.toLowerCase().contains(searchChr)){
                            resultData.add(userModel);
                        }
                    }
                    getTrackListModelsFiltered.tracks = resultData;

                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = getTrackListModelsFiltered.tracks;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                getTrackListModelsFiltered.tracks = (List<TrackModel>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }


    public static class TrackListViewHolder extends RecyclerView.ViewHolder {
        private TextView title, artist;
        private ImageView ivCover;
        private LinearLayout llTrack;
        private View viewTrackallDivider;
        private ApiServiceInterface apiServiceInterface;

        public TrackListViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tv_tracklist_title);
            artist = itemView.findViewById(R.id.tv_tracklist_artist);
            ivCover = itemView.findViewById(R.id.iv_tracklist_cover);

            llTrack = itemView.findViewById(R.id.ll_trackall);
            viewTrackallDivider = itemView.findViewById(R.id.view_trackall_divinder);

            apiServiceInterface = Server.getApiServiceInterface();
        }
    }

}
