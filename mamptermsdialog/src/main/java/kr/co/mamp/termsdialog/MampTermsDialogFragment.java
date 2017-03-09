package kr.co.mamp.termsdialog;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MampTermsDialogFragment extends DialogFragment {


    /**
     * 개인정보 취급방침 URL.
     */
    private static final String MAMP_URL = "https://mampcorp.appspot.com/term.html?pk=";
    /**
     * 다이얼로그 빌더.
     */
    private Builder builder;
    /**
     * 커스텀 뷰.
     */
    private View contentView;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppDialogTheme);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException("Missing required INTERNET permission.");
        }
    }


    /**
     * 다이얼로그 설정하기.
     */
    @SuppressWarnings("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        contentView = View.inflate(getContext(), R.layout.dialog_mamp_terms, null);
        dialog.setContentView(contentView);

        if (builder != null) {
            if (builder.isDimEnabled) {
                Window window = dialog.getWindow();
                if (window != null) {
                    dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                    WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                    if (layoutParams != null) {
                        layoutParams.dimAmount = builder.dimAmount;
                    }
                }
            }
            dialog.setCanceledOnTouchOutside(builder.isOutsideCancelable);
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 커스텀 뷰 설정하기.
        initView();
    }

    /**
     * 커스텀 뷰 설정하기.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initView() {
        // 닫기 버튼.
        contentView.findViewById(R.id.popup_close)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismissAllowingStateLoss();
                    }
                });

        // 웹뷰.
        WebView webView = (WebView) contentView.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(webViewClient);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webView.loadUrl(getPrivacyStatementUrl());
    }


    /**
     * 개인정보 취급방침 url 가져오기.
     */
    private String getPrivacyStatementUrl() {
        if (builder != null && !TextUtils.isEmpty(builder.url)) return builder.url;
        else return MAMP_URL + getContext().getPackageName();
    }


    /**
     * 다이얼로그 표시.
     */
    public void show(FragmentManager fragmentManager) {
        fragmentManager.beginTransaction()
                .add(this, getTag())
                .commitAllowingStateLoss();
    }


    /**
     * 웹뷰 클라이언트.
     */
    private WebViewClient webViewClient = new WebViewClient() {


        /**
         * SSL 오류 처리.
         */
        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

            // 메시지 생성.
            StringBuilder message = new StringBuilder();
            message.append(getString(R.string.certificate_error));
            message.append("\n");
            switch (error.getPrimaryError()) {
                case SslError.SSL_UNTRUSTED:
                    message.append(getString(R.string.SSL_UNTRUSTED));
                    break;
                case SslError.SSL_EXPIRED:
                    message.append(getString(R.string.SSL_EXPIRED));
                    break;
                case SslError.SSL_IDMISMATCH:
                    message.append(getString(R.string.SSL_IDMISMATCH));
                    break;
                case SslError.SSL_NOTYETVALID:
                    message.append(getString(R.string.SSL_NOTYETVALID));
                    break;
            }
            message.append("\n");
            message.append(getString(R.string.ssl_continue_message));

            // 계속할지 묻기.
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.ssl_certificate_error))
                    .setMessage(message)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                            MampTermsDialogFragment.this.dismissAllowingStateLoss();
                        }
                    })
                    .create().show();
        }
    };


    /**
     * 다이얼로그 빌더.
     */
    @SuppressWarnings("unused")
    public static class Builder {


        /**
         * 다이얼로그 여백 클릭 시 닫기 여부.
         */
        private boolean isOutsideCancelable = false;
        /**
         * 다이얼로그 어두운 영역 활성 여부.
         */
        private boolean isDimEnabled = false;
        /**
         * 다이얼로그 어두운 정도.
         * 값이 클수록 진함.(0.0 ~ 1.0)
         */
        private float dimAmount = 0.4f;
        /**
         * 개인정보 취급방침 url.
         */
        private String url;


        /**
         * 다이얼로그 여백 클릭 시 닫기 여부 설정.
         */
        public Builder setOutsideCancelable(boolean cancelable) {
            isOutsideCancelable = cancelable;
            return this;
        }


        /**
         * 다이얼로그 어두운 영역 활성 여부 설정.
         */
        public Builder setDimEnabled(boolean enabled) {
            isDimEnabled = enabled;
            return this;
        }


        /**
         * 다이얼로그 어두운 정도 설정.
         */
        public Builder setDimAmount(float dimAmount) {
            this.dimAmount = dimAmount;
            return this;
        }


        /**
         * 개인정보 취급방침 url 지정하기.
         */
        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }


        /**
         * 다이얼로그 생성.
         */
        public MampTermsDialogFragment create() {

            MampTermsDialogFragment dialogFragment = new MampTermsDialogFragment();
            dialogFragment.builder = this;
            return dialogFragment;
        }
    }
}
