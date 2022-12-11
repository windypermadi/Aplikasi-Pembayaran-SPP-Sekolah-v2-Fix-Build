package pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi_pembayaran;

import static android.view.View.GONE;

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
import pembayaranspp.windypermadi.aplikasipembayaranspp.model.TransaksiRiwayatPembayaranModel;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.TambahPembayaranTransaksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.TambahPembayaranTransaksiInvoice;

public class ListTransaksiPembayaran extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private LinearLayout ly00, ly11, ly22;
    private RecyclerView rv_data, rv_data2;
    private TextView text1, text2;
    List<TransaksiPembayaranModel> TransaksiPembayaranModel;
    List<TransaksiRiwayatPembayaranModel> TransaksiRiwayatPembayaranModel;
    private SwipeRefreshLayout swipe_refresh;
    private TextView et_cari;
    String idkelas, status = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaksi_pembayaran);

        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Transaksi");

        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);
        rv_data = findViewById(R.id.rv_data);
        rv_data2 = findViewById(R.id.rv_data2);
        swipe_refresh = findViewById(R.id.swipe_refresh);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);

        TransaksiPembayaranModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        TransaksiRiwayatPembayaranModel = new ArrayList<>();
        LinearLayoutManager y = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data2.setHasFixedSize(true);
        rv_data2.setLayoutManager(y);
        rv_data2.setNestedScrollingEnabled(true);

        LoadTagihan();

        ActionButton();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void ActionButton() {
        findViewById(R.id.back).setOnClickListener(v -> finish());
        swipe_refresh.setOnRefreshListener(() -> {
            if (status == "1"){
                ly11.setVisibility(GONE);
                ly00.setVisibility(View.VISIBLE);
                TransaksiPembayaranModel.clear();
                rv_data.setVisibility(View.VISIBLE);
                rv_data2.setVisibility(View.GONE);
                LoadTagihan();
            } else {
                ly11.setVisibility(GONE);
                ly00.setVisibility(View.VISIBLE);
                TransaksiRiwayatPembayaranModel.clear();
                rv_data2.setVisibility(View.VISIBLE);
                rv_data.setVisibility(GONE);
                LoadRiwayatTransaksi();
            }
        });
        text1.setOnClickListener(v -> {
            LoadTagihan();
            text1.setBackground(getResources().getDrawable(R.drawable.btn_green));
            text2.setBackgroundColor(Color.WHITE);
            rv_data.setVisibility(View.VISIBLE);
            rv_data2.setVisibility(View.GONE);
        });
        text2.setOnClickListener(v -> {
            text1.setBackgroundColor(Color.WHITE);
            text2.setBackground(getResources().getDrawable(R.drawable.btn_green));
            LoadRiwayatTransaksi();
            rv_data.setVisibility(View.GONE);
            rv_data2.setVisibility(View.VISIBLE);
        });
    }

    private void LoadTagihan() {
        ly11.setVisibility(GONE);
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
                                        responses.getString("id_periode"),
                                        responses.getString("status_transaksi"),
                                        responses.getString("no_bayar"));
                                TransaksiPembayaranModel.add(bk);
                            }

                            PegawaiAdapter adapter = new PegawaiAdapter(getApplicationContext(), TransaksiPembayaranModel);
                            rv_data.setAdapter(adapter);

                            rv_data.setVisibility(View.VISIBLE);
                            rv_data2.setVisibility(View.GONE);
                            ly00.setVisibility(GONE);
                            ly11.setVisibility(View.VISIBLE);
                            swipe_refresh.setRefreshing(false);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ly11.setVisibility(GONE);
                            ly00.setVisibility(GONE);
                            ly22.setVisibility(GONE);
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
                                ly00.setVisibility(GONE);
                                ly11.setVisibility(GONE);
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
            holder.text_tanggal.setText(tagihan.getBulan());
            holder.text_jumlah.setText(tagihan.getNominal_spp());

                holder.cv.setOnClickListener(v -> {
                    if (tagihan.getStatus_transaksi().equals("0")){
                        Intent x = new Intent(mCtx, TambahPembayaranTransaksi.class);
                        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        x.putExtra("idspp", tagihan.getId_spp());
                        x.putExtra("bulan", tagihan.getBulan());
                        x.putExtra("total", tagihan.getNominal_spp());
                        x.putExtra("tahun_ajaran", tagihan.getTahun_ajaran());
                        x.putExtra("id_periode", tagihan.getId_periode());
                        mCtx.startActivity(x);
                    } else if (tagihan.getStatus_transaksi().equals("1")){
                        Intent x = new Intent(mCtx, TambahPembayaranTransaksiInvoice.class);
                        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        x.putExtra("idtransaksi", tagihan.getNo_bayar());
                        mCtx.startActivity(x);
                    } else if (tagihan.getStatus_transaksi().equals("4")){
                        Intent x = new Intent(mCtx, TambahPembayaranTransaksiInvoice.class);
                        x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        x.putExtra("idtransaksi", tagihan.getNo_bayar());
                        mCtx.startActivity(x);
                    }
                });

            if (tagihan.getStatus_transaksi().equals("1")){
                String status = "Pembayaran Belum Lengkap";
                holder.text_status.setText(status);
            } else if (tagihan.getStatus_transaksi().equals("4")){
                String status = "Pembayaran Ditolak";
                holder.text_status.setText(status);
            } else {
                String status = "Bayar Tagihan";
                holder.text_status.setText(status);
            }
        }

        @Override
        public int getItemCount() {
            return TransaksiPembayaranModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_tanggal, text_jumlah, text_status;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_jumlah = itemView.findViewById(R.id.text_jumlah);
                text_tanggal = itemView.findViewById(R.id.text_tanggal);
                text_status = itemView.findViewById(R.id.text_status);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }

    private void LoadRiwayatTransaksi() {
        ly11.setVisibility(GONE);
        ly00.setVisibility(View.VISIBLE);
        TransaksiRiwayatPembayaranModel.clear();
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "listsudahdibayar")
                .addQueryParameter("idsiswa", MainSiswa.iduser)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject responses = response.getJSONObject(i);
                                TransaksiRiwayatPembayaranModel bk = new TransaksiRiwayatPembayaranModel(
                                        responses.getString("id_spp"),
                                        responses.getString("invoice"),
                                        responses.getString("bulan"),
                                        responses.getString("nominal_spp"),
                                        responses.getString("nominal_spp_format"),
                                        responses.getString("tanggal"),
                                        responses.getString("status_transaksi"));
                                TransaksiRiwayatPembayaranModel.add(bk);
                            }

                            RiwayatTransaksiAdapter adapter = new RiwayatTransaksiAdapter(getApplicationContext(), TransaksiRiwayatPembayaranModel);
                            rv_data2.setAdapter(adapter);

                            rv_data.setVisibility(View.GONE);
                            rv_data2.setVisibility(View.VISIBLE);
                            ly00.setVisibility(GONE);
                            ly11.setVisibility(View.VISIBLE);
                            swipe_refresh.setRefreshing(false);
                            customProgress.hideProgress();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            ly11.setVisibility(GONE);
                            ly00.setVisibility(GONE);
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
                                ly00.setVisibility(GONE);
                                ly11.setVisibility(GONE);
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

    public class RiwayatTransaksiAdapter extends RecyclerView.Adapter<RiwayatTransaksiAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<TransaksiRiwayatPembayaranModel> TransaksiRiwayatPembayaranModel;

        RiwayatTransaksiAdapter(Context mCtx, List<TransaksiRiwayatPembayaranModel> TransaksiRiwayatPembayaranModel) {
            this.mCtx = mCtx;
            this.TransaksiRiwayatPembayaranModel = TransaksiRiwayatPembayaranModel;
        }

        @Override
        public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            LayoutInflater inflater = LayoutInflater.from(mCtx);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.model_list_sudah_transaksi, null);
            return new ProductViewHolder(view);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(ProductViewHolder holder, int i) {
            final TransaksiRiwayatPembayaranModel tagihan = TransaksiRiwayatPembayaranModel.get(i);
            holder.text_id.setText(tagihan.getInvoice());
            holder.text_bulan.setText(tagihan.getBulan());
            holder.text_bayar.setText(tagihan.getNominal_spp_format());
            holder.text_tanggal.setText(tagihan.getTanggal());
            if (tagihan.getStatus_transaksi().equals("2")){
                String status = "Belum Diterima";
                holder.text_status.setText(status);
            } else if (tagihan.getStatus_transaksi().equals("3")){
                String status = "Sudah Diterima";
                holder.text_status.setText(status);
            }
        }

        @Override
        public int getItemCount() {
            return TransaksiRiwayatPembayaranModel.size();
        }

        class ProductViewHolder extends RecyclerView.ViewHolder {
            TextView text_id, text_bulan, text_bayar, text_tanggal, text_status;
            CardView cv;

            ProductViewHolder(View itemView) {
                super(itemView);
                text_id = itemView.findViewById(R.id.text_id);
                text_bulan = itemView.findViewById(R.id.text_bulan);
                text_bayar = itemView.findViewById(R.id.text_bayar);
                text_tanggal = itemView.findViewById(R.id.text_tanggal);
                text_status = itemView.findViewById(R.id.text_status);
                cv = itemView.findViewById(R.id.cv);
            }
        }
    }
}