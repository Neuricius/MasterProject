
package com.neuricius.masterproject.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = MovieDB.TABLE_NAME_MOVIE)
public class MovieDB {

    public static final String TABLE_NAME_MOVIE = "movie";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_TMDBID = "tmdbId";
    public static final String FIELD_NAME_ORIGINALTITLE = "originalTitle";
    public static final String FIELD_NAME_POPULARITY = "popularity";
    public static final String FIELD_NAME_POSTERPATH = "posterPath";
    public static final String FIELD_NAME_RELEASEDATE = "releaseDate";
    public static final String FIELD_NAME_TITLE = "title";
    private static final String FIELD_NAME_GENRES = "genres";


    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private Integer movieID;

    @DatabaseField(columnName = FIELD_NAME_TMDBID)
    private Integer id;

    @DatabaseField(columnName = FIELD_NAME_ORIGINALTITLE)
    private String originalTitle;

    @DatabaseField(columnName = FIELD_NAME_POPULARITY)
    private Double popularity;

    @DatabaseField(columnName = FIELD_NAME_POSTERPATH)
    private String posterPath;

    @DatabaseField(columnName = FIELD_NAME_RELEASEDATE)
    private String releaseDate;

    @DatabaseField(columnName = FIELD_NAME_TITLE)
    private String title;

    @DatabaseField(columnName = FIELD_NAME_GENRES)
    private String genres;

    /**
     * No args constructor for use in serialization
     *
     */
    public MovieDB() {
    }

    /**
     * @param movieID
     * @param id
     * @param originalTitle
     * @param popularity
     * @param posterPath
     * @param releaseDate
     * @param title
     * @param genres
     */
    public MovieDB(Integer movieID, Integer id, String originalTitle, Double popularity, String posterPath, String releaseDate, String title, String genres) {
        super();
        this.id = id;
        this.originalTitle = originalTitle;
        this.popularity = popularity;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.title = title;
        this.movieID = movieID;
        this.genres = genres;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }

    public Integer getMovieID() {
        return movieID;
    }

    public void setMovieID(Integer movieID) {
        this.movieID = movieID;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
}
