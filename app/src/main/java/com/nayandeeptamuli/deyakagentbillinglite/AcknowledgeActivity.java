package com.nayandeeptamuli.apdclandroidbilling;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

public class AcknowledgeActivity extends AppCompatActivity {

    class ExecuteNewConnectionDetails extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecuteNewConnectionDetails() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(AcknowledgeActivity.this, "Getting New Connection Details...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return AcknowledgeActivity.this.getNewconnectionDetails(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
        }
    }

    class ExecutePDFDetails extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecutePDFDetails() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(AcknowledgeActivity.this, "Fetching BP and Consumer Details...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return AcknowledgeActivity.this.getpdfDetails(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            if (result.startsWith("NA")) {
                Toast.makeText(AcknowledgeActivity.this.getApplicationContext(), "Sorry!!Service order number and Contract account number not available in the System", 0).show();
                return;
            }
            AcknowledgeActivity.this.pdfGen(result.split("#"));
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_acknowledge);
        findViewById(R.id.tr_0).setVisibility(8);
        findViewById(R.id.tr_1).setVisibility(8);
        findViewById(R.id.tr_2).setVisibility(8);
        findViewById(R.id.tr_3).setVisibility(8);
        findViewById(R.id.tr_4).setVisibility(8);
        findViewById(R.id.tr_5).setVisibility(8);
        findViewById(R.id.tr_6).setVisibility(8);
        ((Button) findViewById(R.id.btn_fetch)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                AcknowledgeActivity.this.fetchNewcondetails();
            }
        });
    }

    public void fetchNewcondetails() {
        if (validate()) {
            new ExecuteNewConnectionDetails().execute(new String[]{((EditText) findViewById(R.id.input_refid)).getText().toString()});
        }
    }

    public String getNewconnectionDetails(String[] params) {
        String outputString = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://www.nayandeeptamuli.com/NewConnectionResponseService/NewConnectionResponse?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://nayandeeptamuli.com/NewConnectionResponse/\"><SOAP-ENV:Body><ns1:getPaymentDetails><referenceID>" + params[0] + "</referenceID>" + "</ns1:getPaymentDetails>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            outputString = readResponse(httpResponse);
            System.out.println("Raw data : " + outputString);
            String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
            outputString = consumerDetails[1].substring(0, consumerDetails[1].indexOf("<"));
            System.out.println(outputString);
            if (outputString.indexOf("@") != -1) {
                final String[] dataSet = outputString.split("@");
                System.out.println(dataSet[0]);
                runOnUiThread(new Runnable() {
                    public void run() {
                        int i;
                        String[] consumerDetails;
                        if (dataSet.length >= 6) {
                            AcknowledgeActivity.this.findViewById(R.id.tr_0).setVisibility(0);
                            for (i = 0; i < dataSet.length; i++) {
                                if (i == 0) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName1)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount1)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date1)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique1)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId1)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique1)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId1)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                                }
                                if (i == 1) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName2)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount2)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date2)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique2)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId2)).setText(consumerDetails[3]);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique2)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId2)).setVisibility(8);
                                }
                                if (i == 2) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName3)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount3)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date3)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique3)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique1)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId3)).setText(consumerDetails[3]);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique3)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId3)).setVisibility(8);
                                }
                                if (i == 3) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName4)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount4)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date4)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique4)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId4)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique4)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId4)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                                }
                                if (i == 4) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName5)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount5)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date5)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique5)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId5)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique5)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId5)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_5).setVisibility(0);
                                }
                                if (i == 5) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName6)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount6)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date6)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique6)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId6)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique6)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId6)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_6).setVisibility(0);
                                }
                            }
                        } else {
                            AcknowledgeActivity.this.findViewById(R.id.tr_0).setVisibility(0);
                            for (i = 0; i < dataSet.length; i++) {
                                if (i == 0) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName1)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount1)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date1)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique1)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId1)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique1)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId1)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                                }
                                if (i == 1) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName2)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount2)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date2)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique2)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId2)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique2)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId2)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                                }
                                if (i == 2) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName3)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount3)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date3)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique3)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId3)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique3)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId3)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                                }
                                if (i == 3) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName4)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount4)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date4)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique4)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId4)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique4)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId4)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                                }
                                if (i == 4) {
                                    consumerDetails = dataSet[i].split("#");
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName5)).setText(consumerDetails[0]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount5)).setText(consumerDetails[1]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.date5)).setText(consumerDetails[2]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique5)).setText(consumerDetails[4]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId5)).setText(consumerDetails[3]);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique5)).setVisibility(8);
                                    ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId5)).setVisibility(8);
                                    AcknowledgeActivity.this.findViewById(R.id.tr_5).setVisibility(0);
                                }
                            }
                        }
                        ((ImageView) AcknowledgeActivity.this.findViewById(R.id.create_pdf_1)).setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                String uniqueId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique1)).getText().toString();
                                String name = ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName1)).getText().toString();
                                String txnId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId1)).getText().toString();
                                String date = ((TextView) AcknowledgeActivity.this.findViewById(R.id.date1)).getText().toString();
                                String amount = ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount1)).getText().toString();
                                new ExecutePDFDetails().execute(new String[]{uniqueId, name, txnId, date, amount});
                            }
                        });
                        ((ImageView) AcknowledgeActivity.this.findViewById(R.id.create_pdf_2)).setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                String uniqueId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique2)).getText().toString();
                                String name = ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName2)).getText().toString();
                                String txnId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId2)).getText().toString();
                                String date = ((TextView) AcknowledgeActivity.this.findViewById(R.id.date2)).getText().toString();
                                String amount = ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount2)).getText().toString();
                                new ExecutePDFDetails().execute(new String[]{uniqueId, name, txnId, date, amount});
                            }
                        });
                        ((ImageView) AcknowledgeActivity.this.findViewById(R.id.create_pdf_3)).setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                String uniqueId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique3)).getText().toString();
                                String name = ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName3)).getText().toString();
                                String txnId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId3)).getText().toString();
                                String date = ((TextView) AcknowledgeActivity.this.findViewById(R.id.date3)).getText().toString();
                                String amount = ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount3)).getText().toString();
                                new ExecutePDFDetails().execute(new String[]{uniqueId, name, txnId, date, amount});
                            }
                        });
                        ((ImageView) AcknowledgeActivity.this.findViewById(R.id.create_pdf_4)).setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                String uniqueId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique4)).getText().toString();
                                String name = ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName4)).getText().toString();
                                String txnId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId4)).getText().toString();
                                String date = ((TextView) AcknowledgeActivity.this.findViewById(R.id.date4)).getText().toString();
                                String amount = ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount4)).getText().toString();
                                new ExecutePDFDetails().execute(new String[]{uniqueId, name, txnId, date, amount});
                            }
                        });
                        ((ImageView) AcknowledgeActivity.this.findViewById(R.id.create_pdf_5)).setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                String uniqueId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique5)).getText().toString();
                                String name = ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName5)).getText().toString();
                                String txnId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId5)).getText().toString();
                                String date = ((TextView) AcknowledgeActivity.this.findViewById(R.id.date5)).getText().toString();
                                String amount = ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount5)).getText().toString();
                                new ExecutePDFDetails().execute(new String[]{uniqueId, name, txnId, date, amount});
                            }
                        });
                        ((ImageView) AcknowledgeActivity.this.findViewById(R.id.create_pdf_6)).setOnClickListener(new OnClickListener() {
                            public void onClick(View v) {
                                String uniqueId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.unique6)).getText().toString();
                                String name = ((TextView) AcknowledgeActivity.this.findViewById(R.id.conName6)).getText().toString();
                                String txnId = ((TextView) AcknowledgeActivity.this.findViewById(R.id.txnId6)).getText().toString();
                                String date = ((TextView) AcknowledgeActivity.this.findViewById(R.id.date6)).getText().toString();
                                String amount = ((TextView) AcknowledgeActivity.this.findViewById(R.id.amount6)).getText().toString();
                                new ExecutePDFDetails().execute(new String[]{uniqueId, name, txnId, date, amount});
                            }
                        });
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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

    public String getpdfDetails(String[] params) {
        String outputString = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://www.nayandeeptamuli.com/NewConnectionResponseService/NewConnectionResponse?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:getCRMDetails xmlns:yq1=\"http://nayandeeptamuli.com/NewConnectionResponse/\"><arg0>" + params[0] + "</arg0>" + "</yq1:getCRMDetails>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            outputString = readResponse(httpResponse);
            System.out.println("Raw data : " + outputString);
            String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
            outputString = consumerDetails[1].substring(0, consumerDetails[1].indexOf("<"));
            if (outputString.trim().length() > 0) {
                outputString = outputString + "#" + params[0] + "#" + params[1] + "#" + params[2] + "#" + params[3] + "#" + params[4];
            } else {
                outputString = "NA";
            }
            System.out.println("Modified : " + outputString);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return outputString;
    }

    public void pdfGen(String[] consumerPdfdetails) {
        Exception ex;
        Throwable th;
        ProgressDialog progressDialog = ProgressDialog.show(this, "Generating Pdf...", "Please wait...", false, true);
        File file = new File("/sdcard/Download/" + consumerPdfdetails[4] + ".pdf");
        Toast.makeText(getApplicationContext(), file.getAbsolutePath(), 0).show();
        Document document = null;
        PdfWriter pdfWriter = null;
        try {
            FileOutputStream file2 = new FileOutputStream(file);
            Document document2 = new Document(PageSize.A4);
            try {
                pdfWriter = PdfWriter.getInstance(document2, file2);
                document2.open();
                Font font = new Font(FontFamily.COURIER, 11.0f, 1);
                font = new Font(FontFamily.COURIER, 11.0f, 1);
                font = new Font(FontFamily.COURIER, 11.0f, 0);
                Font fontFooterText = new Font(FontFamily.COURIER, 7.0f, 0);
                font = new Font(FontFamily.COURIER, 14.0f, 0, BaseColor.BLACK);
                font = new Font(FontFamily.COURIER, 18.0f, 1, BaseColor.GRAY);
                Bitmap bmp = ((BitmapDrawable) getResources().getDrawable(R.drawable.apdclprepaidpaymentreceipt1)).getBitmap();
                OutputStream stream = new ByteArrayOutputStream();
                bmp.compress(CompressFormat.JPEG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.setAbsolutePosition(50.0f, 770.0f);
                image.scalePercent(50.0f);
                document2.add(new Paragraph(" "));
                document2.add(new Paragraph(" "));
                float[] fArr = new float[2];
                PdfPTable pdfPTable = new PdfPTable(new float[]{10.0f, 90.0f});
                pdfPTable.setWidthPercentage(100.0f);
                PdfPCell cell = new PdfPCell(image);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph("Assam Power Distribution Company Limited ", font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                document2.add(pdfPTable);
                document2.add(new Paragraph("           New Connection Registration Payment Receipt", font));
                document2.add(new Paragraph("  ", font));
                pdfPTable = new PdfPTable(1);
                pdfPTable.setWidthPercentage(100.0f);
                fArr = new float[2];
                pdfPTable = new PdfPTable(new float[]{30.0f, 70.0f});
                pdfPTable.setWidthPercentage(50.0f);
                pdfPTable.setHorizontalAlignment(1);
                cell = new PdfPCell(new Paragraph(" Contract Account   Number : ", font));
                cell.setBorder(0);
                cell.setHorizontalAlignment(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(consumerPdfdetails[0], font));
                cell.setBorder(0);
                cell.setHorizontalAlignment(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Service Order Number : ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(consumerPdfdetails[1], font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Consumer Name : ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(consumerPdfdetails[3], font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Transaction Id : ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(consumerPdfdetails[4], font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Payment Amount : ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(consumerPdfdetails[6], font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Transaction Date: ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(consumerPdfdetails[5], font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(pdfPTable);
                cell.setBorderWidthTop(0.0f);
                cell.setBorderWidthLeft(1.5f);
                cell.setBorderWidthRight(1.5f);
                cell.setBorderWidthBottom(0.0f);
                PdfPCell cell1 = new PdfPCell(new Paragraph(" "));
                cell1.setBorder(0);
                cell1.setBorderWidthTop(1.5f);
                cell1.setBorderWidthLeft(1.5f);
                cell1.setBorderWidthRight(1.5f);
                cell1.setBorderWidthBottom(0.0f);
                pdfPTable.addCell(cell1);
                cell1 = new PdfPCell(new Paragraph(" "));
                cell1.setBorder(0);
                cell1.setBorderWidthTop(0.0f);
                cell1.setBorderWidthLeft(1.5f);
                cell1.setBorderWidthRight(1.5f);
                cell1.setBorderWidthBottom(0.0f);
                pdfPTable.addCell(cell1);
                pdfPTable.addCell(cell);
                cell1 = new PdfPCell(new Paragraph("  "));
                cell1.setBorder(0);
                cell1.setBorderWidthTop(0.0f);
                cell1.setBorderWidthLeft(1.5f);
                cell1.setBorderWidthRight(1.5f);
                cell1.setBorderWidthBottom(0.0f);
                pdfPTable.addCell(cell1);
                cell1 = new PdfPCell(new Paragraph(" "));
                cell1.setBorder(0);
                cell1.setBorderWidthTop(0.0f);
                cell1.setBorderWidthLeft(1.5f);
                cell1.setBorderWidthRight(1.5f);
                cell1.setBorderWidthBottom(1.5f);
                pdfPTable.addCell(cell1);
                document2.add(pdfPTable);
                fArr = new float[2];
                PdfPTable tableFooter = new PdfPTable(new float[]{30.0f, 70.0f});
                tableFooter.setWidthPercentage(100.0f);
                cell1 = new PdfPCell(new Paragraph("* Payment Subject to Realisation", fontFooterText));
                cell1.setHorizontalAlignment(0);
                cell1.setBorder(0);
                tableFooter.addCell(cell1);
                cell1 = new PdfPCell(new Paragraph("Thank You", fontFooterText));
                cell1.setHorizontalAlignment(2);
                cell1.setBorder(0);
                tableFooter.addCell(cell1);
                document2.add(tableFooter);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Toast.makeText(AcknowledgeActivity.this.getApplicationContext(), "PDF Generated", 0).show();
                    }
                }, 1000);
                progressDialog.dismiss();
                Intent i = new Intent();
                i.setAction("android.intent.action.VIEW");
                i.setDataAndType(Uri.fromFile(file), "application/pdf");
                i.setFlags(1073741824);
                startActivity(i);
                progressDialog.dismiss();
                document2.close();
                if (pdfWriter != null) {
                    pdfWriter.close();
                    document = document2;
                    return;
                }
            } catch (Exception e) {
                ex = e;
                document = document2;
                try {
                    Toast.makeText(getApplicationContext(), ex.toString(), 0).show();
                    ex.printStackTrace();
                    progressDialog.dismiss();
                    document.close();
                    if (pdfWriter != null) {
                        pdfWriter.close();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    document.close();
                    if (pdfWriter != null) {
                        pdfWriter.close();
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                document = document2;
                document.close();
                if (pdfWriter != null) {
                    pdfWriter.close();
                }
                throw th;
            }
        } catch (Exception e2) {
            ex = e2;
            Toast.makeText(getApplicationContext(), ex.toString(), 0).show();
            ex.printStackTrace();
            progressDialog.dismiss();
            document.close();
            if (pdfWriter != null) {
                pdfWriter.close();
            }
        }
    }

    public boolean validate() {
        if (((EditText) findViewById(R.id.input_refid)).getText().toString().isEmpty()) {
            ((EditText) findViewById(R.id.input_refid)).setError("Please provide the New Connection Reference Id");
            return false;
        }
        ((EditText) findViewById(R.id.input_refid)).setError(null);
        return true;
    }
}
