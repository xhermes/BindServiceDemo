package xeno.bindservicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import xeno.bindservicedemo.utils.ToastUtils;


/**
 * Created by xeno on 2017/10/30.
 */

public class LocalService extends Service {

    public static final String FROM_LOCAL_BINDER = "Activity: RECEIVED a message from local binder..";
    private static final String FROM_ACTIVITY = "LocalService: Activity: RECEIVED from Activity";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            ToastUtils.toast(getApplicationContext(), FROM_ACTIVITY);
        }
    };

    MyBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        public LocalService getService() {
            return LocalService.this;
        }
    }
}
