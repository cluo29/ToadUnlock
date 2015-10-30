package teamc.toadunlock;

/**
 * Created by Comet on 27/10/15.
 */
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;


public class AppInfoProvider {

    private PackageManager packageManager;

    public AppInfoProvider(Context context){
        packageManager = context.getPackageManager();

    }

    public List<AppInfo> getAllApps(){

        List<AppInfo> list = new ArrayList<AppInfo>();
        AppInfo myAppInfo;

        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for(PackageInfo info:packageInfos){
            myAppInfo = new AppInfo();

            String packageName = info.packageName;

            ApplicationInfo appInfo = info.applicationInfo;

            Drawable icon = appInfo.loadIcon(packageManager);

            String appName = appInfo.loadLabel(packageManager).toString();

            myAppInfo.setPackageName(packageName);
            myAppInfo.setAppName(appName);
            myAppInfo.setIcon(icon);

            if(filterApp(appInfo)){
                myAppInfo.setSystemApp(false);
            }else{
                myAppInfo.setSystemApp(true);
            }
            list.add(myAppInfo);
        }
        return list;

    }

    //system app? true or false
    public boolean filterApp(ApplicationInfo info){

        if((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
            return true;
        }else if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
            return true;
        }
        return false;
    }
}
