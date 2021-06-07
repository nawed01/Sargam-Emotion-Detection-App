package com.nawed.sargam.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.nawed.sargam.R;
import com.skydoves.elasticviews.ElasticButton;

import java.util.List;


public class DetectEmotionFragment extends Fragment {

    TextView take_photo_view,analyzing_view;
    Bitmap imageBitmap;
    LottieAnimationView analyze_anim_view,
            happy_anim_view,sad_anim_view,
            heart_anim_view,like_anim_view;
    ElasticButton retake_btn;

    public DetectEmotionFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detect_emotion, container, false);

        take_photo_view = view.findViewById(R.id.take_photo_view);
        analyzing_view = view.findViewById(R.id.analyzing_view);
        happy_anim_view = view.findViewById(R.id.happy_anim_view);
        sad_anim_view = view.findViewById(R.id.sad_anim_view);
        analyze_anim_view = view.findViewById(R.id.analyze_anim_view);
        like_anim_view = view.findViewById(R.id.like_anim_view);
        heart_anim_view = view.findViewById(R.id.heart_anim_view);
        retake_btn = view.findViewById(R.id.retake_btn);

        checkPermissionAndOpenCamera();

        retake_btn.setOnClickListener(v -> {
            // to reset views
            happy_anim_view.setVisibility(View.INVISIBLE);
            sad_anim_view.setVisibility(View.INVISIBLE);
            like_anim_view.setVisibility(View.INVISIBLE);
            heart_anim_view.setVisibility(View.INVISIBLE);
            analyzing_view.setText("Analyzing...");
            analyzing_view.setVisibility(View.INVISIBLE);
            analyze_anim_view.setVisibility(View.VISIBLE);
            take_photo_view.setVisibility(View.VISIBLE);
            retake_btn.setVisibility(View.INVISIBLE);

            checkPermissionAndOpenCamera();
        });

        return view;
    }

    private void startCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, 1);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {

            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            Toast.makeText(getContext(), "Image Captured.", Toast.LENGTH_SHORT).show();

            take_photo_view.setVisibility(View.INVISIBLE);
            analyzing_view.setVisibility(View.VISIBLE);

            //detect face and analyze to compute smile prob.
            Handler handler = new Handler();
            handler.postDelayed(()-> requireActivity()
                    .runOnUiThread(() -> {
                        DetectFace_and_setSmileProb(imageBitmap);
                    }),1000);


        }else {
            retake_btn.setVisibility(View.VISIBLE);
        }
    }

    void checkPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[] {Manifest.permission.CAMERA}, 5);
        } else {
            startCamera();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 5) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            }else
            {   retake_btn.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Permission Denied.", Toast.LENGTH_SHORT).show(); }

        }
    }


    public void DetectFace_and_setSmileProb(Bitmap inputBitmap)
    {
        //Input Image for detector
        InputImage inputImage = InputImage.fromBitmap(inputBitmap,0);
        //detector options
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);
        Task<List<Face>> results =  detector.process(inputImage)
                .addOnSuccessListener(faces ->{
                    int faceListSize = faces.size();
                    if (faceListSize == 0)
                    {
                        Toast.makeText(requireContext(), "No Face Found", Toast.LENGTH_SHORT).show();
                        analyzing_view.setVisibility(View.INVISIBLE);
                        take_photo_view.setVisibility(View.VISIBLE);
                        retake_btn.setVisibility(View.VISIBLE);
                    }else {

                        Face face = faces.get(0);
                        float smileProb = face.getSmilingProbability();

                        if (smileProb > 0.3)
                        {
                            analyzing_view.setText("");
                            analyze_anim_view.setVisibility(View.INVISIBLE);
                            sad_anim_view.setVisibility(View.INVISIBLE);
                            heart_anim_view.setVisibility(View.INVISIBLE);

                            happy_anim_view.setVisibility(View.VISIBLE);
                            analyzing_view.setText("Looks good !, You are happy.\n\nOne thumbs up for you.");
                            analyzing_view.setVisibility(View.VISIBLE);

                            like_anim_view.setVisibility(View.VISIBLE);
                        }else
                        {
                            analyze_anim_view.setVisibility(View.INVISIBLE);
                            happy_anim_view.setVisibility(View.INVISIBLE);

                            sad_anim_view.setVisibility(View.VISIBLE);
                            analyzing_view.setText("You look sad !, Don't worry,\n\nI am virtual but here is my heart for you.");
                            analyzing_view.setVisibility(View.VISIBLE);

                            heart_anim_view.setVisibility(View.VISIBLE);
                        }

                        retake_btn.setVisibility(View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {});

    }
}