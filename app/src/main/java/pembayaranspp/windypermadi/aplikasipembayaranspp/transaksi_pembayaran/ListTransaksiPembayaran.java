package pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi_pembayaran;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.dandyakbar.aplikasipembayaranspp.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

import pembayaranspp.windypermadi.aplikasipembayaranspp.auth.BioSiswaActivity;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MainSiswa;
import pembayaranspp.windypermadi.aplikasipembayaranspp.model.TransaksiPembayaranModel;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.TambahPembayaranTransaksi;

public class ListTransaksiPembayaran extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private LinearLayout ly00, ly11, ly22;
    private RecyclerView rv_data;
    private TextView text1, text2;
    List<TransaksiPembayaranModel> TransaksiPembayaranModel;
    private SwipeRefreshLayout swipe_refresh;
    private TextView et_cari;
    String idkelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaksi_pembayaran);

        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Transaksi");

        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);
        rv_data = findViewById(R.id.rv_data);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);

        TransaksiPembayaranModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        LoadTagihan();

        ActionButton();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void ActionButton() {
        findViewById(R.id.back).setOnClickListener(v -> finish());
        swipe_refresh.setOnRefreshListener(() -> {
            ly11.setVisibility(View.GONE);
            ly00.setVisibility(View.VISIBLE);
            TransaksiPembayaranModel.clear();
            LoadTagihan();
        });
        text1.setOnClickListener(v -> {
            LoadTagihan();
            text1.setBackground(getResources().getDrawable(R.drawable.btn_green));
            text2.setBackgroundColor(Color.WHITE);
        });
        text2.setOnClickListener(v -> {
            text1.setBackgroundColor(Color.WHITE);
            text2.setBackground(getResources().getDrawable(R.drawable.btn_green));
        });
    }

    private void LoadTagihan() {
        ly11.setVisibility(View.GONE);
        ly00.setVisibility(View.VISIBLE);
        TransaksiPembayaranModel.clear();
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "listbelumdibayar")
                .addQueryParameter("idsiswa", MainSiswa.iduser)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                TransaksiPembayaranModel bk = new TransaksiPembayaranModel(
                                        responses.getString("id_spp"),
                                        responses.getString("tahun_ajaran"),
                                        responses.getString("bulan"),
                                        responses.getString("nominal_spp"),
                                        responses.getString("nominal_spp_format"),
                                        responses.getString("jatuh_tempo"),
                                        responses.getString("id_periode"));
                                TransaksiPembayaranModel.add(bk);
                            }

                            PegawaiAdapter adapter = new PegawaiAdapter(getApplicationContext(), TransaksiPembayaranModel);
                            rv_data.setAdapter(adapter);

                            ly00.setVisibility(View.GONE);
                            ly11.setVisibility(View.VISIBLE);
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
                                swipe_refresh.setRefreshing(false);
                                ly00.setVisibility(View.GONE);
                                ly11.setVisibility(View.GONE);
                                CustomDialog.errorDialog(ListTransaksiPembayaran.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(ListTransaksiPembayaran.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class PegawaiAdapter extends RecyclerView.Adapter<PegawaiAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<TransaksiPembayaranModel> TransaksiPembayaranModel;

        PegawaiAdapter(Context mCtx, List<TransaksiPembayaranModel> TransaksiPembayaranModel) {
            this.mCtx = mCtx;
            this.TransaksiPembayaranModel = TransaksiPembayaranModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_tagihan, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final TransaksiPembayaranModel tagihan = TransaksiPembayaranModel.get(i);
            holder.text_tanggal.setText(tagihan.getJatuh_tempo());
            holder.text_jumlah.setText(tagihan.getNominal_spp());
            holder.cv.setOnClickListener(v -> {
                Intent x = new Intent(mCtx, TambahPembayaranTransaksi.class);
                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                x.putExtra("idspp", tagihan.getId_spp());
                x.putExtra("bulan", tagihan.getBulan());
                x.putExtra("total", tagihan.getNominal_spp());
                x.putExtra("id_periode", tagihan.getId_periode());
                mCtx.startActivity(x);
            });
        }

        @Override
        public int getItemCount() {
            return TransaksiPembayaranModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_tanggal, text_jumlah;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_jumlah = itemView.findViewById(R.id.text_jumlah);
                text_tanggal = itemView.findViewById(R.id.text_tanggal);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }

//    private void LoadTransaksi() {
//        ly11.setVisibility(View.GONE);
//        ly00.setVisibility(View.VISIBLE);
//        TransaksiPembayaranModel.clear();
//        customProgress.showProgress(this, false);
//        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
//                .addQueryParameter("TAG", "listsudahdibayar")
//                .addQueryParameter("idsiswa", MainSiswa.iduser)
//                .setPriority(Priority.MEDIUM)
//                .build()
//                .getAsJSONArray(new JSONArrayRequestListener() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        try {
//                            for (int i = 0; i < response.length(); i++) {
//                                JSONObject responses = response.getJSONObject(i);
//                                TransaksiPembayaranModel bk = new TransaksiPembayaranModel(
//                                        responses.getString("id_spp"),
//                                        responses.getString("tahun_ajaran"),
//                                        responses.getString("nominal_spp"),
//                                        responses.getString("jatuh_tempo"));
//                                TransaksiPembayaranModel.add(bk);
//                            }
//
//                            RiwayatTransaksiAdapter adapter = new RiwayatTransaksiAdapter(getApplicationContext(), TransaksiPembayaranModel);
//                            rv_data.setAdapter(adapter);
//
//                            ly00.setVisibility(View.GONE);
//                            ly11.setVisibility(View.VISIBLE);
//                            swipe_refresh.setRefreshing(false);
//                            customProgress.hideProgress();
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            ly11.setVisibility(View.GONE);
//                            ly00.setVisibility(View.GONE);
//                            ly22.setVisibility(View.GONE);
//                            swipe_refresh.setRefreshing(false);
//                            customProgress.hideProgress();
//                        }
//                    }
//
//                    @Override
//                    public void onError(ANError error) {
//                        if (error.getErrorCode() == 400) {
//                            customProgress.hideProgress();
//                            try {
//                                JSONObject body = new JSONObject(error.getErrorBody());
//                                swipe_refresh.setRefreshing(false);
//                                ly00.setVisibility(View.GONE);
//                                ly11.setVisibility(View.GONE);
//                                CustomDialog.errorDialog(ListTransaksiPembayaran.this, body.optString("pesan"));
//                            } catch (JSONException ignored) {
//                            }
//                        } else {
//                            customProgress.hideProgress();
//                            CustomDialog.errorDialog(ListTransaksiPembayaran.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
//                        }
//                    }
//                });
//    }
//
//    public class RiwayatTransaksiAdapter extends RecyclerView.Adapter<RiwayatTransaksiAdapter.ProductViewHolder> {
//        private final Context mCtx;
//        private final List<TransaksiPembayaranModel> TransaksiPembayaranModel;
//
//        PegawaiAdapter(Context mCtx, List<TransaksiPembayaranModel> TransaksiPembayaranModel) {
//            this.mCtx = mCtx;
//            this.TransaksiPembayaranModel = TransaksiPembayaranModel;
//        }
//
//        @Override
//        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
//            LayoutInflater inflater = LayoutInflater.from(mCtx);
//            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_sudah_transaksi, null);
//            return new ProductViewHolder(view);
//        }
//
//        @SuppressLint("SetTextI18n")
//        @Override
//        public void onBindViewHolder(ProductViewHolder holder, int i) {
//            final TransaksiPembayaranModel tagihan = TransaksiPembayaranModel.get(i);
//            holder.text_tanggal.setText(tagihan.getJatuh_tempo());
//            holder.text_jumlah.setText(tagihan.getNominal_spp());
//        }
//
//        @Override
//        public int getItemCount() {
//            return TransaksiPembayaranModel.size();
//        }
//
//        class ProductViewHolder extends RecyclerView.ViewHolder {
//            TextView text_id, text_bulan, text_bayar, text_tanggal, text_status;
//            CardView cv;
//
//            ProductViewHolder(View itemView) {
//                super(itemView);
//                text_id = itemView.findViewById(R.id.text_id);
//                text_bulan = itemView.findViewById(R.id.text_bulan);
//                text_bayar = itemView.findViewById(R.id.text_bayar);
//                text_tanggal = itemView.findViewById(R.id.text_tanggal);
//                text_status = itemView.findViewById(R.id.text_status);
//                cv = itemView.findViewById(R.id.cv);
//            }
//        }
//    }
}