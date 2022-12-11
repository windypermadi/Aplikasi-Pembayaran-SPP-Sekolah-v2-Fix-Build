package pembayaranspp.windypermadi.aplikasipembayaranspp.admintransaksi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dandyakbar.aplikasipembayaranspp.R;

public class WebviewActivity extends AppCompatActivity {
    WebView webView;
    String tahun, bulan, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        Intent i = getIntent();
        tahun = i.getStringExtra("tahun");
        bulan = i.getStringExtra("bulan");
        url = "https://demo.windypermadi.com/pembayaranspp/api/laporan_view.php?tahun_ajaran="+tahun+"&bulan="+bulan;

        String urlString = "http://mysuperwebsite";
        Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage("com.android.chrome");
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            // Chrome browser presumably not installed and open Kindle Browser
            intent.setPackage("com.amazon.cloud9");
            startActivity(intent);
        }

//        checkDownloadPermission();
//        webView = findViewById(R.id.webView);
//        webView.loadUrl(url);
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//        });
//        webView.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype));
//                request.setDescription("Downloading file...");
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimetype));
//                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
//                dm.enqueue(request);
//                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
//                registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
//            }
//            BroadcastReceiver onComplete = new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    Toast.makeText(getApplicationContext(), "Downloading Complete", Toast.LENGTH_SHORT).show();
//                }
//            };
//        });
    }

    private void checkDownloadPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(WebviewActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(WebviewActivity.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(WebviewActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
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
            if(Uri.parse(url).getHost().endsWith("windypermadi.com")) {
                return false;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            view.getContext().startActivity(intent);
            return true;
        }
    }

    public class myWebclient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode==KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}