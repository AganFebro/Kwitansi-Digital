package com.kudig.kwitansidigital;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class input_data extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_data);

        BottomNavigationView navView = findViewById(R.id.nav_view_input);
        navView.setSelectedItemId(0);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.btn_input_home:
                        Intent intent = new Intent(input_data.this, MainActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.btn_input_about:
                        Intent intent1 = new Intent(input_data.this, MainActivity.class);
                        startActivity(intent1);
                        return true;
                }
                return true;
            }
        });







    }
}