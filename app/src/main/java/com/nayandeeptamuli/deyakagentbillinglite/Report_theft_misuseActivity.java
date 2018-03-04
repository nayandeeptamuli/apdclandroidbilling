package com.nayandeeptamuli.apdclandroidbilling;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.itextpdf.awt.PdfGraphics2D;
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

public class Report_theft_misuseActivity extends Activity implements OnItemSelectedListener {
    private static final String[] locations = new String[]{"Select Location", "ABHAYAPURI", "AGOMONI", "AMGURI", "AMINGAON", "AZARA", "BADARPUR", "BAIHATA CHARIALI", "BALIPARA", "BARAMA", "BARDUBI", "BARPETA", "BARPETA ROAD", "BASISTHA", "BASUGAON", "BIJNI", "BILASIPARA", "BISWANATH CHARIALI", "BOKAJAN", "BOKAKHAT", "BOKO", "BONGAIGAON - I", "BONGAIGAON - II", "CAPITAL", "CHAMATA", "CHANDMARI", "CHAPAKHOWA", "CHAPAR", "CHARAIBAHI", "CHARAIDEO", "CHAYGAON", "CHILAPATHAR", "DAMRA", "DEMOW", "DERGAON", "DHAKUAKHANA", "DHEKIAJULI - I", "DHEKIAJULI - II", "DHEMAJI", "DHING", "DHUBRI", "DHUPDHARA", "DIBRUGARH - I", "DIBRUGARH - II", "DIBRUGARH - III", "DIGBOI", "DIPHU - I", "DIPHU - II", "DONKAMOKAM", "DOOMDOOMA", "DURLAVCHERRA", "EDEN", "FAKIRAGRAM", "FANCY BAZAR", "FATASIL", "GARBHANGA", "GAURIPUR", "GAURISAGAR", "GHILAMARA", "GOALPARA", "GOHPUR", "GOLAGHAT - I", "GOLAGHAT - II", "GOLOKGANJ", "GOSAIGAON", "HAFLONG", "HAILAKANDI", "HAJO", "HAMREN", "HOJAI", "HOWRAGHAT", "JALUKBARI", "JAMUGURI", "JONAI", "JORHAT - I", "JORHAT - II", "JORHAT - III", "KAKOJAN", "KALAIN", "KALAPAHAR", "KAMARGAON", "KARIMGANJ", "KATHIATOLI", "KHARUPETIA", "KHERONI", "KOKRAJHAR", "KOLIABOR", "LAHORIGHAT", "LAKHIPUR", "LAKHIPUR", "LALA", "LANKA", "LOWAIRPOA", "LUMDING", "MAIBONG", "MAJBAT", "MAJULI", "MANGALDOI", "MANKACHAR", "MARGHERITA", "MARIANI", "MIRZA", "MORAN", "MORIGAON", "NAGAON - I", "NAGAON - II", "NAGAON - III", "NAHARKATIA", "NALBARI - I", "NALBARI - II", "NAMRUP", "NARENGI", "NAZIRA", "NILAMBAZAR", "NORTH LAKHIMPUR", "PALTANBAZAR", "PATHARKANDI", "PATHSALA", "R.K. NAGAR", "RANGAPARA", "RANGIA - I", "RANGIA - II", "SAMAGURI", "SARBHOG", "SARTHEBARI", "SARUPATHAR", "SILCHAR - I", "SILCHAR - II", "SILCHAR - III", "SIPAJHAR", "SIVASAGAR - I", "SIVASAGAR - II", "SONAI", "SONAPUR", "SOOTEA", "SUALKUCHI", "TAMULPUR", "TANGLA", "TEOK", "TEZPUR - I", "TEZPUR - II", "TIHU", "TINGKHONG", "TINSUKIA - I", "TINSUKIA - II", "TINSUKIA - III", "TITABOR", "UDALGURI", "UDARBAND", "ULUBARI", "UMRANGSU", "UZANBAZAR", "ZOO ROAD"};
    EditText details;
    TextView details_lbl;
    EditText email;
    TextView email_lbl;
    EditText fname;
    TextView fname_lbl;
    EditText lname;
    TextView lname_lbl;
    EditText phone;
    TextView phone_lbl;
    private Spinner spinner;

