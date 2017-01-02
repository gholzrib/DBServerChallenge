package gholzrib.dbserverchallenge.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import gholzrib.dbserverchallenge.R;
import gholzrib.dbserverchallenge.core.models.User;
import gholzrib.dbserverchallenge.core.utils.Constants;
import gholzrib.dbserverchallenge.core.utils.PreferencesManager;
import gholzrib.dbserverchallenge.ui.fragments.Restaurants;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private AlertDialog mLogOutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.act_main_drawer_menu_restaurants);

        User user = PreferencesManager.getUser(this);
        TextView txtUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.nav_header_main_txt_name);
        if (user.getName() != null) txtUserName.setText(user.getName());

        replaceFragment(Restaurants.newInstance(), Constants.FRG_TAG_RESTAURANTS);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Restaurants restaurants = (Restaurants) getSupportFragmentManager().findFragmentByTag(Constants.FRG_TAG_RESTAURANTS);
        if (restaurants != null) {
            if (restaurants.mCurrentMode == Constants.VISUALIZATION_MODE_MAP) {
                menu.getItem(0).setIcon(R.drawable.ic_list_white_24dp);
            } else {
                menu.getItem(0).setIcon(R.drawable.ic_map_white_24dp);
            }
        } else {
            menu.removeItem(R.id.action_view_form);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_view_form) {
            Restaurants restaurants = (Restaurants) getSupportFragmentManager().findFragmentByTag(Constants.FRG_TAG_RESTAURANTS);
            if (restaurants != null) {
                if (restaurants.mCurrentMode == Constants.VISUALIZATION_MODE_MAP) {
                    restaurants.mCurrentMode = Constants.VISUALIZATION_MODE_LIST;
                } else {
                    restaurants.mCurrentMode = Constants.VISUALIZATION_MODE_MAP;
                }
                restaurants.changeVisualizationMode();
                invalidateOptionsMenu();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.act_main_drawer_menu_restaurants:
                replaceFragment(Restaurants.newInstance(), Constants.FRG_TAG_RESTAURANTS);
                break;
            case R.id.act_main_drawer_menu_logout:
                showLogOutDialog();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void replaceFragment(Fragment fragment, final String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_main, fragment, tag)
                .commit();
    }

    private void showLogOutDialog() {
        if (mLogOutDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.logout_title));
            builder.setMessage(getString(R.string.logout_message));
            builder.setCancelable(false);
            builder.setNegativeButton(getString(R.string.logout_action_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.setPositiveButton(getString(R.string.logout_action_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    PreferencesManager.clearPreferences(MainActivity.this);
                    startActivity(new Intent(MainActivity.this, Login.class));
                    dialogInterface.dismiss();
                }
            });
            mLogOutDialog = builder.create();
        }
        mLogOutDialog.show();
    }
}
