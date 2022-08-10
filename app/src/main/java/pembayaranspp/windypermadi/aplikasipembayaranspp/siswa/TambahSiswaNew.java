package pembayaranspp.windypermadi.aplikasipembayaranspp.siswa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;

public class TambahSiswaNew extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private TextView et_cari;
    String jenis_kelamin, idkelas;
    private Spinner listItem;
    private EditText et_nis, et_nama, et_alamat, et_telp, et_nama_ayah, et_nama_ibu, et_kelas;
    ArrayList<HashMap<String, String>> dataTahun = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_siswa_new);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Tambah Siswa");

        et_nis = findViewById(R.id.et_nis);
        et_nama = findViewById(R.id.et_nama);
        listItem = findViewById(R.id.listItem);
        et_alamat = findViewById(R.id.et_alamat);
        et_nama_ayah = findViewById(R.id.et_nama_ayah);
        et_nama_ibu = findViewById(R.id.et_nama_ibu);
        et_telp = findViewById(R.id.et_telp);
        et_kelas = findViewById(R.id.et_kelas);

        ActionButton();
        LoadKelas();
    }
    private void ActionButton() {
        listItem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                jenis_kelamin = listItem.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        findViewById(R.id.text_simpan).setOnClickListener(view -> {
            if (koneksi.isConnected(TambahSiswaNew.this)) {
                TambahData();
            } else {
                CustomDialog.noInternet(TambahSiswaNew.this);
            }
        });
        findViewById(R.id.et_kelas).setOnClickListener(v -> {
            popup_provinsi();
        });
    }

    private void LoadKelas() {
        dataTahun.clear();
        AndroidNetworking.get(Connection.CONNECT + "spp_kelas.php")
                .addQueryParameter("TAG", "list")
                .addQueryParameter("limit", "0")
                .addQueryParameter("offset", "100")
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                HashMap<String, String> map = new HashMap<>();

                                map.put("idkelas", responses.optString("idkelas"));
                                map.put("nama_kelas", responses.optString("nama_kelas"));

                                dataTahun.add(map);
                            }

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
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TambahSiswaNew.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.list_kategori, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alertDialog = dialogBuilder.create();
        ListView lv_kategori = dialogView.findViewById(R.id.lv_kategori);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, dataTahun, R.layout.custom_list_jenis,
                new String[]{"idkelas", "nama_kelas"},
                new int[]{R.id.text_id, R.id.text_nama});
        lv_kategori.setAdapter(simpleAdapter);
        lv_kategori.setOnItemClickListener((parent, view, position, id) -> {
            idkelas = ((TextView) view.findViewById(R.id.text_id)).getText().toString();
            String nama_kategori = ((TextView) view.findViewById(R.id.text_nama)).getText().toString();
            et_kelas.setText(nama_kategori);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    private void TambahData() {
        AndroidNetworking.get(Connection.CONNECT + "spp_siswa.php")
                .addQueryParameter("TAG", "tambah")
                .addQueryParameter("idkelas", idkelas)
                .addQueryParameter("nis", et_nis.getText().toString().trim())
                .addQueryParameter("nama", et_nama.getText().toString().trim())
                .addQueryParameter("jenis_kelamin", jenis_kelamin)
                .addQueryParameter("alamat", et_alamat.getText().toString().trim())
                .addQueryParameter("notelp", et_telp.getText().toString().trim())
                .addQueryParameter("nama_ayah", et_nama_ayah.getText().toString().trim())
                .addQueryParameter("nama_ibu", et_nama_ibu.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successDialog(TambahSiswaNew.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(TambahSiswaNew.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(TambahSiswaNew.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
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
            Intent i = new Intent(TambahSiswaNew.this, ListSiswa.class);
            i.putExtra("idkelas", idkelas);
            startActivity(i);
            finish();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}