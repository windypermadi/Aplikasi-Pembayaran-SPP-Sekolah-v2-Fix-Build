package pembayaranspp.windypermadi.aplikasipembayaranspp.admintransaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.dandyakbar.aplikasipembayaranspp.R;

public class WebviewActivity extends AppCompatActivity {
    WebView webView;
    ProgressBar progressBar;
    String tahun, bulan, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent i = getIntent();
        tahun = i.getStringExtra("tahun");
        bulan = i.getStringExtra("bulan");
        url = "https://demo.windypermadi.com/pembayaranspp/api/download_laporan.php?tahun_ajaran="+tahun+"&bulan="+bulan;

        webView = (WebView) findViewById(R.id.webView);
        progressBar=(ProgressBar) findViewById(R.id.progressBar2);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new MyAppWebViewClient());
    }

    @Override
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public class MyAppWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Uri.parse(url).getHost().endsWith("saptaji.com")) {
                return false;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }
    }

//    public class myWebclient extends WebViewClient {
//        @Override
//        public void onPageFinished(WebView view, String url) {
//            super.onPageFinished(view, url);
//            progressBar.setVisibility(View.GONE);
//        }
//
//        @Override
//        public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            super.onPageStarted(view, url, favicon);
//        }
//
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            view.loadUrl(url);
//            return super.shouldOverrideUrlLoading(view, url);
//        }
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if((keyCode==KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
//            webView.goBack();
//            return true;
//        }
//
//        return super.onKeyDown(keyCode, event);
//    }
}