package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class TransaksiPembayaranModel {
    String id_spp;
    String tahun_ajaran;
    String bulan;
    String nominal_spp;
    String nominal_spp_format;
    String jatuh_tempo;
    String id_periode;

    public TransaksiPembayaranModel(String id_spp, String tahun_ajaran, String bulan, String nominal_spp, String nominal_spp_format, String jatuh_tempo, String id_periode) {
        this.id_spp = id_spp;
        this.tahun_ajaran = tahun_ajaran;
        this.bulan = bulan;
        this.nominal_spp = nominal_spp;
        this.nominal_spp_format = nominal_spp_format;
        this.jatuh_tempo = jatuh_tempo;
        this.id_periode = id_periode;
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
}
