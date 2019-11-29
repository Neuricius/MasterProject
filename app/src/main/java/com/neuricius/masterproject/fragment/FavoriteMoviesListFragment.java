package com.neuricius.masterproject.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.neuricius.masterproject.R;
import com.neuricius.masterproject.activity.MovieDetailsActivity;
import com.neuricius.masterproject.adapter.RvFavoriteMovieAdapter;
import com.neuricius.masterproject.database.model.MovieDB;

import java.util.List;

import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN_DATABASE;
import static com.neuricius.masterproject.util.UtilTools.MOVIE_ID;

public class FavoriteMoviesListFragment extends Fragment {

    private RecyclerView rvList;
    //data provider
    private List<MovieDB> data;

    public List<MovieDB> getData() {
        return data;
    }

    public void setData(List<MovieDB> data) {
        this.data = data;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actor_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupActorList(view);
    }

    private void setupActorList(View view) {
        rvList = view.findViewById(R.id.rvActorList);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        rvList.setLayoutManager(llm);


        RvFavoriteMovieAdapter rvfadapter = new RvFavoriteMovieAdapter(data, getContext());
        rvList.setAdapter(rvfadapter);
        rvfadapter.setListener(new RvFavoriteMovieAdapter.OnFavoriteMovieClickListener() {
            @Override
            public void onFavoriteMovieSelected(MovieDB idMovie) {

                Intent intent = new Intent(getContext(), MovieDetailsActivity.class);
                //id u bazi krece od 1, a u listama od 0 ergo -1
                intent.putExtra(MOVIE_ID, idMovie.getId());
                intent.putExtra(INTENT_ORIGIN, INTENT_ORIGIN_DATABASE);
                startActivity(intent);
            }
        });
    }
}
