package com.neuricius.masterproject.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = KnownForDB.TABLE_NAME_KNOWNFOR)
public class KnownForDB {

    public static final String TABLE_NAME_KNOWNFOR = "knownfor";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_ACTOR_ID = "actor_id";
    public static final String FIELD_NAME_MOVIE_ID = "movie_id";


    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private Integer knownforID;

    @DatabaseField(foreign = true, columnName = FIELD_NAME_ACTOR_ID)
    private ActorDB actor;

    @DatabaseField(foreign = true, columnName = FIELD_NAME_MOVIE_ID)
    private MovieDB movie;

    public KnownForDB() {
    }

    public KnownForDB(Integer knownforID, ActorDB actor, MovieDB movie) {
        this.knownforID = knownforID;
        this.actor = actor;
        this.movie = movie;
    }

    public Integer getKnownforID() {
        return knownforID;
    }

    public void setKnownforID(Integer knownforID) {
        this.knownforID = knownforID;
    }

    public ActorDB getActor() {
        return actor;
    }

    public void setActor(ActorDB actor) {
        this.actor = actor;
    }

    public MovieDB getMovie() {
        return movie;
    }

    public void setMovie(MovieDB movie) {
        this.movie = movie;
    }

    @Override
    public String toString() {
        return actor.toString() + " " + movie.toString();
    }
}
