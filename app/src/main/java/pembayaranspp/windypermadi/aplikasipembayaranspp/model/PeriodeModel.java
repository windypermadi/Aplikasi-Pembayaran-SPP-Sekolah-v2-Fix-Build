package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class PeriodeModel {
    String idperiode;
    String tahun_ajaran;
    String bulan;
    String nominal;

    public PeriodeModel(String idperiode, String tahun_ajaran, String bulan, String nominal) {
        this.idperiode = idperiode;
        this.tahun_ajaran = tahun_ajaran;
        this.bulan = bulan;
        this.nominal = nominal;
    }
    public String getIdperiode() {
        return idperiode;
    }

    public String getTahun_ajaran() {
        return tahun_ajaran;
    }

    public String getBulan() {
        return bulan;
    }

    public String getNominal() {
        return nominal;
    }
}
