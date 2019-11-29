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
import com.neuricius.masterproject.net.model.Result;
import com.neuricius.masterproject.util.Sorting;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.neuricius.masterproject.util.UtilTools.TMDB_API_IMAGE_BASE_URL;

public class RvResultAdapter extends RecyclerView.Adapter<RvResultAdapter.MyActorViewHolder>{

    private List<Result> data;
    private OnResultClickListener listener;
    private Context context;

    public interface OnResultClickListener {
        void onResultSelected(Result result);
    }

    public RvResultAdapter(List<Result> data, Context context) {
        this.data = data;
        this.context = context;
    }

    public void setListener(OnResultClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.actor_list_item, parent, false);

        MyActorViewHolder viewHolder = new MyActorViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyActorViewHolder holder, int position) {
        holder.bind(listener, data.get(position), context);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyActorViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivActorImage;
        private TextView tvActorName;
        private TextView tvKnownFor;
        private RatingBar rbActorRating;

        private View itemView;

        public MyActorViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            ivActorImage = itemView.findViewById(R.id.ivActorImage);
            tvActorName = itemView.findViewById(R.id.tvActorName);
            tvKnownFor = itemView.findViewById(R.id.tvKnownFor);
            rbActorRating = itemView.findViewById(R.id.rbActorRating);
        }

        public void bind(final OnResultClickListener listener, final Result result, Context context){
            tvActorName.setText(result.getName());
            rbActorRating.setRating(result.getPopularity().floatValue()/4f);
            tvKnownFor.setText(Sorting.returnMostKnowFor(result.getKnownFor()).getTitle());

            //ucitavanje slike uz pomoc Picassa
            String path = TMDB_API_IMAGE_BASE_URL + result.getProfilePath();
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
                    listener.onResultSelected(result);
                }
            });
        }
    }




}
