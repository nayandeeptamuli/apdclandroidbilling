package com.nayandeeptamuli.apdclandroidbilling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

public class RaiseComplainActivity extends Activity implements OnItemSelectedListener {
    private static final String[] paths = new String[]{"Select Main Category", "Application Process Delay", "Billing Problem", "Cable Problem", "Fuse Problem", "Help Desk", "Meter Related Issue", "Power Supply Disruption", "Theft Reporting", "Service Request"};
    private Spinner spinner;

    class ExecuteCheckPrepostTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecuteCheckPrepostTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(RaiseComplainActivity.this, "Fetching User details...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return RaiseComplainActivity.this.checkPrepost(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            if (!result.startsWith("PP")) {
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.message)).setText("Unable to connect to server.Please try again later.");
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.consumerid_lbl)).setVisibility(8);
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.consumerId)).setVisibility(8);
                ((Spinner) RaiseComplainActivity.this.findViewById(R.id.main_category)).setVisibility(8);
                ((Spinner) RaiseComplainActivity.this.findViewById(R.id.sub_category)).setVisibility(8);
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.prbdesc_lbl)).setVisibility(8);
                ((EditText) RaiseComplainActivity.this.findViewById(R.id.prob_desc)).setVisibility(8);
                RaiseComplainActivity.this.findViewById(R.id.btn_complain).setVisibility(8);
            }
        }
    }

    class RaiseComplainClass extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        RaiseComplainClass() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(RaiseComplainActivity.this, "Creating docket...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return RaiseComplainActivity.this.postComplaint(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            String docketResult = result.substring(0, result.indexOf("<"));
            if (docketResult.matches("\\d+")) {
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.message)).setText("Your complaint has been raised successfully.Please note the Docket Number : " + result.substring(0, result.indexOf("<")) + " for future reference.");
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.consumerid_lbl)).setVisibility(8);
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.consumerId)).setVisibility(8);
                ((Spinner) RaiseComplainActivity.this.findViewById(R.id.main_category)).setVisibility(8);
                ((Spinner) RaiseComplainActivity.this.findViewById(R.id.sub_category)).setVisibility(8);
                ((TextView) RaiseComplainActivity.this.findViewById(R.id.prbdesc_lbl)).setVisibility(8);
                ((EditText) RaiseComplainActivity.this.findViewById(R.id.prob_desc)).setVisibility(8);
                RaiseComplainActivity.this.findViewById(R.id.btn_complain).setVisibility(8);
                return;
            }
            ((TextView) RaiseComplainActivity.this.findViewById(R.id.message)).setText("Your complaint can not be raised as " + docketResult + ". Please try again.");
            ((TextView) RaiseComplainActivity.this.findViewById(R.id.consumerid_lbl)).setVisibility(8);
            ((TextView) RaiseComplainActivity.this.findViewById(R.id.consumerId)).setVisibility(8);
            ((Spinner) RaiseComplainActivity.this.findViewById(R.id.main_category)).setVisibility(8);
            ((Spinner) RaiseComplainActivity.this.findViewById(R.id.sub_category)).setVisibility(8);
            ((TextView) RaiseComplainActivity.this.findViewById(R.id.prbdesc_lbl)).setVisibility(8);
            ((EditText) RaiseComplainActivity.this.findViewById(R.id.prob_desc)).setVisibility(8);
            RaiseComplainActivity.this.findViewById(R.id.btn_complain).setVisibility(8);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raise_complain);
        if (getIntent().getExtras().getString("ConsumerId").toString().trim().length() == 12) {
            ((TextView) findViewById(R.id.message)).setText("Unable to connect to server.Please try again later.");
            ((TextView) findViewById(R.id.consumerid_lbl)).setVisibility(8);
            ((TextView) findViewById(R.id.consumerId)).setVisibility(8);
            ((Spinner) findViewById(R.id.main_category)).setVisibility(8);
            ((Spinner) findViewById(R.id.sub_category)).setVisibility(8);
            ((TextView) findViewById(R.id.prbdesc_lbl)).setVisibility(8);
            ((EditText) findViewById(R.id.prob_desc)).setVisibility(8);
            findViewById(R.id.btn_complain).setVisibility(8);
            return;
        }
        new ExecuteCheckPrepostTask().execute(new String[]{getIntent().getExtras().getString("ConsumerId").toString()});
        ((EditText) findViewById(R.id.consumerId)).setText(getIntent().getExtras().getString("ConsumerId").toString());
        this.spinner = (Spinner) findViewById(R.id.main_category);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367048, paths);
        adapter.setDropDownViewResource(17367049);
        this.spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(this);
        ((Button) findViewById(R.id.btn_complain)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                RaiseComplainActivity.this.raiseCompaint();
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(5.0f);
        gd.setShape(0);
        gd.setStroke(2, Color.parseColor("#2395D1"));
        ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(11, 33, 97));
        ((TextView) parent.getChildAt(0)).setTextSize(15.0f);
        ((TextView) parent.getChildAt(0)).setBackgroundDrawable(gd);
        ((TextView) parent.getChildAt(0)).setPadding(8, 8, 8, 8);
        String dataSet = parent.getItemAtPosition(position).toString();
        Object obj = -1;
        switch (dataSet.hashCode()) {
            case -1515034270:
                if (dataSet.equals("Application Process Delay")) {
                    obj = null;
                    break;
                }
                break;
            case -1433328198:
                if (dataSet.equals("Billing Problem")) {
                    obj = 1;
                    break;
                }
                break;
            case -1430249500:
                if (dataSet.equals("Service Request")) {
                    obj = 8;
                    break;
                }
                break;
            case -1342079776:
                if (dataSet.equals("Fuse Problem")) {
                    obj = 3;
                    break;
                }
                break;
            case -1053223912:
                if (dataSet.equals("Help Desk")) {
                    obj = 4;
                    break;
                }
                break;
            case -876329971:
                if (dataSet.equals("Theft Reporting")) {
                    obj = 7;
                    break;
                }
                break;
            case -496214999:
                if (dataSet.equals("Power Supply Disruption")) {
                    obj = 6;
                    break;
                }
                break;
            case 331141101:
                if (dataSet.equals("Meter Related Issue")) {
                    obj = 5;
                    break;
                }
                break;
            case 822953340:
                if (dataSet.equals("Cable Problem")) {
                    obj = 2;
                    break;
                }
                break;
        }
        ArrayAdapter<String> adapter;
        switch (obj) {
            case null:
                String[] subCategoryarr = new String[]{"Select Sub-Category", "Change Process Delay", "New Connection Process"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCategoryarr);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 1:
                String[] subCat2 = new String[]{"Select Sub-Category", "Query For Arrear Details", "Request for Duplicate Bills", "Excess Billing Amount", "Other Billing Issues", "Delivery of Consumer Personal Ledger", "Wrong Arrear amount in Bill", "Wrong Reading n Bill"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat2);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 2:
                String[] subCat3 = new String[]{"Select Sub-Category", "Other Cable Issues"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat3);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 3:
                String[] subCat4 = new String[]{"Select Sub-Category", "Fuse Burnt Off", "Other Fuse Issues", "Replacement of Fuse Wire"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat4);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 4:
                String[] subCat5 = new String[]{"Select Sub-Category", "Other Customer Request"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat5);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 5:
                String[] subCat6 = new String[]{"Select Sub-Category", "Provide Meter Details", "Request to Know Estimated Consumption", "Provide Initial Meter Reading", "Other Meter Related Issues", "Meter Test Request"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat6);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 6:
                String[] subCat7 = new String[]{"Select Sub-Category", "Power Failure", "Voltage Fluctuation", "Low Voltage", "Other Power Supply Related Issues"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat7);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 7:
                String[] subCat8 = new String[]{"Select Sub-Category", "Energy Theft", "Other Material Theft"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat8);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            case 8:
                String[] subCat9 = new String[]{"Select Sub-Category", "Name Change", "Category Change", "Load Change", "Meter Change"};
                this.spinner = (Spinner) findViewById(R.id.sub_category);
                adapter = new ArrayAdapter(this, 17367048, subCat9);
                adapter.setDropDownViewResource(17367049);
                this.spinner.setAdapter(adapter);
                this.spinner.setOnItemSelectedListener(this);
                return;
            default:
                return;
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void raiseCompaint() {
        if (validate()) {
            String mainprobVal = ((Spinner) findViewById(R.id.main_category)).getSelectedItem().toString();
            String subprobVal = ((Spinner) findViewById(R.id.sub_category)).getSelectedItem().toString();
            String consumerId = ((EditText) findViewById(R.id.consumerId)).getText().toString();
            String probDesc = ((EditText) findViewById(R.id.prob_desc)).getText().toString();
            new RaiseComplainClass().execute(new String[]{consumerId, mainprobVal, subprobVal, probDesc});
        }
    }

    public String postComplaint(String[] values) {
        String s = "";
        String outputString = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/ComplaintManagementService/ComplaintManagement?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:raiseComplain xmlns:yq1=\"http://nayandeeptamuli.com/complaint/\"><conId>" + values[0] + "</conId>" + "<mainCategory>" + values[1] + "</mainCategory>" + "<subCategory>" + values[2] + "</subCategory>" + "<probDesc>" + values[3] + "</probDesc>" + "</yq1:raiseComplain>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            outputString = readResponse(httpResponse);
            System.out.println(outputString);
            String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
            System.out.println(consumerDetails[1]);
            return consumerDetails[1];
        } catch (Exception ex) {
            ex.printStackTrace();
            return outputString;
        }
    }

    public String readResponse(HttpResponse res) {
        String return_text = "";
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while (true) {
                line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
            return_text = sb.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return return_text;
    }

    public String checkPrepost(String[] params) {
        String s = "";
        String outputString = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/ConsumerDetailsService/ConsumerDetails?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://nayandeeptamuli.com/mobcondet/\"><SOAP-ENV:Body><ns1:returnConsumerDetails><consumerId>" + params[0] + "</consumerId>" + "</ns1:returnConsumerDetails>" + " </SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            outputString = readResponse(httpResponse);
            System.out.println(outputString);
            String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
            System.out.println(consumerDetails[1]);
            return consumerDetails[1];
        } catch (Exception ex) {
            ex.printStackTrace();
            return s;
        }
    }

    public boolean validate() {
        Spinner main_category = (Spinner) findViewById(R.id.main_category);
        Spinner sub_category = (Spinner) findViewById(R.id.sub_category);
        EditText prob_desc = (EditText) findViewById(R.id.prob_desc);
        if (main_category.getSelectedItem().toString().equalsIgnoreCase("Select Main Category")) {
            ((TextView) main_category.getSelectedView()).setError("Please Select a Main Category");
            main_category.requestFocus();
            return false;
        } else if (sub_category.getSelectedItem().toString().equalsIgnoreCase("Select Sub-Category")) {
            ((TextView) sub_category.getSelectedView()).setError("Please Select a Sub Category");
            sub_category.requestFocus();
            return false;
        } else if (prob_desc.getText().toString().isEmpty()) {
            prob_desc.setError("Please provide the Problem Description");
            return false;
        } else {
            prob_desc.setError(null);
            return true;
        }
    }
}
