package com.groupfive.satapp.ui.inventariable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.groupfive.satapp.R;
import com.groupfive.satapp.ui.inventariable.ILocationListener;

public class LocationActivity extends AppCompatActivity implements ILocationListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
    }

    @Override
    public void onLocationClick(String locationModel) {

    }
}
