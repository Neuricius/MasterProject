package com.neuricius.masterproject.net;

import com.neuricius.masterproject.net.model.Actor;
import com.neuricius.masterproject.net.model.Credit;
import com.neuricius.masterproject.net.model.GenreList;
import com.neuricius.masterproject.net.model.Movie;
import com.neuricius.masterproject.net.model.SearchResult;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface TmdbApiEndpoint {

    //http://www.omdbapi.com/?apikey=[yourkey]&s=Batman
    //u principu, sve sto ide izmedju base url i upitnika
    @GET("search/person")
    Call<SearchResult> TMDBSearchPeople(@QueryMap Map<String, String> options);

    @GET("person/{person_id}")
    Call<Actor> TMDBGetActor(@Path("person_id") Integer id, @QueryMap Map<String, String> options);

    @GET("movie/{movie_id}")
    Call<Movie> TMDBGetMovie(@Path("movie_id") Integer id, @QueryMap Map<String, String> options);

    //person/{person_id}/movie_credits
    @GET("person/{person_id}/movie_credits")
    Call<Credit> TMDBGetMovieCredits(@Path("person_id") Integer id, @QueryMap Map<String, String> options);

    //genre/movie/list
    @GET("genre/movie/list")
    Call<GenreList> TMDBGetGenreList(@QueryMap Map<String, String> options);
}
