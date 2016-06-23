package net.chizography.droid.whosfirst;
import android.util.Log;

final class AppLog {
    private AppLog(){}

    @SuppressWarnings("unused")
    public static void d(final String message) {
        Log.d("WHOSFIRST", message);
    }

    @SuppressWarnings("unused")
    public static void e(final String message) {
        Log.e("WHOSFIRST", message);
    }

    @SuppressWarnings("unused")
    public static void i(final String message) {
        Log.i("WHOSFIRST", message);
    }

    @SuppressWarnings("unused")
    public static void v(final String message) {
        Log.v("WHOSFIRST", message);
    }

    @SuppressWarnings("unused")
    public static void w(final String message) {
        Log.w("WHOSFIRST", message);
    }
}
