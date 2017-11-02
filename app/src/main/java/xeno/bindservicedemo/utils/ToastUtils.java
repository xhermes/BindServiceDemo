package xeno.bindservicedemo.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import xeno.bindservicedemo.R;


/**
 * Created by xeno on 2016/2/22.
 */
public class ToastUtils {

    private static Toast toast;

    public static void toast(Context context, String msg) {
        if(toast != null) {
            toast.cancel();
        }

        toast = Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_LONG);

        View v = LayoutInflater.from(context.getApplicationContext()).inflate(R.layout.toast_with_warning, null);
        TextView messageView = (TextView)v.findViewById(R.id.toast_tv_message);
        messageView.setText(msg);

        toast.setView(v);
        toast.show();
    }


}
