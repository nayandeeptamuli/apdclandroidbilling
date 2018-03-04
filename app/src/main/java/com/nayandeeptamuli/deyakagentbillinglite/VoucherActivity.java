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
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseField;
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

public class VoucherActivity extends AppCompatActivity {
    Intent intent;

    class GeneratePDFTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDialog = null;

        GeneratePDFTask() {
        }

        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = ProgressDialog.show(VoucherActivity.this, "Voucher details...", "Please wait...", false, true);
        }

        protected String doInBackground(String... params) {
            return VoucherActivity.this.FetchVoucher(params);
        }

        protected void onPostExecute(String result) {
            Exception ex;
            Throwable th;
            if (!result.contains("exception") || !result.contains("Exception")) {
                String[] voucherDetails = result.split("#");
                String conId = voucherDetails[0];
                String conName = voucherDetails[1];
                String conAddress = voucherDetails[2];
                String voucherAmount = voucherDetails[3];
                String voucherCode = voucherDetails[4];
                String txnId = voucherDetails[5];
                String date = voucherDetails[6];
                String mode = voucherDetails[7].substring(0, voucherDetails[7].indexOf("<"));
                File file = new File("/sdcard/Download/" + txnId + ".pdf");
                Toast.makeText(VoucherActivity.this.getApplicationContext(), file.getAbsolutePath(), 0).show();
                Document document = null;
                PdfWriter pdfWriter = null;
                try {
                    FileOutputStream file2 = new FileOutputStream(file);
                    Document document2 = new Document(PageSize.B7.rotate());
                    try {
                        pdfWriter = PdfWriter.getInstance(document2, file2);
                        document2.open();
                        Font fontText = new Font(FontFamily.COURIER, 8.0f);
                        Bitmap bmp = ((BitmapDrawable) VoucherActivity.this.getResources().getDrawable(R.drawable.apdclprepaidpaymentreceipt)).getBitmap();
                        OutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(CompressFormat.JPEG, 100, stream);
                        Image image = Image.getInstance(stream.toByteArray());
                        image.setAbsolutePosition(BaseField.BORDER_WIDTH_THICK, 16.0f);
                        image.scalePercent(20.0f);
                        document2.add(image);
                        document2.add(new Paragraph(" "));
                        document2.add(new Paragraph(" "));
                        document2.add(new Paragraph("  ", fontText));
                        document2.add(new Paragraph("Consumer Id : " + conId, fontText));
                        document2.add(new Paragraph("Name : " + conName, fontText));
                        document2.add(new Paragraph("Address : " + conAddress, fontText));
                        document2.add(new Paragraph("Voucher Amount: " + voucherAmount, fontText));
                        document2.add(new Paragraph("Voucher Code : " + voucherCode, fontText));
                        document2.add(new Paragraph("Transaction ID: " + txnId, fontText));
                        document2.add(new Paragraph("Date: D/T: " + date, fontText));
                        document2.add(new Paragraph("Voucher Generation Mode : Online ", fontText));
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Toast.makeText(VoucherActivity.this.getApplicationContext(), "PDF Generated", 0).show();
                            }
                        }, 1000);
                        this.progressDialog.dismiss();
                        Intent i = new Intent();
                        i.setAction("android.intent.action.VIEW");
                        i.setDataAndType(Uri.fromFile(file), "application/pdf");
                        i.setFlags(1073741824);
                        VoucherActivity.this.startActivity(i);
                        this.progressDialog.dismiss();
                        document2.close();
                        if (pdfWriter != null) {
                            pdfWriter.close();
                        }
                    } catch (Exception e) {
                        ex = e;
                        document = document2;
                        try {
                            Toast.makeText(VoucherActivity.this.getApplicationContext(), ex.toString(), 0).show();
                            ex.printStackTrace();
                            this.progressDialog.dismiss();
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
                    Toast.makeText(VoucherActivity.this.getApplicationContext(), ex.toString(), 0).show();
                    ex.printStackTrace();
                    this.progressDialog.dismiss();
                    document.close();
                    if (pdfWriter != null) {
                        pdfWriter.close();
                    }
                }
            }
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        String[] condetails1;
        String[] condetails2;
        String[] condetails3;
        String[] condetails4;
        String[] condetails5;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);
        int size = getIntent().getExtras().getInt("Size");
        System.out.println(size);
        ((TextView) findViewById(R.id.link_error)).setVisibility(8);
        if (size == 0) {
            findViewById(R.id.tr_0).setVisibility(8);
            findViewById(R.id.tr_1).setVisibility(8);
            findViewById(R.id.tr_2).setVisibility(8);
            findViewById(R.id.tr_3).setVisibility(8);
            findViewById(R.id.tr_4).setVisibility(8);
            findViewById(R.id.tr_5).setVisibility(8);
            findViewById(R.id.tr_6).setVisibility(8);
            ((TextView) findViewById(R.id.link_error)).setText("This facility is not available for post paid consumers");
            ((TextView) findViewById(R.id.link_error)).setVisibility(0);
        }
        if (size == 1) {
            if (getIntent().getExtras().getString("index_0").toString().contains("@")) {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("@");
            } else {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID1)).setText(condetails1[1]);
            ((TextView) findViewById(R.id.payAmt1)).setText(condetails1[2]);
            ((TextView) findViewById(R.id.payTimeStmp1)).setText(condetails1[3]);
            ((TextView) findViewById(R.id.txnId1)).setText(condetails1[4]);
            findViewById(R.id.tr_2).setVisibility(8);
            findViewById(R.id.tr_3).setVisibility(8);
            findViewById(R.id.tr_4).setVisibility(8);
            findViewById(R.id.tr_5).setVisibility(8);
            findViewById(R.id.tr_6).setVisibility(8);
        }
        if (size == 2) {
            if (getIntent().getExtras().getString("index_0").toString().contains("@")) {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("@");
            } else {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID1)).setText(condetails1[1]);
            ((TextView) findViewById(R.id.payAmt1)).setText(condetails1[2]);
            ((TextView) findViewById(R.id.payTimeStmp1)).setText(condetails1[3]);
            ((TextView) findViewById(R.id.txnId1)).setText(condetails1[4]);
            if (getIntent().getExtras().getString("index_1").toString().contains("@")) {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("@");
            } else {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID2)).setText(condetails2[1]);
            ((TextView) findViewById(R.id.payAmt2)).setText(condetails2[2]);
            ((TextView) findViewById(R.id.payTimeStmp2)).setText(condetails2[3]);
            ((TextView) findViewById(R.id.txnId2)).setText(condetails2[4]);
            findViewById(R.id.tr_3).setVisibility(8);
            findViewById(R.id.tr_4).setVisibility(8);
            findViewById(R.id.tr_5).setVisibility(8);
            findViewById(R.id.tr_6).setVisibility(8);
        }
        if (size == 3) {
            if (getIntent().getExtras().getString("index_0").toString().contains("@")) {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("@");
            } else {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID1)).setText(condetails1[1]);
            ((TextView) findViewById(R.id.payAmt1)).setText(condetails1[2]);
            ((TextView) findViewById(R.id.payTimeStmp1)).setText(condetails1[3]);
            ((TextView) findViewById(R.id.txnId1)).setText(condetails1[4]);
            if (getIntent().getExtras().getString("index_1").toString().contains("@")) {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("@");
            } else {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID2)).setText(condetails2[1]);
            ((TextView) findViewById(R.id.payAmt2)).setText(condetails2[2]);
            ((TextView) findViewById(R.id.payTimeStmp2)).setText(condetails2[3]);
            ((TextView) findViewById(R.id.txnId2)).setText(condetails2[4]);
            if (getIntent().getExtras().getString("index_2").toString().contains("@")) {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("@");
            } else {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID3)).setText(condetails3[1]);
            ((TextView) findViewById(R.id.payAmt3)).setText(condetails3[2]);
            ((TextView) findViewById(R.id.payTimeStmp3)).setText(condetails3[3]);
            ((TextView) findViewById(R.id.txnId3)).setText(condetails3[4]);
            findViewById(R.id.tr_4).setVisibility(8);
            findViewById(R.id.tr_5).setVisibility(8);
            findViewById(R.id.tr_6).setVisibility(8);
        }
        if (size == 4) {
            if (getIntent().getExtras().getString("index_0").toString().contains("@")) {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("@");
            } else {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID1)).setText(condetails1[1]);
            ((TextView) findViewById(R.id.payAmt1)).setText(condetails1[2]);
            ((TextView) findViewById(R.id.payTimeStmp1)).setText(condetails1[3]);
            ((TextView) findViewById(R.id.txnId1)).setText(condetails1[4]);
            if (getIntent().getExtras().getString("index_1").toString().contains("@")) {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("@");
            } else {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID2)).setText(condetails2[1]);
            ((TextView) findViewById(R.id.payAmt2)).setText(condetails2[2]);
            ((TextView) findViewById(R.id.payTimeStmp2)).setText(condetails2[3]);
            ((TextView) findViewById(R.id.txnId2)).setText(condetails2[4]);
            if (getIntent().getExtras().getString("index_2").toString().contains("@")) {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("@");
            } else {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID3)).setText(condetails3[1]);
            ((TextView) findViewById(R.id.payAmt3)).setText(condetails3[2]);
            ((TextView) findViewById(R.id.payTimeStmp3)).setText(condetails3[3]);
            ((TextView) findViewById(R.id.txnId3)).setText(condetails3[4]);
            if (getIntent().getExtras().getString("index_3").toString().contains("@")) {
                condetails4 = getIntent().getExtras().getString("index_3").toString().split("@");
            } else {
                condetails4 = getIntent().getExtras().getString("index_3").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID4)).setText(condetails4[1]);
            ((TextView) findViewById(R.id.payAmt4)).setText(condetails4[2]);
            ((TextView) findViewById(R.id.payTimeStmp4)).setText(condetails4[3]);
            ((TextView) findViewById(R.id.txnId4)).setText(condetails4[4]);
            findViewById(R.id.tr_5).setVisibility(8);
            findViewById(R.id.tr_6).setVisibility(8);
        }
        if (size == 5) {
            if (getIntent().getExtras().getString("index_0").toString().contains("@")) {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("@");
            } else {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID1)).setText(condetails1[1]);
            ((TextView) findViewById(R.id.payAmt1)).setText(condetails1[2]);
            ((TextView) findViewById(R.id.payTimeStmp1)).setText(condetails1[3]);
            ((TextView) findViewById(R.id.txnId1)).setText(condetails1[4]);
            if (getIntent().getExtras().getString("index_1").toString().contains("@")) {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("@");
            } else {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID2)).setText(condetails2[1]);
            ((TextView) findViewById(R.id.payAmt2)).setText(condetails2[2]);
            ((TextView) findViewById(R.id.payTimeStmp2)).setText(condetails2[3]);
            ((TextView) findViewById(R.id.txnId2)).setText(condetails2[4]);
            if (getIntent().getExtras().getString("index_2").toString().contains("@")) {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("@");
            } else {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID3)).setText(condetails3[1]);
            ((TextView) findViewById(R.id.payAmt3)).setText(condetails3[2]);
            ((TextView) findViewById(R.id.payTimeStmp3)).setText(condetails3[3]);
            ((TextView) findViewById(R.id.txnId3)).setText(condetails3[4]);
            if (getIntent().getExtras().getString("index_3").toString().contains("@")) {
                condetails4 = getIntent().getExtras().getString("index_3").toString().split("@");
            } else {
                condetails4 = getIntent().getExtras().getString("index_3").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID4)).setText(condetails4[1]);
            ((TextView) findViewById(R.id.payAmt4)).setText(condetails4[2]);
            ((TextView) findViewById(R.id.payTimeStmp4)).setText(condetails4[3]);
            ((TextView) findViewById(R.id.txnId4)).setText(condetails4[4]);
            if (getIntent().getExtras().getString("index_4").toString().contains("@")) {
                condetails5 = getIntent().getExtras().getString("index_4").toString().split("@");
            } else {
                condetails5 = getIntent().getExtras().getString("index_4").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID5)).setText(condetails5[1]);
            ((TextView) findViewById(R.id.payAmt5)).setText(condetails5[2]);
            ((TextView) findViewById(R.id.payTimeStmp5)).setText(condetails5[3]);
            ((TextView) findViewById(R.id.txnId5)).setText(condetails5[4]);
            findViewById(R.id.tr_6).setVisibility(8);
        }
        if (size == 6) {
            String[] condetails6;
            if (getIntent().getExtras().getString("index_0").toString().contains("@")) {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("@");
            } else {
                condetails1 = getIntent().getExtras().getString("index_0").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID1)).setText(condetails1[1]);
            ((TextView) findViewById(R.id.payAmt1)).setText(condetails1[2]);
            ((TextView) findViewById(R.id.payTimeStmp1)).setText(condetails1[3]);
            ((TextView) findViewById(R.id.txnId1)).setText(condetails1[4]);
            if (getIntent().getExtras().getString("index_1").toString().contains("@")) {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("@");
            } else {
                condetails2 = getIntent().getExtras().getString("index_1").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID2)).setText(condetails2[1]);
            ((TextView) findViewById(R.id.payAmt2)).setText(condetails2[2]);
            ((TextView) findViewById(R.id.payTimeStmp2)).setText(condetails2[3]);
            ((TextView) findViewById(R.id.txnId2)).setText(condetails2[4]);
            if (getIntent().getExtras().getString("index_2").toString().contains("@")) {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("@");
            } else {
                condetails3 = getIntent().getExtras().getString("index_2").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID3)).setText(condetails3[1]);
            ((TextView) findViewById(R.id.payAmt3)).setText(condetails3[2]);
            ((TextView) findViewById(R.id.payTimeStmp3)).setText(condetails3[3]);
            ((TextView) findViewById(R.id.txnId3)).setText(condetails3[4]);
            if (getIntent().getExtras().getString("index_3").toString().contains("@")) {
                condetails4 = getIntent().getExtras().getString("index_3").toString().split("@");
            } else {
                condetails4 = getIntent().getExtras().getString("index_3").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID4)).setText(condetails4[1]);
            ((TextView) findViewById(R.id.payAmt4)).setText(condetails4[2]);
            ((TextView) findViewById(R.id.payTimeStmp4)).setText(condetails4[3]);
            ((TextView) findViewById(R.id.txnId4)).setText(condetails4[4]);
            if (getIntent().getExtras().getString("index_4").toString().contains("@")) {
                condetails5 = getIntent().getExtras().getString("index_4").toString().split("@");
            } else {
                condetails5 = getIntent().getExtras().getString("index_4").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID5)).setText(condetails5[1]);
            ((TextView) findViewById(R.id.payAmt5)).setText(condetails5[2]);
            ((TextView) findViewById(R.id.payTimeStmp5)).setText(condetails5[3]);
            ((TextView) findViewById(R.id.txnId5)).setText(condetails5[4]);
            if (getIntent().getExtras().getString("index_5").toString().contains("@")) {
                condetails6 = getIntent().getExtras().getString("index_5").toString().split("@");
            } else {
                condetails6 = getIntent().getExtras().getString("index_5").toString().split("#");
            }
            ((TextView) findViewById(R.id.conID6)).setText(condetails6[1]);
            ((TextView) findViewById(R.id.payAmt6)).setText(condetails6[2]);
            ((TextView) findViewById(R.id.payTimeStmp6)).setText(condetails6[3]);
            ((TextView) findViewById(R.id.txnId6)).setText(condetails6[4]);
        }
        ((ImageView) findViewById(R.id.create_pdf_1)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoucherActivity.this.pdfgen(((TextView) VoucherActivity.this.findViewById(R.id.conID1)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.payTimeStmp1)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.txnId1)).getText().toString());
            }
        });
        ((ImageView) findViewById(R.id.create_pdf_2)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoucherActivity.this.pdfgen(((TextView) VoucherActivity.this.findViewById(R.id.conID2)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.payTimeStmp2)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.txnId2)).getText().toString());
            }
        });
        ((ImageView) findViewById(R.id.create_pdf_3)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoucherActivity.this.pdfgen(((TextView) VoucherActivity.this.findViewById(R.id.conID3)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.payTimeStmp3)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.txnId3)).getText().toString());
            }
        });
        ((ImageView) findViewById(R.id.create_pdf_4)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoucherActivity.this.pdfgen(((TextView) VoucherActivity.this.findViewById(R.id.conID4)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.payTimeStmp4)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.txnId4)).getText().toString());
            }
        });
        ((ImageView) findViewById(R.id.create_pdf_5)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoucherActivity.this.pdfgen(((TextView) VoucherActivity.this.findViewById(R.id.conID5)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.payTimeStmp5)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.txnId5)).getText().toString());
            }
        });
        ((ImageView) findViewById(R.id.create_pdf_6)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                VoucherActivity.this.pdfgen(((TextView) VoucherActivity.this.findViewById(R.id.conID6)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.payTimeStmp6)).getText().toString(), ((TextView) VoucherActivity.this.findViewById(R.id.txnId6)).getText().toString());
            }
        });
    }

    void pdfgen(String conId, String paytime, String txnId) {
        new GeneratePDFTask().execute(new String[]{conId, paytime, txnId});
    }

    public String FetchVoucher(String[] params) {
        String outputString = "";
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://www.nayandeeptamuli.com/VoucherActivityService/VoucherActivity?wsdl");
            httpPost.setHeader(HttpHeaders.ACCEPT, "text/xml; charset=utf-8");
            httpPost.setHeader("Content-type", "text/xml; charset=utf-8");
            String reqXML = "";
            StringEntity se = new StringEntity("<?xml version=\"1.0\" encoding=\"utf-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><SOAP-ENV:Body><yq1:createVoucher xmlns:yq1=\""><conID>" + params[0] + "</conID>" + "<payTimeStmp>" + params[1] + "</payTimeStmp>" + "<txnId>" + params[2] + "</txnId>" + "</yq1:createVoucher>" + "</SOAP-ENV:Body>" + "</SOAP-ENV:Envelope>");
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
            outputString = ex.toString();
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
}
