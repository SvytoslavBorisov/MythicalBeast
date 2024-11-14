package ru.openbiz64.mythicalbeast


import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.widget.SearchView
//import android.widget.SearchView
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.common.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.openbiz64.mythicalbeast.databinding.ActivityMainBinding
import ru.openbiz64.mythicalbeast.dataclass.CommonConst
import ru.openbiz64.mythicalbeast.fragment.ArticleFragment
import ru.openbiz64.mythicalbeast.fragment.FavoriteFragment
import ru.openbiz64.mythicalbeast.fragment.FragmentManager
import ru.openbiz64.mythicalbeast.fragment.BeastsListFragment
import ru.openbiz64.mythicalbeast.model.DataJsonLoader
import ru.openbiz64.mythicalbeast.model.MainViewModel
import ru.openbiz64.mythicalbeast.model.RoomViewModel
import ru.openbiz64.mythicalbeast.MainApp
import ru.openbiz64.mythicalbeast.fragment.GameFragment
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {
    private val versionDB = 2
    private lateinit var form: ActivityMainBinding

    private lateinit var badge: BadgeDrawable

    private lateinit var vm: MainViewModel
    private val fileName = "beasts"
    private var searchView: SearchView? = null

    private val bannerAdEventListener = BannerAdYandexAdsEventListener()
    private var bannerAdSize: BannerAdSize? = null

    private var mainMenu: Menu? = null

    private val roomViewModel: RoomViewModel by viewModels{
        RoomViewModel.RoomViewModelFactory((applicationContext as MainApp).database)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        form = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(form.root)

        APP_CONTEXT = this

        badge = form.bnMenu.getOrCreateBadge(R.id.id_article)
        badge.isVisible = false

        vm = ViewModelProvider(this).get(MainViewModel::class.java)

        // слушатель для фрагментов
        when(vm.getCurrentFragmentId()){
            R.id.id_beast->{
                FragmentManager.setFragment(BeastsListFragment.newInstance(), this)
                toggleMenu(true)
                supportActionBar?.title = getString(R.string.nmBeasts)
            }
            R.id.id_favorite->{
                FragmentManager.setFragment(FavoriteFragment.newInstance(), this)
                toggleMenu(true)
                supportActionBar?.title = getString(R.string.nmFavorite)
            }
            R.id.id_article->{
                FragmentManager.setFragment(ArticleFragment.newInstance(), this)
                toggleMenu(false)
                supportActionBar?.title = getString(R.string.nmArticle)
            }
            R.id.id_games->{
                FragmentManager.setFragment(GameFragment.newInstance(), this)
                toggleMenu(false)
                supportActionBar?.title = getString(R.string.nmGames)
            }
        }
        setBottomNavListener()

        // слушатель на новые истории
        vm.newArticle.observe(this){
            if (it > 0) {
                badge.isVisible = true
                badge.number = it
            }
        }

        // загрузка существ из БД или из файла с добавлением в БД
        // в зависимости от версии БД на телефоне (нужно при обновлении)
        val sharedPref: SharedPreferences = getSharedPreferences(CommonConst.sharedBlock, Context.MODE_PRIVATE)
        val ver = sharedPref.getInt("versionDB", 0)
        Log.e("MyLog", "Control version $ver")
        if (ver != versionDB){
            Log.e("MyLog", "DB is updating")
            val items = DataJsonLoader.getBeastData(fileName, this@MainActivity)
            items.forEach {
                roomViewModel.insertBeast(it)
            }
            roomViewModel.findBeastByMythology()

            val editor: SharedPreferences.Editor = sharedPref.edit()
            editor.putInt("versionDB", versionDB)
            editor.apply()
            Log.e("MyLog", "DB updated")
        } else Log.e("MyLog", "DB is same version")

        vm.setGameData(DataJsonLoader.getGameData("games", this@MainActivity))

        initAds()

        Log.d("MyLog", "Create complete")

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.search_menu, menu)
        mainMenu = menu
        val searchItem: MenuItem? = menu?.findItem(R.id.app_bar_search)
        val searchManager = this@MainActivity.getSystemService(SEARCH_SERVICE) as SearchManager
        if (searchItem != null) {
            searchView = searchItem.actionView as SearchView
        }
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(this@MainActivity.componentName))

        // Обработчик событий поиска
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null) {
                    FragmentManager.getCurrentFragment()?.setSearchString(p0)
                } else {
                    FragmentManager.getCurrentFragment()?.setSearchString("")
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.app_bar_sort ->{
                showDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun clickBtn(txt: Char) {

    }

    private fun showDialog(){
        val dialog = Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_filter_layout)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.gravity = Gravity.CENTER
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = lp
        dialog.window?.attributes = lp
        dialog.setCanceledOnTouchOutside(true)

        val bt_A = dialog.findViewById<Button>(R.id.b_A)
        bt_A.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('А')
            dialog.dismiss()
        }

        val bt_B = dialog.findViewById<Button>(R.id.b_B)
        bt_B.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Б')
            dialog.dismiss()
        }

        val bt_V = dialog.findViewById<Button>(R.id.b_V)
        bt_V.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('В')
            dialog.dismiss()
        }

        val bt_G = dialog.findViewById<Button>(R.id.b_G)
        bt_G.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Г')
            dialog.dismiss()
        }

        val bt_D = dialog.findViewById<Button>(R.id.b_D)
        bt_D.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Д')
            dialog.dismiss()
        }

        val bt_E = dialog.findViewById<Button>(R.id.b_E)
        bt_E.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Е')
            dialog.dismiss()
        }

        val bt_GE = dialog.findViewById<Button>(R.id.b_GE)
        bt_GE.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Ж')
            dialog.dismiss()
        }

        val bt_Z = dialog.findViewById<Button>(R.id.b_Z)
        bt_Z.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('З')
            dialog.dismiss()
        }

        val bt_I = dialog.findViewById<Button>(R.id.b_I)
        bt_I.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('И')
            dialog.dismiss()
        }

        val bt_K = dialog.findViewById<Button>(R.id.b_K)
        bt_K.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('К')
            dialog.dismiss()
        }

        val bt_L = dialog.findViewById<Button>(R.id.b_L)
        bt_L.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Л')
            dialog.dismiss()
        }

        val bt_M = dialog.findViewById<Button>(R.id.b_M)
        bt_M.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('М')
            dialog.dismiss()
        }

        val bt_N = dialog.findViewById<Button>(R.id.b_N)
        bt_N.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Н')
            dialog.dismiss()
        }

        val bt_O = dialog.findViewById<Button>(R.id.b_O)
        bt_O.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('О')
            dialog.dismiss()
        }

        val bt_P = dialog.findViewById<Button>(R.id.b_P)
        bt_P.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('П')
            dialog.dismiss()
        }

        val bt_R = dialog.findViewById<Button>(R.id.b_R)
        bt_R.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Р')
            dialog.dismiss()
        }

        val bt_S = dialog.findViewById<Button>(R.id.b_S)
        bt_S.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('С')
            dialog.dismiss()
        }

        val bt_T = dialog.findViewById<Button>(R.id.b_T)
        bt_T.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Т')
            dialog.dismiss()
        }

        val bt_U = dialog.findViewById<Button>(R.id.b_U)
        bt_U.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('У')
            dialog.dismiss()
        }

        val bt_F = dialog.findViewById<Button>(R.id.b_F)
        bt_F.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Ф')
            dialog.dismiss()
        }

        val bt_X = dialog.findViewById<Button>(R.id.b_X)
        bt_X.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Х')
            dialog.dismiss()
        }

        val bt_TCH = dialog.findViewById<Button>(R.id.b_TCE)
        bt_TCH.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Ц')
            dialog.dismiss()
        }

        val bt_CHE = dialog.findViewById<Button>(R.id.b_CHE)
        bt_CHE.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Ч')
            dialog.dismiss()
        }

        val bt_SCH = dialog.findViewById<Button>(R.id.b_SCH)
        bt_SCH.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Ш')
            dialog.dismiss()
        }

        val bt_EE = dialog.findViewById<Button>(R.id.b_ЕЕ)
        bt_EE.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Э')
            dialog.dismiss()
        }

        val bt_IU = dialog.findViewById<Button>(R.id.b_IU)
        bt_IU.setOnClickListener{
            FragmentManager.getCurrentFragment()?.setFirstItem('Ю')
            dialog.dismiss()
        }



        dialog.show()
    }
    private fun setBottomNavListener(){
        form.bnMenu.setOnItemSelectedListener {

            // именно такая последовательность чтобы закрыть seqrchview
            searchView?.isIconified = true
            searchView?.clearFocus()
            searchView?.isIconified = true


            when (it.itemId){
                R.id.id_beast->{
                    supportActionBar?.title = getString(R.string.nmBeasts)
                    vm.setCurrentFragmentId(R.id.id_beast)
                    FragmentManager.setFragment(BeastsListFragment.newInstance(), this)
                    toggleMenu(true)
                }
                R.id.id_favorite->{
                    supportActionBar?.title = getString(R.string.nmFavorite)
                    vm.setCurrentFragmentId(R.id.id_favorite)
                    FragmentManager.setFragment(FavoriteFragment.newInstance(), this)
                    toggleMenu(true)
                }
                R.id.id_article->{
                    supportActionBar?.title = getString(R.string.nmArticle)
                    vm.setCurrentFragmentId(R.id.id_article)
                    FragmentManager.setFragment(ArticleFragment.newInstance(), this)
                    toggleMenu(false)

                    // при клике на Истории сохраняется новое количество историй
                    val sharedPref: SharedPreferences = APP_CONTEXT.getSharedPreferences(CommonConst.sharedBlock, Context.MODE_PRIVATE)
                    val editor: SharedPreferences.Editor = sharedPref.edit()
                    vm.articleData.value?.let { it1 ->
                        editor.putInt("articleCount", it1.count())
                        editor.apply()
                    }
                    // скрываем значок количества новых историй
                    badge.isVisible = false
                    badge.number = 0

                }
                R.id.id_games->{
                    supportActionBar?.title = getString(R.string.nmGames)
                    vm.setCurrentFragmentId(R.id.id_games)
                    FragmentManager.setFragment(GameFragment.newInstance(), this)
                    toggleMenu(true)
                }
            }
            true
        }
    }
    private fun toggleMenu(b: Boolean){
        mainMenu?.findItem(R.id.app_bar_sort)?.isVisible = b
    }

// ======================= Yandex Ads===========================
    private fun initAds(){
        MobileAds.initialize(this) {        }
        val adRequest= AdRequest.Builder().build()
        val adWidthPixels = resources.displayMetrics.widthPixels
        val adWidthDp = (adWidthPixels / resources.displayMetrics.density).roundToInt()
        bannerAdSize = BannerAdSize.stickySize(this, adWidthDp)

        form.adMainBanner.apply {
            // Replace demo Ad Unit ID with actual Ad Unit ID
            setAdUnitId("R-M-3169707-1")
            //setAdUnitId("demo-banner-yandex")
            setAdSize(bannerAdSize!!)
            setBannerAdEventListener(bannerAdEventListener)
            form.adMainBanner.loadAd(adRequest)
        }
    }
    private inner class BannerAdYandexAdsEventListener : BannerAdEventListener {

        override fun onAdLoaded() {
            Log.d("MyLog","onAdLoaded")
            form.adMainBanner.visibility = View.VISIBLE
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