package com.itsmeeapp.itsmeeevent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends Activity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private static String TAG = "MAIN";
    private int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    private boolean isPermissionGranted = false;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        } else {
            mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
            setContentView(mScannerView);                // Set the scanner view as the content view
            isPermissionGranted = true;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(isPermissionGranted){
            mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
            mScannerView.startCamera();          // Start camera on resume
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if(isPermissionGranted){
            mScannerView.stopCamera();           // Stop camera on pause
        }
    }

    @Override
    public void handleResult(final Result rawResult) {
        DocumentReference docRef =  FirebaseFirestore.getInstance().collection("users").document(rawResult.getText());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {
                    Map<String, Object> city = new HashMap<>();
                    city.put(rawResult.getText(), true);
                    FirebaseFirestore.getInstance().collection("event").document("event0")
                            .set(city, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {



                                    mScannerView.resumeCameraPreview(MainActivity.this);
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    mScannerView.resumeCameraPreview(MainActivity.this);


                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                    mScannerView.resumeCameraPreview(MainActivity.this);
                                }
                            });
                }
            }
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
        isPermissionGranted = true;
    }

}