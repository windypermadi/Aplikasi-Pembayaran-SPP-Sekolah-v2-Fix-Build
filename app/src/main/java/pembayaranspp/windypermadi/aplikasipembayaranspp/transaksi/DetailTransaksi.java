package pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dandyakbar.aplikasipembayaranspp.R;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;

import org.json.JSONObject;

public class DetailTransaksi extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private TextView et_cari;
    String idtransaksi, metode;
    String nis, nama, nama_kelas, tahun_ajaran, bulan, jumlah_pembayaran;
    TextView text_nis, text_nama, text_kelas, text_tahun, text_bulan, text_total, text_metode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Detail Pembayaran");

        Intent i = getIntent();
        idtransaksi = i.getStringExtra("idtransaksi");

        text_nis = findViewById(R.id.text_nis);
        text_nama = findViewById(R.id.text_nama);
        text_kelas = findViewById(R.id.text_kelas);
        text_tahun = findViewById(R.id.text_tahun);
        text_bulan = findViewById(R.id.text_bulan);
        text_total = findViewById(R.id.text_total);
        text_metode = findViewById(R.id.text_metode);

        LoadData();
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "detail")
                .addQueryParameter("idtransaksi", idtransaksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        nis = response.optString("nis");
                        nama = response.optString("nama");
                        nama_kelas = response.optString("nama_kelas");
                        tahun_ajaran = response.optString("tahun_ajaran");
                        bulan = response.optString("bulan");
                        jumlah_pembayaran = response.optString("jumlah_pembayaran");
                        metode = response.optString("metode_pembayaran");

                        text_nis.setText(nis);
                        text_nama.setText(nama);
                        text_kelas.setText(nama_kelas);
                        text_tahun.setText(tahun_ajaran);
                        text_bulan.setText(bulan);
                        text_total.setText(jumlah_pembayaran);
                        text_metode.setText(metode);
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            customProgress.hideProgress();
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(DetailTransaksi.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }
}