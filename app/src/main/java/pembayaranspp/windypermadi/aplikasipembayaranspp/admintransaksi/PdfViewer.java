package pembayaranspp.windypermadi.aplikasipembayaranspp.admintransaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.model.FileLoader;
import com.dandyakbar.aplikasipembayaranspp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.listener.OnRenderListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;
import java.util.Objects;

public class PdfViewer extends AppCompatActivity {
    ProgressBar pdfViewProgressBar;
    public ProgressDialog pDialog;
    PDFView pdfView;
    String tahun, bulan, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        pdfView = findViewById(R.id.pdfView);
        pdfViewProgressBar = findViewById(R.id.pdfViewProgressBar);
//        pdfViewProgressBar.setVisibility(View.VISIBLE);
//        pDialog = new ProgressDialog(this);
//        pDialog.setCancelable(false);

        Intent i = getIntent();
        tahun = i.getStringExtra("tahun");
        bulan = i.getStringExtra("bulan");
        url = "https://demo.windypermadi.com/pembayaranspp/api/download_laporan.php?tahun_ajaran="+tahun+"&bulan="+bulan;

        pdfView.fromUri(Uri.parse(url))
                .enableSwipe(true)
                .enableDoubletap(false)
                .load();
    }
}