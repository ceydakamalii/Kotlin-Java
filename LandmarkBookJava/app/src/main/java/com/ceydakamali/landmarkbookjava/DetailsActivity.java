package com.ceydakamali.landmarkbookjava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.ceydakamali.landmarkbookjava.databinding.ActivityDetailsBinding;

public class DetailsActivity extends AppCompatActivity {

    //View Binding
    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());//inflate: xml(layout) ile kodumuzu bağlamaya çalıştığımız yer
        View view = binding.getRoot(); //bağladığımız şeyi görünüme çeviriyor
        setContentView(view); // xml yerine bağlamaya çalıştığımız yer ortaya çıkıyor.

        //Intent intent=getIntent();
        //Casting
        //Landmark selectedLandmark =(Landmark) intent.getSerializableExtra("landmark");

        //Landmark selectedLandmark = chosenLandmark;

        Singleton singleton= Singleton.getInstance();
        Landmark selectedLandmark= singleton.getSentLandmark();

        binding.nameText.setText(selectedLandmark.name);
        binding.countryText.setText(selectedLandmark.country);
        binding.imageView.setImageResource(selectedLandmark.image);
    }
}