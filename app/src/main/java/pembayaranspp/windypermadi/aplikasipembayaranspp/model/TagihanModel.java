package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class TagihanModel {
    String id_spp;
    String tahun_ajaran;
    String nominal_spp;
    String jatuh_tempo;

    public TagihanModel(String id_spp, String tahun_ajaran, String nominal_spp, String jatuh_tempo) {
        this.id_spp = id_spp;
        this.tahun_ajaran = tahun_ajaran;
        this.nominal_spp = nominal_spp;
        this.jatuh_tempo = jatuh_tempo;
    }

    public String getId_spp() {
        return id_spp;
    }

    public String getTahun_ajaran() {
        return tahun_ajaran;
    }

    public String getNominal_spp() {
        return nominal_spp;
    }

    public String getJatuh_tempo() {
        return jatuh_tempo;
    }
}
