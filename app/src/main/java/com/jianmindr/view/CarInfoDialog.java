package com.jianmindr.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.jianmindr.R;

/**
 * Created by brightman on 5/8/2017.
 */

public class CarInfoDialog {
    private Dialog mDialog;
    public TextView positive;

    public CarInfoDialog(Context context, String message, @DrawableRes int resId, float fRate) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_layout, null);

        mDialog = new Dialog(context, R.style.dialog_full);
        mDialog.setContentView(view);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    return true;
                }
                return false;
            }
        });

        TextView dialog_message = (TextView) view.findViewById(R.id.dialog_message);
        dialog_message.setText(message);
        ImageView img_head = (ImageView) view.findViewById(R.id.img_head);
        img_head.setImageResource(resId);
        RatingBar rate_driver = (RatingBar) view.findViewById(R.id.rate_driver);
        rate_driver.setRating(fRate);

        positive = (TextView) view.findViewById(R.id.positive);
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }
}
