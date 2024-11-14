package ru.openbiz64.mythicalbeast


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle

import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import ru.openbiz64.mythicalbeast.databinding.QuizLayoutBinding
import java.io.IOException


import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.common.*
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener
import com.yandex.mobile.ads.rewarded.RewardedAdLoader
import com.yandex.mobile.ads.rewarded.RewardedAdLoadListener
import ru.openbiz64.mythicalbeast.dataclass.DataClassQuestion
import ru.openbiz64.mythicalbeast.dataclass.DataClassStatisticResult
import ru.openbiz64.mythicalbeast.dataclass.GameConst
import kotlin.math.roundToInt

class QuestionActivity: AppCompatActivity(), View.OnClickListener, RewardedAdLoadListener {

    private lateinit var form: QuizLayoutBinding
    private lateinit var questionList: ArrayList<DataClassQuestion>
    private var mCurrentPosition: Int = 1
    private var mCorrectAnswers: Int = 0
    private var mWrongAnswers: Int = 0
    private var mCommonQuestion: Int = 0

    private var mLastClickTime: Long = 0
    private lateinit var question: DataClassQuestion

    private var mType: String = ""
    private var mCaption: String = ""

    private var rewardedAd: RewardedAd? = null
    private var rewardedAdLoader: RewardedAdLoader? = null
    private var isAdsload: Boolean = false
    private var bannerAdSize: BannerAdSize? = null
    private var selectedIndex: Int = 0

    private val bannerAdEventListener = BannerAdYandexAdsEventListener()
    private val eventLogger = RewardedAdEventLogger()

    private var statisticData = ArrayList<DataClassStatisticResult>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        form = QuizLayoutBinding.inflate(layoutInflater)
        setContentView(form.root)

        mType = intent.getStringExtra(GameConst.TYPE).toString()
        mCaption = intent.getStringExtra(GameConst.CAPTION).toString()


        val fileName: String = mType
        mCommonQuestion = GameConst.question_for_champgame_count

        questionList = GetQuestionsHelper.getQuestions(fileName, this)
        if (questionList.isEmpty()) finish()

        question = questionList[0]
        if (mCommonQuestion > questionList.size) mCommonQuestion = questionList.size

        //mCommonQuestion = questionList.size //!!!!!!!!!!!!ТОЛЬКО ДЛЯ ТЕСТА!!!!!!!!!!!!!!!!

        val str = "$mCurrentPosition/$mCommonQuestion"
        form.tvProgerss.text = str

        adsInit()

        rewardedAdLoader = RewardedAdLoader(this).apply {
            setAdLoadListener(this@QuestionActivity)
        }
        loadRewardedAd()

        getQuestions()

        form.progressBar.max = mCommonQuestion
        form.bBackQuiz.setOnClickListener { goToBack() }

