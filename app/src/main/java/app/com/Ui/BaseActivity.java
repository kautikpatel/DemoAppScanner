package app.com.Ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app.com.Managers.SharedPreferenceManagerFile;
import app.com.Utils.Log;
import app.com.myapp.R;


public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setNavigationPanel() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //toggle.setDrawerIndicatorEnabled(false); //disable "hamburger to arrow" drawable
        //toggle.setHomeAsUpIndicator(R.drawable.ic_menu_share);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT);
                }
                Log.i("setNavigationOnClickListener", "setNavigationOnClickListener");
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        TextView tvUserName = header.findViewById(R.id.tvUserName);
        TextView tvEmail = header.findViewById(R.id.tvEmail);
        SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(BaseActivity.this);
        String email = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_EMAIL_ID);
        tvEmail.setText(email);
        String firstName = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_FIRST_NAME);
        String lastName = sharedPref.getFromSharedPreference(SharedPreferenceManagerFile.PREFERENCE_FIRST_NAME);
        String name = "";
        if (!TextUtils.isEmpty(firstName)) {
            name = firstName + " ";
        }
        if (!TextUtils.isEmpty(lastName)) {
            name = name + lastName;
        }
        if (TextUtils.isEmpty(name.trim())) {
            tvUserName.setVisibility(View.GONE);
        } else {
            tvUserName.setVisibility(View.VISIBLE);
            tvUserName.setText(name);
        }
        navigationView.setNavigationItemSelectedListener(this);
    }

    protected void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeAsUpIndicator(R.drawable.ic_toggle_back);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onItemSelected(item);
            }
        }, 150);
        return true;
    }

    public boolean onItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dialog.dismiss();
                    SharedPreferenceManagerFile sharedPref = new SharedPreferenceManagerFile(BaseActivity.this);
                    sharedPref.clearPreference();
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            AlertDialog alertDialog;
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, final int which) {
                    dialog.dismiss();
                }
            });
            String desc = "Do you really want to Logout?";
            alertDialog = builder.create();
            alertDialog.setMessage(desc);
            alertDialog.setCancelable(false);
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(final DialogInterface dialog) {
                    Button negativeButton = ((android.support.v7.app.AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                    Button positiveButton = ((android.support.v7.app.AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                    positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
                    negativeButton.setTextColor(getResources().getColor(R.color.color_dark_gray));
                    negativeButton.invalidate();
                    positiveButton.invalidate();
                }
            });
            alertDialog.show();
        } else if (id == R.id.nav_report) {
            Intent intent = new Intent(BaseActivity.this, ReportActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
