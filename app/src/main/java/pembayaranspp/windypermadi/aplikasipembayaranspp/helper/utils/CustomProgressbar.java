package pembayaranspp.windypermadi.aplikasipembayaranspp.helper.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.RelativeLayout;

import com.dandyakbar.aplikasipembayaranspp.R;

public class CustomProgressbar {
    public static CustomProgressbar customProgress = null;
    private Dialog mDialog;
    private RelativeLayout lini;

    public static CustomProgressbar getInstance() {
        if (customProgress == null) {
            customProgress = new CustomProgressbar();
        }
        return customProgress;
    }

    @SuppressLint("ResourceAsColor")
    public void showProgress(Context context, boolean cancelable) {
        mDialog = new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.getWindow().setBackgroundDrawableResource(
                R.color.transparan);
        mDialog.setContentView(R.layout.custom_progressbar);
        lini = mDialog.findViewById(R.id.lini);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(cancelable);
        mDialog.show();
    }

    public void hideProgress() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }
}
