package xeno.bindservicedemo.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import xeno.bindservicedemo.ICalculate;
import xeno.bindservicedemo.R;
import xeno.bindservicedemo.service.AidlService;
import xeno.bindservicedemo.service.LocalService;
import xeno.bindservicedemo.service.MessengerService;
import xeno.bindservicedemo.utils.ToastUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int WHAT_PLUS = 1;
    public static final int WHAT_MULTIPLY = 2;
    private static final String SEND_FROM_ACTIVITY_MESSENGER = "MessengerService: RECEIVED from Activity..";
    public static final String SEND_FROM_ACTIVITY_AIDL = "AidlService: RECEIVED a message from Activity..";

    public static final int STATUS_CONNECTED = 1;
    public static final int STATUS_DISCONNECTED = 2;

    private AppCompatButton mBindBtn;
    private AppCompatButton mSendBtn;
    private AppCompatButton mLocalBtn;
    private AppCompatButton mAidlBtn;

    private LinearLayout mLayout;

    private Messenger mSendingMessenger = null;
    private Messenger mReceivingMessenger = null;

    private LocalService mLocalService;
    private ICalculate mICalculate;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
          showOnLayout("answer from service => " + msg.arg1);
        }
    };

    private int mStatus = STATUS_DISCONNECTED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendBtn = (AppCompatButton) findViewById(R.id.send_message);
        mBindBtn = (AppCompatButton) findViewById(R.id.bind);
        mLocalBtn = (AppCompatButton) findViewById(R.id.local);
        mAidlBtn = (AppCompatButton) findViewById(R.id.aidl);
        mLayout = (LinearLayout) findViewById(R.id.live_layout);

        mSendBtn.setOnClickListener(this);
        mBindBtn.setOnClickListener(this);
        mLocalBtn.setOnClickListener(this);
        mAidlBtn.setOnClickListener(this);

        mReceivingMessenger = new Messenger(mHandler);
    }

    ServiceConnection mMessengerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mStatus = STATUS_CONNECTED;
            showOnLayout("-> MessengerService onServiceConnected");
            mSendingMessenger = new Messenger(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mStatus = STATUS_DISCONNECTED;
            showOnLayout("-> MessengerService onServiceDisconnected");
        }
    };

    ServiceConnection mLocalConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            showOnLayout("-> Local onServiceConnected");
            LocalService.MyBinder binder = ((LocalService.MyBinder) service);
                    mLocalService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            showOnLayout("-> Local onServiceDisconnected");
        }
    };

    ServiceConnection mAidlConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            showOnLayout("-> Aidl onServiceConnected");
            mICalculate = ICalculate.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            showOnLayout("-> Aidl onServiceDisconnected");
        }
    };

    private void aidl() {
        showOnLayout("send by Aidl, arg1 = 100, arg2 = 250");
        if(mICalculate != null) {
            try {
                int a = mICalculate.plus(100, 250);
                showOnLayout("answer from aidl => " + a);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
//            showOnLayout();
        }

    }


    public void localBinder() {

    }

    public void sendMessage() {
        if (mStatus == STATUS_DISCONNECTED) {
            showOnLayout("Send message fail, service is disconnected.");
            return;
        }

        // Create and send a message to the service, using a supported 'what' value
        showOnLayout("send by Messenger, arg1 = 666, arg2 = 888");
        Message msg = Message.obtain(null, WHAT_PLUS, 666, 888);
        msg.replyTo = mReceivingMessenger;

        try {
            mSendingMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showOnLayout(String s) {
        TextView tv = new TextView(MainActivity.this);
        tv.setText(s);
        mLayout.addView(tv);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.local:
                localBinder();
                break;
            case R.id.send_message:
                sendMessage();
                break;
            case R.id.aidl:
                aidl();
                break;
            case R.id.bind:
                Intent intent = new Intent(MainActivity.this, MessengerService.class);
                bindService(intent, mMessengerConnection, BIND_AUTO_CREATE);
                Intent intent2 = new Intent(MainActivity.this, LocalService.class);
                bindService(intent2, mLocalConnection, BIND_AUTO_CREATE);
                Intent intent3 = new Intent(MainActivity.this, AidlService.class);
                bindService(intent3, mAidlConnection, BIND_AUTO_CREATE);
                break;
        }
    }
}
