package com.nawed.sargam.Fragments;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.text.style.TtsSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.nawed.sargam.R;
import com.skydoves.elasticviews.ElasticButton;

import org.w3c.dom.Text;


public class WelcomeFragment extends Fragment {

   LottieAnimationView hi_animation_view;
   TextView bot_name_view,bot_aim_view,bot_detail_view;
   ElasticButton get_started_btn;

    public WelcomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        hi_animation_view = view.findViewById(R.id.hi_animation_view);
        bot_name_view = view.findViewById(R.id.bot_name_view);
        bot_aim_view = view.findViewById(R.id.bot_aim_view);
        bot_detail_view = view.findViewById(R.id.bot_detail_view);
        get_started_btn = view.findViewById(R.id.get_start_btn);

        SetWelcomeAnimSync();

        get_started_btn.setOnClickListener(v -> {
            loadFragment(new DetectEmotionFragment());
        });


        return view;
    }

    private void SetWelcomeAnimSync() {
        Handler handler = new Handler();
        handler.postDelayed(()-> requireActivity()
                .runOnUiThread(() -> {
                    bot_name_view.setVisibility(View.VISIBLE);
                    handler.postDelayed(()-> requireActivity()
                            .runOnUiThread(() ->{
                                bot_name_view.setVisibility(View.INVISIBLE);
                                bot_aim_view.setVisibility(View.VISIBLE);
                                bot_detail_view.postDelayed(()-> {
                                    bot_aim_view.setVisibility(View.INVISIBLE);
                                    bot_detail_view.setVisibility(View.VISIBLE);},1200);
                                get_started_btn.postDelayed(()-> get_started_btn.setVisibility(View.VISIBLE),2000);
                            }),2000);
                }),4000);
    }

    private void loadFragment(Fragment fragment)
    {
        //Loads fragment in container
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.welcome_frag_container,fragment)
                .addToBackStack(null)
                .commit();
    }
}