package pembayaranspp.windypermadi.aplikasipembayaranspp.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.dandyakbar.aplikasipembayaranspp.R;

public class InformasiActivity extends AppCompatActivity {

    TextView et_cari;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informasi);

        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Informasi Pembayaran");
        findViewById(R.id.back).setOnClickListener(v -> finish());
    }
}