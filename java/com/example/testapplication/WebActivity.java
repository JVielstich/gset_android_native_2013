package com.example.testapplication;

import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.Html;
import android.text.method.BaseMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity extends Activity {

    protected FrameLayout webViewPlaceholder;
	protected WebView webView;
	protected View gsetWebview;
	protected ViewGroup parentViewGroup;

    ProgressBar loadingProgressBar,loadingTitle;

    String urlGSET = "http://soe.rutgers.edu/gset";
    String linkDomain = "soe.rutgers.edu/gset";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.state_preserving_impl);

		// Initialize the UI
		initUI();
	}

    protected void initUI()
	{
		// Retrieve UI elements
		webViewPlaceholder = ((FrameLayout)findViewById(R.id.webViewPlaceholder));
	
		// Initialize the WebView if necessary
		if (gsetWebview == null)
		{
			// Create the webview
            setContentView(R.layout.website_main);
        	gsetWebview = (View) findViewById(R.id.gsetWebview);
            parentViewGroup = (ViewGroup)gsetWebview.getParent();
			webView = (WebView) findViewById(R.id.webview);
			webView.getSettings().setSupportZoom(false);
			webView.getSettings().setBuiltInZoomControls(false);
			webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.setScrollbarFadingEnabled(true);
			webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setPluginsEnabled(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            
            // Enable downloads of files within webView
            webView.setDownloadListener(new DownloadListener() {
                public void onDownloadStart(String url, String userAgent,
                        String contentDisposition, String mimetype,
                        long contentLength) {
                  Intent i = new Intent(Intent.ACTION_VIEW);
                  i.setData(Uri.parse(url));
                  startActivity(i);
                }
            });


			// Attach the ProgressBar layout
            loadingProgressBar=(ProgressBar)findViewById(R.id.progressbar_Horizontal);

            webView.setWebChromeClient(new WebChromeClient() {

                // this will be called on page loading progress
                @Override
                public void onProgressChanged(WebView view, int newProgress) {

                    super.onProgressChanged(view, newProgress);

                    loadingProgressBar.setProgress(newProgress);

                    // hide the progress bar if the loading is complete
                    if (newProgress == 100) {
                    loadingProgressBar.setVisibility(View.GONE);
                    } else{
                    loadingProgressBar.setVisibility(View.VISIBLE);
                    }
                }
                
            });   

            webView.setWebViewClient(new WebViewClient() {
            
		        @Override
		        public boolean shouldOverrideUrlLoading(WebView view, String url) {


	                // If the site/domain matches, do not override; let myWebView load the page
		            if (Uri.parse(url).getHost().equals(linkDomain)) {
		                return false;
		            }

		            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
		            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		            startActivity(intent);
		            return true;
		        }

            
	        });
	        
			// Load the first page
			webView.loadUrl(urlGSET);

		}
		parentViewGroup.removeView(gsetWebview);
		// Attach the WebView to its placeholder
		parentViewGroup.addView(gsetWebview);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);

		if (webView != null)
		{
			// Remove the WebView from the old placeholder
			parentViewGroup.removeView(gsetWebview);
		}

		// Load the layout resource for the new configuration
        setContentView(R.layout.state_preserving_impl);

		// Reinitialize the UI
		initUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Save the state of the WebView
		webView.saveState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		// Restore the state of the WebView
		webView.restoreState(savedInstanceState);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webmenu, menu);
        return true;
    }

    // Menu Selections
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_reload:
                Toast.makeText(getApplicationContext(),
                "Reloading web page",
                Toast.LENGTH_LONG).show();
                webView.reload();
                break;

            default:
                break;

        }
        return true;
    }

    public void onBackPressed (){
        if (webView.isFocused() && webView.canGoBack()) {
                webView.goBack();       
        }else {
                WebActivity.this.finish();
        }
    }

}
