package com.neuricius.masterproject.util;

import com.neuricius.masterproject.database.model.ActorDB;
import com.neuricius.masterproject.net.model.KnownFor;
import com.neuricius.masterproject.net.model.Result;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Sorting {

    public static List<Result> sortResultsByPopularity(List<Result> data) {
        List<Result> returnList = data;

        Collections.sort(returnList, new Comparator<Result>() {
            @Override
            public int compare(Result r1, Result r2) {
                return r2.getPopularity().compareTo(r1.getPopularity());
            }
        });

        return returnList;
    }

    public static List<ActorDB> sortFavoritesByPopularity(List<ActorDB> data) {
        List<ActorDB> returnList = data;

        Collections.sort(returnList, new Comparator<ActorDB>() {
            @Override
            public int compare(ActorDB r1, ActorDB r2) {
                return r2.getPopularity().compareTo(r1.getPopularity());
            }
        });

        return returnList;
    }

    public static KnownFor returnMostKnowFor(List<KnownFor> data) {
        KnownFor topStar = data.get(0);

        for (KnownFor kf : data) {
            if (kf.getVoteAverage() > topStar.getVoteAverage()) {
                topStar = kf;
            }
        }

        return topStar;
    }
}
