package com.neuricius.masterproject.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.neuricius.masterproject.R;
import com.neuricius.masterproject.adapter.DrawerListAdapter;
import com.neuricius.masterproject.adapter.RvCreditsAdapter;
import com.neuricius.masterproject.database.DbHelper;
import com.neuricius.masterproject.database.model.ActorDB;
import com.neuricius.masterproject.dialog.AboutDialog;
import com.neuricius.masterproject.model.NavigationItem;
import com.neuricius.masterproject.net.Contract;
import com.neuricius.masterproject.net.TmdbApiService;
import com.neuricius.masterproject.net.model.Actor;
import com.neuricius.masterproject.net.model.Cast;
import com.neuricius.masterproject.net.model.Credit;
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

import static com.neuricius.masterproject.util.UtilTools.ACTOR_ID;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN_DATABASE;
import static com.neuricius.masterproject.util.UtilTools.INTENT_ORIGIN_NET;
import static com.neuricius.masterproject.util.UtilTools.MOVIE_ID;
import static com.neuricius.masterproject.util.UtilTools.TMDB_APIKEY_PARAM_NAME;
import static com.neuricius.masterproject.util.UtilTools.TMDB_API_IMAGE_BASE_URL;

public class ActorDetailsActivity extends AppCompatActivity {

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

    private ImageView ivActorImage;

    private TextView tvActorName;
    private TextView tvDoB;
    private TextView tvDoD;
    private TextView tvBio;
    private TextView tvGender;

    private RatingBar rbActorRating;

    private RecyclerView rvKnownForList;

    private DbHelper databaseHelper;

    private Actor actorDBholder;

