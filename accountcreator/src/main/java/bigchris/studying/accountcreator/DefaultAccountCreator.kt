package bigchris.studying.accountcreator

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

val TAG = "DEFAULTACCOUNTCREATOR"

internal class DefaultAccountCreator : AccountCreator {
    var webView: WebView? = null

    override fun createAccount(email: String, username: String, password: String, context: Context) {
        webView = WebView(context)
        webView?.apply {
            loadUrl("https://old.reddit.com/")
            settings.javaScriptEnabled = true
            webViewClient = CustomWebViewClient(email, username, password)
            setOnKeyListener { view, keyCode, event ->
                if (keyCode.equals(KeyEvent.KEYCODE_BACK) && canGoBack()) {
                    goBack()
                    true
                } else {
                    false
                }
            }
        }
        webView = null
    }

}

class CustomWebViewClient(val email: String, val username: String, val password: String) : WebViewClient() {
    var currentlyLoading = false
    var loginClicked = false
    var enteredEmail = false

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
        return super.shouldOverrideUrlLoading(view, url)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        Log.i(TAG, "onPageFinished")
        if (view != null && !currentlyLoading) {
            if (!loginClicked)
                clickLogin(view)
            else if (!enteredEmail)
                enterEmail(view)
        }
        super.onPageFinished(view, url)
    }

    fun clickLogin(view: WebView) {
        currentlyLoading = true
        view.evaluateJavascript(JavascriptStrings.CLICK_LOGIN) {
            view.postVisualStateCallback(100, CustomVisualCallback())
            loginClicked = true
            currentlyLoading = false
            Log.i(TAG, "Clicked.")
        }
    }

    fun enterEmail(view: WebView) {
        currentlyLoading = true
        view.evaluateJavascript(JavascriptStrings.ENTER_EMAIL + "'${email}'") {
            enteredEmail = true
            currentlyLoading = false
            Log.i(TAG, "Entered Email")
        }
    }
}

class CustomVisualCallback : WebView.VisualStateCallback() {
    override fun onComplete(p0: Long) {
        Log.i(TAG, "Visual Callback")
    }
}