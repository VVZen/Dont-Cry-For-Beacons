package it.vvzen.dontcryforbeacon;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class State2Activity extends AppCompatActivity {

    private static final String TAG = State2Activity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state2);

        // Assigning ui vars to xml
        WebView webView = (WebView) this.findViewById(R.id.wv_state2);
        webView.getSettings().getAllowContentAccess();
        webView.getSettings().getJavaScriptEnabled();
        webView.setWebChromeClient(new WebChromeClient());

        if(MyApp.isInDebug){
            Log.e(TAG, "Entered on State2Activity");
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            String url = extras.getString("URL");
            webView.loadUrl(url);

        }
    }
}
