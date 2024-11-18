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
import ru.openbiz64.mythicalbeast.dataclass.DataClassQuestionWithoutType
import ru.openbiz64.mythicalbeast.dataclass.DataClassStatisticResult
import ru.openbiz64.mythicalbeast.dataclass.GameConst
import kotlin.math.roundToInt

class QuestionActivity: AppCompatActivity(), View.OnClickListener, RewardedAdLoadListener {

    private lateinit var form: QuizLayoutBinding
    private lateinit var questionList: ArrayList<DataClassQuestionWithoutType>
    private var mCurrentPosition: Int = 1
    private var mCorrectAnswers: Int = 0
    private var mWrongAnswers: Int = 0
    private var mCommonQuestion: Int = 0

    private var mLastClickTime: Long = 0
    private lateinit var question: DataClassQuestionWithoutType

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

        questionList = GetQuestionsHelper.getQuestions("vampir", this)
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

    private fun getQuestions() {
        // Создание списков через listOf() для упрощения
        val tvAnswerLineList = listOf(form.tvAnswerLine1Caption, form.tvAnswerLine2Caption)
        val tvAnswerList = listOf(form.tvAnswer1Caption, form.tvAnswer2Caption, form.tvAnswer3Caption, form.tvAnswer4Caption)
        val ivAnswerList = listOf(form.ivAnswer1Image, form.ivAnswer2Image, form.ivAnswer3Image, form.ivAnswer4Image)

        // Сразу делаем все изображения ответов невидимыми
        ivAnswerList.forEach {
            it.visibility = View.GONE
        }

        // Устанавливаем видимость для всех элементов
        listOf(form.clAnswer1, form.clAnswer2, form.clAnswer3, form.clAnswer4).forEach {
            it.visibility = View.VISIBLE
        }

        // Перемешиваем номера
        val listNumbers = mutableListOf(0, 1, 2, 3).apply { shuffle() }
        val listLineNumbers = mutableListOf(0, 1).apply { shuffle() }

        // Обновляем прогресс
        form.progressBar.progress = mCurrentPosition
        form.tvProgerss.text = "$mCurrentPosition/$mCommonQuestion"

        // Получаем вопрос
        question = questionList[mCurrentPosition - 1]
        val wrongAnswersSize = question.wrongAnswers.size

        // Проверяем, сколько неправильных ответов
        if (wrongAnswersSize < 4) {
            form.layoutQuestionLine.visibility = View.VISIBLE
            form.layoutQuestion.visibility = View.GONE
            form.bHelp.visibility = View.INVISIBLE
        } else {
            form.layoutQuestion.visibility = View.VISIBLE
            form.layoutQuestionLine.visibility = View.GONE
            if (isAdsload) form.bHelp.visibility = View.VISIBLE
        }

        // Загружаем картинку, если она есть
        if (question.slugQuestion != "no_pic") {
            try {
                applicationContext.assets.open("images/${question.slugQuestion}.webp").use {
                    val bmp = BitmapDrawable.createFromStream(it, null)?.toBitmap()
                    form.ivImage.setImageBitmap(bmp)
                    form.ivImage.visibility = View.VISIBLE
                }
            } catch (e: IOException) {
                Toast.makeText(this, "File not found", Toast.LENGTH_LONG).show()
            }
        } else {
            form.ivImage.visibility = View.GONE
        }

        // Загружаем текст вопроса
        form.tvQuestion.text = question.textQuestion

        // Действия в зависимости от размера неправильных ответов
        when (wrongAnswersSize) {
            2 -> {
                for (i in 0..1) {
                    val j = listNumbers[i]
                    tvAnswerList[j].visibility = View.VISIBLE
                    tvAnswerList[j].text = question.wrongAnswers[j].text
                }
                tvAnswerList[listNumbers[2]].visibility = View.VISIBLE
                tvAnswerList[listNumbers[2]].text = question.textCorrectAnswer
            }
            1 -> {
                tvAnswerLineList[listLineNumbers[0]].visibility = View.VISIBLE
                tvAnswerLineList[listLineNumbers[0]].text = question.wrongAnswers[0].text

                tvAnswerLineList[listLineNumbers[1]].visibility = View.VISIBLE
                tvAnswerLineList[listLineNumbers[1]].text = question.textCorrectAnswer
            }
            else -> {
                for (i in 0..2) {
                    val j = listNumbers[i]
                    tvAnswerList[j].visibility = View.VISIBLE
                    tvAnswerList[j].text = question.wrongAnswers[j].text
                }
                tvAnswerList[listNumbers[3]].visibility = View.VISIBLE
                tvAnswerList[listNumbers[3]].text = question.textCorrectAnswer
            }
        }
    }

    override fun onClick(p0: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return
        mLastClickTime = SystemClock.elapsedRealtime()

        if (p0 != null) {
            val clickAnswer =
                when (p0?.id) {
                    R.id.clAnswer1, R.id.ivAnswer1Image, R.id.tvAnswer1Caption -> form.tvAnswer1Caption.text.toString()
                    R.id.clAnswer2, R.id.ivAnswer2Image, R.id.tvAnswer2Caption -> form.tvAnswer2Caption.text.toString()
                    R.id.clAnswer3, R.id.ivAnswer3Image, R.id.tvAnswer3Caption -> form.tvAnswer3Caption.text.toString()
                    R.id.clAnswer4, R.id.ivAnswer4Image, R.id.tvAnswer4Caption -> form.tvAnswer4Caption.text.toString()
                    R.id.clAnswerLine1, R.id.tvAnswerLine1Caption -> form.tvAnswerLine1Caption.text.toString()
                    R.id.clAnswerLine2, R.id.tvAnswerLine2Caption -> form.tvAnswerLine2Caption.text.toString()
                    else -> ""
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
            val inputStream = applicationContext.assets.open("gameimage/" + question.slugDialog + ".webp")
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