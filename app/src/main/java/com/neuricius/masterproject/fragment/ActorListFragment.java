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
import com.neuricius.masterproject.activity.ActorDetailsActivity;
import com.neuricius.masterproject.adapter.RvResultAdapter;
import com.neuricius.masterproject.net.model.Result;
import com.neuricius.masterproject.util.Sorting;

import java.util.List;

import static com.neuricius.masterproject.util.UtilTools.ACTOR_ID;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN_NET;

public class ActorListFragment extends Fragment {

    private RecyclerView rvList;
    //data provider
    private List<Result> data;

    public List<Result> getData() {
        return data;
    }

    public void setData(List<Result> data) {
        this.data = Sorting.sortResultsByPopularity(data);
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


        RvResultAdapter rvgadapter = new RvResultAdapter(data, getContext());
        rvList.setAdapter(rvgadapter);
        rvgadapter.setListener(new RvResultAdapter.OnResultClickListener() {
            @Override
            public void onResultSelected(Result result) {

                Intent intent = new Intent(getContext(), ActorDetailsActivity.class);
                intent.putExtra(ACTOR_ID, result.getId());
                intent.putExtra(INTENT_ORIGIN, INTENT_ORIGIN_NET);
                startActivity(intent);
            }
        });
    }
}
