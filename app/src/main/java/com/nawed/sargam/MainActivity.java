package com.nawed.sargam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.nawed.sargam.Fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //load welcome fragment
        loadFragment(new WelcomeFragment());
    }

    private void loadFragment(Fragment fragment)
    {
        //Loads fragment in container
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_frag_container,fragment)
                .commit();
    }
}