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

public class PaymenthistoryActivity extends AppCompatActivity {
    String[] consumerDetails;
    String consumerName;

    class ExecutePaymentHistoryTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        ExecutePaymentHistoryTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(PaymenthistoryActivity.this, "Fetching Payment History...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return PaymenthistoryActivity.this.fetchHistory(params);
        }

        protected void onPostExecute(String result) {
            this.progressDialog.dismiss();
            System.out.println(result + "str");
            TextView tView;
            if (result.indexOf("NO_HISTORY") != -1) {
                tView = (TextView) PaymenthistoryActivity.this.findViewById(R.id.link_error);
                tView.setText("No Payment history available for this consumer");
                tView.setVisibility(0);
                PaymenthistoryActivity.this.findViewById(R.id.tr_0).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_6).setVisibility(8);
            } else if (result.indexOf("NO_OUTSTADING") != -1) {
                tView = (TextView) PaymenthistoryActivity.this.findViewById(R.id.link_error);
                tView.setText("No Payment history available for this consumer");
                tView.setVisibility(0);
                PaymenthistoryActivity.this.findViewById(R.id.tr_0).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_6).setVisibility(8);
            } else {
                PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_3).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_4).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_5).setVisibility(8);
                PaymenthistoryActivity.this.findViewById(R.id.tr_6).setVisibility(8);
                ((TextView) PaymenthistoryActivity.this.findViewById(R.id.link_error)).setVisibility(8);
                String[] stArr = result.substring(result.indexOf("<return"), result.lastIndexOf("</return>")).split("</return>");
                PaymenthistoryActivity.this.consumerDetails = new String[stArr.length];
                int i = -1;
                for (String st : stArr) {
                    i++;
                    PaymenthistoryActivity.this.consumerDetails[i] = st.substring(st.indexOf(62) + 1).trim();
                    System.out.println(PaymenthistoryActivity.this.consumerDetails[i]);
                }
                for (i = 0; i < PaymenthistoryActivity.this.consumerDetails.length; i += 4) {
                    if (i == 0) {
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.conID1)).setText(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"));
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.paymonth1)).setText(PaymenthistoryActivity.this.consumerDetails[i]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.payamount1)).setText(PaymenthistoryActivity.this.consumerDetails[i + 1]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.txnId1)).setText(PaymenthistoryActivity.this.consumerDetails[i + 2]);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                    }
                    if (i == 4) {
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.conID2)).setText(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"));
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.paymonth2)).setText(PaymenthistoryActivity.this.consumerDetails[i]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.payamount2)).setText(PaymenthistoryActivity.this.consumerDetails[i + 1]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.txnId2)).setText(PaymenthistoryActivity.this.consumerDetails[i + 2]);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                    }
                    if (i == 8) {
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.conID3)).setText(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"));
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.paymonth3)).setText(PaymenthistoryActivity.this.consumerDetails[i]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.payamount3)).setText(PaymenthistoryActivity.this.consumerDetails[i + 1]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.txnId3)).setText(PaymenthistoryActivity.this.consumerDetails[i + 2]);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                    }
                    if (i == 12) {
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.conID4)).setText(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"));
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.paymonth4)).setText(PaymenthistoryActivity.this.consumerDetails[i]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.payamount4)).setText(PaymenthistoryActivity.this.consumerDetails[i + 1]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.txnId4)).setText(PaymenthistoryActivity.this.consumerDetails[i + 2]);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                    }
                    if (i == 16) {
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.conID5)).setText(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"));
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.paymonth5)).setText(PaymenthistoryActivity.this.consumerDetails[i]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.payamount5)).setText(PaymenthistoryActivity.this.consumerDetails[i + 1]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.txnId5)).setText(PaymenthistoryActivity.this.consumerDetails[i + 2]);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_5).setVisibility(0);
                    }
                    if (i == 20) {
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.conID6)).setText(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"));
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.paymonth6)).setText(PaymenthistoryActivity.this.consumerDetails[i]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.payamount6)).setText(PaymenthistoryActivity.this.consumerDetails[i + 1]);
                        ((TextView) PaymenthistoryActivity.this.findViewById(R.id.txnId6)).setText(PaymenthistoryActivity.this.consumerDetails[i + 2]);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_1).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_2).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_3).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_4).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_5).setVisibility(0);
                        PaymenthistoryActivity.this.findViewById(R.id.tr_6).setVisibility(0);
                    }
                }
                ((ImageView) PaymenthistoryActivity.this.findViewById(R.id.create_pdf_1)).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymenthistoryActivity.this.pdfgen(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"), PaymenthistoryActivity.this.consumerName, PaymenthistoryActivity.this.consumerDetails[0], PaymenthistoryActivity.this.consumerDetails[1], PaymenthistoryActivity.this.consumerDetails[2], PaymenthistoryActivity.this.consumerDetails[3]);
                    }
                });
                ((ImageView) PaymenthistoryActivity.this.findViewById(R.id.create_pdf_2)).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymenthistoryActivity.this.pdfgen(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"), PaymenthistoryActivity.this.consumerName, PaymenthistoryActivity.this.consumerDetails[4], PaymenthistoryActivity.this.consumerDetails[5], PaymenthistoryActivity.this.consumerDetails[6], PaymenthistoryActivity.this.consumerDetails[7]);
                    }
                });
                ((ImageView) PaymenthistoryActivity.this.findViewById(R.id.create_pdf_3)).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymenthistoryActivity.this.pdfgen(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"), PaymenthistoryActivity.this.consumerName, PaymenthistoryActivity.this.consumerDetails[8], PaymenthistoryActivity.this.consumerDetails[9], PaymenthistoryActivity.this.consumerDetails[10], PaymenthistoryActivity.this.consumerDetails[11]);
                    }
                });
                ((ImageView) PaymenthistoryActivity.this.findViewById(R.id.create_pdf_4)).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymenthistoryActivity.this.pdfgen(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"), PaymenthistoryActivity.this.consumerName, PaymenthistoryActivity.this.consumerDetails[12], PaymenthistoryActivity.this.consumerDetails[13], PaymenthistoryActivity.this.consumerDetails[14], PaymenthistoryActivity.this.consumerDetails[15]);
                    }
                });
                ((ImageView) PaymenthistoryActivity.this.findViewById(R.id.create_pdf_5)).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymenthistoryActivity.this.pdfgen(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"), PaymenthistoryActivity.this.consumerName, PaymenthistoryActivity.this.consumerDetails[16], PaymenthistoryActivity.this.consumerDetails[17], PaymenthistoryActivity.this.consumerDetails[18], PaymenthistoryActivity.this.consumerDetails[19]);
                    }
                });
                ((ImageView) PaymenthistoryActivity.this.findViewById(R.id.create_pdf_6)).setOnClickListener(new OnClickListener() {
                    public void onClick(View v) {
                        PaymenthistoryActivity.this.pdfgen(PaymenthistoryActivity.this.getIntent().getExtras().getString("ConsumerId"), PaymenthistoryActivity.this.consumerName, PaymenthistoryActivity.this.consumerDetails[20], PaymenthistoryActivity.this.consumerDetails[21], PaymenthistoryActivity.this.consumerDetails[22], PaymenthistoryActivity.this.consumerDetails[23]);
                    }
                });
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((int) R.layout.activity_paymenthistory);
        ((TextView) findViewById(R.id.link_error)).setVisibility(8);
        String consumerId = getIntent().getExtras().getString("ConsumerId");
        String data = getIntent().getExtras().getString("data");
        if (data.startsWith("PP")) {
            this.consumerName = data.split("#")[1];
            new ExecutePaymentHistoryTask().execute(new String[]{consumerId});
            return;
        }
        TextView tView = (TextView) findViewById(R.id.link_error);
        tView.setText("This facility is not available for Pre-Paid Consumer");
        tView.setVisibility(0);
        findViewById(R.id.tr_0).setVisibility(8);
        findViewById(R.id.tr_1).setVisibility(8);
        findViewById(R.id.tr_2).setVisibility(8);
        findViewById(R.id.tr_3).setVisibility(8);
        findViewById(R.id.tr_4).setVisibility(8);
        findViewById(R.id.tr_5).setVisibility(8);
        findViewById(R.id.tr_6).setVisibility(8);
    }

    public String fetchHistory(String[] params) {
        String s = "";
        String outputString = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://www.nayandeeptamuli.com/OnlinePaymentServiceService/OnlinePaymentService?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:fetchPayhistory xmlns:yq1=\"http://nayandeeptamuli.com/onlinepay/\"><arg0>" + params[0] + "</arg0>" + "</yq1:fetchPayhistory>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            outputString = readResponse(httpResponse);
            System.out.println(outputString);
            String[] consumerDetails = outputString.replaceAll("</return>", "").split("<return>");
            System.out.println("Output : " + consumerDetails[1]);
            outputString = consumerDetails[1].substring(0, consumerDetails[1].indexOf("<"));
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

    void pdfgen(String conId, String consumerName, String payMonth, String payAmount, String mode, String txnId) {
        Exception ex;
        Throwable th;
        ProgressDialog progressDialog = ProgressDialog.show(this, "Generating Pdf...", "Please wait...", false, true);
        File file = new File("/sdcard/Download/" + txnId + ".pdf");
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
                document2.add(new Paragraph("                       Payment Receipt", font));
                document2.add(new Paragraph("  ", font));
                pdfPTable = new PdfPTable(1);
                pdfPTable.setWidthPercentage(100.0f);
                fArr = new float[2];
                pdfPTable = new PdfPTable(new float[]{30.0f, 70.0f});
                pdfPTable.setWidthPercentage(50.0f);
                pdfPTable.setHorizontalAlignment(1);
                cell = new PdfPCell(new Paragraph(" Consumer Id : ", font));
                cell.setBorder(0);
                cell.setHorizontalAlignment(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(conId, font));
                cell.setBorder(0);
                cell.setHorizontalAlignment(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Name : ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(consumerName, font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Payment Mode: ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(mode, font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Receipt No : ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(txnId, font));
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(" Pay Received : ", font));
                cell.setHorizontalAlignment(0);
                cell.setBorder(0);
                pdfPTable.addCell(cell);
                cell = new PdfPCell(new Paragraph(payAmount, font));
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
                        Toast.makeText(PaymenthistoryActivity.this.getApplicationContext(), "PDF Generated", 0).show();
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
}
