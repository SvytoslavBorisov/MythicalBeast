package ru.openbiz64.mythicalbeast

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.webkit.WebSettings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import ru.openbiz64.mythicalbeast.R

import ru.openbiz64.mythicalbeast.databinding.WebLayoutBinding
import kotlin.math.roundToInt

class WebActivity : AppCompatActivity() {
    lateinit var form: WebLayoutBinding

    private val bannerAdEventListener = BannerAdYandexAdsEventListener()
    private var bannerAdSize: BannerAdSize? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        form = WebLayoutBinding.inflate(layoutInflater)
        setContentView(form.root)
        super.onCreate(savedInstanceState)

        val title = intent.getStringExtra("title")
        val url = intent.getStringExtra("url")


        if (title?.isNotEmpty() == true) supportActionBar?.title = title
        if (url?.isNotEmpty() == true)  {
            if (url.contains("http")){
                form.webBrowser.loadUrl(url)
            } else {
                form.webBrowser.loadUrl("file:///android_asset/html/$url.html")
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val webSettings: WebSettings =  form.webBrowser.settings
        webSettings.javaScriptEnabled = true

        adsInit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun adsInit(){

        val adRequest= AdRequest.Builder().build()
        val adWidthPixels = resources.displayMetrics.widthPixels
        val adWidthDp = (adWidthPixels / resources.displayMetrics.density).roundToInt()
        bannerAdSize = BannerAdSize.stickySize(this@WebActivity, adWidthDp)

        form.adWebView.apply {
            // Replace demo Ad Unit ID with actual Ad Unit ID
            setAdUnitId("R-M-3169707-2")
            //setAdUnitId("demo-banner-yandex")
            setAdSize(bannerAdSize!!)
            setBannerAdEventListener(bannerAdEventListener)
        }

        MobileAds.initialize(this as Activity){}

        form.adWebView.loadAd(adRequest)

    }

    private inner class BannerAdYandexAdsEventListener : BannerAdEventListener {

        override fun onAdLoaded() {
            Log.d("MyLog","onAdLoaded")
            form.adWebView.isVisible = true
        }

        override fun onAdFailedToLoad(adRequestError: AdRequestError) {
            Log.d("MyLog", adRequestError.description)
        }

        override fun onAdClicked() {
            Log.d("MyLog", "onAdClicked")
        }

        override fun onLeftApplication() {
            Log.d("MyLog", "onLeftApplication")
        }

        override fun onReturnedToApplication() {
            Log.d("MyLog", "onReturnedToApplication")
        }

        override fun onImpression(p0: ImpressionData?) {
            Log.d("MyLog", "onImpression")
        }
    }

}