    private boolean landscapeMode = false;
    private boolean listShown = false;
    private boolean detailShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor_details);

        setupDrawer();
        setupActionBar();

        Integer idActor = getIntent().getIntExtra(ACTOR_ID, 0);
        getActorService(idActor);
    }

    public void setupKnownForList(final List<Cast> data) {
        rvKnownForList = findViewById(R.id.rvKnownForList);

        GridLayoutManager glm = new GridLayoutManager( ActorDetailsActivity.this, 3 );
        rvKnownForList.setLayoutManager(glm);

        RvCreditsAdapter rvCreditsAdapter = new RvCreditsAdapter(data, ActorDetailsActivity.this);
        rvKnownForList.setAdapter(rvCreditsAdapter);
        rvCreditsAdapter.setListener(new RvCreditsAdapter.OnMovieClickListener() {
            @Override
            public void onMovieSelected(Movie movie) {
                Intent movieIntent = new Intent(ActorDetailsActivity.this, MovieDetailsActivity.class);
                movieIntent.putExtra(MOVIE_ID, movie.getId());
                movieIntent.putExtra(INTENT_ORIGIN, INTENT_ORIGIN_NET);
                startActivity(movieIntent);
            }
        });
    }

    private void setupActorDetails(Actor actor) {
        this.actorDBholder = actor;
        tvActorName = findViewById(R.id.tvActorName);
        tvActorName.setText(actor.getName());

        tvDoB = findViewById(R.id.tvDoB);
        tvDoB.setText(actor.getBirthday());


        tvGender = findViewById(R.id.tvGender);
        if (actor.getGender()==0) {
            LinearLayout llGender = findViewById(R.id.llGender);
            llGender.setVisibility(View.GONE);
        } else {
        tvGender.setText(UtilTools.returnGender(actor.getGender()));
        }

        tvDoD = findViewById(R.id.tvDoD);
        if(actor.getDeathday()==null) {
            LinearLayout llDeathDay = findViewById(R.id.llDeathDay);
            llDeathDay.setVisibility(View.GONE);
        } else {
            tvDoD.setText(actor.getDeathday()+"");
        }

        rbActorRating = findViewById(R.id.rbActorRating);
        rbActorRating.setRating(actor.getPopularity().floatValue()/4f);

        tvBio = findViewById(R.id.tvBio);
        tvBio.setText(actor.getBiography());

        ivActorImage = findViewById(R.id.ivActorImage);
        String path = TMDB_API_IMAGE_BASE_URL + actor.getProfilePath();
        Uri uri = Uri.parse(path);
        Picasso.
                with(this).
                load(uri).
                placeholder(R.drawable.ic_pic_placeholder_foreground).
                error(R.drawable.ic_pic_error_foreground).
                centerCrop().
                fit().
                into(ivActorImage);

        //ubaciti kod da povlaci GetCredits
//        getCreditsService(actor.getId());
//        setupKnownForList(UtilTools.getCastfromJson("credits.json"));
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
        drawerList.setOnItemClickListener(new ActorDetailsActivity.DrawerItemClickListener());
        drawerList.setAdapter(adapter);
    }

    private void setupActionBar() {
        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.actor_details));
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
                            storagePermGranted = PermissionCheck.isStoragePermissionGranted(ActorDetailsActivity.this);
                        } while (!storagePermGranted);
                        addActorToDB();
                        break;
                    case INTENT_ORIGIN_DATABASE:
                        boolean storagePermGranted2;
                        do{
                            storagePermGranted2 = PermissionCheck.isStoragePermissionGranted(ActorDetailsActivity.this);
                        } while (!storagePermGranted2);
                        removeActorFromDB();
                        break;
                    default:
                        break;
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void removeActorFromDB() {
        if (PermissionCheck.isStoragePermissionGranted(ActorDetailsActivity.this)){
            try {
                List<ActorDB> listaZaBrisanje = getDatabaseHelper().getActorDao().queryBuilder()
                        .where()
                        .eq(ActorDB.FIELD_NAME_NAME, actorDBholder.getName())
                        .query();

                int deletedEntriesNo = getDatabaseHelper().getActorDao().delete(listaZaBrisanje);
                UtilTools.sharedPrefNotify(ActorDetailsActivity.this, getResources().getString(R.string.deleted) ,getResources().getString(R.string.entries_deleted_no) + deletedEntriesNo);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }



    }

    private void addActorToDB() {
        ActorDB actorDB = new ActorDB();
        actorDB.setId(actorDBholder.getId());
        actorDB.setName(actorDBholder.getName());
        actorDB.setProfilePath(actorDBholder.getProfilePath());
        actorDB.setPopularity(actorDBholder.getPopularity());
        actorDB.setKnownForDepartment(actorDBholder.getKnownForDepartment());


            try {
                getDatabaseHelper().getActorDao().create(actorDB);
                UtilTools.sharedPrefNotify(ActorDetailsActivity.this, getResources().getString(R.string.success) , getResources().getString(R.string.actor_added_to_favorites));
            } catch (SQLException e) {
                e.printStackTrace();
                UtilTools.sharedPrefNotify(ActorDetailsActivity.this, getResources().getString(R.string.error) ,getResources().getString(R.string.please_try_again_later));
            }



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

    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0:
                Intent searchIntent = new Intent(ActorDetailsActivity.this, SearchActorsActivity.class);
                startActivity(searchIntent);
                break;
            case 1:
                Intent favIntent = new Intent(ActorDetailsActivity.this, FavoritesActivity.class);
                startActivity(favIntent);
                break;
            case 2:
                Intent settings = new Intent(ActorDetailsActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case 3:
                if (dialog == null){
                    dialog = new AboutDialog(ActorDetailsActivity.this).prepareDialog();
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

    private void getActorService(Integer idActor) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(TMDB_APIKEY_PARAM_NAME, Contract.getApiKey(ActorDetailsActivity.this));

        Call<Actor> call = TmdbApiService.apiInterface().TMDBGetActor(idActor ,queryParams);
        call.enqueue(new Callback<Actor>() {
            @Override
            public void onResponse(Call<Actor> call, Response<Actor> response) {
                if (response.code() == 200){
                    Actor resp = response.body();
                    setupActorDetails(resp);
                    //ovde treba ispaliti odmah async task da skupi sve credits
                    getCreditsService(resp.getId());
                } else {
                    UtilTools.sharedPrefNotify(ActorDetailsActivity.this, getResources().getString(R.string.error), "" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Actor> call, Throwable t) {

            }
        });
    }

    private void getCreditsService(Integer idActor) {
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(TMDB_APIKEY_PARAM_NAME, Contract.getApiKey(getBaseContext()));

        Call<Credit> call = TmdbApiService.apiInterface().TMDBGetMovieCredits(idActor ,queryParams);
        call.enqueue(new Callback<Credit>() {
            @Override
            public void onResponse(Call<Credit> call, Response<Credit> response) {
                if (response.code() == 200){
                    Credit resp = response.body();
                    setupKnownForList(resp.getCast());
                } else {
                    UtilTools.sharedPrefNotify(ActorDetailsActivity.this, getResources().getString(R.string.error), "" + response.code());
                }
            }

            @Override
            public void onFailure(Call<Credit> call, Throwable t) {

            }
        });
    }

    //Metoda koja komunicira sa bazom podataka
    public DbHelper getDatabaseHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DbHelper.class);
        }
        return databaseHelper;
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
        actorDBholder = null;
    }
}
