package pembayaranspp.windypermadi.aplikasipembayaranspp.tagihan;

import static pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog.successDialog;

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
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dandyakbar.aplikasipembayaranspp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MainSiswa;
import pembayaranspp.windypermadi.aplikasipembayaranspp.model.TagihanModel;
import pembayaranspp.windypermadi.aplikasipembayaranspp.model.TransaksiModel;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.DetailTransaksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.TambahPembayaranTransaksiInvoice;
import pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi.TransaksiPembayaran;

public class MenuTagihan extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private LinearLayout ly00, ly11, ly22;
    private RecyclerView rv_data;
    List<TagihanModel> TagihanModel;
    private SwipeRefreshLayout swipe_refresh;
    private TextView et_cari;
    String idkelas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_tagihan);

        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Tagihan SPP");

        ly00 = findViewById(R.id.ly00);
        ly11 = findViewById(R.id.ly11);
        rv_data = findViewById(R.id.rv_data);
        swipe_refresh = findViewById(R.id.swipe_refresh);

        TagihanModel = new ArrayList<>();
        LinearLayoutManager x = new GridLayoutManager(this, 1, LinearLayoutManager.VERTICAL, false);
        rv_data.setHasFixedSize(true);
        rv_data.setLayoutManager(x);
        rv_data.setNestedScrollingEnabled(true);

        ActionButton();
    }

    @Override
    protected void onResume() {
        ly11.setVisibility(View.GONE);
        ly00.setVisibility(View.VISIBLE);
        TagihanModel.clear();
        LoadTagihan();
        super.onResume();
    }

    private void ActionButton() {
        findViewById(R.id.back).setOnClickListener(v -> finish());
        swipe_refresh.setOnRefreshListener(() -> {
            ly11.setVisibility(View.GONE);
            ly00.setVisibility(View.VISIBLE);
            TagihanModel.clear();
            LoadTagihan();
        });
    }

    private void LoadTagihan() {
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
                                TagihanModel bk = new TagihanModel(
                                        responses.getString("id_spp"),
                                        responses.getString("tahun_ajaran"),
                                        responses.getString("nominal_spp"),
                                        responses.getString("jatuh_tempo"));
                                TagihanModel.add(bk);
                            }

                            PegawaiAdapter adapter = new PegawaiAdapter(getApplicationContext(), TagihanModel);
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
                                CustomDialog.errorDialog(MenuTagihan.this, body.optString("pesan"));
                            } catch (JSONException ignored) {
                            }
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(MenuTagihan.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    public class PegawaiAdapter extends RecyclerView.Adapter<PegawaiAdapter.ProductViewHolder> {
        private final Context mCtx;
        private final List<TagihanModel> TagihanModel;

        PegawaiAdapter(Context mCtx, List<TagihanModel> TagihanModel) {
            this.mCtx = mCtx;
            this.TagihanModel = TagihanModel;
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
            final TagihanModel tagihan = TagihanModel.get(i);
            holder.text_tanggal.setText(tagihan.getJatuh_tempo());
            holder.text_jumlah.setText(tagihan.getNominal_spp());
        }

        @Override
        public int getItemCount() {
            return TagihanModel.size();
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
}