package nano.yallam.toodoo.util;

import android.app.ProgressDialog;
import android.content.Context;

import nano.yallam.toodoo.R;

public class Utils {

    private Utils() {}

    /**
     * Generate top layer progress indicator.
     *
     * @param context    activity context
     * @param message    dialog message
     * @param cancelable can be progress layer canceled
     * @return dialog
     */
    public static ProgressDialog generateProgressDialog(Context context, String message, boolean cancelable) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message != null ? message : context.getString(R.string.loading));
        progressDialog.setCancelable(cancelable);
        return progressDialog;
    }

}
