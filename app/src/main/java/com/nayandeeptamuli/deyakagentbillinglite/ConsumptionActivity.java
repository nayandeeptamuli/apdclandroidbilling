package com.nayandeeptamuli.apdclandroidbilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.itextpdf.text.pdf.BaseField;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

public class ConsumptionActivity extends AppCompatActivity {
    BarChart bchart;
    Intent intent = null;
    final HashMap<Integer, String> numMap = new HashMap();

    class ExecuteTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecuteTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(ConsumptionActivity.this, "Generating graph...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            String res = "";
            System.out.println(params[0]);
            return ConsumptionActivity.this.consumptionDetails(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            ConsumptionActivity.this.bchart.invalidate();
        }
    }

    class ExecutecheckConsumertypeTask extends AsyncTask<String, Integer, String> {
        ExecutecheckConsumertypeTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {
            return fetchUsernamecontype(params);
        }

        protected void onPostExecute(String result) {
            TextView textView = (TextView) ConsumptionActivity.this.findViewById(R.id.link_error);
            if (result.split("#")[1].equalsIgnoreCase("PRE")) {
                textView.setVisibility(0);
                ConsumptionActivity.this.bchart.setVisibility(8);
                return;
            }
            textView.setVisibility(8);
            ConsumptionActivity.this.bchart.setVisibility(0);
        }

        public String fetchUsernamecontype(String[] params) {
            String outputString = "";
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/IdentifyConsumerService/IdentifyConsumer?wsdl");
                httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
                httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
                String reqXML = "";
                StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:getConsumerDetails xmlns:yq1=\"http://nayandeeptamuli.com/mobapp/\"><consumerId>" + params[0] + "</consumerId>" + "</yq1:getConsumerDetails>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
                se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                outputString = ConsumptionActivity.this.readResponse(httpResponse);
                System.out.println(outputString);
                String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
                System.out.println(consumerDetails[1]);
                outputString = consumerDetails[1].substring(0, consumerDetails[1].indexOf("<"));
                System.out.println("outputString : " + outputString);
                return outputString;
            } catch (Exception ex) {
                ex.printStackTrace();
                return outputString;
            }
        }
    }

    public class IndexAxisValueFormatter implements IAxisValueFormatter {
        private int mValueCount;
        private String[] mValues;

        public IndexAxisValueFormatter() {
            this.mValues = new String[0];
            this.mValueCount = 0;
        }

        public IndexAxisValueFormatter(String[] values) {
            this.mValues = new String[0];
            this.mValueCount = 0;
            if (values != null) {
                setValues(values);
            }
        }

        public IndexAxisValueFormatter(Collection<String> values) {
            this.mValues = new String[0];
            this.mValueCount = 0;
            if (values != null) {
                setValues((String[]) values.toArray(new String[values.size()]));
            }
        }

        public String getFormattedValue(float value, AxisBase axis) {
            int index = Math.round(value);
            if (index < 0 || index >= this.mValueCount || index != ((int) value)) {
                return "";
            }
            return this.mValues[index];
        }

        public String[] getValues() {
            return this.mValues;
        }

        public void setValues(String[] values) {
            if (values == null) {
                values = new String[0];
            }
            this.mValues = values;
            this.mValueCount = values.length;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_consumption);
        ((TextView) findViewById(R.id.link_error)).setVisibility(8);
        new ExecutecheckConsumertypeTask().execute(new String[]{getIntent().getExtras().getString("ConsumerId")});
        this.bchart = (BarChart) findViewById(R.id.barchart1);
        this.bchart.animateY(2000);
        String conId = getIntent().getExtras().getString("ConsumerId");
        new ExecuteTask().execute(new String[]{conId});
    }

    public String consumptionDetails(String[] valuse) {
        String outputDetails = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/BillviewServiceService/BillviewService?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity stringEntity = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:getConsumption xmlns:yq1=\"http://nayandeeptamuli.com/BillView/\"><arg0>" + valuse[0] + "</arg0>" + "</yq1:getConsumption>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
            stringEntity.setContentEncoding((Header) new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(stringEntity);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            outputDetails = readResponse(httpResponse);
            System.out.println(outputDetails);
            String[] stArr = outputDetails.substring(outputDetails.indexOf("<return"), outputDetails.lastIndexOf("</return>")).split("</return>");
            String[] consumerDetails = new String[stArr.length];
            int i = -1;
            for (String st : stArr) {
                i++;
                consumerDetails[i] = st.substring(st.indexOf(62) + 1).trim();
            }
            System.out.println(consumerDetails);
            System.out.println("***********************");
            this.intent = new Intent(this, ConsumptionActivity.class);
            this.intent.putExtra("ConsumerId", valuse[0]);
            System.out.println("Modify :" + outputDetails);
            int selectedColor = Color.rgb(70, TransportMediator.KEYCODE_MEDIA_RECORD, 180);
            ArrayList<BarEntry> yVals1 = new ArrayList();
            int count = 0;
            ArrayList<String> xVals = new ArrayList();
            String[] xValues = new String[(consumerDetails.length / 7)];
            for (i = 0; i < consumerDetails.length; i += 7) {
                yVals1.add(new BarEntry((float) count, (float) Integer.parseInt(consumerDetails[i].trim())));
                this.numMap.put(Integer.valueOf(count), consumerDetails[i + 1]);
                xVals.add(consumerDetails[i + 1]);
                xValues[count] = consumerDetails[i + 1].trim();
                count++;
                System.out.println(consumerDetails[i] + " " + consumerDetails[i + 1]);
            }
            Description desc = new Description();
            desc.setText("");
            this.bchart.setDrawBarShadow(false);
            this.bchart.setDrawValueAboveBar(true);
            this.bchart.setDescription(desc);
            this.bchart.setPinchZoom(false);
            this.bchart.setDrawGridBackground(false);
            BarDataSet barDataSet = new BarDataSet(yVals1, "Units Consumed (kWh)");
            barDataSet.setColors(selectedColor);
            List dataSets = new ArrayList();
            dataSets.add(barDataSet);
            BarData data = new BarData(dataSets);
            data.setDrawValues(true);
            data.setValueTextSize(10.0f);
            data.setBarWidth(0.9f);
            XAxis xAxis = this.bchart.getXAxis();
            xAxis.setGranularity(BaseField.BORDER_WIDTH_THIN);
            xAxis.setCenterAxisLabels(true);
            xAxis.setDrawGridLines(true);
            xAxis.setPosition(XAxisPosition.BOTTOM);
            xAxis.setCenterAxisLabels(false);
            xAxis.setSpaceMin(data.getBarWidth() / BaseField.BORDER_WIDTH_MEDIUM);
            xAxis.setSpaceMax(data.getBarWidth() / BaseField.BORDER_WIDTH_MEDIUM);
            IndexAxisValueFormatter formatter = new IndexAxisValueFormatter();
            formatter.setValues(xValues);
            xAxis.setValueFormatter(formatter);
            this.bchart.setTouchEnabled(false);
            this.bchart.setFitBars(true);
            this.bchart.setData(data);
            System.out.println("hoe jaa" + data.getDataSets().get(0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return outputDetails;
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
