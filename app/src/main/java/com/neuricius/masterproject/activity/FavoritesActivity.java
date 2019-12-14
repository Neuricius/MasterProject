package com.neuricius.masterproject.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.neuricius.masterproject.R;
import com.neuricius.masterproject.adapter.DrawerListAdapter;
import com.neuricius.masterproject.async.NetworkStateReceiver;
import com.neuricius.masterproject.database.DbHelper;
import com.neuricius.masterproject.database.model.ActorDB;
import com.neuricius.masterproject.database.model.MovieDB;
import com.neuricius.masterproject.dialog.AboutDialog;
import com.neuricius.masterproject.fragment.FavoriteActorsListFragment;
import com.neuricius.masterproject.fragment.FavoriteMoviesListFragment;
import com.neuricius.masterproject.model.NavigationItem;
import com.neuricius.masterproject.net.model.Actor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static com.neuricius.masterproject.util.UtilTools.setUpReceiver;

public class FavoritesActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }


    }



    private Spinner spFavorites;
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerPane;

    private ArrayList<NavigationItem> navigationItems = new ArrayList<NavigationItem>();
    private AlertDialog dialog;


    private DbHelper databaseHelper;

    private NetworkStateReceiver networkStateReceiver;

    private FavoriteActorsListFragment favoriteActorsListFragment;
    private FavoriteMoviesListFragment favoriteMoviesListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        setupDrawer();
        setupActionBar();

        setupSpinner();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpReceiver(FavoritesActivity.this, networkStateReceiver);

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                try {
                    List<ActorDB> allActors = getDatabaseHelper().getActorDao().queryForAll();
                    setupFavoriteActorsList(allActors);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    List<MovieDB> allMovies = getDatabaseHelper().getMovieDao().queryForAll();
                    setupFavoriteMoviesList(allMovies);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void setupSpinner(){
        spFavorites = findViewById(R.id.spFavorites);
        spFavorites.setOnItemSelectedListener(this);

        ArrayList<String> spinnerOptions = new ArrayList<>();
        spinnerOptions.add(getResources().getString(R.string.actors));
        spinnerOptions.add(getResources().getString(R.string.movies));

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, spinnerOptions);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFavorites.setAdapter(aa);
    }

    private void setupFavoriteActorsList(List<ActorDB> data) {
        favoriteActorsListFragment = new FavoriteActorsListFragment();
        favoriteActorsListFragment.setData(data);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flFavoritesFrame, favoriteActorsListFragment);
        transaction.commit();
    }

    private void setupFavoriteMoviesList(List<MovieDB> data) {
        favoriteMoviesListFragment = new FavoriteMoviesListFragment();
        favoriteMoviesListFragment.setData(data);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flFavoritesFrame, favoriteMoviesListFragment);
        transaction.commit();
    }

    private void setupActionBar() {
        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.favourites));
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
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);
    }

    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0:
                Intent searchIntent = new Intent(FavoritesActivity.this, SearchActorsActivity.class);
                startActivity(searchIntent);
                break;
            case 1:
                Intent favIntent = new Intent(FavoritesActivity.this, FavoritesActivity.class);
                startActivity(favIntent);
                break;
            case 2:
                Intent settings = new Intent(FavoritesActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case 3:
                if (dialog == null){
                    dialog = new AboutDialog(FavoritesActivity.this).prepareDialog();
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
}
