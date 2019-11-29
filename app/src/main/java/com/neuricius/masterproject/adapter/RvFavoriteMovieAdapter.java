package com.neuricius.masterproject.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.neuricius.masterproject.R;
import com.neuricius.masterproject.database.model.MovieDB;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.neuricius.masterproject.util.UtilTools.TMDB_API_IMAGE_BASE_URL;

public class RvFavoriteMovieAdapter extends RecyclerView.Adapter<RvFavoriteMovieAdapter.MyFavoriteMovieViewHolder>{

    private List<MovieDB> data;
    private RvFavoriteMovieAdapter.OnFavoriteMovieClickListener listener;
    private Context context;

    public interface OnFavoriteMovieClickListener {
        void onFavoriteMovieSelected(MovieDB idMovie);
    }

    public RvFavoriteMovieAdapter(List<MovieDB> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setListener(RvFavoriteMovieAdapter.OnFavoriteMovieClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvFavoriteMovieAdapter.MyFavoriteMovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_item, parent, false);

        RvFavoriteMovieAdapter.MyFavoriteMovieViewHolder viewHolder = new RvFavoriteMovieAdapter.MyFavoriteMovieViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RvFavoriteMovieAdapter.MyFavoriteMovieViewHolder holder, int position) {
        holder.bind(listener, data.get(position), context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyFavoriteMovieViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivMoviePoster;
        private TextView tvMovieTitle;
        private TextView tvMovieYear;
        private TextView tvMovieGenre;

        private RatingBar rbMovieRating;


        private View itemView;

        public MyFavoriteMovieViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            ivMoviePoster = itemView.findViewById(R.id.ivMoviePoster);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieYear = itemView.findViewById(R.id.tvMovieYear);
            tvMovieGenre = itemView.findViewById(R.id.tvMovieGenre);
            rbMovieRating = itemView.findViewById(R.id.rbMovieRating);
        }

        public void bind(final RvFavoriteMovieAdapter.OnFavoriteMovieClickListener listener, final MovieDB movieDB, Context context){
            tvMovieTitle.setText(movieDB.getTitle()+"("+movieDB.getOriginalTitle()+")");
            tvMovieYear.setText(movieDB.getReleaseDate().substring(0,4));
            tvMovieGenre.setText(movieDB.getGenres());
            rbMovieRating.setRating(movieDB.getPopularity().floatValue()/4f);

            //ucitavanje slike uz pomoc Picassa
            String path = TMDB_API_IMAGE_BASE_URL + movieDB.getPosterPath();
            Uri uri = Uri.parse(path);
            Picasso.
                    with(context).
                    load(uri).
                    placeholder(R.drawable.ic_pic_placeholder_foreground).
                    error(R.drawable.ic_pic_error_foreground).
                    centerCrop().
                    fit().
                    into(ivMoviePoster);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFavoriteMovieSelected(movieDB);
                }
            });
        }
    }




}
