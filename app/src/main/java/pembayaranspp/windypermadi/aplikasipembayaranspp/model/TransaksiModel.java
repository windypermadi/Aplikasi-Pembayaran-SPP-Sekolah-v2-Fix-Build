package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class TransaksiModel {
    String idtransaksi;
    String invoice;
    String nama;
    String bulan;
    String tahun;
    String jumlah_pembayaran;
    String file_pembayaran;
    String status_approve;
    String tgl_create;

    public TransaksiModel(String idtransaksi, String invoice, String nama, String bulan, String tahun, String jumlah_pembayaran, String file_pembayaran, String status_approve, String tgl_create) {
        this.idtransaksi = idtransaksi;
        this.invoice = invoice;
        this.nama = nama;
        this.bulan = bulan;
        this.tahun = tahun;
        this.jumlah_pembayaran = jumlah_pembayaran;
        this.file_pembayaran = file_pembayaran;
        this.status_approve = status_approve;
        this.tgl_create = tgl_create;
    }

    public String getIdtransaksi() {
        return idtransaksi;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getNama() {
        return nama;
    }

    public String getBulan() {
        return bulan;
    }

    public String getTahun() {
        return tahun;
    }

    public String getJumlah_pembayaran() {
        return jumlah_pembayaran;
    }

    public String getFile_pembayaran() {
        return file_pembayaran;
    }

    public String getStatus_approve() {
        return status_approve;
    }

    public String getTgl_create() {
        return tgl_create;
    }
}
