package com.ceydakamali.artbookjava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ceydakamali.artbookjava.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //bu menu oluşturulduğunda ne olacak, bağlama işlemi yapılcak menuyu layoutu koda baglicaz.

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.art_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //menuye tıklandığında ne olacak
        if(item.getItemId() == R.id.addArt){
            Intent intent = new Intent(this, ArtActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}