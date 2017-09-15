package nano.yallam.toodoo.util;

import android.app.ProgressDialog;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import nano.yallam.toodoo.R;

public final class Utils {

    private Utils() {
        throw new AssertionError("Can't be instantiated!");
    }

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

    public static String getShortenedText(String txt, int length) {
        String result;
        if (txt.length() <= length) {
            result = txt;
        } else {
            result = txt.substring(0, length - 3) + "...";
        }

        return result;
    }

    public static String getFriendlyDue(long due) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd");
        return dateFormat.format(new Date(due));
    }
}
