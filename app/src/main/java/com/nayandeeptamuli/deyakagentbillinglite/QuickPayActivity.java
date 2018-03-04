package com.nayandeeptamuli.apdclandroidbilling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
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

public class QuickPayActivity extends Activity {
    TextView condue_lbl;
    TextView condue_msg;
    TextView conemail_lbl;
    TextView conid_lbl;
    TextView coninv_lbl;
    TextView coninvdt_lbl;
    TextView conloc_lbl;
    TextView conlocdesc_lbl;
    TextView conname_lbl;
    TextView conpayamnt_lbl;
    EditText consumerAmount;
    EditText consumerDuedate;
    EditText consumerId;
    EditText consumerInvoice;
    EditText consumerInvoicedt;
    EditText consumerLoccode;
    EditText consumerName;
    EditText consumeremail;
    EditText consumerlocDesc;
    EditText input_conid;

    class ExecuteTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecuteTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(QuickPayActivity.this, "Fetching details from the System", "Please wait...", false, true);
            this.progressDialog.getWindow().setGravity(17);
        }

        protected String doInBackground(String... params) {
            String res = "";
            return QuickPayActivity.this.PostPaymentonlineData(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            System.out.println(result);
            if (!result.equalsIgnoreCase("NO")) {
                String[] consumerDetails = result.split("#");
                QuickPayActivity.this.consumerId.setText(consumerDetails[0]);
                QuickPayActivity.this.consumerName.setText(consumerDetails[1]);
                QuickPayActivity.this.consumerInvoice.setText(consumerDetails[2]);
                QuickPayActivity.this.consumerInvoicedt.setText(consumerDetails[3]);
                QuickPayActivity.this.consumerDuedate.setText(consumerDetails[4]);
                QuickPayActivity.this.consumerAmount.setText(consumerDetails[5]);
                QuickPayActivity.this.consumerLoccode.setText(consumerDetails[6]);
                QuickPayActivity.this.consumerlocDesc.setText(consumerDetails[7]);
                QuickPayActivity.this.consumeremail.setText(consumerDetails[9].substring(0, consumerDetails[9].lastIndexOf("</ns2:fetchBilldetailsResponse>")));
                QuickPayActivity.this.consumerId.setVisibility(0);
                QuickPayActivity.this.consumerName.setVisibility(0);
                QuickPayActivity.this.consumerInvoice.setVisibility(0);
                QuickPayActivity.this.consumerInvoicedt.setVisibility(0);
                QuickPayActivity.this.consumerDuedate.setVisibility(0);
                QuickPayActivity.this.consumerAmount.setVisibility(0);
                QuickPayActivity.this.consumerLoccode.setVisibility(0);
                QuickPayActivity.this.consumerlocDesc.setVisibility(0);
                QuickPayActivity.this.consumeremail.setVisibility(0);
                QuickPayActivity.this.conid_lbl.setVisibility(0);
                QuickPayActivity.this.conname_lbl.setVisibility(0);
                QuickPayActivity.this.coninv_lbl.setVisibility(0);
                QuickPayActivity.this.coninvdt_lbl.setVisibility(0);
                QuickPayActivity.this.condue_lbl.setVisibility(0);
                QuickPayActivity.this.conpayamnt_lbl.setVisibility(0);
                QuickPayActivity.this.conloc_lbl.setVisibility(0);
                QuickPayActivity.this.conlocdesc_lbl.setVisibility(0);
                QuickPayActivity.this.conemail_lbl.setVisibility(0);
                QuickPayActivity.this.condue_msg.setVisibility(0);
                QuickPayActivity.this.input_conid.setVisibility(8);
                ((Button) QuickPayActivity.this.findViewById(R.id.btn_pay)).setVisibility(0);
                ((Button) QuickPayActivity.this.findViewById(R.id.btn_fetch)).setVisibility(8);
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_pay);
        this.conid_lbl = (TextView) findViewById(R.id.conid_lbl);
        this.conname_lbl = (TextView) findViewById(R.id.conname_lbl);
        this.coninv_lbl = (TextView) findViewById(R.id.coninv_lbl);
        this.coninvdt_lbl = (TextView) findViewById(R.id.coninvdt_lbl);
        this.condue_lbl = (TextView) findViewById(R.id.condue_lbl);
        this.conpayamnt_lbl = (TextView) findViewById(R.id.conpayamnt_lbl);
        this.conloc_lbl = (TextView) findViewById(R.id.conloc_lbl);
        this.conlocdesc_lbl = (TextView) findViewById(R.id.conlocdesc_lbl);
        this.conemail_lbl = (TextView) findViewById(R.id.conemail_lbl);
        this.condue_msg = (TextView) findViewById(R.id.condue_msg);
        this.input_conid = (EditText) findViewById(R.id.input_conid);
        this.consumerId = (EditText) findViewById(R.id.consumerId);
        this.consumerName = (EditText) findViewById(R.id.consumerName);
        this.consumerInvoice = (EditText) findViewById(R.id.consumerInvoice);
        this.consumerInvoicedt = (EditText) findViewById(R.id.consumerInvoicedt);
        this.consumerDuedate = (EditText) findViewById(R.id.consumerDuedate);
        this.consumerAmount = (EditText) findViewById(R.id.consumerAmount);
        this.consumerLoccode = (EditText) findViewById(R.id.consumerLoccode);
        this.consumerlocDesc = (EditText) findViewById(R.id.consumerlocDesc);
        this.consumeremail = (EditText) findViewById(R.id.consumeremail);
        this.consumerId.setVisibility(8);
        this.consumerName.setVisibility(8);
        this.consumerInvoice.setVisibility(8);
        this.consumerInvoicedt.setVisibility(8);
        this.consumerDuedate.setVisibility(8);
        this.consumerAmount.setVisibility(8);
        this.consumerLoccode.setVisibility(8);
        this.consumerlocDesc.setVisibility(8);
        this.consumeremail.setVisibility(8);
        this.conid_lbl.setVisibility(8);
        this.conname_lbl.setVisibility(8);
        this.coninv_lbl.setVisibility(8);
        this.coninvdt_lbl.setVisibility(8);
        this.condue_lbl.setVisibility(8);
        this.conpayamnt_lbl.setVisibility(8);
        this.conloc_lbl.setVisibility(8);
        this.conlocdesc_lbl.setVisibility(8);
        this.conemail_lbl.setVisibility(8);
        this.condue_msg.setVisibility(8);
        ((Button) findViewById(R.id.btn_pay)).setVisibility(8);
        ((Button) findViewById(R.id.btn_fetch)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                new ExecuteTask().execute(new String[]{((EditText) QuickPayActivity.this.findViewById(R.id.input_conid)).getText().toString()});
            }
        });
    }

    public String PostPaymentonlineData(String[] valuse) {
        HttpClient httpClient;
        Exception ex;
        String ouputDetails = "";
        String outputString = "";
        try {
            HttpClient httpClient2 = new DefaultHttpClient();
            try {
                HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/OnlinePaymentServiceService/OnlinePaymentService?wsdl");
                try {
                    httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
                    httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
                    String reqXML = "";
                    StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:fetchBilldetails xmlns:yq1=\"http://nayandeeptamuli.com/onlinepay/\"><arg0>" + valuse[0] + "</arg0>" + "</yq1:fetchBilldetails>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
                    se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                    httpPost.setEntity(se);
                    HttpResponse httpResponse = httpClient2.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    outputString = readResponse(httpResponse);
                    System.out.println(outputString);
                    String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
                    System.out.println("Pos : 0 :" + consumerDetails[0]);
                    System.out.println("Pos : 1 :" + consumerDetails[1]);
                    if (!consumerDetails[1].startsWith("com.sap.mw.jco.JCO$Exception") && outputString.indexOf("PAID") == -1 && consumerDetails[1].indexOf("NO OUTSTANDING") == -1 && consumerDetails[1].indexOf("BLOCK") == -1 && consumerDetails[1].indexOf("NOT FOUND") == -1) {
                        System.out.println(consumerDetails);
                        ouputDetails = valuse[0] + "#" + consumerDetails[2] + "#" + consumerDetails[3] + "#" + consumerDetails[4] + "#" + consumerDetails[5] + "#" + consumerDetails[6] + "#" + consumerDetails[7] + "#" + consumerDetails[8] + "#" + consumerDetails[9] + "#" + consumerDetails[10] + "#";
                    } else {
                        ouputDetails = "NO";
                    }
                    HttpPost httpPost2 = httpPost;
                    httpClient = httpClient2;
                } catch (Exception e) {
                    ex = e;
                    httpPost2 = httpPost;
                    httpClient = httpClient2;
                    ex.printStackTrace();
                    return ouputDetails;
                }
            } catch (Exception e2) {
                ex = e2;
                httpClient = httpClient2;
                ex.printStackTrace();
                return ouputDetails;
            }
        } catch (Exception e3) {
            ex = e3;
            ex.printStackTrace();
            return ouputDetails;
        }
        return ouputDetails;
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
}
