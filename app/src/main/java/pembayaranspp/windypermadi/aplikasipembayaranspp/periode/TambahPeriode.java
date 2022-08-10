package pembayaranspp.windypermadi.aplikasipembayaranspp.periode;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TambahPeriode extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    private TextView et_cari;
    private EditText et_tahun, et_bulan, et_nominal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_periode);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Tambah Periode");
        findViewById(R.id.back).setOnClickListener(v -> finish());
        et_tahun = findViewById(R.id.et_tahun);
        et_bulan = findViewById(R.id.et_bulan);
        et_nominal = findViewById(R.id.et_nominal);

        et_bulan.setOnClickListener(v -> {
            popupBulan();
        });

        findViewById(R.id.text_simpan).setOnClickListener(v -> {
            if(koneksi.isConnected(TambahPeriode.this)){
                TambahData();
            } else {
                CustomDialog.noInternet(TambahPeriode.this);
            }
        });
    }

    private void popupBulan() {
        View dialogView = getLayoutInflater().inflate(R.layout.custom_dialog_list_bulan, null);
        final Dialog dialog = new Dialog(TambahPeriode.this);
        dialog.getWindow().setGravity(Gravity.CENTER_VERTICAL);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (metrics.widthPixels / 100) * 100;
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(dialogView);
        ((TextView) dialogView.findViewById(R.id.dialogTitle)).setText("LOKASI");
        ListView listView = dialog.findViewById(R.id.listView);

        final ArrayList<String> sortList = new ArrayList<String>();

        final String lokasi_arr[] = TambahPeriode.this.getResources().getStringArray(R.array.bulan);
        for (String str : lokasi_arr) {
            sortList.add(str);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(TambahPeriode.this, R.layout.listitem,
                sortList);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String str_lokasi = lokasi_arr[position];
            et_bulan.setText(str_lokasi);
            dialog.dismiss();
        });

        dialog.show();
    }

    private void TambahData() {
        AndroidNetworking.get(Connection.CONNECT + "spp_periode.php")
                .addQueryParameter("TAG", "tambah")
                .addQueryParameter("tahun_ajaran", et_tahun.getText().toString().trim())
                .addQueryParameter("bulan", et_bulan.getText().toString().trim())
                .addQueryParameter("nominal_spp", et_nominal.getText().toString().trim())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successDialog(TambahPeriode.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(TambahPeriode.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(TambahPeriode.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
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
            Intent i = new Intent(TambahPeriode.this, ListPeriodeActivity.class);
            startActivity(i);
            finish();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}