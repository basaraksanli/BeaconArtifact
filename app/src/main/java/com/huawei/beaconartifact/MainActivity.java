package com.huawei.beaconartifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.WindowManager;

import com.huawei.beaconartifact.model.Artifact;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public String[] permissionList() {
        return new String[]{
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        BluetoothAdapter bAdapter = BluetoothAdapter.getDefaultAdapter();
        bAdapter.enable();


        boolean permissionGranted = true;

        for (String permission : permissionList())
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionGranted = false;
                break;
            }

        if (!permissionGranted)
            requestPermissions(permissionList(), 1);
        else
            BeaconUtils.getInstance().startScanning(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean granted = true;
        for (int result : grantResults)
            if (result != PackageManager.PERMISSION_GRANTED) {
                granted = false;
                break;
            }

        if (granted)
            BeaconUtils.getInstance().startScanning(this);

    }

}