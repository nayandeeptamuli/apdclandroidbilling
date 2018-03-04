package com.nayandeeptamuli.apdclandroidbilling;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;


public class BillPrintActivity extends AppCompatActivity {
    static Context ctx;
    static Activity act;
    static Menu mnu;
    CommonFunction cf;

    private static final int REQUEST_ENABLE_BT = 2;
    static BluetoothService mService = null;
    BluetoothDevice con_dev = null;
    private static final int REQUEST_CONNECT_DEVICE = 1;

    public static boolean bpconn = false;

    public static String conid;

    public static String consumer_subdiv;
    public static String consumer_dtr;
    public static String consumer_bill_date;
    public static String consumer_bill_no;
    public static String consumer_apdcl_bill_no;
    public static String consumer_bill_period;
    public static String consumer_consumption_day;
    public static String consumer_due_date;

    public static String consumer_id;
    public static String consumer_deyakid;
    public static String consumer_name;
    public static String consumer_address;

    public static String consumer_category;
    public static String consumer_conntype;
    public static String consumer_mfactor;
    public static String consumer_cload;

    public static String consumer_meter_no;
    public static String consumer_prev_reading;
    public static String consumer_curr_reading;
    public static String consumer_total_unit;
    public static String consumer_pf;
    public static String consumer_consumed_unit;
    public static String consumer_billed_unit;

    public static String consumer_slabs;
    public static String consumer_eng_amount;
    public static String consumer_subsidy;
    public static String consumer_total_eng_amount;
    public static String consumer_fix_amount;
    public static String consumer_mrent_amount;
    public static String consumer_eduty;
    public static String consumer_fppa;
    public static String consumer_current_demand;
    public static String consumer_pa;
    public static String consumer_as;
    public static String consumer_cs;
    public static String consumer_ta;
    public static String consumer_adjustment;
    public static String consumer_nba;
    public static String consumer_nbadd;

    public static String mydate="";
    public static int mstatus;

    public static boolean noproblem=true;

