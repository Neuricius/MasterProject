package com.neuricius.masterproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.neuricius.masterproject.database.model.ActorDB;
import com.neuricius.masterproject.database.model.KnownForDB;
import com.neuricius.masterproject.database.model.MovieDB;

public class DbHelper extends OrmLiteSqliteOpenHelper {

    //Dajemo ime bazi
    private static final String DATABASE_NAME    = "actorsandmovies.db";

    //i pocetnu verziju baze. Obicno krece od 1
    private static final int    DATABASE_VERSION = 1;

    //dao za glumca
    private Dao<ActorDB, Integer> actorDao = null;

    //dao za film
    private Dao<MovieDB, Integer> movieDao = null;

    //dao za vezu
    private Dao<KnownForDB, Integer> filmDao = null;

    //Potrebno je dodati konstruktor zbog pravilne inicijalizacije biblioteke
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Prilikom kreiranja baze potrebno je da pozovemo odgovarajuce metode biblioteke
    //prilikom kreiranja moramo pozvati TableUtils.createTable za svaku tabelu koju imamo
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, ActorDB.class);
            TableUtils.createTable(connectionSource, MovieDB.class);
            TableUtils.createTable(connectionSource, KnownForDB.class);

        }  catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    //kada zelimo da izmenomo tabele, moramo pozvati TableUtils.dropTable za sve tabele koje imamo
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, ActorDB.class, true);
            TableUtils.dropTable(connectionSource, MovieDB.class, true);
            TableUtils.dropTable(connectionSource, KnownForDB.class, true);
            onCreate(database, connectionSource);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }

    //jedan Dao objekat sa kojim komuniciramo. Ukoliko imamo vise tabela
    //potrebno je napraviti Dao objekat za svaku tabelu
    public Dao<ActorDB, Integer> getActorDao() throws java.sql.SQLException {
        if (actorDao == null) {
            actorDao = getDao(ActorDB.class);
        }
        return actorDao;
    }

    public Dao<MovieDB, Integer> getMovieDao() throws java.sql.SQLException {
        if (movieDao == null) {
            movieDao = getDao(MovieDB.class);
        }
        return movieDao;
    }

    public Dao<KnownForDB, Integer> getKnownForDao() throws java.sql.SQLException {
        if (filmDao == null) {
            filmDao = getDao(KnownForDB.class);
        }
        return filmDao;
    }


    //obavezno prilikom zatvarnaj rada sa bazom osloboditi resurse
    @Override
    public void close() {
        actorDao = null;
        movieDao = null;
        filmDao = null;

        super.close();
    }

}
