package com.nayandeeptamuli.apdclandroidbilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

public class ComplaintDetailsActivity extends AppCompatActivity {

    class ExecuteCheckPrepostTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecuteCheckPrepostTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(ComplaintDetailsActivity.this, "Checking User details...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return ComplaintDetailsActivity.this.checkPrepost(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            if (!result.startsWith("PP")) {
                ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.link_error)).setText("This facility is currently available for R-APDRP post paid consumers only. For any complain, please call our toll free number 1912");
                ComplaintDetailsActivity.this.findViewById(R.id.tr_0).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
            } else if (result.indexOf("HTML") != -1) {
                ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.link_error)).setText("Server busy.Please try after some time.");
                ComplaintDetailsActivity.this.findViewById(R.id.tr_0).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
            } else {
                new ExecuteGetDocketTask().execute(new String[]{ComplaintDetailsActivity.this.getIntent().getExtras().getString("ConsumerId").toString()});
            }
        }
    }

    class ExecuteGetDocketTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecuteGetDocketTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(ComplaintDetailsActivity.this, "Get Docket details...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return ComplaintDetailsActivity.this.getDocketDetails(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            System.out.println("End of Fetch Docket Details");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_complaint_details);
        if (getIntent().getExtras().getString("ConsumerId").toString().trim().length() == 12) {
            ((TextView) findViewById(R.id.link_error)).setText("This facility is currently available for R-APDRP post paid consumers only. For any complain, please call our toll free number 1912");
            findViewById(R.id.tr_0).setVisibility(8);
            findViewById(R.id.tr_1).setVisibility(8);
            findViewById(R.id.tr_2).setVisibility(8);
            findViewById(R.id.tr_3).setVisibility(8);
            findViewById(R.id.tr_4).setVisibility(8);
            findViewById(R.id.tr_5).setVisibility(8);
            findViewById(R.id.tr_6).setVisibility(8);
            return;
        }
        new ExecuteCheckPrepostTask().execute(new String[]{getIntent().getExtras().getString("ConsumerId").toString()});
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

    public String getDocketDetails(String[] params) {
        System.out.println("Hii...Yess");
        String s = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/ComplaintManagementService/ComplaintManagement?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://nayandeeptamuli.com/complaint/\"><SOAP-ENV:Body><ns1:docketList><conId>" + params[0] + "</conId>" + "</ns1:docketList>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            s = readResponse(httpResponse);
            System.out.println(s);
            String[] stArr = s.substring(s.indexOf("<return"), s.lastIndexOf("</return>")).split("</return>");
            final String[] consumerDetails = new String[stArr.length];
            String consmerId = params[0];
            int i = -1;
            for (String st : stArr) {
                i++;
                consumerDetails[i] = st.substring(st.indexOf(62) + 1).trim();
                System.out.println(consumerDetails[i]);
            }
            runOnUiThread(new Runnable() {
                public void run() {
                    if (consumerDetails.length < 3) {
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.link_error)).setText("No Docket Details available");
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_0).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
                    } else if (consumerDetails.length == 3) {
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo1)).setText(consumerDetails[0]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status1)).setText(consumerDetails[1]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date1)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[2])));
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
                    } else if (consumerDetails.length == 6) {
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo1)).setText(consumerDetails[0]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status1)).setText(consumerDetails[1]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date1)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[2])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo2)).setText(consumerDetails[3]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status2)).setText(consumerDetails[4]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date2)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[5])));
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
                    } else if (consumerDetails.length == 9) {
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo1)).setText(consumerDetails[0]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status1)).setText(consumerDetails[1]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date1)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[2])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo2)).setText(consumerDetails[3]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status2)).setText(consumerDetails[4]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date2)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[5])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo3)).setText(consumerDetails[6]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status3)).setText(consumerDetails[7]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date3)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[8])));
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
                    } else if (consumerDetails.length == 12) {
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo1)).setText(consumerDetails[0]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status1)).setText(consumerDetails[1]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date1)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[2])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo2)).setText(consumerDetails[3]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status2)).setText(consumerDetails[4]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date2)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[5])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo3)).setText(consumerDetails[6]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status3)).setText(consumerDetails[7]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date3)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[8])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo4)).setText(consumerDetails[9]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status4)).setText(consumerDetails[10]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date4)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[11])));
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
                    } else if (consumerDetails.length == 15) {
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo1)).setText(consumerDetails[0]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status1)).setText(consumerDetails[1]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date1)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[2])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo2)).setText(consumerDetails[3]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status2)).setText(consumerDetails[4]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date2)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[5])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo3)).setText(consumerDetails[6]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status3)).setText(consumerDetails[7]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date3)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[8])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo4)).setText(consumerDetails[9]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status4)).setText(consumerDetails[10]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date4)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[11])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo5)).setText(consumerDetails[12]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status5)).setText(consumerDetails[13]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date5)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[14])));
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(8);
                    } else if (consumerDetails.length == 18) {
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo1)).setText(consumerDetails[0]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status1)).setText(consumerDetails[1]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date1)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[2])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo2)).setText(consumerDetails[3]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status2)).setText(consumerDetails[4]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date2)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[5])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo3)).setText(consumerDetails[6]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status3)).setText(consumerDetails[7]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date3)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[8])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo4)).setText(consumerDetails[9]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status4)).setText(consumerDetails[10]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date4)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[11])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo5)).setText(consumerDetails[12]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status5)).setText(consumerDetails[13]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date5)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[14])));
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo6)).setText(consumerDetails[15]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.status6)).setText(consumerDetails[16]);
                        ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.date6)).setText(new SimpleDateFormat("dd.MM.yyyy").format(new Date(consumerDetails[17])));
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_5).setVisibility(0);
                        ComplaintDetailsActivity.this.findViewById(R.id.tr_6).setVisibility(0);
                    }
                    ((Button) ComplaintDetailsActivity.this.findViewById(R.id.btn_details1)).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(ComplaintDetailsActivity.this, ViewDocketActivity.class);
                            intent.putExtra("DocketNo", ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo1)).getText().toString());
                            ComplaintDetailsActivity.this.startActivity(intent);
                        }
                    });
                    ((Button) ComplaintDetailsActivity.this.findViewById(R.id.btn_details2)).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(ComplaintDetailsActivity.this, ViewDocketActivity.class);
                            intent.putExtra("DocketNo", ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo2)).getText().toString());
                            ComplaintDetailsActivity.this.startActivity(intent);
                        }
                    });
                    ((Button) ComplaintDetailsActivity.this.findViewById(R.id.btn_details3)).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(ComplaintDetailsActivity.this, ViewDocketActivity.class);
                            intent.putExtra("DocketNo", ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo3)).getText().toString());
                            ComplaintDetailsActivity.this.startActivity(intent);
                        }
                    });
                    ((Button) ComplaintDetailsActivity.this.findViewById(R.id.btn_details4)).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(ComplaintDetailsActivity.this, ViewDocketActivity.class);
                            intent.putExtra("DocketNo", ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo4)).getText().toString());
                            ComplaintDetailsActivity.this.startActivity(intent);
                        }
                    });
                    ((Button) ComplaintDetailsActivity.this.findViewById(R.id.btn_details5)).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(ComplaintDetailsActivity.this, ViewDocketActivity.class);
                            intent.putExtra("DocketNo", ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo5)).getText().toString());
                            ComplaintDetailsActivity.this.startActivity(intent);
                        }
                    });
                    ((Button) ComplaintDetailsActivity.this.findViewById(R.id.btn_details6)).setOnClickListener(new OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(ComplaintDetailsActivity.this, ViewDocketActivity.class);
                            intent.putExtra("DocketNo", ((TextView) ComplaintDetailsActivity.this.findViewById(R.id.docketNo6)).getText().toString());
                            ComplaintDetailsActivity.this.startActivity(intent);
                        }
                    });
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }
}
