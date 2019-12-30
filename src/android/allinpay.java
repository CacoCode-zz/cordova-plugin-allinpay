package org.apache.cordova.xl;

import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.allinpay.sdkwallet.facade.TlWalletSdk;
import com.allinpay.sdkwallet.facade.WalletVerifyDelegate;

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

    private static final String TAG = "allinpay";
    private String PHONE_STRING = "";
    private String USERID_STRING = "";
    private String TOKEN_STRING = "";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        Log.i(TAG, "initialize");
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.i(TAG, action + " args: " + args.toString());
        if (action.equals("toCustomActivity")) {
            String functionCode = args.getString(0);
            String subFunctionCode = args.getString(1);
            this.toCustomActivity(functionCode, subFunctionCode, callbackContext);
            return true;
        }
        if (action.equals("verifyWallet")) {
            String phone = args.getString(0);
            String userId = args.getString(1);
            String token = args.getString(2);
            this.verifyWallet(phone, userId, token, callbackContext);
            return true;
        }
        return false;
    }

    private void verifyWallet(String phone, String userId, String token, CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                TlWalletSdk.verifyWallet(cordova.getActivity(), phone, userId, token);
                PHONE_STRING = phone;
                USERID_STRING = userId;
                TOKEN_STRING = token;
                callbackContext.success("VERIFY_WALLET_SUCCESS");
            }
        });
    }

    private void toCustomActivity(String functionCode, String subFunctionCode, CallbackContext callbackContext) {
        if (functionCode != null && functionCode.length() > 0) {
            cordova.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    TlWalletSdk.getInstance().toCustomActivity(functionCode, subFunctionCode,
                            new WalletVerifyDelegate() {
                                @Override
                                public void onLogin() {
                                    Log.i(TAG, "onLogin: " + PHONE_STRING);
                                    TlWalletSdk.verifyWallet(cordova.getActivity(), PHONE_STRING, USERID_STRING, TOKEN_STRING);
                                }

                                @Override
                                public void onError(int errorCode, String msg) {
                                    Log.i(TAG, "onError: " + msg);
                                    if (errorCode == TlWalletSdk.ERROR_TOKEN_INVAILED_CODE) {
                                        // 1、短时间连续请求5次返回参数无效，sdk自动阻断。
                                        // 2、重新获取token
                                        // 3、重新验证身份
                                        TlWalletSdk.verifyWallet(cordova.getActivity(), PHONE_STRING, USERID_STRING, TOKEN_STRING);
                                    }
                                    callbackContext.error(msg);
                                }

                                @Override
                                public void onSuccess(int functionCode, String msg) {
                                    Log.i(TAG, "onSuccess: " + msg);
                                }

                                @Override
                                public void onStartLoading() {
                                    Log.i(TAG, "onStartLoading: ");
                                }

                                @Override
                                public void onDismissLoading() {
                                    Log.i(TAG, "onDismissLoading: ");
                                }
                            });
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
