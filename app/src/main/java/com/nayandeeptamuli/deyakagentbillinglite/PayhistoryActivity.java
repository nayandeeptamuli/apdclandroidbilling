package com.nayandeeptamuli.apdclandroidbilling;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.itextpdf.text.pdf.BaseField;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
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

public class PayhistoryActivity extends AppCompatActivity {
    Intent intent = null;
    LineChart lineChart;

    class ExecutePaymentHistoryTask extends AsyncTask<String, Integer, String> {
        ExecutePaymentHistoryTask() {
        }

        protected String doInBackground(String... params) {
            String res = "";
            System.out.println(params[0]);
            return PayhistoryActivity.this.paymenthistoryDetails(params);
        }

        protected void onPostExecute(String result) {
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
        setContentView((int) R.layout.activity_payhistory);
        Description desc = new Description();
        desc.setText("");
        this.lineChart = (LineChart) findViewById(R.id.linechart);
        this.lineChart.animateY(5000);
        this.lineChart.setDrawGridBackground(false);
        this.lineChart.setDescription(desc);
        this.lineChart.setPinchZoom(false);
        String conId = getIntent().getExtras().getString("ConsumerId");
        new ExecutePaymentHistoryTask().execute(new String[]{conId});
    }

    public String paymenthistoryDetails(String[] valuse) {
        String outputDetails = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/OnlinePaymentServiceService/OnlinePaymentService?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity stringEntity = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:fetchPayhistory xmlns:yq1=\"http://nayandeeptamuli.com/onlinepay/\"><arg0>" + valuse[0] + "</arg0>" + "</yq1:fetchPayhistory>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
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
            System.out.println("***********************");
            this.intent = new Intent(this, PayhistoryActivity.class);
            this.intent.putExtra("ConsumerId", valuse[0]);
            System.out.println("Modify :" + outputDetails);
            int selectedColor = Color.rgb(70, TransportMediator.KEYCODE_MEDIA_RECORD, 180);
            ArrayList<Entry> yVals1 = new ArrayList();
            int count = 0;
            ArrayList<String> xVals = new ArrayList();
            String[] xValues = new String[(consumerDetails.length / 4)];
            for (i = 0; i < consumerDetails.length; i += 4) {
                yVals1.add(new Entry((float) count, Float.parseFloat(consumerDetails[i + 1])));
                xVals.add(consumerDetails[i]);
                xValues[count] = consumerDetails[i];
                count++;
            }
            LineDataSet lineDataSet = new LineDataSet(yVals1, "Payment History");
            lineDataSet.setColors(selectedColor);
            lineDataSet.setCircleColor(ViewCompat.MEASURED_STATE_MASK);
            lineDataSet.setLineWidth(BaseField.BORDER_WIDTH_THIN);
            lineDataSet.setCircleRadius(BaseField.BORDER_WIDTH_THICK);
            lineDataSet.setDrawCircleHole(false);
            lineDataSet.setValueTextSize(9.0f);
            lineDataSet.setDrawFilled(true);
            List dataSets = new ArrayList();
            dataSets.add(lineDataSet);
            LineData lineData = new LineData(dataSets);
            lineData.setValueTextSize(10.0f);
            XAxis xAxis = this.lineChart.getXAxis();
            xAxis.setGranularity(BaseField.BORDER_WIDTH_THIN);
            xAxis.setCenterAxisLabels(true);
            xAxis.setDrawGridLines(true);
            xAxis.setPosition(XAxisPosition.BOTTOM);
            IndexAxisValueFormatter formatter = new IndexAxisValueFormatter();
            formatter.setValues(xValues);
            xAxis.setValueFormatter(formatter);
            this.lineChart.setTouchEnabled(false);
            this.lineChart.setDrawGridBackground(false);
            this.lineChart.setData(lineData);
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
