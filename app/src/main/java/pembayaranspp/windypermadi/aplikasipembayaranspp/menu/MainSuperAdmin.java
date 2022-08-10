package pembayaranspp.windypermadi.aplikasipembayaranspp.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.content.Intent;
import android.os.Bundle;

import com.dandyakbar.aplikasipembayaranspp.R;

import java.io.File;
import java.util.HashMap;

import pembayaranspp.windypermadi.aplikasipembayaranspp.admin.AdminActivity;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.SessionManager;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CekKoneksi;
import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils.CustomProgressbar;

public class MainSuperAdmin extends AppCompatActivity {
    CustomProgressbar customProgress = CustomProgressbar.getInstance();
    CekKoneksi koneksi = new CekKoneksi();
    public SessionManager SessionManager;
    private AppCompatTextView text_nama;
    private AppCompatImageView img_logout;
    public static String iduser, username;
    public static String idsekolah = "0001";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_super_admin);
        SessionManager = new SessionManager(MainSuperAdmin.this);
        SessionManager.checkLogin();
        HashMap<String, String> user = SessionManager.getUserDetails();
        iduser = user.get(SessionManager.KEY_ID);
        username = user.get(SessionManager.KEY_USERNAME);

        text_nama = findViewById(R.id.text_nama);
        img_logout = findViewById(R.id.img_logout);

        ActionButton();
        text_nama.setText("Halo " + username);
    }

    private void ActionButton() {
        img_logout.setOnClickListener(v -> logoutUser());
        findViewById(R.id.cv7).setOnClickListener(v -> startActivity(new Intent(MainSuperAdmin.this, AdminActivity.class)));
    }

    private void logoutUser() {
        clearApplicationData();
        SessionManager.logoutUser();
        finishAffinity();
    }

    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            assert children != null;
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            int i = 0;
            assert children != null;
            while (i < children.length) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
                i++;
            }
        }

        assert dir != null;
        return dir.delete();
    }
}