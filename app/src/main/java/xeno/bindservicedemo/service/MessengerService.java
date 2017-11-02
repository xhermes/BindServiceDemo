package xeno.bindservicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import xeno.bindservicedemo.activity.MainActivity;
import xeno.bindservicedemo.utils.ToastUtils;


/**
 * Created by xeno on 2017/10/30.
 * Service实现一个 Handler，由其接收来自客户端的每个调用的回调
 * Handler 用于创建 Messenger 对象（对 Handler 的引用）
 * Messenger 创建一个 IBinder，Service通过 onBind() 使其返回客户端
 * 客户端使用 IBinder 将 Messenger（引用Service的 Handler）实例化，然后使用后者将 Message 对象发送给Service
 * Service在其 Handler 中（具体地讲，是在 handleMessage() 方法中）接收每个 Message。
 这样，客户端并没有调用Service的“方法”。而客户端传递的“消息”（Message 对象）是Service在其 Handler 中接收的。
 */

public class MessengerService extends Service {


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int a = msg.arg1 + msg.arg2;
            Message msgReply = Message.obtain(this, 0, a, 0);
            try {
                msg.replyTo.send(msgReply);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };
    private Messenger mMessenger;

    public MessengerService() {
        super();
        mMessenger = new Messenger(mHandler);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "-> MessageService onBind()", Toast.LENGTH_SHORT).show();
        return mMessenger.getBinder();
    }

}
