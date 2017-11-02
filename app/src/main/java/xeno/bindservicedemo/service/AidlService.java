package xeno.bindservicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import xeno.bindservicedemo.ICalculate;

/**
 * Created by xeno on 2017/11/1.
 */

public class AidlService extends Service {

    private CalculateImpl mCalculate = new CalculateImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mCalculate;
    }

    public String getString() {
        return "hahahahah";
    }

    public class CalculateImpl extends ICalculate.Stub {

        @Override
        public int plus(int a, int b) throws RemoteException {
            return a + b;
        }

        @Override
        public int multiply(int a, int b) throws RemoteException {
            return a * b;
        }

        public AidlService getService() {
            return AidlService.this;
        }
    }
}
