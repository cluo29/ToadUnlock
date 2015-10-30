package teamc.toadunlock;

import android.graphics.drawable.Drawable;

/**
 * Created by Comet on 27/10/15.
 */

public class AppInfo {
    private Drawable icon;
    private String appName;
    private String packageName;
    private boolean isSystemApp;
    private long codesize;
    public long getCodesize() {
        return codesize;
    }
    public void setCodesize(long codesize) {
        this.codesize = codesize;
    }
    public Drawable getIcon() {
        return icon;
    }
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public boolean isSystemApp() {
        return isSystemApp;
    }
    public void setSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }

}