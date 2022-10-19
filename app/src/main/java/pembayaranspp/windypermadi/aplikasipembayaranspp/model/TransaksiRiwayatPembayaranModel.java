package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class TransaksiRiwayatPembayaranModel {
    String id_spp;
    String invoice;
    String bulan;
    String nominal_spp;
    String nominal_spp_format;
    String tanggal;

    public TransaksiRiwayatPembayaranModel(String id_spp, String invoice, String bulan, String nominal_spp, String nominal_spp_format, String tanggal) {
        this.id_spp = id_spp;
        this.invoice = invoice;
        this.bulan = bulan;
        this.nominal_spp = nominal_spp;
        this.nominal_spp_format = nominal_spp_format;
        this.tanggal = tanggal;
    }

    public String getId_spp() {
        return id_spp;
    }

    public String getInvoice() {
        return invoice;
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

    public String getTanggal() {
        return tanggal;
    }
}
