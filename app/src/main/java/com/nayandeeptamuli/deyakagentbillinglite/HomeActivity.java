package com.nayandeeptamuli.apdclandroidbilling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;




public class HomeActivity extends AppCompatActivity {

    Context ctx = this;
    CommonFunction cf = new CommonFunction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new BackupOperation(ctx);

        AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, UploadReceiver.class);
        this.sendBroadcast(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),600000,pendingIntent);



        LinearLayout home_1 = (LinearLayout) this.findViewById(R.id.home_button_1);
        LinearLayout home_2 = (LinearLayout) this.findViewById(R.id.home_button_2);
        LinearLayout home_3 = (LinearLayout) this.findViewById(R.id.home_button_3);
        LinearLayout home_4 = (LinearLayout) this.findViewById(R.id.home_button_4);

        final Intent consumer = new Intent(ctx, ConsumerActivity.class);

        home_1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                DatabaseOperation dbo = new DatabaseOperation(ctx);
                Cursor aCR = dbo.selectsqlInformation(dbo,"select "+ TableData.TableInfo.TABLE_SYSTEM_upload_max +" from "+ TableData.TableInfo.TABLE_SYSTEM_NAME);
                if(aCR.getCount() >0) {
                    aCR.moveToFirst();
                    int umax = Integer.valueOf(Rcrypt.decode(dbo.key,aCR.getString(0)));

                    Cursor mCR = dbo.selectsqlInformation(dbo,"select "+ TableData.TableInfo.TABLE_MDATA_id +" from "+ TableData.TableInfo.TABLE_MDATA_NAME +" where "+ TableData.TableInfo.TABLE_MDATA_n_status +"<>''");
                    if(mCR.getCount()<umax) {
                        consumer.putExtra("type", "0");
                        startActivity(consumer);
                    }else{
                        new AlertDialog.Builder(ctx)
                                .setIcon(R.drawable.ic_action_notification)
                                .setTitle("Notification")
                                .setMessage("A lot billing is done. Please upload them.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                }
            }
        });

        home_2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                /*
                Intent payment = new Intent(act, PaymentActivity.class);
                startActivity(payment);
                */
            }
        });

        home_3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                consumer.putExtra("type","1");
                startActivity(consumer);
            }
        });

        home_4.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                consumer.putExtra("type","2");
                startActivity(consumer);
            }
        });

    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        CommonFunction.makeToast(ctx,"Tap cross to close").show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_quit) {
            Intent login = new Intent(ctx, LoginActivity.class);
            this.finish();
            startActivity(login);
            return true;
        }else if (id == R.id.action_report) {
            Intent report = new Intent(ctx, ReportActivity.class);
            this.finish();
            startActivity(report);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }
}
