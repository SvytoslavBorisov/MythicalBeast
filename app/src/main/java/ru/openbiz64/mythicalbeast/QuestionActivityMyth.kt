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
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import ru.openbiz64.mythicalbeast.databinding.QuizLayoutMythBinding
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

class QuestionActivityMyth: AppCompatActivity(), View.OnClickListener, RewardedAdLoadListener {

    private lateinit var form: QuizLayoutMythBinding
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
        form = QuizLayoutMythBinding.inflate(layoutInflater)
        setContentView(form.root)

        supportActionBar?.hide()

        val mFilename = intent.getStringExtra(GameConst.FILE_QUESTION_NAME).toString()
        if (mFilename.isEmpty()) goToBack()

        // загрузка вопросов из файла
        loadQuestionData(mFilename)

        // загрузка рекламы
        adsInit()

        rewardedAdLoader = RewardedAdLoader(this).apply {
            setAdLoadListener(this@QuestionActivityMyth)
        }
        loadRewardedAd()

        // слушатель на кнопку НАЗАД (стрелка)
        form.bBackQuiz.setOnClickListener { goToBack() }

        // слушатель на кнопку ПОМОЩЬ
        form.bHelp.setOnClickListener {
            showRewardedAd()
        }
    }

    private fun loadQuestionData(filename: String){
        val queue = Volley.newRequestQueue(APP_CONTEXT)
        val sRequest = StringRequest(
            Request.Method.GET,
            filename,
            { response ->

                if (response.isNotEmpty()) {

                    // получили в response содержимое json файла как строку
                    questionList = GetQuestionsHelper.getQuestionsFromRequest(response)
                    if (questionList.isEmpty()) goToBack()

                    // запускаем игру
                    startQuiz()
                }
            },
            {
                Log.d("MyLog", "VolleyError: $it")
                goToBack()
            }
        )
        sRequest.setShouldCache(false)
        queue.add(sRequest)
    }

    private fun startQuiz(){

        // максимальное количество вопросов
        mCommonQuestion = GameConst.question_for_champgame_count
        form.progressBar.max = mCommonQuestion

        // первый вопрос
        question = questionList[0]

        // на случай, если вопросов было меньше
        if (mCommonQuestion > questionList.size) mCommonQuestion = questionList.size

        //mCommonQuestion = questionList.size //!!!!!!!!!!!!ТОЛЬКО ДЛЯ ТЕСТА!!!!!!!!!!!!!!!!

        // установка вопроса
        getQuestions()

    }

    private fun getQuestions() {

        // Создание списков через listOf() для упрощения
        val clAnswerList = listOf(form.clAnswerLine1, form.clAnswerLine2, form.clAnswerLine3, form.clAnswerLine4)
        val tvAnswerList = listOf(form.tvAnswerLine1Caption, form.tvAnswerLine2Caption, form.tvAnswerLine3Caption, form.tvAnswerLine4Caption)

        // Обновляем прогресс
        form.progressBar.progress = mCurrentPosition
        form.tvProgerss.text = "$mCurrentPosition/$mCommonQuestion"

        // Устанавливаем невидимость для всех элементов
        clAnswerList.forEach {
            it.visibility = View.GONE
        }

        val answerList =  ArrayList<String>()

        // Получаем вопрос
        question = questionList[mCurrentPosition - 1]

        // добавляем верный ответ к списку вопросов
        answerList.add(question.textCorrectAnswer)

        // получаем список неверных ответов и тасуем его
        val wrongAnswer = question.wrongAnswers
        wrongAnswer.shuffle()

        // записываем неверные ответы в список вопросов
        var wrongAnswersSize = wrongAnswer.size
        if (wrongAnswersSize > 3) wrongAnswersSize = 3

        for (i in 0..< wrongAnswersSize){
            answerList.add(wrongAnswer[i].text)
        }

        // тасуем список вопросов
        answerList.shuffle()

        // Загружаем текст вопросов в элементы
        for (i in 0..< answerList.size){
            tvAnswerList[i].text = answerList[i]
            clAnswerList[i].visibility = View.VISIBLE
            tvAnswerList[i].visibility = View.VISIBLE
        }

        // Загружаем картинку, если она есть
        if ((question.slugQuestion != "no_pic") and (question.slugQuestion.isNotEmpty())){
            try {
                applicationContext.assets.open(question.slugQuestion).use {
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

    }

    override fun onClick(p0: View?) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) return
        mLastClickTime = SystemClock.elapsedRealtime()

        if (p0 != null) {
            val clickAnswer =
                when (p0.id) {
                    R.id.clAnswerLine1, R.id.tvAnswerLine1Caption -> form.tvAnswerLine1Caption.text.toString()
                    R.id.clAnswerLine2, R.id.tvAnswerLine2Caption -> form.tvAnswerLine2Caption.text.toString()
                    R.id.clAnswerLine3, R.id.tvAnswerLine3Caption -> form.tvAnswerLine3Caption.text.toString()
                    R.id.clAnswerLine4, R.id.tvAnswerLine4Caption -> form.tvAnswerLine4Caption.text.toString()
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

        if ((question.slugDialog.isNotEmpty()) and (question.slugDialog != "no_pic")){
            try {
                val inputStream = applicationContext.assets.open(question.slugDialog)
                val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                img.setImageBitmap(bmp)
            } catch (e: IOException) {
                Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_LONG).show()
            }
        } else img.visibility = View.GONE


        answer.text = question.textCorrectAnswer
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

        if (form.tvAnswerLine1Caption.text.toString() != question.textCorrectAnswer) {
            counter++
            form.tvAnswerLine1Caption.visibility = View.INVISIBLE
            form.clAnswerLine1.visibility = View.INVISIBLE
        }

        if (form.tvAnswerLine2Caption.text.toString() != question.textCorrectAnswer) {
            counter++
            form.tvAnswerLine2Caption.visibility = View.INVISIBLE
            form.clAnswerLine2.visibility = View.INVISIBLE
        }
        if (counter > 1) return

        if (form.tvAnswerLine3Caption.text.toString() != question.textCorrectAnswer) {
            counter++
            form.tvAnswerLine3Caption.visibility = View.INVISIBLE
            form.clAnswerLine3.visibility = View.INVISIBLE
        }
        if (counter > 1) return

        if (form.tvAnswerLine4Caption.text.toString() != question.textCorrectAnswer) {
            //counter++
            form.tvAnswerLine4Caption.visibility = View.INVISIBLE
            form.clAnswerLine4.visibility = View.INVISIBLE
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
        //val intent = Intent(this, MainActivity::class.java)
        //startActivity(intent)
        finish()

    }

    ////////////////////////////////////////////////////////////////////////////////////////////


    private fun adsInit(){

        val adRequest= AdRequest.Builder().build()
        val adWidthPixels = resources.displayMetrics.widthPixels
        val adWidthDp = (adWidthPixels / resources.displayMetrics.density).roundToInt()
        bannerAdSize = BannerAdSize.stickySize(this@QuestionActivityMyth, adWidthDp)

        form.adViewMain.apply {
            // Replace demo Ad Unit ID with actual Ad Unit ID
            //setAdUnitId("R-M-1769422-1")
            setAdUnitId("demo-banner-yandex")
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
        AdRequestConfiguration.Builder("demo-rewarded-yandex").build() //R-M-1769422-2 //demo-rewarded-yandex

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
            show(this@QuestionActivityMyth)
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