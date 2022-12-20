package pembayaranspp.windypermadi.aplikasipembayaranspp.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.dandyakbar.aplikasipembayaranspp.R;

import java.util.HashMap;

import pembayaranspp.windypermadi.aplikasipembayaranspp.helper.SessionManager;
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MainAdmin;
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MainSiswa;
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MainSuperAdmin;
import pembayaranspp.windypermadi.aplikasipembayaranspp.menu.MenuKepala;

public class MainActivity extends AppCompatActivity {
    private LinearLayout cv1, cv2, cv3;
    public SessionManager session;
    public String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        status = user.get(SessionManager.KEY_STATUS);

        if (status.equals("0")){
            Intent i = new Intent(MainActivity.this, MainSuperAdmin.class);
            startActivity(i);
            finish();
        } else if (status.equals("1")){
            Intent i = new Intent(MainActivity.this, MainAdmin.class);
            startActivity(i);
            finish();
        } else if (status.equals("2")){
            Intent i = new Intent(MainActivity.this, MenuKepala.class);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(MainActivity.this, MainSiswa.class);
            startActivity(i);
            finish();
        }

//        cv1 = findViewById(R.id.cv1);
//        cv2 = findViewById(R.id.cv2);
//        cv3 = findViewById(R.id.cv3);
//
//        cv1.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, LoginActivity.class);
//            i.putExtra("status_login", "1");
//            startActivity(i);
//            finish();
//        });
//        cv2.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, LoginActivity.class);
//            i.putExtra("status_login", "3");
//            startActivity(i);
//            finish();
//        });
//        cv3.setOnClickListener(v -> {
//            Intent i = new Intent(MainActivity.this, LoginActivity.class);
//            i.putExtra("status_login", "2");
//            startActivity(i);
//            finish();
//        });
    }
}