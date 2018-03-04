package com.nayandeeptamuli.apdclandroidbilling;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ReadingActivity extends AppCompatActivity {

    Context ctx = this;
    Activity act = this;

    CommonFunction cf = new CommonFunction();

    float gps_lati=0, gps_longi=0, gps_alti=0;

    DatabaseOperation dbo;

    String conid,clist;
    String uploadfilename;
    String selectpath;
    String binary_image="na";
    int gps_verfication = 0;

    Spinner MREAD_input_1;
    EditText MREAD_input_2, MREAD_input_3, MREAD_input_4;
    TextView MREAD_error_2, MREAD_error_3;
    ImageView MREAD_voice_2, MREAD_voice_4;
    int mread_data_1;
    String mread_data_2, mread_data_3, mread_data_4;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);
        new ManageDir();
        key = DatabaseOperation.key;

        Intent intent = getIntent();
        conid = intent.getStringExtra("id");
        clist = intent.getStringExtra("clist");

        MREAD_input_1 = (Spinner) this.findViewById(R.id.meterreading_input_1);
        MREAD_input_2 = (EditText) this.findViewById(R.id.meterreading_input_2);
        MREAD_input_3 = (EditText) this.findViewById(R.id.meterreading_input_3);
        MREAD_input_4 = (EditText) this.findViewById(R.id.meterreading_input_4);

        MREAD_voice_2 = (ImageView) this.findViewById(R.id.reading_voice);
        MREAD_voice_4 = (ImageView) this.findViewById(R.id.pf_voice);

        MREAD_input_2.setEnabled(true);
        MREAD_input_3.setEnabled(true);
        MREAD_input_4.setEnabled(true);

        MREAD_error_2 = (TextView) this.findViewById(R.id.meterreading_input_2_error);
        MREAD_error_3 = (TextView) this.findViewById(R.id.meterreading_input_3_error);

        ArrayAdapter mstatus_adapter;
        mstatus_adapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_item,ctx.getResources().getStringArray(R.array.meter_status_list));
        mstatus_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        MREAD_input_1.setAdapter(mstatus_adapter);
        MREAD_input_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mread_data_1 = position;
                if((position == 0) || (position == 3) || (position == 4)){
                    MREAD_input_2.setEnabled(true);
                    MREAD_input_3.setEnabled(true);
                    MREAD_input_4.setEnabled(true);

                }else{
                    MREAD_input_2.setText("");
                    MREAD_input_3.setText("");
                    MREAD_input_4.setText("");

                    MREAD_input_2.setEnabled(false);
                    MREAD_input_3.setEnabled(false);
                    MREAD_input_4.setEnabled(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        MREAD_voice_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mread_data_1 == 0) {
                    MREAD_input_2.setText("");
                    MREAD_input_3.setText("");
                    promptSpeechInput(101);
                }
            }
        });

        MREAD_voice_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mread_data_1 == 0) {
                    MREAD_input_4.setText("");
                    promptSpeechInput(102);
                }
            }
        });

        billing_proceed();
    }

    //***Reading***********************************************************************************//

    private void get_readingdata(){
        try {
            ImageView imageframe = (ImageView) this.findViewById(R.id.reading_image);
            imageframe.setImageURI(Uri.fromFile(new File(selectpath)));

            MREAD_input_2.setText("");
            MREAD_input_3.setText("");

            final DatabaseOperation sdb = new DatabaseOperation(ctx);
            String srquery = "select "+ TableData.TableInfo.TABLE_MDATA_premeterstatus +" from "+ TableData.TableInfo.TABLE_MDATA_NAME +" where "+ TableData.TableInfo.TABLE_MDATA_id +"='"+ conid +"'";
            final Cursor sCR = sdb.selectsqlInformation(sdb,srquery);
            if(sCR.getCount()>0) {
                sCR.moveToFirst();
                int premstat = Integer.valueOf(Rcrypt.decode(key,sCR.getString(0)));
                if(premstat>0) {
                    mread_data_1 = premstat;
                    MREAD_input_1.setSelection(premstat);
                    MREAD_input_1.setEnabled(false);
                }
            }
        }catch(OutOfMemoryError error){
            Runtime.getRuntime().gc();
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_action_notification)
                    .setTitle("Notification")
                    .setMessage("Dear fellow, sorry to say that your allocated memory for this application given by android is full. Please close tha app and reopen it. Please, for effective use make your screen resolution low")
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
    }


    String curmeterreading_m;
    String curmeterreading,meterstatus; boolean alert_show = false; boolean mtr_msg= false;
    private void process_reading_data() {
        alert_show = false; mtr_msg= false;
        mread_data_2 = MREAD_input_2.getText().toString();
        mread_data_3 = MREAD_input_3.getText().toString();
        mread_data_4 = MREAD_input_4.getText().toString();

        if((mread_data_1==0 && check_eligibility()) || (mread_data_1>0)){

            final DatabaseOperation db = new DatabaseOperation(ctx);
            String rquery = "select * from "+ TableData.TableInfo.TABLE_MDATA_NAME +" where "+ TableData.TableInfo.TABLE_MDATA_id +"='"+ conid +"'";
            final Cursor CR = db.selectsqlInformation(db,rquery);
            if(CR.getCount()>0){
                CR.moveToFirst();

                meterstatus = mread_data_1 +"";
                String premeterreading = Rcrypt.decode(key,CR.getString(24));
                curmeterreading = mread_data_2 +"";
                curmeterreading_m = mread_data_2 +"";

                if((mread_data_1 == 0) || (mread_data_1 == 3) || (mread_data_1 == 4)){
                    int prem = Integer.valueOf(premeterreading);
                    int currm = Integer.valueOf(curmeterreading);
                    if(prem >= currm ){
                        mtr_msg = true;

                        currm = (int) Math.floor(prem + Config.advanceBilling());
                        curmeterreading = currm +"";
                        mread_data_2 = currm +"";
                    }
                    alert_show = true;
                }
                else{
                    curmeterreading = "-1";
                }

                if(binary_image.isEmpty()){
                    new AlertDialog.Builder(ctx)
                            .setIcon(R.drawable.ic_action_notification)
                            .setTitle("Notification")
                            .setMessage("Take the meter reading picture")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();

                                    piccapture();
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
                else{
                    if(premeterreading.isEmpty() ){

                        new AlertDialog.Builder(ctx)
                                .setIcon(R.drawable.ic_action_notification)
                                .setTitle("Notification")
                                .setMessage("Data problem, click ok")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        dialog.dismiss();

                                        String[] scol = new String[]{TableData.TableInfo.TABLE_MDATA_id};
                                        String[] sval = new String[]{conid};
                                        db.deleteInformation(db, TableData.TableInfo.TABLE_MDATA_NAME,scol,sval);

                                        goto_consumer_list(clist);
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }
                    else{
                        final long timestamp = cf.convert_to_timestamp(cf.CurrentDateTime());

                        long bill_date_from_timestamp = Long.valueOf(Rcrypt.decode(key,CR.getString(23)));

                        if(timestamp > bill_date_from_timestamp){

                            DatabaseOperation aDB = new DatabaseOperation(ctx);
                            final Cursor aCR = aDB.selectsqlInformation(aDB, "select * from "+ TableData.TableInfo.TABLE_SYSTEM_NAME);
                            aCR.moveToFirst();

                            String bcode = Rcrypt.decode(key, aCR.getString(4));

                            int subdiv = 1000 + Integer.valueOf(Rcrypt.decode(key, aCR.getString(7)));
                            int dtrcode = 1000 + Integer.valueOf(Rcrypt.decode(key, CR.getString(8)));
                            final String billno =  subdiv +""+ dtrcode +""+ bcode +""+ timestamp;

                            if(alert_show){
                                String mmsg = "Meter reading is "+ curmeterreading;
                                if(mtr_msg){
                                    mmsg = "The reading ("+ curmeterreading_m +"), you have entered is wrong. Please verify again.";
                                }

                                new AlertDialog.Builder(ReadingActivity.this)
                                        .setTitle("Check Reading one more time")
                                        .setMessage(mmsg)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub

                                                // OK meter billing
                                                reading_analysis(CR, 0, aCR, billno, timestamp +"");

                                                dialog.cancel();
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {

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
                            else{
                                // not ok meter billing
                                reading_analysis(CR, 1, aCR, billno, timestamp+"");
                            }
                        }
                        else{
                            AlertDialog.Builder Timedialog = new AlertDialog.Builder(ctx)
                                    .setIcon(R.drawable.ic_action_notification)
                                    .setTitle("Notification")
                                    .setMessage("Your Date Time Setting is abnormal. Please go to Settings -> Date & Time and correct it.")
                                    .setPositiveButton("Go", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent LocSettingIntent = new Intent(Settings.ACTION_DATE_SETTINGS);
                                            act.startActivityForResult(LocSettingIntent, 0);
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO Auto-generated method stub
                                            dialog.cancel();
                                            dialog.dismiss();
                                            goto_consumer_list(clist);
                                        }
                                    })
                                    .setCancelable(false);

                            AlertDialog TimedialogAlert = Timedialog.create();
                        }
                    }
                }

            }
        }
    }


    private void reading_analysis(Cursor CR, int t, Cursor aCR, String billno, String billdate) {
        String imei = DatabaseOperation.imeino;

        String aid          = aCR.getString(0);

        ArrayList<String> bdata = BillProcess.ReadingProcess(CR,t,aCR,billdate,curmeterreading,meterstatus,mread_data_4);
        String apdcl_billno         = bdata.get(0);
        String pf                   = bdata.get(1);
        String consumption_day      = bdata.get(2);
        String unit_consumed        = bdata.get(3);
        String unit_pf              = bdata.get(4);
        String unit_billed          = bdata.get(5);
        String due_date             = bdata.get(6);

        String eng_brkup            = bdata.get(7);
        String energy_chrg          = bdata.get(8);
        String subsidy              = bdata.get(9);
        String total_energy_chrg    = bdata.get(10);
        String fixed_chrg           = bdata.get(11);
        String meter_rent           = bdata.get(12);
        String eduty                = bdata.get(13);
        String fppa                 = bdata.get(14);
        String current_demand       = bdata.get(15);
        String pa                   = bdata.get(16);
        String as                   = bdata.get(17);
        String cs                   = bdata.get(18);
        String total_arrear         = bdata.get(19);
        String net_bill_amount      = bdata.get(20);
        String net_bill_amount_dd   = bdata.get(21);

        String[] col,val;
        col = new String[30];                                                       val = new String[30];
        col[0]  = TableData.TableInfo.TABLE_MDATA_n_billno;                         val[0]  = Rcrypt.encode(imei,billno);
        col[1]  = TableData.TableInfo.TABLE_MDATA_n_status;                         val[1]  = Rcrypt.encode(imei,meterstatus);
        col[2]  = TableData.TableInfo.TABLE_MDATA_n_reading_date;                   val[2]  = Rcrypt.encode(imei,billdate);
        col[3]  = TableData.TableInfo.TABLE_MDATA_n_postmeter_read;                 val[3]  = Rcrypt.encode(imei,curmeterreading);
        col[4]  = TableData.TableInfo.TABLE_MDATA_n_meterpic;                       val[4]  = Rcrypt.encode(imei,uploadfilename);
        col[5]  = TableData.TableInfo.TABLE_MDATA_n_meterpic_binary;                val[5]  = Rcrypt.encode(imei,binary_image);
        col[6]  = TableData.TableInfo.TABLE_MDATA_n_unit_consumed;                  val[6]  = Rcrypt.encode(imei,unit_consumed +"");
        col[7]  = TableData.TableInfo.TABLE_MDATA_n_unit_billed;                    val[7]  = Rcrypt.encode(imei,unit_billed +"");
        col[8]  = TableData.TableInfo.TABLE_MDATA_n_consumption_day;                val[8]  = Rcrypt.encode(imei,consumption_day +"");
        col[9]  = TableData.TableInfo.TABLE_MDATA_n_due_date;                       val[9]  = Rcrypt.encode(imei,due_date +"");
        col[10] = TableData.TableInfo.TABLE_MDATA_n_energy_brkup;                   val[10] = Rcrypt.encode(imei,eng_brkup +"");
        col[11] = TableData.TableInfo.TABLE_MDATA_n_energy_amount;                  val[11] = Rcrypt.encode(imei,energy_chrg +"");
        col[12] = TableData.TableInfo.TABLE_MDATA_n_subsidy;                        val[12] = Rcrypt.encode(imei,subsidy +"");
        col[13] = TableData.TableInfo.TABLE_MDATA_n_total_energy_charge;            val[13] = Rcrypt.encode(imei,total_energy_chrg +"");
        col[14] = TableData.TableInfo.TABLE_MDATA_n_fixed_charge;                   val[14] = Rcrypt.encode(imei,fixed_chrg +"");
        col[15] = TableData.TableInfo.TABLE_MDATA_n_electricity_duty;               val[15] = Rcrypt.encode(imei,eduty +"");
        col[16] = TableData.TableInfo.TABLE_MDATA_n_fppa_charge;                    val[16] = Rcrypt.encode(imei,fppa +"");
        col[17] = TableData.TableInfo.TABLE_MDATA_n_current_demand;                 val[17] = Rcrypt.encode(imei,current_demand +"");
        col[18] = TableData.TableInfo.TABLE_MDATA_n_total_arrear;                   val[18] = Rcrypt.encode(imei,total_arrear +"");
        col[19] = TableData.TableInfo.TABLE_MDATA_n_net_bill_amount;                val[19] = Rcrypt.encode(imei,net_bill_amount +"");
        col[20] = TableData.TableInfo.TABLE_MDATA_n_net_bill_amount_after_duedate;  val[20] = Rcrypt.encode(imei,net_bill_amount_dd +"");
        col[21] = TableData.TableInfo.TABLE_MDATA_n_gps_verification;               val[21] = Rcrypt.encode(imei,gps_verfication +"");
        col[22] = TableData.TableInfo.TABLE_MDATA_n_ocr_analysis;                   val[22] = Rcrypt.encode(imei,"0");
        col[23] = TableData.TableInfo.TABLE_MDATA_n_pf;                             val[23] = Rcrypt.encode(imei,pf+"");
        col[24] = TableData.TableInfo.TABLE_MDATA_aid;                              val[24] = aid;
        col[25] = TableData.TableInfo.TABLE_MDATA_n_meter_rent;                     val[25] = Rcrypt.encode(imei,meter_rent +"");
        col[26] = TableData.TableInfo.TABLE_MDATA_n_current_surcharge;              val[26] = Rcrypt.encode(imei, cs +"");
        col[27] = TableData.TableInfo.TABLE_MDATA_n_unit_pf;                        val[27] = Rcrypt.encode(imei, unit_pf +"");
        col[28] = TableData.TableInfo.TABLE_MDATA_n_apdcl_billno;                   val[28] = Rcrypt.encode(imei, apdcl_billno +"");
        col[29] = TableData.TableInfo.TABLE_MDATA_n_curr_reading;                   val[29] = Rcrypt.encode(imei, curmeterreading_m +"");


        DatabaseOperation DB = new DatabaseOperation(ctx);
        DB.updateInformation(DB, TableData.TableInfo.TABLE_MDATA_NAME, col, val, new String[]{TableData.TableInfo.TABLE_MDATA_id}, new String[]{conid});

        reading_update_done();
    }


    private void reading_update_done(){
        Intent billprint = new Intent(act, BillPrintActivity.class);
        billprint.putExtra("id",conid);
        billprint.putExtra("t","0");
        act.finish();
        startActivity(billprint);
    }






    private boolean check_eligibility(){
        boolean r = false;

        {
            MREAD_input_2.setBackgroundResource(R.drawable.form_input_back);
            MREAD_input_3.setBackgroundResource(R.drawable.form_input_back);

            MREAD_error_2.setText("");
            MREAD_error_3.setText("");
        }

        if(mread_data_2.isEmpty()){MREAD_input_2.setBackgroundResource(R.drawable.form_input_error); MREAD_error_2.setText("Enter meter reading");}
        if(mread_data_3.isEmpty()){MREAD_input_3.setBackgroundResource(R.drawable.form_input_error); MREAD_error_3.setText("Re-enter meter reading");}
        //spl
        if((! mread_data_2.isEmpty()) && (! mread_data_3.isEmpty()) && (! mread_data_2.equals(mread_data_3))){MREAD_error_3.setBackgroundResource(R.drawable.form_input_error); MREAD_error_3.setText("Re-enter meter reading correctly");}

        if((! mread_data_2.isEmpty()) && (! mread_data_3.isEmpty()) && (mread_data_2.equals(mread_data_3))) {
            r = true;
        }
        return r;
    }



    /////////////////////////////////////////////////////////////////////////////////////////////


    private void billing_proceed(){
        //String where = "";
        //String where = " and ("+ TableData.TableInfo.TABLE_MDATA_n_status +"='' or "+ TableData.TableInfo.TABLE_MDATA_n_status +" is NULL)";
        String where = "";
        final DatabaseOperation db = new DatabaseOperation(ctx);
        String query = "select "+ TableData.TableInfo.TABLE_MDATA_id +","+ TableData.TableInfo.TABLE_MDATA_slab +","+ TableData.TableInfo.TABLE_MDATA_premeter_read_date +" from "+ TableData.TableInfo.TABLE_MDATA_NAME +" where "+ TableData.TableInfo.TABLE_MDATA_id +"='"+ conid +"'"+ where;

        final Cursor CR = db.selectsqlInformation(db,query);
        if(CR.getCount()>0) {
            CR.moveToFirst();

            String s       = Rcrypt.decode(key,CR.getString(1));
            long Bpredate = Integer.valueOf(Rcrypt.decode(key,CR.getString(2)));
            long Bposdate = cf.convert_to_timestamp(cf.CurrentDateTime());
            int Bconsumption_day = (int) ((Bposdate - Bpredate) / (3600 * 24));
            if(Bconsumption_day<1000) {

                ArrayList<String> slabs = cf.json_decode(cf.base64_decode(s));
                if (slabs.get(1).toString().isEmpty()) {
                    ((LinearLayout) this.findViewById(R.id.powerfactor)).setVisibility(View.GONE);
                } else {
                    ((LinearLayout) this.findViewById(R.id.powerfactor)).setVisibility(View.VISIBLE);
                }

                piccapture();
            }else{
                new AlertDialog.Builder(ctx)
                        .setIcon(R.drawable.ic_action_notification)
                        .setTitle("Notification")
                        .setMessage("Billing cannot be done due to unnecessary consumption day. Kindly Contact subdivision with Consumer No.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();

                                goto_consumer_list(clist);
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }else{
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_action_notification)
                    .setTitle("Notification")
                    .setMessage("Billing already done")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();

                            goto_consumer_list(clist);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }


    }



    //////////////////////////////////////////////////////////////////////////////////////
    //Image
    Uri imguri = null;
    public void piccapture(){
        uploadfilename = cf.convert_to_timestamp(cf.CurrentDateTime())+"_"+ conid +".jpg";

        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Reading Image of Consumer id "+ Rcrypt.decode(DatabaseOperation.key,conid));
        cv.put(MediaStore.Images.Media.DESCRIPTION,"DateTime "+ cf.CurrentDateTime());
        cv.put(MediaStore.Images.Media.LATITUDE,gps_lati);
        cv.put(MediaStore.Images.Media.LONGITUDE,gps_longi);

        //imguri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);
        imguri = Uri.parse("file:///sdcard/deyakpic.jpg");

        Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cam.putExtra(MediaStore.EXTRA_OUTPUT,imguri);
        cam.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        startActivityForResult(cam,2);

    }



    public void imagecompress(){

        Bitmap imgbit = BitmapFactory.decodeFile(selectpath);
        Bitmap imglow = cf.scaleDownBitmap(imgbit,1024,ctx);
        try {
            FileOutputStream outstream = new FileOutputStream(ManageDir.TempImage);
            imglow.compress(Bitmap.CompressFormat.JPEG, 30, outstream);
            outstream.flush();
            outstream.close();
            /*********************************/
            get_readingdata();
            /*********************************/
            File temp_img = new File(selectpath);
            if(temp_img.exists()){
                temp_img.delete();
                new File(ManageDir.TempImage).renameTo(new File(ManageDir.ImagePath +"/"+ uploadfilename));
            }


            Bitmap binary_img = scaleDownBitmap(imglow, 100, ctx);
            FileOutputStream binarystream = new FileOutputStream(ManageDir.TempImage);
            binary_img.compress(Bitmap.CompressFormat.JPEG, 30, binarystream);
            binarystream.flush();
            binarystream.close();


        }catch(Exception e) {
            e.printStackTrace();
        }

        imgbit.recycle();
        imglow.recycle();

    }

    public static Bitmap scaleDownBitmap(Bitmap photo, int newWidth, Context context) {
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        int w =(int) (newWidth * densityMultiplier);
        int h =(int) ((w * photo.getHeight())/photo.getWidth());
        photo=Bitmap.createScaledBitmap(photo, w, h, true);
        return photo;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        CommonFunction.makeToast(ctx,"Tap cross to close").show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reading, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_close) {
            this.finish();
            goto_consumer_list(clist);
            return true;
        }else if (id == R.id.action_ok) {
            process_reading_data();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goto_consumer_list(String t){
        Intent consumerlist = new Intent(act, ConsumerActivity.class);
        consumerlist.putExtra("type",t);
        act.finish();
        startActivity(consumerlist);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().gc();
    }



    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput(int REQ_CODE) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        String msg = "";
        if(REQ_CODE == 101){
            msg = "Say meter reading";
        }else if(REQ_CODE == 102){
            msg = "Say power factor";
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, msg);
        try {
            startActivityForResult(intent, REQ_CODE);
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
    protected void  onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                if(resultCode == RESULT_OK) {
                    selectpath = ManageDir.CameraImage;
                    imagecompress();
                }else {
                    goto_consumer_list(clist);
                }
            }else{
                new AlertDialog.Builder(ctx)
                        .setIcon(R.drawable.ic_action_notification)
                        .setTitle("Notification")
                        .setMessage("SD Card mounting problem")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();

                                goto_consumer_list("0");
                            }
                        })
                        .setCancelable(false)
                        .show();
            }
        }else if(requestCode == 101){
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String r = result.get(0);
                if(cf.isNumber(r)) {
                    MREAD_input_2.setText(r);
                    MREAD_input_3.setText(r);
                }else{
                    new AlertDialog.Builder(ctx)
                            .setIcon(R.drawable.ic_action_notification)
                            .setTitle("Notification")
                            .setMessage("Please say reading number")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                    MREAD_input_2.setText("");
                                    MREAD_input_3.setText("");
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }
        }else if(requestCode == 102){
            if (resultCode == RESULT_OK && null != data) {

                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String r = result.get(0);
                if(cf.isNumber(r)) {
                    MREAD_input_4.setText(r);
                }else{
                    new AlertDialog.Builder(ctx)
                            .setIcon(R.drawable.ic_action_notification)
                            .setTitle("Notification")
                            .setMessage("Please say Power Factor number")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                    MREAD_input_4.setText("");
                                }
                            })
                            .setCancelable(false)
                            .show();
                }
            }
        }
    }


}
