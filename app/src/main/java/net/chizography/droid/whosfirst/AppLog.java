package net.chizography.droid.whosfirst;
import android.util.Log;

public final class AppLog {
    private AppLog(){}
    
    public static int d(final String message) {
        return Log.d("WHOSFIRST", message);
    }
    
    public static int e(final String message) {
        return Log.e("WHOSFIRST", message);
    }
    
    public static int i(final String message) {
        return Log.i("WHOSFIRST", message);
    }
    
    public static int v(final String message) {
        return Log.v("WHOSFIRST", message);
    }
    
    public static int w(final String message) {
        return Log.w("WHOSFIRST", message);
    }
}
