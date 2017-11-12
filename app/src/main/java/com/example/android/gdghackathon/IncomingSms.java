package com.example.android.gdghackathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by sharaddadhich on 12/11/17.
 */

public class IncomingSms extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();


    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if(message.contains("Team Khoj")) {

                        Intent gotomain =  new Intent();
                        gotomain.putExtra("lat",28.6184);
                        gotomain.putExtra("long",77.3726);
                        gotomain.setClassName("com.example.android.gdghackathon","com.example.android.gdghackathon.Activities.GetLocationActivity");
                        gotomain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(gotomain);
                    }

                    Log.i("SmsReceiver", "senderNum: "+ senderNum + "; message: " + message);


                    // Show Alert
                    int duration = Toast.LENGTH_LONG;


                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}

