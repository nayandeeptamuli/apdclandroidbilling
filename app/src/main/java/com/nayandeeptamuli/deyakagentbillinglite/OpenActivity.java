package com.nayandeeptamuli.apdclandroidbilling;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;



public class OpenActivity extends AppCompatActivity {

    Activity act = this;
    Context ctx = this;
    private static Class GOINTENT = LoginActivity.class;

    String[] allpermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        allpermission = new String[9];
        allpermission[0] = Manifest.permission.BLUETOOTH;
        allpermission[1] = Manifest.permission.BLUETOOTH_ADMIN;
        allpermission[2] = Manifest.permission.READ_PHONE_STATE;
        allpermission[3] = Manifest.permission.INTERNET;
        allpermission[4] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        allpermission[5] = Manifest.permission.CAMERA;
        allpermission[6] = Manifest.permission.ACCESS_FINE_LOCATION;
        allpermission[7] = Manifest.permission.ACCESS_COARSE_LOCATION;
        allpermission[8] = Manifest.permission.RECEIVE_BOOT_COMPLETED;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(allpermission, 1);
        }else{
            check_license_go();
        }

    }


    private void check_license_go() {
        //go_direction();

        if (LicenseCheck.check_license(ctx)) {
            go_direction();
        }else{
            new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        check_license_go();
                    }
                },
                2000);
        }
    }


    private void go_direction(){

        new android.os.Handler().postDelayed(
            new Runnable() {
                public void run() {
                    Intent go_c = new Intent(act, GOINTENT);
                    act.startActivity(go_c);
                    act.finish();
                }
            },
            2000);
    }


    /*all permission__________*/
    private void getAllPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(allpermission, 1);
        }
    }

    //--------------------------------------------------------------------------------------------

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            int i; int t=0;
            for(i=0;i<permissions.length;i++){
                t = t + grantResults[i];
            }

            if(t<0){
                new AlertDialog.Builder(ctx)
                        .setIcon(R.drawable.ic_action_notification)
                        .setTitle("Permission")
                        .setMessage("All permissions must be allowed.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();
                                getAllPermission();
                            }
                        })
                        .setCancelable(false)
                        .show();
            }else{
                check_license_go();
            }
        }
    }


    //--------------------------------------------------------------------------------------------
    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
