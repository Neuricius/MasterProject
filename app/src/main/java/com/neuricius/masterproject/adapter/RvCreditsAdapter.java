package com.neuricius.masterproject.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neuricius.masterproject.R;
import com.neuricius.masterproject.net.Contract;
import com.neuricius.masterproject.net.TmdbApiService;
import com.neuricius.masterproject.net.model.Cast;
import com.neuricius.masterproject.net.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.neuricius.masterproject.util.UtilTools.TMDB_APIKEY_PARAM_NAME;
import static com.neuricius.masterproject.util.UtilTools.TMDB_API_IMAGE_BASE_URL;

public class RvCreditsAdapter extends RecyclerView.Adapter<RvCreditsAdapter.MyCreditsViewHolder>{

    private List<Cast> knownForData;
    private OnMovieClickListener listener;
    private Context context;

    public interface OnMovieClickListener {
        void onMovieSelected(Movie movie);
    }

    public RvCreditsAdapter(List<Cast> knownForData, Context context) {
        this.knownForData = knownForData;
        this.context = context;
    }

    public void setListener(OnMovieClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyCreditsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.known_for_card_item, parent, false);

        MyCreditsViewHolder viewHolder = new MyCreditsViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyCreditsViewHolder holder, int position) {
        holder.bind(listener, knownForData.get(position), context);
    }

    @Override
    public int getItemCount() {
        return knownForData.size();
    }

    public class MyCreditsViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivMovieImage;
        private TextView tvMovieTitle;

        private View itemView;

        //konstruktor, sluzi za definisanje elemenata
        public MyCreditsViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            ivMovieImage = itemView.findViewById(R.id.ivMovieImage);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
        }
        //u bind metodi elementima se dodeljuju vrednosti
        public void bind(final OnMovieClickListener listener, final Cast cast, Context context) {
            String path = TMDB_API_IMAGE_BASE_URL + cast.getPosterPath();
            Uri uri = Uri.parse(path);
            Picasso.
                    with(context).
                    load(uri).
                    placeholder(R.drawable.ic_pic_placeholder_foreground).
                    error(R.drawable.ic_pic_error_foreground).
                    centerCrop().
                    fit().
                    into(ivMovieImage);

            tvMovieTitle.setText(cast.getTitle());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMovieService(cast.getId());
                }
            });
        }
    }

    private void getMovieService(Integer idMovie) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(TMDB_APIKEY_PARAM_NAME, Contract.API_KEY);

        Call<Movie> call = TmdbApiService.apiInterface().TMDBGetMovie(idMovie ,queryParams);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.code() == 200){
                    listener.onMovieSelected(response.body());
                } else {
                    Log.e("RESPONSE ERROR", response.message());
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.e("RESPONSE FAILURE", t.getMessage());
            }
        });

    }
}
