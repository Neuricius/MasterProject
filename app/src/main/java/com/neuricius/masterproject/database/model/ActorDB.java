package com.neuricius.masterproject.database.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.neuricius.masterproject.net.model.Actor;


@DatabaseTable(tableName = ActorDB.TABLE_NAME_ACTOR)
public class ActorDB extends Actor {

    public static final String TABLE_NAME_ACTOR = "actor";

    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_PROFILEPATH = "profilePath";
    public static final String FIELD_NAME_POPULARITY = "popularity";
    public static final String FIELD_NAME_TMDBID = "tmdbid";
    private static final String FIELD_NAME_KNOWNFORDEPT = "knownForDepartment";


    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private Integer actorID;

    @DatabaseField(columnName = FIELD_NAME_TMDBID)
    private Integer id;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String name;

    @DatabaseField(columnName = FIELD_NAME_PROFILEPATH)
    private String profilePath;

    @DatabaseField(columnName = FIELD_NAME_POPULARITY)
    private Double popularity;

    @DatabaseField(columnName = FIELD_NAME_KNOWNFORDEPT)
    private String knownForDepartment;

    /**
     * No args constructor for use in serialization
     *
     */
    public ActorDB() {
    }

    /**
     * @param actorID
     * @param id
     * @param name
     * @param popularity
     * @param profilePath
     * @param knownForDepartment
     */
    public ActorDB(Integer actorID, Integer id, String name,Double popularity, String profilePath, String knownForDepartment) {
        super();
        this.id = id;
        this.name = name;
        this.popularity = popularity;
        this.profilePath = profilePath;
        this.actorID = actorID;
        this.knownForDepartment = knownForDepartment;
    }

    public Integer getActorID() {
        return actorID;
    }

    public void setActorID(Integer actorID) {
        this.actorID = actorID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    @Override
    public String toString() {
        return name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getKnownForDepartment() {
        return knownForDepartment;
    }

    @Override
    public void setKnownForDepartment(String knownForDepartment) {
        this.knownForDepartment = knownForDepartment;
    }
}