package pembayaranspp.windypermadi.aplikasipembayaranspp.model;

public class SiswaModel {
    String idsiswa;
    String nis;
    String nama;
    String nama_kelas;

    public SiswaModel(String idsiswa, String nis, String nama, String nama_kelas) {
        this.idsiswa = idsiswa;
        this.nis = nis;
        this.nama = nama;
        this.nama_kelas = nama_kelas;
    }

    public String getIdsiswa() {
        return idsiswa;
    }

    public String getNis() {
        return nis;
    }

    public String getNama() {
        return nama;
    }

    public String getNama_kelas() {
        return nama_kelas;
    }
}
