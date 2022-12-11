package pembayaranspp.windypermadi.aplikasipembayaranspp.admintransaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.dandyakbar.aplikasipembayaranspp.R;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.ZoomImageActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailApprovalActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private TextView et_cari;
    String idtransaksi;
    String nis, nama, nama_kelas, tahun_ajaran, bulan, jumlah_pembayaran, gambar, metode;
    TextView text_nis, text_nama, text_kelas, text_tahun, text_bulan, text_total, text_metode;
    ImageView img_upload;
    TextView text_tolak, text_setujui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_approval);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Detail Persetujuan");

        Intent i = getIntent();
        idtransaksi = i.getStringExtra("idtransaksi");

        text_nis = findViewById(R.id.text_nis);
        text_nama = findViewById(R.id.text_nama);
        text_kelas = findViewById(R.id.text_kelas);
        text_tahun = findViewById(R.id.text_tahun);
        text_bulan = findViewById(R.id.text_bulan);
        text_total = findViewById(R.id.text_total);
        img_upload = findViewById(R.id.img_upload);
        text_tolak = findViewById(R.id.text_tolak);
        text_setujui = findViewById(R.id.text_setujui);
        text_metode = findViewById(R.id.text_metode);

        LoadData();
        ActionButton();
    }

    private void ActionButton() {
        text_setujui.setOnClickListener(v -> {
            TambahData();
        });
        text_tolak.setOnClickListener(v -> {
            TolakData();
            successDialog(DetailApprovalActivity.this, "Bukti transaksi ini telah ditolak! Segera hubungi murid ini untuk mendapatkan bantuan!");
        });
        img_upload.setOnClickListener(v -> {
            Intent x = new Intent(DetailApprovalActivity.this, ZoomImageActivity.class);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            x.putExtra("idtransaksi", idtransaksi);
            startActivity(x);
        });
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
                        gambar = response.optString("file_pembayaran");
                        metode = response.optString("metode_pembayaran");

                        Glide.with(getApplicationContext())
                                .load(gambar)
                                .error(R.drawable.logo)
                                .into(img_upload);
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
                            CustomDialog.errorDialog(DetailApprovalActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void TambahData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "approve_pembayaran")
                .addQueryParameter("invoice", idtransaksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        successDialog(DetailApprovalActivity.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(DetailApprovalActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(DetailApprovalActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void TolakData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "tolak_pembayaran")
                .addQueryParameter("invoice", idtransaksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        successDialog(DetailApprovalActivity.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(DetailApprovalActivity.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(DetailApprovalActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public void successDialog(final Context context, final String alertText) {
        final View inflater = LayoutInflater.from(context).inflate(R.layout.custom_success_dialog, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context).setView(inflater);
        builder.setCancelable(false);
        final TextView ket = inflater.findViewById(R.id.keterangan);
        ket.setText(alertText);
        final AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparan);
        inflater.findViewById(R.id.ok).setOnClickListener(v -> {
            Intent x = new Intent(DetailApprovalActivity.this, ApprovalActivity.class);
            x.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(x);
            finish();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}