        form.bHelp.setOnClickListener {
            showRewardedAd()
        }
    }

    override fun onDestroy() {
        rewardedAdLoader?.setAdLoadListener(null)
        rewardedAdLoader = null
        destroyRewardedAd()
        super.onDestroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        goToBack()
    }

    private fun goToBack(){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()

    }

    private fun getQuestions(){

        val tvAnswerLineList = ArrayList<TextView>()
        tvAnswerLineList.add(0,  form.tvAnswerLine1Caption)
        tvAnswerLineList.add(1,  form.tvAnswerLine2Caption)

        val tvAnswerList = ArrayList<TextView>()
        tvAnswerList.add(0,  form.tvAnswer1Caption)
        tvAnswerList.add(1,  form.tvAnswer2Caption)
        tvAnswerList.add(2,  form.tvAnswer3Caption)
        tvAnswerList.add(3,  form.tvAnswer4Caption)

        val ivAnswerList = ArrayList<ImageView>()
        ivAnswerList.add(0,  form.ivAnswer1Image)
        ivAnswerList.add(1,  form.ivAnswer2Image)
        ivAnswerList.add(2,  form.ivAnswer3Image)
        ivAnswerList.add(3,  form.ivAnswer4Image)

        form.clAnswer1.visibility = View.VISIBLE
        form.clAnswer2.visibility = View.VISIBLE
        form.clAnswer3.visibility = View.VISIBLE
        form.clAnswer4.visibility = View.VISIBLE

        var listNumbers:ArrayList<Int> = arrayListOf(0,1,2,3)
        listNumbers.shuffle()

        var listLineNumbers:ArrayList<Int> = arrayListOf(0,1)
        listLineNumbers.shuffle()

        form.progressBar.progress = mCurrentPosition
        question = questionList[mCurrentPosition-1]
        form.tvProgerss.text = mCurrentPosition.toString() + "/" + mCommonQuestion.toString()

        if (question.type >= 6) {
            form.layoutQuestionLine.visibility = View.VISIBLE
            form.layoutQuestion.visibility = View.GONE
            form.bHelp.visibility =  View.INVISIBLE
        }  else {
            form.layoutQuestion.visibility = View.VISIBLE
            form.layoutQuestionLine.visibility = View.GONE
            if (isAdsload) form.bHelp.visibility =  View.VISIBLE
        }

        when (question.type) {

            1 -> {
                try {
                    val inputStream = applicationContext.assets.open("images/" + question.slugQuestion + ".webp")
                    val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                    form.ivImage.setImageBitmap(bmp)
                    form.ivImage.visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                }


                form.tvQuestion.text = question.textQuestion

                for (i in 0..2){
                    val j = listNumbers[i]
                    ivAnswerList[j].visibility = View.GONE

                    tvAnswerList[j].visibility = View.VISIBLE
                    tvAnswerList[j].text = question.wrongAnswers[i].text
                }

                ivAnswerList[listNumbers[3]].visibility = View.GONE
                tvAnswerList[listNumbers[3]].visibility = View.VISIBLE
                tvAnswerList[listNumbers[3]].text = question.textCorrectAnswer

            }

            2 -> {
                try {
                    val inputStream = applicationContext.assets.open("images/" + question.slugQuestion + ".webp")
                    val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                    form.ivImage.setImageBitmap(bmp)
                    form.ivImage.visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                }
                form.tvQuestion.text = question.textQuestion

                for (i in 0..2) {
                    val j = listNumbers[i]

                    try {
                        val inputStream = applicationContext.assets.open("images/" + question.wrongAnswers[j].slug + ".webp")
                        val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                        ivAnswerList[j].setImageBitmap(bmp)
                        ivAnswerList[j].visibility = View.VISIBLE
                    } catch (e: IOException) {
                        Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                    }

                    tvAnswerList[j].visibility = View.VISIBLE
                    tvAnswerList[j].text = question.wrongAnswers[j].text
                }

                try {
                    val inputStream = applicationContext.assets.open("images/" + question.slugCorrectAnswer + ".webp")
                    val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                    ivAnswerList[listNumbers[3]].setImageBitmap(bmp)
                    ivAnswerList[listNumbers[3]].visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                }
                tvAnswerList[listNumbers[3]].visibility = View.VISIBLE
                tvAnswerList[listNumbers[3]].text = question.textCorrectAnswer

            }

            3 -> {
                form.ivImage.visibility = View.GONE
                form.tvQuestion.text = question.textQuestion

                for (i in 0..2) {
                    val j = listNumbers[i]
                    ivAnswerList[j].visibility = View.GONE
                    tvAnswerList[j].visibility = View.VISIBLE
                    tvAnswerList[j].text = question.wrongAnswers[j].text
                }

                ivAnswerList[listNumbers[3]].visibility = View.GONE
                tvAnswerList[listNumbers[3]].visibility = View.VISIBLE
                tvAnswerList[listNumbers[3]].text = question.textCorrectAnswer

            }

            4 -> {
                form.ivImage.visibility = View.GONE
                form.tvQuestion.text = question.textQuestion

                for (i in 0..2) {
                    val j = listNumbers[i]

                    try {
                        val inputStream = applicationContext.assets.open("images/" + question.wrongAnswers[j].slug + ".webp")
                        val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                        ivAnswerList[j].setImageBitmap(bmp)
                        ivAnswerList[j].visibility = View.VISIBLE
                    } catch (e: IOException) {
                        Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                    }

                    tvAnswerList[j].visibility = View.GONE
                    tvAnswerList[j].text = question.wrongAnswers[j].text
                }

                try {
                    val inputStream = applicationContext.assets.open("images/" + question.slugCorrectAnswer + ".webp")
                    val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                    ivAnswerList[listNumbers[3]].setImageBitmap(bmp)
                    ivAnswerList[listNumbers[3]].visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                }
                tvAnswerList[listNumbers[3]].visibility = View.GONE
                tvAnswerList[listNumbers[3]].text = question.textCorrectAnswer

            }

            5-> {
                form.ivImage.visibility = View.GONE
                form.tvQuestion.text = question.textQuestion

                for (i in 0..2) {
                    val j = listNumbers[i]

                    try {
                        val inputStream = applicationContext.assets.open("images/" + question.wrongAnswers[j].slug + ".webp")
                        val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                        ivAnswerList[j].setImageBitmap(bmp)
                        ivAnswerList[j].visibility = View.VISIBLE
                    } catch (e: IOException) {
                        Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                    }

                    tvAnswerList[j].visibility = View.VISIBLE
                    tvAnswerList[j].text = question.wrongAnswers[j].text
                }

                try {
                    val inputStream = applicationContext.assets.open("images/" + question.slugCorrectAnswer + ".webp")
                    val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                    ivAnswerList[listNumbers[3]].setImageBitmap(bmp)
                    ivAnswerList[listNumbers[3]].visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
                }
                tvAnswerList[listNumbers[3]].visibility = View.VISIBLE
                tvAnswerList[listNumbers[3]].text = question.textCorrectAnswer

            }

            6 -> {


                try {
                    val inputStream = applicationContext.assets.open("images/" + question.slugQuestion + ".webp")
                    val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                    form.ivImage.setImageBitmap(bmp)
                    form.ivImage.visibility = View.VISIBLE
                } catch (e: IOException) {
                    Toast.makeText(this, "Обновите приложение", Toast.LENGTH_LONG).show()
                }


                form.tvQuestion.text = question.textQuestion

                tvAnswerLineList[listLineNumbers[0]].visibility = View.VISIBLE
                tvAnswerLineList[listLineNumbers[0]].text = question.wrongAnswers[0].text

                tvAnswerLineList[listLineNumbers[1]].visibility = View.VISIBLE
                tvAnswerLineList[listLineNumbers[1]].text = question.textCorrectAnswer

            }


        }
    }

    override fun onClick(p0: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return
        mLastClickTime = SystemClock.elapsedRealtime()

        var clickAnswer: String = ""

        if (p0 != null) {
            when (p0.id){
                R.id.clAnswer1,R.id.ivAnswer1Image,R.id.tvAnswer1Caption -> {

                    clickAnswer = form.tvAnswer1Caption.text.toString()//.replace("/n", " ")
                }
                R.id.clAnswer2,R.id.ivAnswer2Image,R.id.tvAnswer2Caption -> {

                    clickAnswer = form.tvAnswer2Caption.text.toString()//.replace("/n", " ")
                }
                R.id.clAnswer3,R.id.ivAnswer3Image,R.id.tvAnswer3Caption -> {

                    clickAnswer = form.tvAnswer3Caption.text.toString()//.replace("/n", " ")
                }
                R.id.clAnswer4,R.id.ivAnswer4Image,R.id.tvAnswer4Caption -> {

                    clickAnswer = form.tvAnswer4Caption.text.toString()//.replace("/n", " ")
                }
                R.id.clAnswerLine1,R.id.tvAnswerLine1Caption -> {
                    clickAnswer = form.tvAnswerLine1Caption.text.toString()//.replace("/n", " ")
                }
                R.id.clAnswerLine2,R.id.tvAnswerLine2Caption -> {
                    clickAnswer = form.tvAnswerLine2Caption.text.toString()//.replace("/n", " ")
                }
            }
            if (clickAnswer == question.textCorrectAnswer) {
                mCorrectAnswers++
                showResultDialog(clickAnswer, true)
                form.tvCorrectAnswers.text = mCorrectAnswers.toString()
            } else {
                mWrongAnswers++
                showResultDialog(clickAnswer, false)
                form.tvErrorAnswers.text = mWrongAnswers.toString()
            }
        }
    }



    private fun showResultDialog(errorAnswer: String, result: Boolean) {

        val dialog = Dialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_after_answer)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.gravity = Gravity.CENTER
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.show()
        dialog.window!!.attributes = lp
        dialog.getWindow()?.setAttributes(lp)
        dialog.setCancelable(false)

        val bDialogNext = dialog.findViewById<Button>(R.id.bDialogNext)
        val res = dialog.findViewById<TextView>(R.id.tvDialogResult)
        val img = dialog.findViewById<ImageView>(R.id.ivDialogImage)
        val answer = dialog.findViewById<TextView>(R.id.tvDialogAnswer)

        val comment = dialog.findViewById<TextView>(R.id.tvDialogComment)

        if (result) res.text = getString(R.string.correct) else res.text = getString(R.string.wrong)



        statisticData.add(DataClassStatisticResult(question.textQuestion, question.slugQuestion, question.textCorrectAnswer, errorAnswer, result))

        try {
            val inputStream = applicationContext.assets.open("gameimage/" + question.slugDialogPic + ".webp")
            val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
            img.setImageBitmap(bmp)
        } catch (e: IOException) {
            Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_LONG).show()
        }

        answer.text = question.textCorrectAnswer.replace("\n", " ")
        comment.text = question.commentCorrectAnswers

        bDialogNext.setOnClickListener {

            mCurrentPosition++
            if (mCurrentPosition <= mCommonQuestion) {
                dialog.dismiss()
                getQuestions()
            } else {
                dialog.dismiss()

                    val i = Intent(this, ResultActivity::class.java).apply {
                        putExtra(GameConst.TYPE, mType)
                        putExtra(GameConst.CAPTION, mCaption)
                        putExtra(GameConst.CORRECT, mCorrectAnswers)
                        putExtra(GameConst.WRONG, mWrongAnswers)
                        putParcelableArrayListExtra(GameConst.STATISTIC, statisticData)
                    }
                    startActivity(i)
                finish()
            }
        }
        dialog.show()
    }

    private fun hide_50_50(){

        var counter: Int = 0

        if (form.tvAnswer1Caption.text.toString() != question.textCorrectAnswer) {
            counter++
            form.tvAnswer1Caption.visibility = View.INVISIBLE
            form.ivAnswer1Image.visibility = View.INVISIBLE
            form.clAnswer1.visibility = View.INVISIBLE
        }

        if (form.tvAnswer2Caption.text.toString() != question.textCorrectAnswer) {
            counter++
            form.tvAnswer2Caption.visibility = View.INVISIBLE
            form.ivAnswer2Image.visibility = View.INVISIBLE
            form.clAnswer2.visibility = View.INVISIBLE
        }
        if (counter > 1) return

        if (form.tvAnswer3Caption.text.toString() != question.textCorrectAnswer) {
            counter++
            form.tvAnswer3Caption.visibility = View.INVISIBLE
            form.ivAnswer3Image.visibility = View.INVISIBLE
            form.clAnswer3.visibility = View.INVISIBLE
        }
        if (counter > 1) return

        if (form.tvAnswer4Caption.text.toString() != question.textCorrectAnswer) {
            //counter++
            form.tvAnswer4Caption.visibility = View.INVISIBLE
            form.ivAnswer4Image.visibility = View.INVISIBLE
            form.clAnswer4.visibility = View.INVISIBLE
        }
    }


    private fun adsInit(){

        val adRequest= AdRequest.Builder().build()
        val adWidthPixels = resources.displayMetrics.widthPixels
        val adWidthDp = (adWidthPixels / resources.displayMetrics.density).roundToInt()
        bannerAdSize = BannerAdSize.stickySize(this@QuestionActivity, adWidthDp)

        form.adViewMain.apply {
            // Replace demo Ad Unit ID with actual Ad Unit ID
            setAdUnitId("R-M-1769422-1")
            //setAdUnitId("demo-banner-yandex")
            setAdSize(bannerAdSize!!)
            setBannerAdEventListener(bannerAdEventListener)
        }

        MobileAds.initialize(this as Activity){}

        form.adViewMain.loadAd(adRequest)

    }

    private fun loadRewardedAd(){
        form.bHelp.visibility =  View.INVISIBLE
        rewardedAdLoader?.loadAd(createAdRequestConfiguration())
    }

    private fun createAdRequestConfiguration(): AdRequestConfiguration =
        AdRequestConfiguration.Builder("R-M-1769422-2").build() //R-M-1769422-2 //demo-rewarded-yandex

    private inner class BannerAdYandexAdsEventListener : BannerAdEventListener {

        override fun onAdLoaded() {
            Log.d("MyLog","onAdLoaded")
            form.adViewMain.isVisible = true
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



    private fun destroyRewardedAd() {
        // don't forget to clean up event listener to null?
        rewardedAd?.setAdEventListener(null)
        rewardedAd = null
    }

    private fun showRewardedAd() {
        form.bHelp.visibility = View.INVISIBLE
        rewardedAd?.apply {
            setAdEventListener(eventLogger)
            show(this@QuestionActivity)
        }
    }


    private inner class RewardedAdEventLogger : RewardedAdEventListener {

        override fun onAdShown() {

        }

        override fun onAdFailedToShow(adError: AdError) {

        }

        override fun onAdDismissed() {
            destroyRewardedAd()
            loadRewardedAd()
        }

        override fun onRewarded(reward: Reward) {
            hide_50_50()
        }

        override fun onAdClicked() {

        }

        override fun onAdImpression(data: ImpressionData?) {

        }
    }

    override fun onAdLoaded(p0: RewardedAd) {
        isAdsload = true
        this.rewardedAd = p0
        form.bHelp.visibility = View.VISIBLE
    }

    override fun onAdFailedToLoad(p0: AdRequestError) {
        isAdsload = false
        form.bHelp.visibility =  View.INVISIBLE
    }


}