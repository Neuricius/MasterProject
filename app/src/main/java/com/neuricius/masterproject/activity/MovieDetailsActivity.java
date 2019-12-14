package com.neuricius.masterproject.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.neuricius.masterproject.R;
import com.neuricius.masterproject.adapter.DrawerListAdapter;
import com.neuricius.masterproject.async.NetworkStateReceiver;
import com.neuricius.masterproject.database.DbHelper;
import com.neuricius.masterproject.database.model.MovieDB;
import com.neuricius.masterproject.dialog.AboutDialog;
import com.neuricius.masterproject.model.NavigationItem;
import com.neuricius.masterproject.net.Contract;
import com.neuricius.masterproject.net.TmdbApiService;
import com.neuricius.masterproject.net.model.Genre;
import com.neuricius.masterproject.net.model.Movie;
import com.neuricius.masterproject.util.PermissionCheck;
import com.neuricius.masterproject.util.UtilTools;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN_DATABASE;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN_NET;
import static com.neuricius.masterproject.util.UtilTools.LOG_TAG_SQL_EXCEPTION;
import static com.neuricius.masterproject.util.UtilTools.MOVIE_ID;
import static com.neuricius.masterproject.util.UtilTools.TMDB_APIKEY_PARAM_NAME;
import static com.neuricius.masterproject.util.UtilTools.TMDB_API_IMAGE_BASE_URL;
import static com.neuricius.masterproject.util.UtilTools.setUpReceiver;

public class MovieDetailsActivity extends AppCompatActivity {

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();
    private AlertDialog dialog;

    private ImageView ivCoverImage;
    private ImageView ivMoviePoster;

    private TextView tvGenre;
    private TextView tvReleaseYear;
    private TextView tvMovieName;
    private TextView tvMovieOriginalName;
    private TextView tvMovieTagline;
    private TextView tvOverview;

    private RatingBar rbMovieRating;

    private DbHelper databaseHelper;

    private Movie movieDBholder;
    private String genresDBholder;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        setupDrawer();
        setupActionBar();
        Integer idMovie = getIntent().getIntExtra(MOVIE_ID, 0);
        getMovieService(idMovie);
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpReceiver(MovieDetailsActivity.this, networkStateReceiver);

    }

    @Override
    protected void onPause() {
        if(networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_detail_options_menu, menu);
        switch (getIntent().getStringExtra(INTENT_ORIGIN)) {
            case INTENT_ORIGIN_NET:
                menu.findItem(R.id.action_add).setTitle(getResources().getString(R.string.add_to_favorites));
                break;
            case INTENT_ORIGIN_DATABASE:
                menu.findItem(R.id.action_add).setTitle(getResources().getString(R.string.remove_from_favorites));
                break;
            default:
                menu.findItem(R.id.action_add).setVisible(false);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                switch (getIntent().getStringExtra(INTENT_ORIGIN)) {
                    case INTENT_ORIGIN_NET:
                        boolean storagePermGranted;
                        do{
                            storagePermGranted = PermissionCheck.isStoragePermissionGranted(MovieDetailsActivity.this);
                        } while (!storagePermGranted);
                        addMovieToDB();
                        break;
                    case INTENT_ORIGIN_DATABASE:
                        boolean storagePermGranted2;
                        do{
                            storagePermGranted2 = PermissionCheck.isStoragePermissionGranted(MovieDetailsActivity.this);
                        } while (!storagePermGranted2);
                        removeMovieFromDB();
                        break;
                    default:
                        break;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // nakon rada sa bazo podataka potrebno je obavezno
        //osloboditi resurse!
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    private void setupDrawer() {
        // Draws navigation items
        navigationItems.add(new NavigationItem(getString(R.string.drawer_search), getString(R.string.drawer_search_long), R.drawable.ic_action_search_foreground));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_fav), getString(R.string.drawer_fav_long), R.drawable.ic_action_fav_foreground));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_settings_long), R.drawable.ic_action_settings_foreground));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about_foreground));

        drawerLayout = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.navList);

        // Populate the Navigtion Drawer with options
        drawerPane = findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setOnItemClickListener(new MovieDetailsActivity.DrawerItemClickListener());
        drawerList.setAdapter(adapter);
    }

    private void setupActionBar() {
        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.movie_details));
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_home_foreground);
            actionBar.setHomeButtonEnabled(true);
            actionBar.show();
        }

        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
