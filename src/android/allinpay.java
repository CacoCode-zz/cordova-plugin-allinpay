package org.apache.cordova.plugin;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.allinpay.sdkwallet.facade.TlWalletSdk;

import java.lang.Object;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class allinpay extends CordovaPlugin {

    private static final String TAG = "org.apache.cordova.ali.Alipay";
    private static final String Execute = "Execute: ";
    private static final String with = " with: ";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, Execute + action + with + args.toString());
        if (action.equals("coolMethod")) {
            String message = args.getString(0);
            this.coolMethod(message, callbackContext);
            return true;
        }
        return false;
    }

    private void coolMethod(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    callbackContext.success(message); // Thread-safe.
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
