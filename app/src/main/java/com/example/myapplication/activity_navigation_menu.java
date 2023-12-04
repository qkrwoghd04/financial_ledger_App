package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class activity_navigation_menu extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{

    FloatingActionButton floatingActionButton;
    DrawerLayout drawerLayout;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_menu);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        floatingActionButton = findViewById(R.id.fab);
        drawerLayout =findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        replaceFragment(new HomeFragment());

        bottomNavigationView.setBackground(null);
//        bottomNavigationView.setOnItemSelectedListener(item -> {
//
//                switch (item.getItemId()) {
//                    case R.id.home:
//                        replaceFragment(new HomeFragment());
//                        break;
//                    case R.id.statistic:
//                        replaceFragment(new StatisticFragment());
//                        break;
//                    case R.id.transaction:
//                        replaceFragment(new TransactionFragment());
//                        break;
//                    case R.id.account_info:
//                        replaceFragment(new AccountFragment());
//                        break;
//                }
//
//            return true;
//        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomDialog();
            }
        });
    }

    // Outside OnCreate
    private  void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout);

        LinearLayout expensesLayout = dialog.findViewById(R.id.layoutExpenses);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        expensesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(activity_navigation_menu.this,"Upload your expense",Toast.LENGTH_SHORT).show();

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}