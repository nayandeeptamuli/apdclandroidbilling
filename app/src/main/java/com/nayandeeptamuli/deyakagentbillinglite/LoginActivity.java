package com.nayandeeptamuli.apdclandroidbilling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class LoginActivity extends AppCompatActivity {

    static String pin = "";
    Activity act = this;
    Context ctx = this;
    LayoutInflater l;

    DatabaseOperation dbo;
    Cursor CR;
    String imei;

    CommonFunction cf = new CommonFunction();

    public static EditText PIN_EditText;

    private ImageView BUT_SPECK;
    private final int REQ_CODE_SPEECH_INPUT = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        l = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);

        TextView demotv = (TextView) this.findViewById(R.id.demo);
        demotv.setText(Config.getSystem(ctx));

        /*
        if(OpenCVLoader.initDebug()){
            CommonFunction.makeToast(ctx,"Opencv Loaded").show();
        }else{
            CommonFunction.makeToast(ctx,"Opencv not Loaded").show();
        }
        */
        imei = dbo.imeino;

        ManageDir md = new ManageDir();


        File dbf = new File(TableData.TableInfo.DATABASE_NAME);
        if(! dbf.exists()){
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_action_notification)
                    .setTitle("Notification")
                    .setMessage("Database is not available. Import Database First.")
                    .setPositiveButton("Import Database", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();

                            Intent dbtransfer = new Intent(ctx, DBTransferActivity.class);
                            startActivity(dbtransfer);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }else{
            dbo = new DatabaseOperation(ctx);
            dbo.spl_Query(dbo);
        }


        PIN_EditText = (EditText) this.findViewById(R.id.login_pin);
        PIN_EditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                pin_entry(view);
                return false;
            }
        });

        BUT_SPECK = (ImageView) this.findViewById(R.id.login_voice);
        BUT_SPECK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput(v);
            }
        });


        ////////////////////////////////////////////////////////////////
        goto_dbtransfer();
    }

    private void pin_entry(View v){
        pin = PIN_EditText.getText().toString();
        if (pin.length() == 4) {
            pin_given(v);
        }
    }

    private void pin_given(View v){
        PIN_EditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        final ImageView LOCK_ImageView = (ImageView) this.findViewById(R.id.login_lock);
        final ImageView UNLOCK_ImageView = (ImageView) this.findViewById(R.id.login_unlock);

        try {
            CR = dbo.selectsqlInformation(dbo,"select "+ TableData.TableInfo.TABLE_SYSTEM_aid +","+ TableData.TableInfo.TABLE_SYSTEM_imei +","+ TableData.TableInfo.TABLE_SYSTEM_pin +","+ TableData.TableInfo.TABLE_SYSTEM_mydate +" from "+ TableData.TableInfo.TABLE_SYSTEM_NAME);

            if (CR.getCount() > 0) {
                CR.moveToFirst();

                if (CR.getString(0).equals("0")) {
                    String imeikey = Rcrypt.getRkey(pin);

                    if (imei.equals(Rcrypt.decode(imeikey, CR.getString(1)))) {
                        first_setup(v);
                    } else {
                        new AlertDialog.Builder(ctx)
                                .setIcon(R.drawable.ic_action_notification)
                                .setTitle("Notification")
                                .setMessage("UNAUTHORIZED DATA IMPORT")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        dialog.dismiss();

                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                } else {

                    if (Rcrypt.decode(dbo.key, CR.getString(2)).equals(pin.hashCode() + "")) {
                        if (check_mydate(CR)) {
                            if (check_memory()) {
                                LOCK_ImageView.setVisibility(View.GONE);
                                UNLOCK_ImageView.setVisibility(View.VISIBLE);

                                Intent go_home = new Intent(act, HomeActivity.class);
                                act.startActivity(go_home);
                                act.finish();
                            }
                        }
                    } else {

                        new AlertDialog.Builder(ctx)
                                .setIcon(R.drawable.ic_action_notification)
                                .setTitle("Notification")
                                .setMessage("INVALID PIN")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        dialog.dismiss();
                                        PIN_EditText.setText("");
                                    }
                                })
                                .setCancelable(false)
                                .show();

                    }
                }
            } else {
                finish();
            }
        }
        catch(android.database.sqlite.SQLiteException e){
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_action_notification)
                    .setTitle("Notification")
                    .setMessage("Database is not available. Import Database First.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                            PIN_EditText.setText("");
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }




    public void first_setup(final View view){
        final ProgressDialog ploading = new ProgressDialog(ctx);
        ploading.setMessage("Please wait... First time synchronization");
        ploading.setCancelable(false);
        ploading.show();


        final String c = cf.getCode(12);
        ArrayList<String> d = new ArrayList<String>();
        d.add(0, pin);
        d.add(1, imei);

        String ra = RemoteAddress.AddressInfo.HOST + Config.getAccessURL(ctx) + RemoteAddress.AddressInfo.REGISTRATION_LINK;
        RemoteConnection rconn = new RemoteConnection(ctx);
        rconn.senddata(ra, c, d, new ResponseAction() {
            @Override
            public void onSuccessAction(String r) {
                ploading.hide(); ploading.cancel();
                if(r.isEmpty()){
                    Snackbar.make(view, "Server problem, try later", Snackbar.LENGTH_LONG).setAction("Network", null).show();
                }else{
                    if(r.equals("0")){
                        Snackbar.make(view, "INVALID DEVICE, enter pin from correct device", Snackbar.LENGTH_LONG).setAction("Error", null).show();
                    }else{

                        String[] appcol = new String[2];                    String[] appval = new String[2];
                        appcol[0]= TableData.TableInfo.TABLE_SYSTEM_aid;    appval[0]=Rcrypt.encode(imei,r);
                        appcol[1]= TableData.TableInfo.TABLE_SYSTEM_pin;    appval[1]=Rcrypt.encode(imei,pin.hashCode() +"");

                        dbo.updateInformation(dbo, TableData.TableInfo.TABLE_SYSTEM_NAME, appcol, appval, new String[]{TableData.TableInfo.TABLE_SYSTEM_aid},new String[]{"0"});

                        pin_given(view);
                    }
                }
                PIN_EditText.setText("");
            }

            @Override
            public void onFailureAction(int code) {
                Snackbar.make(view, "Internet connection problem, try later", Snackbar.LENGTH_LONG).setAction("Network", null).show();
                PIN_EditText.setText("");
                ploading.hide(); ploading.cancel();
            }
        });

    }


    private boolean check_mydate(Cursor CR){
        boolean r = false;
        String mydate = Rcrypt.decode(DatabaseOperation.key,CR.getString(3));
        /////
        Calendar calendar = Calendar.getInstance();

        DateFormat dformat = new SimpleDateFormat("dd-MM-yyyy");
        String today = dformat.format(calendar.getTime());

        Date mydate_d = cf.strtodate(mydate, "dd-MM-yyyy");
        calendar.setTime(mydate_d);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date mydatel_d = calendar.getTime();
        String ltday = cf.datetostr(mydatel_d);

        long today_timestamp 	= cf.convert_to_timestamp(today +" 00:00:00 am");
        long from_timestamp 	= cf.convert_to_timestamp(mydate +" 00:00:00 am");
        long to_timestamp		= cf.convert_to_timestamp(ltday +" 00:00:00 am");

        if(today_timestamp < from_timestamp){
            new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_action_notification)
                .setTitle("Notification")
                .setMessage("Setup your date setting")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        PIN_EditText.setText("");
                    }
                })
                .setCancelable(false)
                .show();
        }else if(today_timestamp > to_timestamp){
            new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_action_notification)
                .setTitle("Notification")
                .setMessage("Billing time is over")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        PIN_EditText.setText("");
                    }
                })
                .setCancelable(false)
                .show();
        }else{
            r = true;
        }

        return r;
    }

    float ssh=0;
    private boolean check_memory(){
        boolean r = false;
        long sdcard_st = Environment.getExternalStorageDirectory().getUsableSpace();
        ssh = (float) Math.floor(sdcard_st /(1024*1024));

        if(ssh <40) {
            new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_action_notification)
                .setTitle("Notification")
                .setMessage("Memory is too low, need minimum 40MB space")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        PIN_EditText.setText("");
                    }
                })
                .setCancelable(false)
                .show();
        }else{
            r = true;
        }
        return  r;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {

    }

    private void goto_dbtransfer(){

        ImageView dbtbut = (ImageView) this.findViewById(R.id.login_dbtransfer_but);

        dbtbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final View DV = l.inflate(R.layout.dialog_pass, null);
                final EditText impass = (EditText) DV.findViewById(R.id.dev_pass);
                impass.setHint("TYPE DB TRANSFER PASS");

                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("Entering into developers option")
                        .setView(DV)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                                dialog.dismiss();

                                if (impass.getText().toString().equals("123456")) {
                                    Intent dbtransfer = new Intent(ctx, DBTransferActivity.class);
                                    finish();
                                    startActivity(dbtransfer);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }

    int ktime = 7; int kfact = 0;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Cursor bdCR = dbo.selectsqlInformation(dbo,"select "+ TableData.TableInfo.TABLE_SYSTEM_aid +","+ TableData.TableInfo.TABLE_SYSTEM_imei +","+ TableData.TableInfo.TABLE_SYSTEM_pin +","+ TableData.TableInfo.TABLE_SYSTEM_mydate +" from "+ TableData.TableInfo.TABLE_SYSTEM_NAME);
        bdCR.moveToFirst();
        if (bdCR.getCount() > 0) {
            bdCR.moveToFirst();
            if (! bdCR.getString(0).equals("0")) {

                if (ktime > 0) {
                    if (keyCode == 24) {
                        kfact++;
                    } else if (keyCode == 25) {
                        kfact--;
                    } else if (keyCode == 4) {

                        if (ktime == 1 && kfact == 0) {

                            View DV = l.inflate(R.layout.dialog_pass, null);
                            final EditText impass = (EditText) DV.findViewById(R.id.dev_pass);
                            impass.setHint("TYPE DEV PASS");

                            new AlertDialog.Builder(LoginActivity.this)
                                    .setTitle("Entering into developers option")
                                    .setView(DV)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            dialog.cancel();
                                            dialog.dismiss();

                                            if (impass.getText().toString().equals("13361272")) {
                                                startActivity(new Intent(act, CalculationCheckActivity.class));
                                            } else {
                                                dialog.cancel();
                                                dialog.dismiss();
                                                ktime = 7; kfact = 0;
                                            }
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            dialog.cancel();
                                            dialog.dismiss();
                                            ktime = 7; kfact = 0;
                                        }
                                    })
                                    .setCancelable(false)
                                    .show();
                        }
                        if (ktime == 1 && kfact == 2) {
                            final View DV = l.inflate(R.layout.dialog_pass, null);
                            final EditText impass = (EditText) DV.findViewById(R.id.dev_pass);
                            impass.setHint("TYPE BACKDOOR PASS");

                            final ProgressDialog ploading = new ProgressDialog(ctx);
                            ploading.setMessage("Please wait... Backdoor opening");
                            ploading.setCancelable(false);
                            ploading.show();

                            final String c = cf.getCode(12);
                            ArrayList<String> d = new ArrayList<String>();
                            d.add(0, imei);

                            String ra = RemoteAddress.AddressInfo.HOST + Config.getAccessURL(ctx) + RemoteAddress.AddressInfo.BACKDOORPASS_LINK;
                            RemoteConnection rconn = new RemoteConnection(ctx);
                            rconn.senddata(ra, c, d, new ResponseAction() {
                                @Override
                                public void onSuccessAction(final String r) {
                                    ploading.hide();
                                    ploading.cancel();
                                    if (r.isEmpty()) {
                                        show_message("Server problem, try later");
                                    } else {
                                        if (r.equals("0")) {
                                            show_message("INVALID DEVICE");
                                        } else if (r.equals("1")) {
                                            show_message("Backdoor is not valid for this device. Contact administrator");
                                        } else {

                                            new AlertDialog.Builder(LoginActivity.this)
                                                    .setTitle("Entering into backdoor option")
                                                    .setView(DV)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // TODO Auto-generated method stub
                                                            dialog.cancel();
                                                            dialog.dismiss();

                                                            if (r.equals(impass.getText().toString())) {
                                                                startActivity(new Intent(act, BackDoorActivity.class));
                                                            } else {
                                                                dialog.cancel();
                                                                dialog.dismiss();
                                                                ktime = 7; kfact = 0;
                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // TODO Auto-generated method stub
                                                            dialog.cancel();
                                                            dialog.dismiss();
                                                            ktime = 7; kfact = 0;
                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailureAction(int code) {
                                    show_message("Internet connection problem, try later");
                                    ploading.hide();
                                    ploading.cancel();
                                    ktime = 7; kfact = 0;
                                }
                            });


                        }
                        ktime = 7; kfact = 0;
                    }
                }
                ktime--;
            }
        }

        return true;
    }

    private void show_message(String msg){
        new AlertDialog.Builder(ctx)
                .setIcon(R.drawable.ic_action_notification)
                .setTitle("Notification")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                        ktime = 7; kfact = 0;
                    }
                })
                .setCancelable(false)
                .show();
    }


    /**
     * Showing google speech input dialog
     * */
    View speechview = null;
    private void promptSpeechInput(View v) {
        speechview = v;
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say your 4 digit PIN");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_action_notification)
                    .setTitle("Notification")
                    .setMessage("Sorry! Your device doesn\\'t support speech input")
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

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String r = result.get(0);
                    if(cf.isNumber(r)) {
                        PIN_EditText.setText(r);
                        PIN_EditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_NORMAL);
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        pin_entry(speechview);
                                    }
                                },
                                1000);
                    }else{
                        new AlertDialog.Builder(ctx)
                            .setIcon(R.drawable.ic_action_notification)
                            .setTitle("Notification")
                            .setMessage("Please say your 4 digit PIN")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                    PIN_EditText.setText("");
                                }
                            })
                            .setCancelable(false)
                            .show();
                    }
                }
                break;
            }

        }
    }

}
