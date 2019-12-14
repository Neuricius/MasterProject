package com.neuricius.masterproject.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.neuricius.masterproject.R;
import com.neuricius.masterproject.adapter.DrawerListAdapter;
import com.neuricius.masterproject.async.NetworkStateReceiver;
import com.neuricius.masterproject.fragment.ActorListFragment;
import com.neuricius.masterproject.model.NavigationItem;
import com.neuricius.masterproject.net.Contract;
import com.neuricius.masterproject.net.TmdbApiService;
import com.neuricius.masterproject.net.model.Result;
import com.neuricius.masterproject.net.model.SearchResult;
import com.neuricius.masterproject.util.UtilTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.neuricius.masterproject.util.PermissionCheck.PERM_TAG;
import static com.neuricius.masterproject.util.UtilTools.TMDB_APIKEY_PARAM_NAME;
import static com.neuricius.masterproject.util.UtilTools.TMDB_QUERY_PARAM_NAME;
import static com.neuricius.masterproject.util.UtilTools.setUpReceiver;
import static com.neuricius.masterproject.util.UtilTools.sharedPrefNotify;

public class SearchActorsActivity extends AppCompatActivity {

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


    private ActorListFragment actorListFragment;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_actors);

        setupDrawer();
        setupActionBar();
        setupSearchBar();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setUpReceiver(SearchActorsActivity.this, networkStateReceiver);
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            Log.v(PERM_TAG,"Permission: "+permissions[0]+ "was "+grantResults[0]);
        }
    }

    private void setupDrawer() {
        // Draws navigation items
        navigationItems.add(new NavigationItem(getString(R.string.drawer_fav), getString(R.string.drawer_fav_long), R.drawable.ic_action_fav_foreground));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_settings), getString(R.string.drawer_settings_long), R.drawable.ic_action_settings_foreground));
        navigationItems.add(new NavigationItem(getString(R.string.drawer_about), getString(R.string.drawer_about_long), R.drawable.ic_action_about_foreground));

        drawerLayout =  findViewById(R.id.drawer_layout);
        drawerList =  findViewById(R.id.navList);

        // Populate the Navigtion Drawer with options
        drawerPane =  findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, navigationItems);

        // set a custom shadow that overlays the main content when the drawer opens
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        drawerList.setAdapter(adapter);
    }

    private void setupActionBar() {
        // Enable ActionBar app icon to behave as action to toggle nav drawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getResources().getString(R.string.search));
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

    //ukoliko nam ne treba meni sa opcijama, samo izostavimo metode onCreateOptionsMenu & onOptionsItemSelected
//    //kreiranje menija za opcije sa desne strane
//    //izgled menija se definise kroz XML
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_detail_options_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    //kada se klikne na neku opciju iz menija
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_add:
//                Toast.makeText(this, "napisati metod za dodavanje", Toast.LENGTH_SHORT).show();
//                break;
//            case R.id.action_edit:
//                Toast.makeText(this, "napisati metod za osvezavanje", Toast.LENGTH_SHORT).show();                break;
//            case R.id.action_about:
//                UtilTools.showAboutDialog(SearchActorsActivity.this);
//                break;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void selectItemFromDrawer(int position) {
        switch (position) {
            case 0:
                Intent favIntent = new Intent(SearchActorsActivity.this, FavoritesActivity.class);
                startActivity(favIntent);
                break;
            case 1:
                Intent settings = new Intent(SearchActorsActivity.this, SettingsActivity.class);
                startActivity(settings);
                break;
            case 2:
                UtilTools.showAboutDialog(SearchActorsActivity.this);
                break;
        }

        drawerList.setItemChecked(position, true);
        drawerLayout.closeDrawer(drawerPane);
    }

    private void setupSearchBar(){
        final EditText query = findViewById(R.id.etSearch);

        Button bSearch = findViewById(R.id.bSearch);
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UtilTools.sharedPrefPreserveData(SearchActorsActivity.this)) {
                    searchPeopleService(query.getText().toString()); //ne treba menjati prazna polja, izgleda da retrofit onda poludi
                } else {
                    sharedPrefNotify(SearchActorsActivity.this, getResources().getString(R.string.aborted) ,getResources().getString(R.string.not_on_wifi));
                }
            }
        });
    }

    private void setupResultListRV(List<Result> data) {
        actorListFragment = new ActorListFragment();
        actorListFragment.setData(data);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.flActorFrame, actorListFragment);
        transaction.commit();
    }

    private void searchPeopleService(String query){

        /* https://api.themoviedb.org/3/search/person?api_key=50038a6708b8a31d633d6e86190e6cd9&query=Brian%20Adams */
        HashMap<String, String> queryParams = new HashMap<>();
        queryParams.put(TMDB_QUERY_PARAM_NAME, query);
        queryParams.put(TMDB_APIKEY_PARAM_NAME, Contract.getApiKey(getBaseContext()));

        Call<SearchResult> call = TmdbApiService.apiInterface().TMDBSearchPeople(queryParams);
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.code() == 200){
                    SearchResult resp = response.body();
                    setupResultListRV(resp.getResults());
                } else {
                    sharedPrefNotify(SearchActorsActivity.this, getResources().getString(R.string.error), "" + response.code());
                }
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                sharedPrefNotify(SearchActorsActivity.this, getResources().getString(R.string.error), "" + t.getMessage());
            }
        });
    }



}
