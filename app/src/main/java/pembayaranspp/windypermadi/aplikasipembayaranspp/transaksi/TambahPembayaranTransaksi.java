package pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MainSiswa;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class TambahPembayaranTransaksi extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private TextView et_cari;
    String nis, nama, kelas;
    String metode_pembayaran;
    private Spinner listItem;
    private EditText et_nis, et_nama, et_kelas, et_tahun, et_bulan, et_total, et_tanggal_bayar, et_nama_rekening;
    private TextView text_simpan;

    ArrayList<HashMap<String, String>> dataTahun = new ArrayList<>();
    ArrayList<HashMap<String, String>> dataBulan = new ArrayList<>();

    String idperiode, idtransaksi, idspp;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;

//    DatabaseReference dbref;

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
        et_tanggal_bayar = findViewById(R.id.et_tanggal_bayar);
        et_nama_rekening = findViewById(R.id.et_nama_rekening);
        text_simpan = findViewById(R.id.text_simpan);

        et_cari = findViewById(R.id.et_cari);
        findViewById(R.id.back).setOnClickListener(view -> finish());
        et_cari.setText("Pembayaran SPP");

        myCalendar = Calendar.getInstance();
        date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "yyyy-MM-dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            et_tanggal_bayar.setText(sdf.format(myCalendar.getTime()));
        };

        ActionButton();
        LoadData();

        Intent i = getIntent();
        idspp = i.getStringExtra("idspp");
        String bulan = i.getStringExtra("bulan");
        String total = i.getStringExtra("total");
        String tahun_ajaran = i.getStringExtra("tahun_ajaran");
        idperiode = i.getStringExtra("id_periode");
        et_bulan.setText(bulan);
        et_total.setText(total);
        et_tahun.setText(tahun_ajaran);
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
        et_tanggal_bayar.setOnClickListener(v -> {
            new DatePickerDialog(TambahPembayaranTransaksi.this, date,
                    myCalendar.get(Calendar.YEAR),
                    myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        text_simpan.setOnClickListener(view -> {
            if (koneksi.isConnected(this)) {
                TambahData();
            } else {
                CustomDialog.noInternet(TambahPembayaranTransaksi.this);
            }
        });
    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar newDate = Calendar.getInstance();
            newDate.set(year, month, dayOfMonth);
            et_tanggal_bayar.setText(dateFormatter.format(newDate.getTime()));
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
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
                new String[]{"idperiode", "bulan", "nominal_spp"},
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
                .addQueryParameter("idspp", idspp)
                .addQueryParameter("metode_pembayaran", metode_pembayaran)
                .addQueryParameter("nama_rekening_pembayar", et_nama_rekening.getText().toString().trim())
                .addQueryParameter("tanggal_bayar", et_tanggal_bayar.getText().toString().trim())
                .addQueryParameter("bulan", et_bulan.getText().toString().trim())
                .addQueryParameter("jumlah_pembayaran", et_total.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        idtransaksi = response.optString("idtransaksi");
                        addFirebase(idtransaksi);
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

    private void addFirebase(String idtransaksi) {
        //instansiasi database firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Referensi database yang dituju
        DatabaseReference myRef = database.getReference("transaksi_spp").child(idtransaksi);
        //memberi nilai pada referensi yang dituju
        myRef.child("idperiode").setValue(idperiode);
        myRef.child("idspp").setValue(idspp);
        myRef.child("metode_pembayaran").setValue(metode_pembayaran);
        myRef.child("nama_rekening_pembayar").setValue(et_nama_rekening.getText().toString().trim());
        myRef.child("tanggal_bayar").setValue(et_tanggal_bayar.getText().toString().trim());
        myRef.child("bulan").setValue(et_bulan.getText().toString().trim());
        myRef.child("jumlah_pembayaran").setValue(et_total.getText().toString().trim());
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