package com.kudig.kwitansidigital;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kudig.kwitansidigital.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Button Home,About;
    private FloatingActionButton Add;

    KwitansiDB KwitansiDB;

    List<KwitansiEntity> KwitansiList;

    ListView list;

    KwitansiDAO kwitansiDAO;

    RecyclerView myRecycler;

    KwitansiAdapter kwitansiAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Home = findViewById(R.id.btn_home);
        Add = findViewById(R.id.add_btn);
        About = findViewById(R.id.btn_about);
        myRecycler = findViewById(R.id.kwitansiRecycler);

        kwitansiAdapter = new KwitansiAdapter(getApplicationContext());

        myRecycler.setAdapter(kwitansiAdapter);
        myRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fetchData();

        RoomDatabase.Callback myCallBack = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);
            }
        };


        KwitansiDB = Room.databaseBuilder(getApplicationContext(), KwitansiDB.class,
                "KwitansiDB").addCallback(myCallBack).build();

        kwitansiDAO = KwitansiDB.getKwitansiDAO();

        KwitansiDB = KwitansiDB.getInstance(getApplicationContext());


        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_about)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navigation_home);
            }
        });
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navigation_add);
            }
        });
        About.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.navigation_about);
            }
        });


    }


    private void fetchData() {
        if (kwitansiDAO != null) {
            List<KwitansiEntity> KwitansiList = kwitansiDAO.getAllKwitansi();
            KwitansiList = KwitansiDB.getKwitansiDAO().getAllKwitansi();
            for (int i = 0; i < KwitansiList.size(); i++) {
                KwitansiEntity kwitansi = KwitansiList.get(i);
                kwitansiAdapter.addKwitansi(kwitansi);
            }

        }
    }

}