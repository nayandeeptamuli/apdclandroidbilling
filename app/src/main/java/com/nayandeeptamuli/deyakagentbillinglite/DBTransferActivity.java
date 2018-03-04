package com.nayandeeptamuli.apdclandroidbilling;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.directory.directory.ManageDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;



public class DBTransferActivity extends AppCompatActivity {

    Context ctx = this;
    CommonFunction cf = new CommonFunction();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbtransfer);


        LinearLayout dbtransferimport = (LinearLayout) this.findViewById(R.id.dbtransfer_import);
        dbtransferimport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChooser();
            }
        });

        LinearLayout dbtransferexport = (LinearLayout) this.findViewById(R.id.dbtransfer_export);
        dbtransferexport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database_backup();
            }
        });


        new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_action_notification)
                .setTitle("Notification")
                .setMessage("Rename your Database file to deyak.db and place it in '"+ ManageDir.ImportPath +"' folder.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();

        LinearLayout dbtransferdown = (LinearLayout) this.findViewById(R.id.dbtransfer_download);
        dbtransferdown.setVisibility(View.GONE);

    }


    private void showChooser() {
        if((new File(ManageDir.ImportDB).exists())){
            database_import();
        }else{

            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_action_notification)
                    .setTitle("Notification")
                    .setMessage("Database file is not found in '"+ ManageDir.ImportPath +"'.")
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


    public void  database_import() {

        final ProgressDialog ploading = new ProgressDialog(ctx);
        ploading.setMessage("Please wait to import database");
        ploading.setCancelable(false);
        ploading.show();

        //BackupOperation bko = new BackupOperation(ctx);

        database_transfer(ManageDir.ImportDB, TableData.TableInfo.DATABASE_NAME);
        database_import_dbupdate();


        ploading.cancel();
        ploading.dismiss();


        new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_action_notification)
                .setTitle("Notification")
                .setMessage("Database import completed.")
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

    private void database_transfer(String from, String to){

        try {


            String inFileName = from;
            FileInputStream fis = null;

            String outFileName = to;
            OutputStream output = null;

            byte[] buffer = new byte[1024];
            int length;

            fis = new FileInputStream(inFileName);

            output = new FileOutputStream(outFileName);
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();

            Log.d("DB import","Import Successful");


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void database_import_dbupdate(){
        DatabaseOperation dbo = new DatabaseOperation(ctx);
        String key = dbo.key;
        String[] ccol = new String[6];
        ccol[0] = TableData.TableInfo.TABLE_MDATA_consumer_name;
        ccol[1] = TableData.TableInfo.TABLE_MDATA_cid;
        ccol[2] = TableData.TableInfo.TABLE_MDATA_meter_no;
        ccol[3] = TableData.TableInfo.TABLE_MDATA_consumer_address;
        ccol[4] = TableData.TableInfo.TABLE_MDATA_id;
        ccol[5] = TableData.TableInfo.TABLE_MDATA_qrcode;
        Cursor cr = dbo.selectSearchInformation(dbo, TableData.TableInfo.TABLE_MDATA_NAME,ccol,null,null);
        if(cr.getCount()>0){
            cr.moveToFirst();
            while(!cr.isAfterLast()){

                String[] ucol = new String[5];                                  String[] uval   = new String[5];
                ucol[0]     = TableData.TableInfo.TABLE_MDATA_search_name;      uval[0]         = Rcrypt.decode(key,cr.getString(0));
                ucol[1]     = TableData.TableInfo.TABLE_MDATA_search_cid;       uval[1]         = Rcrypt.decode(key,cr.getString(1));
                ucol[2]     = TableData.TableInfo.TABLE_MDATA_search_meterno;   uval[2]         = Rcrypt.decode(key,cr.getString(2));
                ucol[3]     = TableData.TableInfo.TABLE_MDATA_search_address;   uval[3]         = Rcrypt.decode(key,cr.getString(3));
                ucol[4]     = TableData.TableInfo.TABLE_MDATA_qrcode;           uval[4]         = Rcrypt.decode(key,cr.getString(5));

                String[] scol = new String[]{TableData.TableInfo.TABLE_MDATA_id}; String[] sval = new String[]{cr.getString(4)};

                dbo.updateInformation(dbo, TableData.TableInfo.TABLE_MDATA_NAME, ucol, uval, scol, sval);

                cr.moveToNext();
            }
        }
    }





    /*___backup__________________*/

    private void database_backup(){

        final ProgressDialog ploading = new ProgressDialog(ctx);
        ploading.setMessage("Please wait to export database");
        ploading.setCancelable(false);
        ploading.show();

        BackupOperation bko = new BackupOperation(ctx);

        ploading.cancel();
        ploading.dismiss();


        new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_action_notification)
                .setTitle("Notification")
                .setMessage("Database backup completed. Find your backup file in '"+ ManageDir.BackupPath +"' folder.")
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





    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        CommonFunction.makeToast(ctx,"Tap cross to close").show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dbtransfer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_close) {
            Intent login = new Intent(ctx, LoginActivity.class);
            this.finish();
            startActivity(login);
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
