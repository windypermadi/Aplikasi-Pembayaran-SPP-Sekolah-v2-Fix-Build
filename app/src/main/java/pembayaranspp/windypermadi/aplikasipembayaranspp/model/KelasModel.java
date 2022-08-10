package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class KelasModel {
    String idkelas;
    String nama_kelas;

    public KelasModel(String idkelas, String nama_kelas) {
        this.idkelas = idkelas;
        this.nama_kelas = nama_kelas;
    }

    public String getIdkelas() {
        return idkelas;
    }

    public String getNama_kelas() {
        return nama_kelas;
    }
}
