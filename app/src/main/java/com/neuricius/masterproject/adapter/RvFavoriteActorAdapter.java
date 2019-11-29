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
import com.neuricius.masterproject.database.model.ActorDB;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.neuricius.masterproject.util.UtilTools.TMDB_API_IMAGE_BASE_URL;

public class RvFavoriteActorAdapter extends RecyclerView.Adapter<RvFavoriteActorAdapter.MyFavoritesViewHolder>{

    private List<ActorDB> data;
    private RvFavoriteActorAdapter.OnFavoritesClickListener listener;
    private Context context;

    public interface OnFavoritesClickListener {
        void onFavoritesSelected(ActorDB idActor);
    }

    public RvFavoriteActorAdapter(List<ActorDB> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setListener(RvFavoriteActorAdapter.OnFavoritesClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvFavoriteActorAdapter.MyFavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.actor_list_item, parent, false);

        RvFavoriteActorAdapter.MyFavoritesViewHolder viewHolder = new RvFavoriteActorAdapter.MyFavoritesViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RvFavoriteActorAdapter.MyFavoritesViewHolder holder, int position) {
        holder.bind(listener, data.get(position), context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyFavoritesViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivActorImage;
        private TextView tvActorName;
        private TextView tvKnownFor;
        private RatingBar rbActorRating;

        private View itemView;

        public MyFavoritesViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            ivActorImage = itemView.findViewById(R.id.ivActorImage);
            tvActorName = itemView.findViewById(R.id.tvActorName);
            tvKnownFor = itemView.findViewById(R.id.tvKnownFor);
            rbActorRating = itemView.findViewById(R.id.rbActorRating);
        }

        public void bind(final RvFavoriteActorAdapter.OnFavoritesClickListener listener, final ActorDB actorDB, Context context){
            tvActorName.setText(actorDB.getName());
            rbActorRating.setRating(actorDB.getPopularity().floatValue()/4f);
            tvKnownFor.setText(actorDB.getKnownForDepartment());

            //ucitavanje slike uz pomoc Picassa
            String path = TMDB_API_IMAGE_BASE_URL + actorDB.getProfilePath();
            Uri uri = Uri.parse(path);
            Picasso.
                    with(context).
                    load(uri).
                    placeholder(R.drawable.ic_pic_placeholder_foreground).
                    error(R.drawable.ic_pic_error_foreground).
                    centerCrop().
                    fit().
                    into(ivActorImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onFavoritesSelected(actorDB);
                }
            });
        }
    }




}
