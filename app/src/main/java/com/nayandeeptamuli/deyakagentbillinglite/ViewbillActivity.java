package com.nayandeeptamuli.apdclandroidbilling;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;

public class ViewbillActivity extends Activity {
    ImageButton img;
    Intent intent = null;
    TextView textView;

    class ExecuteTask extends AsyncTask<String, Integer, String> {
        ExecuteTask() {
        }

        protected String doInBackground(String... params) {
            return ViewbillActivity.this.PostData(params);
        }

        protected void onPostExecute(String result) {
            Toast.makeText(ViewbillActivity.this.getApplicationContext(), result, 0).show();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewbill);
        int size = getIntent().getExtras().getInt("Size");
        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("ConsumerId"), 0).show();
        TextView text = (TextView) findViewById(R.id.invoiceid_1);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceId_0"));
        text = (TextView) findViewById(R.id.invoicedate_1);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceDate_0"));
        text = (TextView) findViewById(R.id.payamount_1);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceAmount_0"));
        text = (TextView) findViewById(R.id.dudate_1);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("DueDate_0"));
        text = (TextView) findViewById(R.id.invoiceid_2);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceId_1"));
        text = (TextView) findViewById(R.id.invoicedate_2);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceDate_1"));
        text = (TextView) findViewById(R.id.payamount_2);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceAmount_1"));
        text = (TextView) findViewById(R.id.dudate_2);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("DueDate_1"));
        text = (TextView) findViewById(R.id.invoiceid_3);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceId_2"));
        text = (TextView) findViewById(R.id.invoicedate_3);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceDate_2"));
        text = (TextView) findViewById(R.id.payamount_3);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("InvoiceAmount_2"));
        text = (TextView) findViewById(R.id.dudate_3);
        text.setTextColor(Color.parseColor("#FFFFFF"));
        text.setText(getIntent().getExtras().getString("DueDate_2"));
        text.setTextColor(Color.parseColor("#FFFFFF"));
    }

    public String PostData(String[] valuse) {
        return "";
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
