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

/**
 * This class echoes a string called from JavaScript.
 */
public class allinpay extends CordovaPlugin {

    private static final String functionCode = "ACCT1007";
    private static final String subFunctionCode = "001";
    private static final String phone = "18774957686";
    private static final String userId = "190514550483818";

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
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
                    TlWalletSdk.getInstance().toCustomActivity(functionCode,subFunctionCode, new WalletVerifyDelegate() {
                        @Override
                        public void onLogin() {
                            TlWalletSdk.verifyWallet(cordova.getActivity(), phone, userId, "");
                            callbackContext.success(phone);
                        }

                        @Override
                        public void onError(int errorCode, String msg) {
                            if (errorCode == TlWalletSdk.ERROR_TOKEN_INVAILED_CODE) {
                                //1、短时间连续请求5次返回参数无效，sdk自动阻断。
                                //2、重新获取token
                                //3、重新验证身份
                                TlWalletSdk.verifyWallet(cordova.getActivity(), phone, userId, "");
                            }
                            callbackContext.error(msg);
                        }

                        @Override
                        public void onSuccess(int functionCode, String msg) {

                        }

                        @Override
                        public void onStartLoading() {
                            //成功,可以在这里做startLoading操作
                            try {
                               //callbackContext.error("onStartLoading");
                            } catch (Exception e) {

                            }
                        }

                        @Override
                        public void onDismissLoading() {
                            //callbackContext.error("onDismissLoading");
                        }
                    });
                   // callbackContext.success(message); // Thread-safe.
                }
            });
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
