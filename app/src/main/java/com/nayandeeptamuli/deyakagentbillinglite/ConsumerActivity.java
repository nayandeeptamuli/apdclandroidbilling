package com.nayandeeptamuli.apdclandroidbilling;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;


import java.util.ArrayList;



public class ConsumerActivity extends AppCompatActivity {

    Context ctx = this;
    ViewGroup vg;
    LayoutInflater l;
    Activity act = this;

    android.support.design.widget.AppBarLayout ACTB_H,CATE_H;
    EditText SEARCH;
    ImageView SEARCH_BACK,SEARCH_CLEAR;

    ListView CONLIST;
    Cursor CON_CURSOR;

    public String conid=""; public String dtr="";
    int type,tcate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumer);

        AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, UploadReceiver.class);
        this.sendBroadcast(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, intent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),600000,pendingIntent);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        l = (LayoutInflater) ctx.getSystemService(ctx.LAYOUT_INFLATER_SERVICE);

        Intent hintent = getIntent();
        type = Integer.valueOf(hintent.getStringExtra("type"));

        allaction();
    }


    public void allaction(){


        ACTB_H  = (android.support.design.widget.AppBarLayout) this.findViewById(R.id.main_actionbar);
        CATE_H  = (android.support.design.widget.AppBarLayout) this.findViewById(R.id.category_select_container);
        CONLIST = (ListView) this.findViewById(R.id.consumer_listdata);
		
        SEARCH          = (EditText) this.findViewById(R.id.cate_search);
        SEARCH_BACK     = (ImageView) this.findViewById(R.id.cate_back);
        SEARCH_CLEAR    = (ImageView) this.findViewById(R.id.cate_clear);

        reset_cate();

        CONLIST.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                final Cursor CR = CON_CURSOR;
                CR.moveToPosition(position);

                conid = CR.getString(0);
                int survey = Integer.valueOf(Rcrypt.decode(DatabaseOperation.key,CR.getString(6)));

                if(type == 0){
                    if(survey == 0){
                        Intent surveyintent = new Intent(act,SurveyActivity.class);
                        surveyintent.putExtra("id",conid);
                        startActivity(surveyintent);
                    }else {
                        Intent readingintent = new Intent(act,ReadingActivity.class);
                        readingintent.putExtra("id",conid);
                        readingintent.putExtra("clist",type+"");
                        startActivity(readingintent);
                    }

                }else if(type == 1){
                    Intent billprintintent = new Intent(act,BillPrintActivity.class);
                    billprintintent.putExtra("id",conid);
                    billprintintent.putExtra("t","1");
                    startActivity(billprintintent);
                }else if(type == 2){
                    Intent readingintent = new Intent(act,ReadingActivity.class);
                    readingintent.putExtra("id",conid);
                    readingintent.putExtra("clist",type+"");
                    startActivity(readingintent);
                }
                finish();
            }
        });
        ///////////////////////////////////////////////

        put_dtrselect();

        ///////////////////////////////////////////////
        SEARCH.addTextChangedListener(new TextWatcher(){

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before,int count) {
                // TODO Auto-generated method stub
                final StringBuilder sb = new StringBuilder(s.length());
                sb.append(s);
                String str =  sb.toString().toUpperCase();

                String wh ="";
                if(tcate == 0){
                    wh = "upper("+ TableData.TableInfo.TABLE_MDATA_search_name +") like '"+ str +"%'";
                }else if(tcate == 1){
                    wh = "upper("+ TableData.TableInfo.TABLE_MDATA_search_cid +") like '%"+ str +"'";
                }else if(tcate == 2){
                    wh = "upper("+ TableData.TableInfo.TABLE_MDATA_search_meterno +") like '"+ str +"%'";
                }else if(tcate == 3){
                    wh = "upper("+ TableData.TableInfo.TABLE_MDATA_search_address +") like '%"+ str +"%'";
                }


                collect_consumer_data(wh,false);
            }

            @Override
            public void afterTextChanged(Editable s) {}

        });


        FloatingActionButton qrc = (FloatingActionButton) findViewById(R.id.qrc);
        if(type == 0){
            qrc.setVisibility(View.VISIBLE);
        }else{
            qrc.setVisibility(View.GONE);
        }

        qrc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent qrcintnt = new Intent(act, QRCodeScanActivity.class);
                qrcintnt.putExtra("t","0");
                startActivity(qrcintnt);
            }
        });
    }





    public void show_search_dialog(){

        View DV = l.inflate(R.layout.dialog_conlistcategoryselect, null);
        Spinner BYLIST = (Spinner) DV.findViewById(R.id.conlist_by);
        ArrayAdapter<String> byadp = new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,ctx.getResources().getStringArray(R.array.conlist_by_list));
        BYLIST.setAdapter(byadp);

        BYLIST.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                tcate = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                tcate =0;
            }
        });

        new AlertDialog.Builder(ConsumerActivity.this)
                .setTitle("Select Search Option")
                .setIcon(R.drawable.ic_action_notification)
                .setView(DV)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        show_category(tcate);
                        dialog.cancel();
                        dialog.dismiss();
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


    private void show_category(int n){
        ACTB_H.setVisibility(View.GONE);
        if(n==0){
            SEARCH.setInputType(InputType.TYPE_CLASS_TEXT);
        }else if(n==1){
            SEARCH.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else if(n==2){
            SEARCH.setInputType(InputType.TYPE_CLASS_TEXT);
        }else if(n==3){
            SEARCH.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        String[] search_hint = this.getResources().getStringArray(R.array.conlist_by_list);
        SEARCH.setHint(search_hint[n]);
        SEARCH.setText("");
        CATE_H.setVisibility(View.VISIBLE);

        SEARCH_CLEAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SEARCH.setText("");
            }
        });

        SEARCH_BACK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reset_cate();
            }
        });
    }




    private void reset_cate(){
        CATE_H.setVisibility(View.GONE);
        ACTB_H.setVisibility(View.VISIBLE);
        collect_consumer_data(null,true);
    }


    private void collect_consumer_data(String wh,boolean msgshow){
        DatabaseOperation DB = new DatabaseOperation(ctx);

        String[] ccol = new String[9];
        ccol[0] = TableData.TableInfo.TABLE_MDATA_id;
        ccol[1] = TableData.TableInfo.TABLE_MDATA_n_status;
        ccol[2] = TableData.TableInfo.TABLE_MDATA_consumer_name;
        ccol[3] = TableData.TableInfo.TABLE_MDATA_cid;
        ccol[4] = TableData.TableInfo.TABLE_MDATA_consumer_address;
        ccol[5] = TableData.TableInfo.TABLE_MDATA_meter_no;
        ccol[6] = TableData.TableInfo.TABLE_MDATA_survey;
        ccol[7] = TableData.TableInfo.TABLE_MDATA_oldcid;
        ccol[8] = TableData.TableInfo.TABLE_MDATA_premeterstatus;

        if(wh==null){
            wh = "";
        }else{
            wh = " and "+wh;
        }

        if(dtr.isEmpty()){
            wh += "";
        }else{
            wh += " and dtrno='"+ dtr +"'";
        }

        String where = null;
        if(type==0) {
            where = "("+ TableData.TableInfo.TABLE_MDATA_n_status + "='' or " + TableData.TableInfo.TABLE_MDATA_n_status + " is NULL) and "+ TableData.TableInfo.TABLE_MDATA_search_reject +"='0'"+wh;
        }else if(type==1){
            where = "("+ TableData.TableInfo.TABLE_MDATA_n_status + "<>'')"+wh;
        }else if(type==2) {
            where = "("+ TableData.TableInfo.TABLE_MDATA_n_status + "='' or " + TableData.TableInfo.TABLE_MDATA_n_status + " is NULL) and "+ TableData.TableInfo.TABLE_MDATA_search_reject +"='1'"+wh;
        }

        String orderby = TableData.TableInfo.TABLE_MDATA_search_name;

        Cursor cCR = DB.selectSearchInformation(DB, TableData.TableInfo.TABLE_MDATA_NAME,ccol,where,orderby);
        if(cCR.getCount() >0) {
            put_consumer_list(cCR);
        }else{
            if(msgshow) {
                new AlertDialog.Builder(ctx)
                        .setIcon(R.drawable.ic_action_notification)
                        .setTitle("Notification")
                        .setMessage("No consumer is found")
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
    }
    private void put_consumer_list(Cursor CR){

        ConsumerAdapter conlist_adp = new ConsumerAdapter(ctx, CR, R.layout.sublayout_consumer_list, R.array.meter_status_list, R.id.consumerlist_name, R.id.consumerlist_cid, R.id.consumerlist_meterno, R.id.consumerlist_oldcid, R.id.consumerlist_address, R.id.consumerlist_premeterstatus);
        CONLIST.setAdapter(conlist_adp);
        CON_CURSOR = CR;
    }

    AlertDialog DTRdialog = null;
    private void put_dtrselect(){
        if(type == 0) {
            final ArrayList<String> dtrnolist_arr = new ArrayList<String>();
            final DatabaseOperation dtrdb = new DatabaseOperation(ctx);
            Cursor dtrCR = dtrdb.selectsqlInformation(dtrdb, "SELECT " + TableData.TableInfo.TABLE_MDATA_dtrno + " from " + TableData.TableInfo.TABLE_MDATA_NAME);
            if (dtrCR.getCount() > 0) {
                dtrCR.moveToFirst();
                while(dtrCR.moveToNext()){
                    String dtrno = Rcrypt.decode(dtrdb.key,dtrCR.getString(0));
                    if(! dtrnolist_arr.contains(dtrno)){
                        dtrnolist_arr.add(dtrno);
                    }
                }

                View DV = l.inflate(R.layout.dialog_dtrlist, null);
                ListView DTRLIST = (ListView) DV.findViewById(R.id.dtrlist);
                ArrayAdapter<String> dtradapter = new ArrayAdapter<String>(ctx,android.R.layout.simple_list_item_1,dtrnolist_arr);
                DTRLIST.setAdapter(dtradapter);
                DTRLIST.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dtr = Rcrypt.encode(dtrdb.imeino,dtrnolist_arr.get(position));
                        DTRdialog.cancel();
                        DTRdialog.dismiss();
                        collect_consumer_data(null,true);
                    }
                });

                DTRdialog = new AlertDialog.Builder(ConsumerActivity.this)
                        .setTitle("Select DTR")
                        .setIcon(R.drawable.ic_action_notification)
                        .setView(DV)
                        .setCancelable(false)
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                dtr = "";
                                dialog.cancel();
                                dialog.dismiss();
                                collect_consumer_data(null,true);
                            }
                        })
                        .setCancelable(false)
                        .show();

            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        CommonFunction.makeToast(ctx,"Tap cross to close").show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_consumer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_close) {
            Intent home = new Intent(ctx, HomeActivity.class);
            this.finish();
            startActivity(home);
            return true;
        }else if (id == R.id.action_search) {
            show_search_dialog();
            return true;
        }else if(id == R.id.action_dtr){
            put_dtrselect();
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
