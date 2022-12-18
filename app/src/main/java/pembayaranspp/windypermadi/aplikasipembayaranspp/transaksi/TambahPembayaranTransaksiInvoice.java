package pembayaranspp.windypermadi.aplikasipembayaranspp.transaksi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.dandyakbar.aplikasipembayaranspp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.Connection;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomDialog;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

public class TambahPembayaranTransaksiInvoice extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();

    private TextView et_cari;
    String idtransaksi;
    String nis, nama, nama_kelas, tahun_ajaran, bulan, jumlah_pembayaran, metode;
    TextView text_nis, text_nama, text_kelas, text_tahun, text_bulan, text_total, text_metode;
    //upload
    private ImageView img_upload, btn_upload;
    Uri FilePath;
    String gambar = "kosong";
    Intent intent;
    Bitmap bitmap;
    public final int REQUEST_CAMERA = 0;
    public final int SELECT_FILE = 1;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private Uri mImageUri;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

    //Kode permintaan untuk memilih metode pengambilan gamabr
    private static final int REQUEST_CODE_CAMERA = 1;
    private static final int REQUEST_CODE_GALLERY = 2;

    public TambahPembayaranTransaksiInvoice() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pembayaran_transaksi_invoice);
        et_cari = findViewById(R.id.et_cari);
        et_cari.setText("Kirim Bukti Transfer");

        Intent i = getIntent();
        idtransaksi = i.getStringExtra("idtransaksi");

        text_nis = findViewById(R.id.text_nis);
        text_nama = findViewById(R.id.text_nama);
        text_kelas = findViewById(R.id.text_kelas);
        text_tahun = findViewById(R.id.text_tahun);
        text_bulan = findViewById(R.id.text_bulan);
        text_metode = findViewById(R.id.text_metode);
        text_total = findViewById(R.id.text_total);
        img_upload = findViewById(R.id.img_upload);
        btn_upload = findViewById(R.id.btn_upload);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btn_upload.setOnClickListener(view -> {
            if (checkPermission()) {
//                selectImage();
                gambar = "isi";
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), SELECT_FILE);
            } else {
                requestPermission();
            }
        });
        findViewById(R.id.text_simpan).setOnClickListener(view -> {
            if (koneksi.isConnected(this)) {
                if (gambar.equals("isi")) {
                    File file = new File(getRealPathFromURI(FilePath));
                    customProgress.showProgress(this, false);
                    tambahdata(file);
                } else {
                    CustomDialog.errorDialog(TambahPembayaranTransaksiInvoice.this, "Silahkan upload bukti transaksi. Bukti transaksi tidak boleh kosong!");
                }
            } else {
                CustomDialog.noInternet(TambahPembayaranTransaksiInvoice.this);
            }
        });

        LoadData();
    }

    private void LoadData() {
        customProgress.showProgress(this, false);
        AndroidNetworking.get(Connection.CONNECT + "spp_transaksi.php")
                .addQueryParameter("TAG", "detail")
                .addQueryParameter("idtransaksi", idtransaksi)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        customProgress.hideProgress();
                        nis = response.optString("nis");
                        nama = response.optString("nama");
                        nama_kelas = response.optString("nama_kelas");
                        tahun_ajaran = response.optString("tahun_ajaran");
                        bulan = response.optString("bulan");
                        jumlah_pembayaran = response.optString("jumlah_pembayaran");
                        metode = response.optString("metode_pembayaran");

                        text_nis.setText(nis);
                        text_nama.setText(nama);
                        text_kelas.setText(nama_kelas);
                        text_tahun.setText(tahun_ajaran);
                        text_bulan.setText(bulan);
                        text_total.setText(jumlah_pembayaran);
                        text_metode.setText(metode);
                    }

                    @Override
                    public void onError(ANError error) {
                        if (error.getErrorCode() == 400) {
                            customProgress.hideProgress();
                        } else {
                            customProgress.hideProgress();
                            CustomDialog.errorDialog(TambahPembayaranTransaksiInvoice.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                        }
                    }
                });
    }

    private void showDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title dialog
        alertDialogBuilder.setTitle("Ambil gambar dari galeri");

        // set pesan dari dialog
        alertDialogBuilder
                .setMessage("Ambil Gambar Dari Galeri")
                .setIcon(R.mipmap.ic_launcher)
                .setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        gambar = "isi";
                        intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), SELECT_FILE);
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // jika tombol ini diklik, akan menutup dialog
                        // dan tidak terjadi apa2
                        dialog.cancel();
                    }
                });

        // membuat alert dialog dari builder
        AlertDialog alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog
        alertDialog.show();
    }

    private void selectImage() {
        img_upload.setImageResource(0);
        final CharSequence[] items = {"Ambil foto", "Pilih dari galeri",
                "Batal"};
        AlertDialog.Builder builder = new AlertDialog.Builder(TambahPembayaranTransaksiInvoice.this);
        builder.setTitle("Upload bukti transaksi");
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setItems(items, (dialog, item) -> {
            if (items[item].equals("Ambil foto")) {
                //intent khusus untuk menangkap foto lewat kamera
                gambar = "isi";
                if (ContextCompat.checkSelfPermission(TambahPembayaranTransaksiInvoice.this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(TambahPembayaranTransaksiInvoice.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                } else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            } else if (items[item].equals("Pilih dari galeri")) {
                gambar = "isi";
                intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Pilih gambar"), SELECT_FILE);
            } else if (items[item].equals("Batal")) {
                gambar = "kosong";
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                try {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    img_upload.setImageBitmap(bitmap);
                    FilePath = getImageUri(TambahPembayaranTransaksiInvoice.this, bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE && data != null && data.getData() != null) {
                try {
                    // mengambil gambar dari Gallery
                    bitmap = MediaStore.Images.Media.getBitmap(TambahPembayaranTransaksiInvoice.this.getContentResolver(), data.getData());
                    img_upload.setImageBitmap(bitmap);
                    FilePath = getImageUri(TambahPembayaranTransaksiInvoice.this, bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    public void tambahdata(File file) {
        if (file.length() == 0) {
            AndroidNetworking.upload(Connection.CONNECT + "spp_transaksi")
                    .addMultipartParameter("TAG", "tambahBuktiTransaksi")
                    .addMultipartParameter("idtransaksi", idtransaksi)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            popupBerhasil(response.optString("pesan"));
                            customProgress.hideProgress();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("data1", "onError errorCode : " + error.getErrorCode());
                            Log.d("data1", "onError errorBody : " + error.getErrorBody());
                            Log.d("data1", "onError errorDetail : " + error.getErrorDetail());
                            customProgress.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    CustomDialog.errorDialog(TambahPembayaranTransaksiInvoice.this, body.optString("pesan"));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                CustomDialog.errorDialog(TambahPembayaranTransaksiInvoice.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        } else {
            firebaseUploadImage();
            AndroidNetworking.upload(Connection.CONNECT + "spp_transaksi.php")
                    .addMultipartParameter("TAG", "tambahBuktiTransaksi")
                    .addMultipartParameter("idtransaksi", idtransaksi)
                    .addMultipartFile("uploadedfile", file)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            popupBerhasil(response.optString("pesan"));
                            customProgress.hideProgress();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.d("data1", "onError errorCode : " + error.getErrorCode());
                            Log.d("data1", "onError errorBody : " + error.getErrorBody());
                            Log.d("data1", "onError errorDetail : " + error.getErrorDetail());
                            customProgress.hideProgress();
                            if (error.getErrorCode() == 400) {
                                try {
                                    JSONObject body = new JSONObject(error.getErrorBody());
                                    CustomDialog.errorDialog(TambahPembayaranTransaksiInvoice.this, body.optString("pesan"));
                                } catch (JSONException ignored) {
                                }
                            } else {
                                CustomDialog.errorDialog(TambahPembayaranTransaksiInvoice.this, "Sambunganmu dengan server terputus. Periksa sambungan internet, lalu coba lagi.");
                            }
                        }
                    });
        }
    }

    private void firebaseUploadImage() {
        StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
        ref.putFile(FilePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        customProgress.hideProgress();
                        Toast.makeText(TambahPembayaranTransaksiInvoice.this, "Uploaded",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        customProgress.hideProgress();
                        Toast.makeText(TambahPembayaranTransaksiInvoice.this, "Failed " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        customProgress.hideProgress();
                    }
                });
    }

    private void popupBerhasil(String isi) {
        customProgress.hideProgress();
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(TambahPembayaranTransaksiInvoice.this);
        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.custom_success_dialog, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();
        TextView keterangan = dialogView.findViewById(R.id.keterangan);
        keterangan.setText(isi);
        dialogView.findViewById(R.id.ok).setOnClickListener(v -> {
            finish();
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(TambahPembayaranTransaksiInvoice.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(TambahPembayaranTransaksiInvoice.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(TambahPembayaranTransaksiInvoice.this, "Write External Storage permission allows us to save files. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(TambahPembayaranTransaksiInvoice.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("value", "Permission Granted, Now you can use local drive .");
            } else {
                Log.e("value", "Permission Denied, You cannot use local drive .");
            }
        }
    }

    //versi bite
    //Method ini digunakan untuk mengambil gambar dari Kamera
//    private void getImage(){
//        CharSequence[] menu = {"Kamera", "Galeri"};
//        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
//                .setTitle("Upload Image")
//                .setItems(menu, (dialog1, which) -> {
//                    switch (which){
//                        case 0:
//                            //Mengambil gambar dari Kemara ponsel
//                            Intent imageIntentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(imageIntentCamera, REQUEST_CODE_CAMERA);
//                            break;
//
//                        case 1:
//                            //Mengambil gambar dari galeri
//                            Intent imageIntentGallery = new Intent(Intent.ACTION_PICK,
//                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(imageIntentGallery, REQUEST_CODE_GALLERY);
//                            break;
//                    }
//                });
//        dialog.create();
//        dialog.show();
//    }
//
//    //Method ini digunakan untuk mengupload gambar pada Storage
//    private void uploadImage(){
//        //Mendapatkan data dari ImageView sebagai Bytes
//        img_upload.setDrawingCacheEnabled(true);
//        img_upload.buildDrawingCache();
//        Bitmap bitmap = ((BitmapDrawable) img_upload.getDrawable()).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//        //Mengkompress bitmap menjadi JPG dengan kualitas gambar 100%
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] bytes = stream.toByteArray();
//
//        //Lokasi lengkap dimana gambar akan disimpan
//        String namaFile = UUID.randomUUID()+".jpg";
//        String pathImage = "gambar/"+namaFile;
//        UploadTask uploadTask = reference.child(pathImage).putBytes(bytes);
//        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        customProgress.hideProgress();
//                        Toast.makeText(TambahPembayaranTransaksiInvoice.this, "Uploading Berhasil", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        customProgress.hideProgress();
//                        Toast.makeText(TambahPembayaranTransaksiInvoice.this, "Uploading Gagal", Toast.LENGTH_SHORT).show();
//                    }
//                })
//                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                        customProgress.showProgress(TambahPembayaranTransaksiInvoice.this, false);
//                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
////                        progressBar.setProgress((int) progress);
//                    }
//                });
//    }
//
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.text_simpan:
//                //Menerapkan kejadian saat tombol upload di klik
//                uploadImage();
//                break;
//            case R.id.btn_upload:
//                //Menerapkan kejadian saat tombol pilih gambar di klik
//                getImage();
//                break;
//        }
//    }
}