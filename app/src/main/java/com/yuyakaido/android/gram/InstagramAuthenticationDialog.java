package com.yuyakaido.android.gram;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by yuyakaido on 12/26/15.
 */
public class InstagramAuthenticationDialog extends DialogFragment {

    public interface InstagramAuthenticationCallback {
        void onInstagramAuthenticationCompleted(String accessToken);
    }

    private static final String AUTHENTICATION_BASE_URL = "https://api.instagram.com/oauth/authorize/";

    public static final String CLIENT_ID = "85c5d32532df41de97294ac10bb4f420";
    public static final String REDIRECT_URI = "http://yuyakaido.hatenablog.com/";

    private static final String KEY_CLIENT_ID = "client_id";
    private static final String KEY_REDIRECT_URI = "redirect_uri";
    private static final String KEY_RESPONSE_TYPE = "response_type";

    private InstagramAuthenticationCallback mCallback;

    private static String getAuthenticationUrl() {
        StringBuilder builder = new StringBuilder(AUTHENTICATION_BASE_URL);
        builder.append("?");
        builder.append(KEY_CLIENT_ID).append("=").append(CLIENT_ID).append("&");
        builder.append(KEY_REDIRECT_URI).append("=").append(REDIRECT_URI).append("&");
        builder.append(KEY_RESPONSE_TYPE).append("=").append("token");
        return builder.toString();
    }

    public static DialogFragment newInstance() {
        return new InstagramAuthenticationDialog();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof InstagramAuthenticationCallback) {
            mCallback = (InstagramAuthenticationCallback) activity;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_instagram_authentication, container, false);

        WebView webView = (WebView) view.findViewById(R.id.dialog_instagram_authentication_web_view);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new InstagramWebViewClient());
        String url = getAuthenticationUrl();
        webView.loadUrl(url);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getContext());
        Window window = dialog.getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        lp.width = metrics.widthPixels;
        dialog.getWindow().setAttributes(lp);
    }

    private class InstagramWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(REDIRECT_URI)) {
                String urls[] = url.split("=");
                if (mCallback != null) {
                    mCallback.onInstagramAuthenticationCompleted(urls[1]);
                }
                dismissAllowingStateLoss();
                return true;
            }
            return false;
        }

    }

}
