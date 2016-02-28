package io.github.swiftassist.swiftassist;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by avik on 2/20/2016.
 */
public class ServiceLauncher extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

            context.startService(new Intent(context, EmergencyResponseService.class));
            Toast.makeText(context, "intent recieved", Toast.LENGTH_SHORT);
    }

    private boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
