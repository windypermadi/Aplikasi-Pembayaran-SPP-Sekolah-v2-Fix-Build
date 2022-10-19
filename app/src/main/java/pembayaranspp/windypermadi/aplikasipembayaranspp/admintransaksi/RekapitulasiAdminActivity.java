package pembayaranspp.windypermadi.aplikasipembayaranspp.admintransaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dandyakbar.aplikasipembayaranspp.R;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.TambahPembayaranTransaksiInvoice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RekapitulasiAdminActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private TextView et_cari;
    private EditText et_tahun, et_bulan;
    private TextView text_siswa, text_transaksi, text_total;

    ArrayList<HashMap<String, String>> dataTahun = new ArrayList<>();
    ArrayList<HashMap<String, String>> dataBulan = new ArrayList<>();

    String jumlah_transaksi, jumlah_siswa, total_pembayaran_format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekapitulasi_admin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Rekapitulasi SPP");
        et_tahun = findViewById(R.id.et_tahun);
        et_bulan = findViewById(R.id.et_bulan);
        text_siswa = findViewById(R.id.text_siswa);
        text_transaksi = findViewById(R.id.text_transaksi);
        text_total = findViewById(R.id.text_total);

        LoadLaporan();
        LoadProvinsi();

        et_tahun.setOnClickListener(v -> {
            popup_provinsi();
        });
        et_bulan.setOnClickListener(v -> {
            popup_bulan();
        });

        findViewById(R.id.text_tutup).setOnClickListener(view -> {
            Intent x = new Intent(RekapitulasiAdminActivity.this, WebviewActivity.class);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            x.putExtra("tahun", et_tahun.getText().toString().trim());
            x.putExtra("bulan", et_bulan.getText().toString().trim());
            startActivity(x);
        });
        findViewById(R.id.text_simpan).setOnClickListener(v -> {
            if(koneksi.isConnected(RekapitulasiAdminActivity.this)){
                LoadLaporan();
            } else {
                CustomDialog.noInternet(RekapitulasiAdminActivity.this);
            }
        });
    }

    private void LoadProvinsi() {
        dataTahun.clear();
        AndroidNetworking.get(Connection.CONNECT + "spp_laporan.php")
                .addQueryParameter("TAG", "list_tahun")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put("tahun_ajaran", responses.optString("tahun_ajaran"));

                                dataTahun.add(map);
                            }

                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                    }
                });
    }

    private void popup_provinsi() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RekapitulasiAdminActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.list_kategori, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kategori = dialogView.findViewById(R.id.lv_kategori);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataTahun, R.layout.custom_list_jenis,
                new String[]{"tahun_ajaran"},
                new int[]{R.id.text_nama});
        lv_kategori.setAdapter(simpleAdapter);
        lv_kategori.setOnItemClickListener((parent, view, position, id) -> {
            String nama_kategori = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            et_tahun.setText(nama_kategori);
            LoadBulan(nama_kategori);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void LoadBulan(String tahun) {
        customProgress.showProgress(this, false);
        dataBulan.clear();
        AndroidNetworking.get(Connection.CONNECT + "spp_laporan.php")
                .addQueryParameter("TAG", "list_bulan")
                .addQueryParameter("tahun", tahun)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put("bulan", responses.optString("bulan"));

                                dataBulan.add(map);
                            }

                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                    }
                });
    }

    private void popup_bulan() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(RekapitulasiAdminActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.list_kategori, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kategori = dialogView.findViewById(R.id.lv_kategori);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataBulan, R.layout.custom_list_jenis,
                new String[]{"bulan"},
                new int[]{R.id.text_nama});
        lv_kategori.setAdapter(simpleAdapter);
        lv_kategori.setOnItemClickListener((parent, view, position, id) -> {
            String nama_kategori = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            et_bulan.setText(nama_kategori);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void LoadLaporan() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_laporan.php")
                .addQueryParameter("TAG", "laporan")
                .addQueryParameter("tahun_ajaran", et_tahun.getText().toString().trim())
                .addQueryParameter("bulan", et_bulan.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        jumlah_transaksi = response.optString("jumlah_transaksi");
                        jumlah_siswa = response.optString("jumlah_siswa");
                        total_pembayaran_format = response.optString("total_pembayaran_format");

                        text_siswa.setText(jumlah_siswa + " Siswa");
                        text_transaksi.setText(jumlah_transaksi + " Transaksi");
                        text_total.setText(total_pembayaran_format);
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                    }
                });
    }
}