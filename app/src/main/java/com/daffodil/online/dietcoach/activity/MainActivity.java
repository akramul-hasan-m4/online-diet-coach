package com.daffodil.online.dietcoach.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.daffodil.online.dietcoach.BuildConfig;
import com.daffodil.online.dietcoach.R;
import com.daffodil.online.dietcoach.db.local.SharedPreferencesConfig;
import com.daffodil.online.dietcoach.model.Users;
import com.daffodil.online.dietcoach.ui.ChatRoomFragment;
import com.daffodil.online.dietcoach.ui.DashBoard;
import com.daffodil.online.dietcoach.ui.DoctorProfile;
import com.daffodil.online.dietcoach.ui.DrRegFragment;
import com.daffodil.online.dietcoach.ui.FoodFragment;
import com.daffodil.online.dietcoach.ui.SerialListFragment;
import com.daffodil.online.dietcoach.ui.UserProfile;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;

import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.util.Objects;

import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.CURRENT_USER;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.NO;
import static com.daffodil.online.dietcoach.db.local.ShareStoreConstants.REMEMBER_ME;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";
    DrawerLayout drawer;
    //private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusBar));
      //  FloatingActionButton fab = findViewById(R.id.fab);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigationView);

        if (findViewById(R.id.nav_host_fragment) != null){
            if (savedInstanceState != null){
                return;
            }else{
                getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new DashBoard()).commit();
            }
        }

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, drawer);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        hideMenu(navigationView);
    }

    private void hideMenu(NavigationView navigationView){
        Menu menu = navigationView.getMenu();
        MenuItem target = menu.findItem(R.id.nav_user_profile);
        Gson gson = new Gson();
        String user = SharedPreferencesConfig.getStringData(Objects.requireNonNull(this), CURRENT_USER);
        Users userObj = gson.fromJson(user, Users.class);

        if (userObj != null && userObj.getUserType() != null) {

            if(userObj.getUserType().equalsIgnoreCase("admin")){
                target.setVisible(true);
            }else {
                target.setVisible(false);
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.nav_host_fragment), drawer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.add(1, 1, Menu.NONE, "Log Out");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == 1) {
            SharedPreferencesConfig.saveStringData(Objects.requireNonNull(getApplicationContext()), REMEMBER_ME, NO);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0){
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        item.setChecked(true);
        if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_serial_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new SerialListFragment()).addToBackStack(null).commit();
        }else if (id == R.id.nav_send) {
            rateUs();
        }else if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new DashBoard()).addToBackStack(null).commit();
        }else if (id == R.id.nav_user_profile) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new UserProfile()).addToBackStack(null).commit();
        }else if (id == R.id.nav_chat) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new ChatRoomFragment()).addToBackStack(null).commit();
        }else if (id == R.id.nav_food_list) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new FoodFragment()).addToBackStack(null).commit();
        }else if (id == R.id.nav_dr_reg) {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new DrRegFragment()).addToBackStack(null).commit();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void rateUs(){
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    private void shareApp(){
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Hello friend you can use online diet coach. More details in play Store " +
                Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Online diet coach In bangladesh");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
