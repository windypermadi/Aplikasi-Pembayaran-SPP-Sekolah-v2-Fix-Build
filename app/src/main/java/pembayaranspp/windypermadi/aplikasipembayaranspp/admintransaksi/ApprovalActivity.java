package pembayaranspp.windypermadi.aplikasipembayaranspp.admintransaksi;

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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dandyakbar.aplikasipembayaranspp.R;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;
import pembayaranspp.windypermadi.aplikasipembayaranspp.model.TransaksiModel;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.DetailTransaksi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApprovalActivity extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private LinearLayout ly00, ly11, ly22;
    private RecyclerView rv_data;
    List<pembayaranspp.windypermadi.aplikasipembayaranspp.model.TransaksiModel> TransaksiModel;
    int limit = 0, offset = 10;
    private TextView text_more;
    private SwipeRefreshLayout swipe_refresh;
    private TextView et_cari;
    String idkelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//  set status text dark
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Persetujuan Pembayaran SPP");

        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);
        ly22 = findViewById(R.id.ly22);
        rv_data = findViewById(R.id.rv_data);
        text_more = findViewById(R.id.text_more);
        swipe_refresh = findViewById(R.id.swipe_refresh);

        TransaksiModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        ActiomButton();
    }

    private void ActiomButton() {
        findViewById(R.id.back).setOnClickListener(v -> finish());
        text_more.setOnClickListener(v -> {
            limit = limit + offset;
            LoadPegawai(limit, offset);
        });
        swipe_refresh.setOnRefreshListener(() -> {
            ly11.setVisibility(View.GONE);
            ly00.setVisibility(View.VISIBLE);
            ly22.setVisibility(View.GONE);
            limit = 0;
            TransaksiModel.clear();
            LoadPegawai(limit, offset);
        });
        findViewById(R.id.text_riwayat).setOnClickListener(v -> startActivity(new Intent(ApprovalActivity.this, RekapitulasiPembayaranActivity.class)));
    }

    @Override
    protected void onResume() {
        ly11.setVisibility(View.GONE);
        ly00.setVisibility(View.VISIBLE);
        ly22.setVisibility(View.GONE);
        TransaksiModel.clear();
        limit = 0;
        LoadPegawai(limit, offset);
        super.onResume();
    }

    private void LoadPegawai(int limit, int offset) {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "admin_listapprovepersiswa")
                .addQueryParameter("limit", String.valueOf(limit))
                .addQueryParameter("offset", String.valueOf(offset))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                TransaksiModel bk = new TransaksiModel(
                                        responses.getString("idtransaksi"),
                                        responses.getString("invoice"),
                                        responses.getString("nama"),
                                        responses.getString("bulan"),
                                        responses.getString("tahun_ajaran"),
                                        responses.getString("jumlah_pembayaran"),
                                        responses.getString("file_pembayaran"),
                                        responses.getString("status_approve"),
                                        responses.getString("tgl_create"));
                                TransaksiModel.add(bk);
                            }

                            PegawaiAdapter adapter = new PegawaiAdapter(getApplicationContext(), TransaksiModel);
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
                            customProgress.hideProgress();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            customProgress.hideProgress();
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
                                    CustomDialog.errorDialog(ApprovalActivity.this, body.optString("pesan"));
                                } else if (kode.equals("1")) {
                                    //mencapai batas limit
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.VISIBLE);
                                    ly22.setVisibility(View.GONE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ApprovalActivity.this, body.optString("pesan"));
                                } else {
                                    //2 tiket dibatalkan
                                    swipe_refresh.setRefreshing(false);
                                    ly00.setVisibility(View.GONE);
                                    ly11.setVisibility(View.GONE);
                                    ly22.setVisibility(View.VISIBLE);
                                    text_more.setVisibility(View.GONE);
                                    CustomDialog.errorDialog(ApprovalActivity.this, body.optString("pesan"));
                                }
                            } catch (JSONException ignored) {
                            }
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(ApprovalActivity.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class PegawaiAdapter extends RecyclerView.Adapter<PegawaiAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<TransaksiModel> TransaksiModel;

        PegawaiAdapter(Context mCtx, List<TransaksiModel> TransaksiModel) {
            this.mCtx = mCtx;
            this.TransaksiModel = TransaksiModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_transaksi, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final TransaksiModel kelas = TransaksiModel.get(i);
            holder.text_id.setText(kelas.getInvoice());
            holder.text_nama.setText(kelas.getNama());
            holder.text_tanggal.setText(kelas.getBulan() + " | " + kelas.getTahun());
            holder.text_status.setText(kelas.getTgl_create());
            holder.cv.setOnClickListener(v -> {
                if (kelas.getStatus_approve().equals("N")){
                    Intent x = new Intent(mCtx, DetailApprovalActivity.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    x.putExtra("idtransaksi", kelas.getIdtransaksi());
                    mCtx.startActivity(x);
                } else {
                    Intent x = new Intent(mCtx, DetailTransaksi.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    x.putExtra("idtransaksi", kelas.getIdtransaksi());
                    mCtx.startActivity(x);
                }
            });
        }

        @Override
        public int getItemCount() {
            return TransaksiModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_id, text_nama, text_status, text_tanggal;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_nama = itemView.findViewById(R.id.text_nama);
                text_id = itemView.findViewById(R.id.text_id);
                text_status = itemView.findViewById(R.id.text_status);
                text_tanggal = itemView.findViewById(R.id.text_tanggal);
                cv = itemView.findViewById(R.id.cv);
            }
        }
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
//            onResume();
            alertDialog.dismiss();
        });
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alertDialog.show();
    }
}