    class SubmitTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        SubmitTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(Report_theft_misuseActivity.this, "Request in Progress", "Please wait...", false, true);
            this.progressDialog.getWindow().setGravity(17);
        }

        protected String doInBackground(String... params) {
            String res = "";
            return Report_theft_misuseActivity.this.reportTheftData(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            if (result.equalsIgnoreCase("S")) {
                ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.fname_lbl)).setVisibility(8);
                ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.lname_lbl)).setVisibility(8);
                ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.email_lbl)).setVisibility(8);
                ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.phone_lbl)).setVisibility(8);
                ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.details_lbl)).setVisibility(8);
                ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.fname)).setVisibility(8);
                ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.lname)).setVisibility(8);
                ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.email)).setVisibility(8);
                ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.phone)).setVisibility(8);
                ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.details)).setVisibility(8);
                ((Button) Report_theft_misuseActivity.this.findViewById(R.id.btn_submit)).setVisibility(8);
                Report_theft_misuseActivity.this.spinner.setVisibility(8);
                ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.header)).setVisibility(8);
                ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.report_msg)).setText("Thank you for providing your valuable Report");
                return;
            }
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.header)).setVisibility(0);
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.fname_lbl)).setVisibility(0);
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.lname_lbl)).setVisibility(0);
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.email_lbl)).setVisibility(0);
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.phone_lbl)).setVisibility(0);
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.details_lbl)).setVisibility(0);
            ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.fname)).setVisibility(0);
            ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.lname)).setVisibility(0);
            ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.email)).setVisibility(0);
            ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.phone)).setVisibility(0);
            ((EditText) Report_theft_misuseActivity.this.findViewById(R.id.details)).setVisibility(0);
            ((Button) Report_theft_misuseActivity.this.findViewById(R.id.btn_submit)).setVisibility(0);
            Report_theft_misuseActivity.this.spinner.setVisibility(0);
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.header)).setVisibility(0);
            ((TextView) Report_theft_misuseActivity.this.findViewById(R.id.report_msg)).setText("Sorry! Please Try Again");
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_theft_misuse);
        this.fname_lbl = (TextView) findViewById(R.id.fname_lbl);
        this.lname_lbl = (TextView) findViewById(R.id.lname_lbl);
        this.email_lbl = (TextView) findViewById(R.id.email_lbl);
        this.phone_lbl = (TextView) findViewById(R.id.phone_lbl);
        this.details_lbl = (TextView) findViewById(R.id.details_lbl);
        this.fname = (EditText) findViewById(R.id.fname);
        this.lname = (EditText) findViewById(R.id.lname);
        this.email = (EditText) findViewById(R.id.email);
        this.phone = (EditText) findViewById(R.id.phone);
        this.details = (EditText) findViewById(R.id.details);
        this.spinner = (Spinner) findViewById(R.id.location);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367048, locations);
        adapter.setDropDownViewResource(17367049);
        this.spinner.setAdapter(adapter);
        this.spinner.setOnItemSelectedListener(this);
        ((Button) findViewById(R.id.btn_submit)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (Report_theft_misuseActivity.this.validate()) {
                    new SubmitTask().execute(new String[]{Report_theft_misuseActivity.this.fname.getText().toString() + " " + Report_theft_misuseActivity.this.lname.getText().toString(), Report_theft_misuseActivity.this.email.getText().toString(), Report_theft_misuseActivity.this.phone.getText().toString(), Report_theft_misuseActivity.this.spinner.getSelectedItem().toString(), Report_theft_misuseActivity.this.details.getText().toString()});
                }
            }
        });
    }

    public void onItemSelected(AdapterView<?> adapterView, View v, int position, long id) {
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public String reportTheftData(String[] valuse) {
        Exception ex;
        String outputString = "";
        try {
            HttpPost httpPost;
            HttpClient httpClient;
            HttpClient httpClient2 = new DefaultHttpClient();
            try {
                httpPost = new HttpPost("https://www.nayandeeptamuli.com/StreetLightOutageService/StreetLightOutage?wsdl");
            } catch (Exception e) {
                ex = e;
                httpClient = httpClient2;
                ex.printStackTrace();
                return outputString;
            }
            HttpPost httpPost2;
            try {
                httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
                httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
                String reqXML = "";
                StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:insertToStreetLightData xmlns:yq1=\""><name>" + valuse[0] + "</name>" + "<emailId>" + valuse[1] + "</emailId>" + "<mobileNumber>" + valuse[2] + "</mobileNumber>" + "<location>" + valuse[3] + "</location>" + "<usrNote>" + valuse[4] + "</usrNote>" + "<feedback_nature>" + "RMT" + "</feedback_nature>" + "</yq1:insertToStreetLightData>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
                se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
                httpPost.setEntity(se);
                HttpResponse httpResponse = httpClient2.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                outputString = readResponse(httpResponse);
                System.out.println(outputString);
                String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
                outputString = consumerDetails[1].substring(0, consumerDetails[1].indexOf("<"));
                System.out.println("Output : " + outputString);
                httpPost2 = httpPost;
                httpClient = httpClient2;
            } catch (Exception e2) {
                ex = e2;
                httpPost2 = httpPost;
                httpClient = httpClient2;
                ex.printStackTrace();
                return outputString;
            }
        } catch (Exception e3) {
            ex = e3;
            ex.printStackTrace();
            return outputString;
        }
        return outputString;
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

    boolean isEmailValid(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean validate() {
        Spinner location = (Spinner) findViewById(R.id.location);
        EditText fname = (EditText) findViewById(R.id.fname);
        EditText lname = (EditText) findViewById(R.id.lname);
        EditText email = (EditText) findViewById(R.id.email);
        EditText phone = (EditText) findViewById(R.id.phone);
        EditText details = (EditText) findViewById(R.id.details);
        if (fname.getText().toString().isEmpty()) {
            fname.setError("Please provide the First Name");
            return false;
        } else if (lname.getText().toString().isEmpty()) {
            lname.setError("Please provide the Last Name");
            return false;
        } else if (email.getText().toString().isEmpty() || email.getText().toString().equalsIgnoreCase("NA")) {
            email.setError("Please provide a Email Id");
            return false;
        } else if (!isEmailValid(email.getText().toString())) {
            email.setError("Please provide a Valid Email Id");
            return false;
        } else if (phone.getText().toString().isEmpty() || phone.getText().toString().equalsIgnoreCase("NA")) {
            phone.setError("Please provide a Mobile Number");
            return false;
        } else if (!phone.getText().toString().matches("\\d+")) {
            phone.setError("Mobile Number should be Numeric");
            return false;
        } else if (phone.getText().toString().startsWith("0")) {
            phone.setError("Mobile Number can not start with 0");
            return false;
        } else if (phone.getText().toString().length() != 10) {
            phone.setError("Mobile Number should be 10 digit");
            return false;
        } else if (location.getSelectedItem().toString().equalsIgnoreCase("Select Location")) {
            ((TextView) location.getSelectedView()).setError("Please Select Location");
            location.requestFocus();
            return false;
        } else if (details.getText().toString().isEmpty()) {
            details.setError("Please provide your Outage Details");
            return false;
        } else if (details.getText().toString().length() > PdfGraphics2D.AFM_DIVISOR) {
            details.setError("Please provide Outage Details within 1000 characters");
            return false;
        } else {
            fname.setError(null);
            lname.setError(null);
            email.setError(null);
            phone.setError(null);
            details.setError(null);
            return true;
        }
    }
}