//                getSupportActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
//                getSupportActionBar().setTitle(drawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
    }

    private void setupMovieDetails(Movie movie) {
        this.movieDBholder = movie;

        ivCoverImage = findViewById(R.id.ivCoverImage);
        String path = TMDB_API_IMAGE_BASE_URL + movie.getBackdropPath();
        Uri uri = Uri.parse(path);
        Picasso.
                with(this).
                load(uri).
                placeholder(R.drawable.ic_pic_placeholder_foreground).
                error(R.drawable.ic_pic_error_foreground).
                centerCrop().
                fit().
                into(ivCoverImage);

        ivMoviePoster = findViewById(R.id.ivMoviePoster);
        path = TMDB_API_IMAGE_BASE_URL + movie.getPosterPath();
        uri = Uri.parse(path);
        Picasso.
                with(this).
                load(uri).
                placeholder(R.drawable.ic_pic_placeholder_foreground).
                error(R.drawable.ic_pic_error_foreground).
                centerCrop().
                fit().
                into(ivMoviePoster);

        tvGenre = findViewById(R.id.tvGenre);
        String genres = "";
        for (Genre genre : movie.getGenres()){
            genres += genre.getName() +"|";
        }
        this.genresDBholder = genres;
        tvGenre.setText(genres);

        tvReleaseYear = findViewById(R.id.tvReleaseYear);
        tvReleaseYear.setText(movie.getReleaseDate().substring(0,4));

        tvMovieName = findViewById(R.id.tvMovieName);
        tvMovieName.setText(movie.getTitle());

        tvMovieOriginalName = findViewById(R.id.tvMovieOriginalName);
        tvMovieOriginalName.setText(movie.getOriginalTitle());

        tvMovieTagline = findViewById(R.id.tvMovieTagline);
        tvMovieTagline.setText(movie.getTagline());

        tvOverview = findViewById(R.id.tvOverview);
        tvOverview.setText(movie.getOverview());

        rbMovieRating = findViewById(R.id.rbMovieRating);
        rbMovieRating.setRating(movie.getVoteAverage().floatValue());

    }

    private void removeMovieFromDB() {
        try {
            List<MovieDB> listaZaBrisanje = getDatabaseHelper().getMovieDao().queryBuilder()
                    .where()
                    .eq(MovieDB.FIELD_NAME_TITLE, movieDBholder.getTitle())
                    .query();

            int deletedEntriesNo = getDatabaseHelper().getMovieDao().delete(listaZaBrisanje);
            UtilTools.sharedPrefNotify(MovieDetailsActivity.this, getResources().getString(R.string.deleted) ,getResources().getString(R.string.entries_deleted_no) + deletedEntriesNo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addMovieToDB() {
        MovieDB movieDB = new MovieDB();
        movieDB.setId(movieDBholder.getId());
        movieDB.setPosterPath(movieDBholder.getPosterPath());
        movieDB.setTitle(movieDBholder.getTitle());
        movieDB.setOriginalTitle(movieDBholder.getOriginalTitle());
        movieDB.setPopularity(movieDBholder.getVoteAverage());
        movieDB.setReleaseDate(movieDBholder.getReleaseDate());
        movieDB.setGenres(genresDBholder);

        try {
            getDatabaseHelper().getMovieDao().create(movieDB);
            UtilTools.sharedPrefNotify(MovieDetailsActivity.this, getResources().getString(R.string.success) ,getResources().getString(R.string.movie_added_to_favorites));
        } catch (SQLException e) {
            Log.e(LOG_TAG_SQL_EXCEPTION, e.getMessage());
            e.printStackTrace();
            UtilTools.sharedPrefNotify(MovieDetailsActivity.this, getResources().getString(R.string.error) ,getResources().getString(R.string.please_try_again_later));
        }

    }

    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0:
                Intent searchIntent = new Intent(MovieDetailsActivity.this, SearchActorsActivity.class);
                startActivity(searchIntent);
                break;
            case 1:
                Intent favIntent = new Intent(MovieDetailsActivity.this, FavoritesActivity.class);
                startActivity(favIntent);
                break;
            case 2:
                Intent settings = new Intent(MovieDetailsActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case 3:
                if (dialog == null){
                    dialog = new AboutDialog(MovieDetailsActivity.this).prepareDialog();
                } else {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                dialog.show();
                break;
        }

        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerPane);
    }

    //Metoda koja komunicira sa bazom podataka
    public DbHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DbHelper.class);
        }
        return databaseHelper;
    }

    private void getMovieService(Integer idMovie){

        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(TMDB_APIKEY_PARAM_NAME, Contract.getApiKey(getBaseContext()));

        Call<Movie> call = TmdbApiService.apiInterface().TMDBGetMovie(idMovie, queryParams);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.code() == 200){
                    setupMovieDetails(response.body());
                } else {
                    UtilTools.sharedPrefNotify(MovieDetailsActivity.this, getResources().getString(R.string.error), "" + response.code());
                }

            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }
}
