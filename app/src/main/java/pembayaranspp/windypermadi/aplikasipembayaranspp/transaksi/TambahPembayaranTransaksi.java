package pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MainSiswa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TambahPembayaranTransaksi extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private TextView et_cari;
    String nis, nama, kelas;
    String metode_pembayaran;
    private Spinner listItem;
    private EditText et_nis, et_nama, et_kelas, et_tahun, et_bulan, et_total;
    private TextView text_simpan;

    ArrayList<HashMap<String, String>> dataTahun = new ArrayList<>();
    ArrayList<HashMap<String, String>> dataBulan = new ArrayList<>();

    String idperiode, idtransaksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pembayaran_transaksi);

        listItem = findViewById(R.id.listItem);
        et_nis = findViewById(R.id.et_nis);
        et_nama = findViewById(R.id.et_nama);
        et_kelas = findViewById(R.id.et_kelas);
        et_tahun = findViewById(R.id.et_tahun);
        et_bulan = findViewById(R.id.et_bulan);
        et_total = findViewById(R.id.et_total);
        text_simpan = findViewById(R.id.text_simpan);

        et_cari = findViewById(R.id.et_cari);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        et_cari.setText("Pembayaran SPP");

        ActionButton();
        LoadData();
    }

    private void ActionButton() {
        listItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                metode_pembayaran = listItem.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        et_tahun.setOnClickListener(view -> {
            popup_provinsi();
        });
        et_bulan.setOnClickListener(view -> {
            popup_bulan();
        });
        text_simpan.setOnClickListener(view -> {
            if (koneksi.isConnected(this)){
                TambahData();
            } else {
                CustomDialog.noInternet(TambahPembayaranTransaksi.this);
            }
        });
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_siswa.php")
                .addQueryParameter("TAG", "detail")
                .addQueryParameter("idsiswa", MainSiswa.iduser)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        nis = response.optString("nis");
                        nama = response.optString("nama");
                        kelas = response.optString("nama_kelas");

                        et_nis.setText(nis);
                        et_nama.setText(nama);
                        et_kelas.setText(kelas);

                        LoadProvinsi();
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            customProgress.hideProgress();
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(TambahPembayaranTransaksi.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void LoadProvinsi() {
        dataTahun.clear();
        AndroidNetworking.get(Connection.CONNECT + "spp_pembayaran.php")
                .addQueryParameter("TAG", "cekTahun")
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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TambahPembayaranTransaksi.this);
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
            LoadBulan();
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void LoadBulan() {
        customProgress.showProgress(this, false);
        dataBulan.clear();
        AndroidNetworking.get(Connection.CONNECT + "spp_pembayaran.php")
                .addQueryParameter("TAG", "cekBulan")
                .addQueryParameter("tahun", et_tahun.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put("idperiode", responses.optString("idperiode"));
                                map.put("bulan", responses.optString("bulan"));
                                map.put("nominal_spp", responses.optString("nominal_spp"));

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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TambahPembayaranTransaksi.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.list_kategori, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kategori = dialogView.findViewById(R.id.lv_kategori);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataBulan, R.layout.custom_list_jenis,
                new String[]{"idperiode","bulan", "nominal_spp"},
                new int[]{R.id.text_id, R.id.text_nama, R.id.text_spp});
        lv_kategori.setAdapter(simpleAdapter);
        lv_kategori.setOnItemClickListener((parent, view, position, id) -> {
            String nama_kategori = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            String spp = ((TextView) view.findViewById(R.id.text_spp)).getText().toString();
            idperiode = ((TextView) view.findViewById(R.id.text_id)).getText().toString();
            et_bulan.setText(nama_kategori);
            et_total.setText(spp);

            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void TambahData() {
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "tambah")
                .addQueryParameter("idsiswa", MainSiswa.iduser)
                .addQueryParameter("idperiode", idperiode)
                .addQueryParameter("metode_pembayaran", metode_pembayaran)
                .addQueryParameter("bulan", et_bulan.getText().toString().trim())
                .addQueryParameter("jumlah_pembayaran", et_total.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        idtransaksi = response.optString("idtransaksi");
                        successDialog(TambahPembayaranTransaksi.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(TambahPembayaranTransaksi.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(TambahPembayaranTransaksi.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
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
            Intent i = new Intent(TambahPembayaranTransaksi.this, TambahPembayaranTransaksiInvoice.class);
//            Intent i = new Intent(TambahPembayaranTransaksi.this, TambahPembayaranTransaksiInvoice.class);
            i.putExtra("idtransaksi", idtransaksi);
            startActivity(i);
            finish();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}