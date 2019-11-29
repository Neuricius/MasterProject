
package com.neuricius.masterproject.net.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GenreList {

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GenreList() {
    }

    /**
     * 
     * @param genres
     */
    public GenreList(List<Genre> genres) {
        super();
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Override
    public String toString() {
        return genres.toString();
    }

}