    private static String t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_print);

        cf = new CommonFunction();
        ///////////////Bluetooth Printer//////////////

        mService = new BluetoothService(this, mHandler);
        if( mService.isAvailable() == false ){
            CommonFunction.makeToast(ctx, "Bluetooth is not available").show();
            finish();
        }

        ctx = this;
        act = this;
        ////////////////////////////////


        Intent in = getIntent();
        conid = in.getStringExtra("id");
        t = in.getStringExtra("t");

        DatabaseOperation adb = new DatabaseOperation(ctx);
        Cursor aCR = adb.selectsqlInformation(adb, "select * from appdata");
        if(aCR.getCount() >0){
            aCR.moveToFirst();
            mydate = aCR.getString(4);
        }



        DatabaseOperation DB = new DatabaseOperation(this);
        final Cursor cCR = DB.selectsqlInformation(DB, "SELECT * FROM "+ TableData.TableInfo.TABLE_MDATA_NAME +" WHERE "+ TableData.TableInfo.TABLE_MDATA_id +"='"+ conid +"'");

        if(cCR.getCount() == 1 ){
            /*
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.ic_action_notification)
                    .setTitle("Notification")
                    .setMessage(conid)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    })
                    .setCancelable(false)
                    .show();
            */
            cCR.moveToFirst();
            CommonFunction cf = new CommonFunction();

            mstatus = Integer.valueOf(Rcrypt.decode(DB.key,cCR.getString(46)));


            consumer_subdiv 			= Rcrypt.decode(DB.key,cCR.getString(7));
            consumer_dtr 				= Rcrypt.decode(DB.key,cCR.getString(8));
            consumer_bill_no 			= Rcrypt.decode(DB.key,cCR.getString(45));
            consumer_apdcl_bill_no 	    = Rcrypt.decode(DB.key,cCR.getString(72));
            consumer_bill_date 			= cf.convert_to_date(Long.valueOf(Rcrypt.decode(DB.key,cCR.getString(47)))).substring(0,10);
            consumer_bill_period 		= cf.convert_to_date(Long.valueOf(Rcrypt.decode(DB.key,cCR.getString(23)))).substring(0,10) +" to "+ cf.convert_to_date(Long.valueOf(Rcrypt.decode(DB.key,cCR.getString(47)))).substring(0,10);

            consumer_consumption_day    = Rcrypt.decode(DB.key, cCR.getString(53));
            consumer_due_date           = cf.convert_to_date(Long.valueOf(Rcrypt.decode(DB.key, cCR.getString(54)))).substring(0, 10);


            consumer_id 		        = Rcrypt.decode(DB.key,cCR.getString(10));
            consumer_deyakid 		    = Rcrypt.decode(DB.key,cCR.getString(9));
            consumer_name 		        = Rcrypt.decode(DB.key,cCR.getString(15));
            consumer_address 	        = Rcrypt.decode(DB.key,cCR.getString(16));

            consumer_category 	        = Rcrypt.decode(DB.key,cCR.getString(17));
            consumer_conntype 	        = Rcrypt.decode(DB.key,cCR.getString(18)) +" Phase";
            consumer_mfactor 	        = Rcrypt.decode(DB.key,cCR.getString(19));
            consumer_cload 		        = Rcrypt.decode(DB.key,cCR.getString(20));

            consumer_meter_no 		    = Rcrypt.decode(DB.key,cCR.getString(21));
            consumer_prev_reading 	    = Rcrypt.decode(DB.key,cCR.getString(24));
            if(mstatus ==0){
                consumer_curr_reading = Rcrypt.decode(DB.key, cCR.getString(48));
                consumer_total_unit = ((int) Math.floor(Integer.valueOf(consumer_curr_reading) - Integer.valueOf(consumer_prev_reading))) +"";
            }else if((mstatus ==3) || (mstatus ==4)){
                String[] statusdata = getResources().getStringArray(R.array.meter_status_list);
                consumer_curr_reading = Rcrypt.decode(DB.key, cCR.getString(48));
                consumer_total_unit = ((int) Math.floor(Integer.valueOf(consumer_curr_reading) - Integer.valueOf(consumer_prev_reading))) +"";
                consumer_curr_reading = consumer_curr_reading +" ("+ statusdata[mstatus] +")";
            }else{
                String[] statusdata = getResources().getStringArray(R.array.meter_status_list);
                consumer_curr_reading = statusdata[mstatus];
                consumer_total_unit = Rcrypt.decode(DB.key, cCR.getString(51));
                noproblem = false;
            }


            consumer_pf = Rcrypt.decode(DB.key, cCR.getString(68));
            consumer_consumed_unit = Rcrypt.decode(DB.key, cCR.getString(51));
            consumer_billed_unit = Rcrypt.decode(DB.key, cCR.getString(52));

            consumer_slabs = Rcrypt.decode(DB.key, cCR.getString(55));
            consumer_eng_amount = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(56))));
            consumer_subsidy = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(57))));
            consumer_total_eng_amount = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(58))));
            consumer_fix_amount = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(59))));
            consumer_mrent_amount = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(70))));
            consumer_eduty = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(60))));
            consumer_fppa = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(61))));
            consumer_current_demand = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(62))));
            consumer_pa = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(27))));
            consumer_as = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(28))));
            consumer_cs = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(69))));
            consumer_ta = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(63))));
            consumer_adjustment = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(30))));
            consumer_nba = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(64))));
            consumer_nbadd = CommonFunction.currency_string(Float.parseFloat(Rcrypt.decode(DB.key, cCR.getString(65))));

            Log.d("bprint", consumer_cs);

            try {
                create_bill();
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        else{
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        CommonFunction.makeToast(ctx, "Press cross to exit").show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_billprint, menu);
        mnu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_close) {
            Intent consumerlist = new Intent(act, ConsumerActivity.class);
            consumerlist.putExtra("type",t);
            startActivity(consumerlist);
            finish();
            return true;
        }else if(id == R.id.action_connect){
            Intent serverIntent = new Intent(this, DeviceList.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            return true;
        }else if(id == R.id.action_print){
            if(noproblem || Config.averageBilling()){
                try {
                        print_data_ok();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                CommonFunction.makeToast(ctx, "Ok bill printing").show();
            }
            else{
                try {
                    print_data_notok();
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                CommonFunction.makeToast(ctx, "Not Ok Slip printing").show();
            }
            return true;
        }

        else if(id == R.id.action_edit){
            Intent readingintent = new Intent(act,ReadingActivity.class);
            readingintent.putExtra("id",conid);
            startActivity(readingintent);
            finish();
            return  true;
        }

        return super.onOptionsItemSelected(item);
    }


    ////////////////////////////////////////////////////

    /////////////////Remote Connection Data Sending////////////////////////

    private void create_bill() throws JSONException{
        if(noproblem || Config.averageBilling()) {
            TextView BILL_SUBDIV = (TextView) this.findViewById(R.id.bill_s1_subdiv);
            TextView BILL_DATE = (TextView) this.findViewById(R.id.bill_s1_billdate);
            TextView BILL_BNO = (TextView) this.findViewById(R.id.bill_s1_bno);
            TextView BILL_PERIOD = (TextView) this.findViewById(R.id.bill_s1_period);
            TextView BILL_LASTDATE = (TextView) this.findViewById(R.id.bill_s1_duedate);

            TextView BILL_CON_ID = (TextView) this.findViewById(R.id.bill_s2_con_id);
            TextView BILL_DYK_ID = (TextView) this.findViewById(R.id.bill_s2_1_con_id);
            TextView BILL_CON_NAME = (TextView) this.findViewById(R.id.bill_s2_con_name);

            TextView BILL_CON_ADD = (TextView) this.findViewById(R.id.bill_s3_con_address);
            TextView BILL_CATEGORY = (TextView) this.findViewById(R.id.bill_s3_category);
            TextView BILL_CONNTYPE = (TextView) this.findViewById(R.id.bill_s3_conn_type);
            TextView BILL_MFACTOR = (TextView) this.findViewById(R.id.bill_mfactor);
            TextView BILL_CLOAD = (TextView) this.findViewById(R.id.bill_cload);

            TextView BILL_MET_NO = (TextView) this.findViewById(R.id.bill_b_meter_no);
            TextView BILL_PRE_READ = (TextView) this.findViewById(R.id.bill_b_pre_reading);
            TextView BILL_POST_READ = (TextView) this.findViewById(R.id.bill_b_post_reading);
            TextView BILL_TOT_READ = (TextView) this.findViewById(R.id.bill_b_total_reading);
            TextView BILL_B_READ = (TextView) this.findViewById(R.id.bill_b_billed_reading);

            TableLayout BILL_CHARGE = (TableLayout) this.findViewById(R.id.bill_charge_detail);

            TextView BILL_ENG_AM = (TextView) this.findViewById(R.id.bill_b_energy_amount);
            TextView BILL_SUBSIDY = (TextView) this.findViewById(R.id.bill_b_subsidy);
            TextView BILL_TENG_AM = (TextView) this.findViewById(R.id.bill_b_totenergy);
            TextView BILL_FIX_AM = (TextView) this.findViewById(R.id.bill_b_fixedcharge);
            TextView BILL_MRENT_AM = (TextView) this.findViewById(R.id.bill_b_meterrent);
            TextView BILL_ED_AM = (TextView) this.findViewById(R.id.bill_b_duty);
            TextView BILL_FPPA_AM = (TextView) this.findViewById(R.id.bill_b_fppa);
            TextView BILL_AR_AM = (TextView) this.findViewById(R.id.bill_b_arrear);
            TextView BILL_ADJ = (TextView) this.findViewById(R.id.bill_b_adjustment);
            TextView BILL_NET_AM = (TextView) this.findViewById(R.id.bill_b_nba);
            TextView BILL_NET_AM_D = (TextView) this.findViewById(R.id.bill_b_nbadue);


            //***** bill calculation *********//

            BILL_SUBDIV.setText(consumer_subdiv);
            BILL_DATE.setText(consumer_bill_date);
            BILL_BNO.setText(consumer_bill_no);
            BILL_PERIOD.setText(consumer_bill_period);
            BILL_LASTDATE.setText(consumer_due_date);

            BILL_CON_ID.setText(consumer_id);
            BILL_DYK_ID.setText(consumer_deyakid);
            BILL_CON_NAME.setText(consumer_name);
            BILL_CON_ADD.setText(consumer_address);

            BILL_CATEGORY.setText(consumer_category);
            BILL_CONNTYPE.setText(consumer_conntype);
            BILL_MFACTOR.setText(consumer_mfactor);
            BILL_CLOAD.setText(consumer_cload);

            BILL_MET_NO.setText(consumer_meter_no);
            BILL_PRE_READ.setText(consumer_prev_reading);
            BILL_POST_READ.setText(consumer_curr_reading);
            BILL_TOT_READ.setText(consumer_consumed_unit);
            BILL_B_READ.setText(consumer_billed_unit);

            ///////////energy charge//////////////////////////////////////////////////////

            LayoutInflater inlay = (LayoutInflater) this.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            {
                View lv = inlay.inflate(R.layout.sublayout_billslabformat, null);
                TextView slab_name = (TextView) lv.findViewById(R.id.slab_name);
                TextView slab_unit = (TextView) lv.findViewById(R.id.slab_unit);
                TextView slab_rate = (TextView) lv.findViewById(R.id.slab_rate);
                TextView slab_amount = (TextView) lv.findViewById(R.id.slab_amount);

                slab_name.setTypeface(Typeface.DEFAULT_BOLD);
                slab_unit.setTypeface(Typeface.DEFAULT_BOLD);
                slab_rate.setTypeface(Typeface.DEFAULT_BOLD);
                slab_amount.setTypeface(Typeface.DEFAULT_BOLD);

                slab_name.setText("Name");
                slab_unit.setText("Unit");
                slab_rate.setText("Rate (Rs)");
                slab_amount.setText("Amount (Rs)");

                BILL_CHARGE.addView(lv);
            }

            JSONArray slab_arr = new JSONArray(consumer_slabs);
            int i;
            for (i = 0; i < slab_arr.length(); i++) {
                View lv = inlay.inflate(R.layout.sublayout_billslabformat, null);
                TextView slab_name = (TextView) lv.findViewById(R.id.slab_name);
                TextView slab_unit = (TextView) lv.findViewById(R.id.slab_unit);
                TextView slab_rate = (TextView) lv.findViewById(R.id.slab_rate);
                TextView slab_amount = (TextView) lv.findViewById(R.id.slab_amount);

                JSONArray slab_data = slab_arr.getJSONArray(i);
                int j = i + 1;
                slab_name.setText(slab_data.get(0).toString());
                slab_unit.setText(slab_data.get(1).toString());
                slab_rate.setText("X " + slab_data.get(2).toString());
                slab_amount.setText("= " + slab_data.get(3).toString());

                BILL_CHARGE.addView(lv);
            }

            BILL_ENG_AM.setText(consumer_eng_amount);
            BILL_SUBSIDY.setText(consumer_subsidy);
            BILL_TENG_AM.setText(consumer_total_eng_amount);
            BILL_FIX_AM.setText(consumer_fix_amount);
            BILL_MRENT_AM.setText(consumer_mrent_amount);
            BILL_ED_AM.setText(consumer_eduty);
            BILL_FPPA_AM.setText(consumer_fppa);
            BILL_AR_AM.setText(consumer_ta);
            BILL_ADJ.setText(consumer_adjustment);
            BILL_NET_AM.setText(consumer_nba);
            //BILL_NET_AM_D.setText(consumer_nbadd);
            BILL_NET_AM_D.setVisibility(View.GONE);

            ((ScrollView)this.findViewById(R.id.billing_container_ok)).setVisibility(View.VISIBLE);
        }else{
           ((LinearLayout)this.findViewById(R.id.billing_container_not_ok)).setVisibility(View.VISIBLE);
        }
    }


    /*
     * *********************************************************************
     * Bluetooth Print
     * ************************************************************************
     */
    public static void bpicon_show(){
        if(mnu != null){
            MenuItem item = mnu.findItem(R.id.action_connect);
            boolean bpd = (!bpconn);
            item.setVisible(bpd);

            MenuItem pitem = mnu.findItem(R.id.action_print);
            boolean pbpd = (bpconn);
            pitem.setVisible(pbpd);
        }
    }


    public void bp_print(){

        if( mService.isBTopen() == false)
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }

        try {
            bpconn = false;
            bpicon_show();
        } catch (Exception ex) {
            Log.e("Bluetooth Problem",ex.getMessage());
        }

    }



    public void print_data_ok() throws JSONException{
        ProgressDialog ld = new ProgressDialog(ctx);
        ld.setMessage("Bill is printing now");
        ld.setCancelable(false);
        ld.show();

        String print_string = "\n";
        print_string += "-------------------------------";
        mService.sendMessage(print_string, "GBK");
        print_ltxt(":: APDCL Billing ::", false);

        printLogo();

        print_string =  "-------------------------------\n";
        print_string += "            * DEYAK *\n";
        print_string += "  * Designed and Developed by *\n";
        print_string += "   * ARK Informatics (P) Ltd *\n";
        print_string += "-------------------------------\n";
        print_string += "Sub Div : "+  consumer_subdiv +"\n";
        print_string += "-------------------------------\n";
        print_string += "DTRno   : "+ consumer_dtr +"\n";
        print_string += "Con ID  : "+ consumer_id +"\n";
        print_string += "DEYAK ID: "+ consumer_deyakid +"\n";
        print_string += ""+ consumer_name +"\n";
        print_string += ""+ consumer_address + "\n\n";
        print_string += "Bill No : "+ consumer_bill_no +"\n";
        print_string += "Bill Period : \n"+ consumer_bill_period +"\n";
        print_string += "Bill Date   : "+ consumer_bill_date +"\n";
        print_string += "Consumption : "+ consumer_consumption_day +" Days\n";
        print_string += "Due Date    : " + consumer_due_date +"\n";
        mService.sendMessage(print_string, "GBK");

        print_ltxt(consumer_due_date ,false);

        print_string = "-------------------------------\n";
        print_string += "APDCL Bill No : "+ consumer_apdcl_bill_no +"\n";
        print_string += "-------------------------------\n";
        print_string += "  ";
        print_string += "Category.  : "+ consumer_category +"\n";
        print_string += "  ";
        print_string += "Meter No.  : "+ consumer_meter_no +"\n";
        print_string += "  ";
        print_string += "Connection : "+ consumer_conntype +"\n";
        print_string += "  ";
        print_string += "M Factor   : "+ consumer_mfactor +"\n";
        print_string += "  ";
        print_string += "Load       : "+ consumer_cload +"\n";
        print_string += "  ";
        print_string += "Curr Rdng  : "+ consumer_curr_reading +"\n";
        print_string += "  ";
        print_string += "Prev Rdng  : "+ consumer_prev_reading +"\n";
        print_string += "-------------------------------\n";
        print_string += "  ";
        print_string += "Unit Consumed : "+ consumer_consumed_unit +"\n";
        print_string += "  ";
        print_string += "Power Factor  : "+ consumer_pf +"\n";
        print_string += "  ";
        print_string += "Unit Billed   : "+ consumer_billed_unit +"\n";
        print_string += "-------------------------------\n";
        print_string += "           # BILL #\n";

        int p;
        print_string += "-------------------------------\n";
        print_string += " Sl   Unit    Rate       Charge\n";
        print_string += "-------------------------------\n";

        JSONArray slab_arr = new JSONArray(consumer_slabs);
        int i;
        for(i=0; i<slab_arr.length(); i++){
            JSONArray slab_data = slab_arr.getJSONArray(i);
            String s = slab_data.get(0).toString();
            String u = Integer.valueOf(slab_data.get(1).toString())+"";
            String r = CommonFunction.currency_string(Float.parseFloat(slab_data.get(2).toString()));
            String c = CommonFunction.currency_string(Float.parseFloat(slab_data.get(3).toString()));

            String s_p = " ";
            String u_p = "       ";     p=u_p.length() - u.length(); u_p = u_p.substring(0,p);
            String r_p = "     ";       p=r_p.length() - r.length(); r_p = r_p.substring(0,p);
            String c_p = "          ";  p=c_p.length() - c.length(); c_p = c_p.substring(0,p);

            String temps = s_p +""+ s +"-"+ u_p +""+ u +" X " + r_p +""+ r +" = "+ c_p +""+ c +"\n";
            print_string += temps;
        }

        String e_p = "            "; String pp;

        print_string += "-------------------------------\n";

        p=e_p.length() - consumer_eng_amount.length(); pp = e_p.substring(0,p);
        print_string += " Energy Amount - = "+ pp +""+ consumer_eng_amount +"\n";

        p=e_p.length() - consumer_subsidy.length(); pp = e_p.substring(0,p);
        print_string += " Subsidy -       =" + pp +"-"+ consumer_subsidy +"\n";

        print_string += "-------------------------------\n";

        p=e_p.length() - consumer_total_eng_amount.length(); pp = e_p.substring(0,p);
        print_string += " Tot Eng Chrg -  = " + pp +""+ consumer_total_eng_amount +"\n";

        p=e_p.length() - consumer_fix_amount.length(); pp = e_p.substring(0,p);
        print_string += " Fixed Chrg -    = " + pp +""+ consumer_fix_amount +"\n";

        p=e_p.length() - consumer_mrent_amount.length(); pp = e_p.substring(0,p);
        print_string += " Mtr Rent -      = " + pp +""+ consumer_mrent_amount +"\n";

        p=e_p.length() - consumer_eduty.length(); pp = e_p.substring(0,p);
        print_string += " ED -            = " + pp +""+ consumer_eduty +"\n";

        p=e_p.length() - consumer_fppa.length(); pp = e_p.substring(0,p);
        print_string += " FPPPA -         = " + pp +""+ consumer_fppa +"\n";

        print_string += "-------------------------------\n";

        p=e_p.length() - consumer_current_demand.length(); pp = e_p.substring(0,p);
        print_string += " Curnt Demnd -   = " + pp +""+ consumer_current_demand +"\n";

        p=e_p.length() - consumer_pa.length(); pp = e_p.substring(0,p);
        print_string += "     PA -        = " + pp +""+ consumer_pa +"\n";

        p=e_p.length() - consumer_as.length(); pp = e_p.substring(0,p);
        print_string += "     AS -        = " + pp +""+ consumer_as +"\n";

        p=e_p.length() - consumer_cs.length(); pp = e_p.substring(0,p);
        print_string += "     CS -        = " + pp +""+ consumer_cs +"\n";

        p=e_p.length() - consumer_ta.length(); pp = e_p.substring(0,p);
        print_string += " Total Arr -     = " + pp +""+ consumer_ta +"\n";

        p=e_p.length() - consumer_adjustment.length(); pp = e_p.substring(0,p);
        print_string += " Adjustment -    =" + pp +"-"+ consumer_adjustment +"\n";

        print_string += "-------------------------------\n";

        p=e_p.length() - consumer_nba.length(); pp = e_p.substring(0,p);
        print_string += " Net Bill Amnt - = " + pp +""+ consumer_nba +"\n";

        mService.sendMessage(print_string, "GBK");

        print_ltxt(consumer_nba ,false);


        //printImage();

        print_string =  "-------------------------------\n";
        print_string += "          * THANK YOU *\n";
        print_string += "---::---::---::---::---::---::-\n";
        print_string += "          ACKNOWLEDGEMENT\n";
        print_string += "         -----------------\n";
        print_string += "  Con No : "+ consumer_id + "\n";
        print_string += "  DEYAKID: "+ consumer_deyakid + "\n";
        print_string += "  Name   : "+ consumer_name + "\n";
        print_string += "  Amt    : "+ consumer_nba + "\n";
        print_string += "  Due Dt : "+ consumer_due_date + "\n\n";
        print_string += "  Customer\n";
        print_string += "  Signature :------------------\n";
        print_string += "  ";
        print_string += "  ";

        mService.sendMessage(print_string, "GBK");
        ld.dismiss();
        ld.hide();
    }

    public void enter_cmd(byte[] cmd){
        mService.write(cmd);
    }

    public void print_data_notok() throws JSONException{
        ProgressDialog ld = new ProgressDialog(ctx);
        ld.setMessage("Bill is printing now");
        ld.setCancelable(false);
        ld.show();

        String print_string = "\n";
        print_string += "-------------------------------";
        mService.sendMessage(print_string, "GBK");
        print_ltxt(":: APDCL Billing ::", false);

        printLogo();

        print_string =  "-------------------------------\n";
        print_string += "            * DEYAK *\n";
        print_string += "  * Designed and Developed by *\n";
        print_string += "   * ARK Informatics (P) Ltd *\n";
        print_string += "-------------------------------\n";
        print_string += "Sub Div : "+  consumer_subdiv +"\n";
        print_string += "-------------------------------\n";
        print_string += "DTRno   : "+ consumer_dtr +"\n";
        print_string += "Con ID  : "+ consumer_id +"\n";
        print_string += "DEYAK ID: "+ consumer_deyakid +"\n";
        print_string += ""+ consumer_name +"\n";
        print_string += ""+ consumer_address + "\n\n";
        print_string += "Bill No : "+ consumer_bill_no +"\n";
        print_string += "Bill Period : \n"+ consumer_bill_period +"\n";
        print_string += "Bill Date : "+ consumer_bill_date +"\n";

        print_string += "-------------------------------\n";
        print_string += "  ";
        print_string += "Category.  : "+ consumer_category +"\n";
        print_string += "  ";
        print_string += "Meter No.  : "+ consumer_meter_no +"\n";
        print_string += "  ";
        print_string += "Connection : "+ consumer_conntype +"\n";
        print_string += "  ";
        print_string += "M Factor   : "+ consumer_mfactor +"\n";
        print_string += "  ";
        print_string += "Load       : "+ consumer_cload +"\n";
        print_string += "  ";
        print_string += "Curr Rdng  : "+ consumer_curr_reading +"\n";
        print_string += "  ";
        print_string += "Prev Rdng  : "+ consumer_prev_reading +"\n";
        print_string += "-------------------------------\n";
        print_string += "Dear Consumer, your meter is defected. Please contact with subdivion as soon possible.\n";
        print_string +=  "-------------------------------\n";
        print_string += "          * THANK YOU *\n";
        print_string += "---::---::---::---::---::---::-\n";

        mService.sendMessage(print_string, "GBK");
        ld.dismiss();
        ld.hide();
    }



    ////////////////////////////////////////////////////
	/*image print*/
    ///////////////////////////////////////////////////


    //打印图形
    @SuppressLint("SdCardPath")
    private void printLogo() {
        byte[] sendData = null;
        PrintPic pg = new PrintPic();
        pg.initCanvas(376);
        pg.initPaint();

        Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.ic_department_bw);
        File f = new File(getExternalCacheDir()+"/image.png");
        try {
            FileOutputStream outStream = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) { throw new RuntimeException(e); }

        pg.drawImage(149, 0, f.getAbsolutePath());
        sendData = pg.printDraw();
        mService.write(sendData);   //打印byte流数据
        byte[] esc = {0x00};
        mService.write(esc);
    }



    ////////////////////////////////////////////////////
	/*large text print*/
    ///////////////////////////////////////////////////
    public void print_ltxt(String txt,boolean w){
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;
        cmd[1] = 0x61;
        cmd[2] = 0x01;
        mService.write(cmd);

        cmd[1] = 0x21;
        cmd[2] = 0x10;
        mService.write(cmd);

        if(w){
            cmd[2] = 0x20;
            mService.write(cmd);
        }

        mService.sendMessage(txt, "GBK");

        print_reset();
    }
    public void print_reset(){
        byte[] cmd = new byte[3];
        cmd[0] = 0x1b;

        cmd[1] = 0x21;
        cmd[2] = 0x00;
        mService.write(cmd);

        cmd[1] = 0x61;
        cmd[2] = 0x00;
        mService.write(cmd);
    }

    /*
     * *********************************************************************
     * Bluetooth Code
     * ************************************************************************
     */
    private final static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoothService.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            CommonFunction.makeToast(ctx, "Connect successfully").show();
                            bpconn = true;
                            bpicon_show();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            Log.d("Bluetooth Connection","Device Connecting");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            Log.d("Bluetooth Connection","Device Listening");
                            break;
                    }
                    break;
                case BluetoothService.MESSAGE_CONNECTION_LOST:
                    CommonFunction.makeToast(ctx, "Device connection was lost").show();
                    bpconn = false;
                    bpicon_show();
                    break;
                case BluetoothService.MESSAGE_UNABLE_CONNECT:
                    CommonFunction.makeToast(ctx, "Unable to connect device").show();
                    bpconn = false;
                    bpicon_show();
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if( mService.isBTopen() == false)
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        bpconn = false;
        bpicon_show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mService != null) {
            mService.stop();
            Runtime.getRuntime().gc();
        }
        mService = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("Bluetooth code", requestCode+"");
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    CommonFunction.makeToast(ctx, "Bluetooth open successful").show();
                } else {
                    finish();
                }
                break;
            case  REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceList.EXTRA_DEVICE_ADDRESS);
                    con_dev = mService.getDevByMac(address);

                    mService.connect(con_dev);
                }
                break;
        }
    }
}
