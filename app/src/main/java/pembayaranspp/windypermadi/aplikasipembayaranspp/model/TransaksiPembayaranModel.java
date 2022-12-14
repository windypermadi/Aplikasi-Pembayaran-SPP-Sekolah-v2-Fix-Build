package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class TransaksiPembayaranModel {
    String id_spp;
    String tahun_ajaran;
    String bulan;
    String nominal_spp;
    String nominal_spp_format;
    String jatuh_tempo;
    String id_periode;
    String status_transaksi;
    String no_bayar;

    public TransaksiPembayaranModel(String id_spp, String tahun_ajaran, String bulan,
                                    String nominal_spp, String nominal_spp_format, String jatuh_tempo, String id_periode, String status_transaksi, String no_bayar) {
        this.id_spp = id_spp;
        this.tahun_ajaran = tahun_ajaran;
        this.bulan = bulan;
        this.nominal_spp = nominal_spp;
        this.nominal_spp_format = nominal_spp_format;
        this.jatuh_tempo = jatuh_tempo;
        this.id_periode = id_periode;
        this.status_transaksi = status_transaksi;
        this.no_bayar = no_bayar;
    }

    public String getId_spp() {
        return id_spp;
    }

    public String getTahun_ajaran() {
        return tahun_ajaran;
    }

    public String getBulan() {
        return bulan;
    }

    public String getNominal_spp() {
        return nominal_spp;
    }

    public String getNominal_spp_format() {
        return nominal_spp_format;
    }

    public String getJatuh_tempo() {
        return jatuh_tempo;
    }

    public String getId_periode() {
        return id_periode;
    }

    public String getStatus_transaksi() {
        return status_transaksi;
    }

    public String getNo_bayar() {
        return no_bayar;
    }
}
