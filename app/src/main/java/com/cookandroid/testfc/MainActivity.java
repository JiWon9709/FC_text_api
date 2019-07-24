package com.cookandroid.testfc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText edittext;
    TextView single_result;
    Button submit;
    String tag = "product search";
    String prdlstReportNo ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edittext = (EditText) findViewById(R.id.edittext);
        single_result = (TextView) findViewById(R.id.single_result);
        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String srvUrl = "http://apis.data.go.kr/B553748/CertImgListService/getCertImgListService";
                String srvKey = "O1eRMhYRV%2FmwW1rCavaxORY44We%2FrqFL%2FBPUR9iB9%2FuPzH42fKkJ9oHRD58hxsvT5WYAWXaLXj51Y4%2Bgluldsg%3D%3D";
                String strSrch = edittext.getText().toString();
                //String strUrl = srvUrl + "?ServiceKey=" + srvKey + "&prdlstReportNo=" + strSrch;
                String strUrl = srvUrl + "?ServiceKey=" + srvKey + "&prdlstNm=" + strSrch;

                new DownloadWebpageTask().execute(strUrl);
            }
        });
    }

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                return (String) downloadUrl((String) urls[0]);
            } catch (IOException e) {
                return "==>다운로드 실패";
            }
        }
        protected void onPostExecute(String result) {
            Log.d(tag, result);
            //tv.append(result + "\n");
            //tv.append("========== 파싱 결과 ==========\n");
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                boolean p = false, rm = false, all = false;
                single_result.setText("");

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                        ;
                    } else if (eventType == XmlPullParser.START_TAG) {
                        String tag_name = xpp.getName();
                        if (tag_name.equals("prdlstNm"))
                            p = true;
                        if (tag_name.equals("rawmtrl"))
                            rm = true;
                        if (tag_name.equals("allergy"))
                            all = true;
                    } else if (eventType == XmlPullParser.TEXT) {
                        if (p) {
                            //String content = xpp.getText();
                            single_result.setText(xpp.getText());
                            p = false;
                        }
                        if (rm) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText() + "\n");
                            rm = false;
                        }
                        if (all) {
                            //String content = xpp.getText();
                            single_result.append(xpp.getText() + "\n");
                            all = false;
                        }
                    } else if (eventType == XmlPullParser.END_TAG) {
                        ;
                    }
                    eventType = xpp.next();
                } // while
            } catch (Exception e) {
                //tv.setText("\n"+e.getMessage());
            }
          /*  if(single_result.getText().length() == 0){//빈값이 넘어올때의 처리
                single_result.setText("발령된 여행경보가 없습니다.");
            }*/
        }

        private String downloadUrl(String myurl) throws IOException {
            HttpURLConnection conn = null;
            try {
                Log.d(tag, "downloadUrl : " + myurl);
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line = null;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }

                return page;
            } catch (Exception e) {
                return " ";
            } finally {
                conn.disconnect();
            }
        }
    }
}
