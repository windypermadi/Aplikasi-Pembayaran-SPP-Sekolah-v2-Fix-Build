package pembayaranspp.windypermadi.aplikasipembayaranspp.siswa;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dandyakbar.aplikasipembayaranspp.R;

import pembayaranspp.windypermadi.aplikasipembayaranspp.auth.BioSiswaActivity;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;
import pembayaranspp.windypermadi.aplikasipembayaranspp.model.SiswaModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListSiswa extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private LinearLayout ly00, ly11, ly22;
    private RecyclerView rv_data;
    List<SiswaModel> SiswaModel;
    int limit = 0, offset = 10;
    private TextView text_more;
    private SwipeRefreshLayout swipe_refresh;
    private EditText text_search;
    private TextView et_cari;
    String idkelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_siswa);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Daftar Siswa");

        Intent i = getIntent();
        idkelas = i.getStringExtra("idkelas");

        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);
        ly22 = findViewById(R.id.ly22);
        rv_data = findViewById(R.id.rv_data);
        text_more = findViewById(R.id.text_more);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        text_search = findViewById(R.id.text_search);

        SiswaModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        ActiomButton();
    }

    private void ActiomButton() {
        findViewById(R.id.add).setOnClickListener(v -> {
            Intent i = new Intent(ListSiswa.this, TambahSiswa.class);
            i.putExtra("idkelas", idkelas);
            startActivity(i);
        });
        findViewById(R.id.back).setOnClickListener(v -> finish());
        text_search.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                ly11.setVisibility(View.GONE);
                ly00.setVisibility(View.VISIBLE);
                ly22.setVisibility(View.GONE);
                limit = 0;
                SiswaModel.clear();
                LoadPegawai(limit, offset, text_search.getText().toString().trim());
                return true;
            }
            return false;
        });
        text_more.setOnClickListener(v -> {
            limit = limit + offset;
            LoadPegawai(limit, offset, text_search.getText().toString().trim());
        });
        swipe_refresh.setOnRefreshListener(() -> {
            ly11.setVisibility(View.GONE);
            ly00.setVisibility(View.VISIBLE);
            ly22.setVisibility(View.GONE);
            text_search.setText("");
            limit = 0;
            SiswaModel.clear();
            LoadPegawai(limit, offset, text_search.getText().toString().trim());
        });
    }

    @Override
    protected void onResume() {
        ly11.setVisibility(View.GONE);
        ly00.setVisibility(View.VISIBLE);
        ly22.setVisibility(View.GONE);
        SiswaModel.clear();
        text_search.setText("");
        limit = 0;
        LoadPegawai(limit, offset, text_search.getText().toString().trim());
        super.onResume();
    }

    private void LoadPegawai(int limit, int offset, String cari) {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_siswa.php")
                .addQueryParameter("TAG", "list")
                .addQueryParameter("idkelas", idkelas)
                .addQueryParameter("limit", String.valueOf(limit))
                .addQueryParameter("offset", String.valueOf(offset))
                .addQueryParameter("q", cari)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                SiswaModel bk = new SiswaModel(
                                        responses.getString("idsiswa"),
                                        responses.getString("nis"),
                                        responses.getString("nama"),
                                        responses.getString("nama_kelas"));
                                SiswaModel.add(bk);
                            }

                            PegawaiAdapter adapter = new PegawaiAdapter(getApplicationContext(), SiswaModel);
                            rv_data.setAdapter(adapter);

                            ly00.setVisibility(View.GONE);
                            ly11.setVisibility(View.VISIBLE);
                            ly22.setVisibility(View.GONE);
                            if (adapter.getItemCount() < offset) {
                                text_more.setVisibility(View.GONE);
                            } else {
                                text_more.setVisibility(View.VISIBLE);
                            }

                            swipe_refresh.setRefreshing(false);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ly11.setVisibility(View.GONE);
                            ly00.setVisibility(View.GONE);
                            ly22.setVisibility(View.GONE);
                            swipe_refresh.setRefreshing(false);
//                            hideDialog();
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                String kode = body.optString("kode");
                                if (kode.equals("0")) {
                                    //tidak ada data
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.GONE);
                                    ly22.setVisibility(View.VISIBLE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ListSiswa.this, body.optString("pesan"));
                                } else if (kode.equals("1")) {
                                    //mencapai batas limit
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.VISIBLE);
                                    ly22.setVisibility(View.GONE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ListSiswa.this, body.optString("pesan"));
                                } else {
                                    //2 tiket dibatalkan
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.GONE);
                                    ly22.setVisibility(View.VISIBLE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ListSiswa.this, body.optString("pesan"));
                                }
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(ListSiswa.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class PegawaiAdapter extends RecyclerView.Adapter<PegawaiAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<SiswaModel> SiswaModel;

        PegawaiAdapter(Context mCtx, List<SiswaModel> SiswaModel) {
            this.mCtx = mCtx;
            this.SiswaModel = SiswaModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_siswa, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final SiswaModel kelas = SiswaModel.get(i);
            holder.text_nama.setText(kelas.getNama());
            holder.text_nis.setText(kelas.getNis());
            holder.text_level.setText(kelas.getNama_kelas());
            holder.cv.setOnClickListener(v -> {
                Intent x = new Intent(mCtx, BioSiswaActivity.class);
                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                x.putExtra("idsiswa", kelas.getIdsiswa());
                mCtx.startActivity(x);
            });
        }

        @Override
        public int getItemCount() {
            return SiswaModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_nama, text_level, text_nis;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_level = itemView.findViewById(R.id.text_level);
                text_nis = itemView.findViewById(R.id.text_nis);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }

    private void HapusData(String idkelas) {
        AndroidNetworking.get(Connection.CONNECT + "spp_kelas.php")
                .addQueryParameter("TAG", "hapus")
                .addQueryParameter("idkelas", idkelas)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        successDialog(ListSiswa.this, response.optString("pesan"));
                    }

                    @Override
                    public void onError(ANError error) {
                        customProgress.hideProgress();
                        if (error.getErrorCode() == 400) {
                            try {
                                JSONObject body = new JSONObject(error.getErrorBody());
                                CustomDialog.errorDialog(ListSiswa.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            CustomDialog.errorDialog(ListSiswa.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
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
            onResume();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}