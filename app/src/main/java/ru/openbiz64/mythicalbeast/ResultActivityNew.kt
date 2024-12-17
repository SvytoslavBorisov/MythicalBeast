package ru.openbiz64.mythicalbeast

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ru.openbiz64.mythicalbeast.adapter.ResultStatisticAdapter
import ru.openbiz64.mythicalbeast.databinding.ResultActivityBinding
import ru.openbiz64.mythicalbeast.databinding.ResultActivityNewBinding
import ru.openbiz64.mythicalbeast.dataclass.DataClassStatisticResult
import ru.openbiz64.mythicalbeast.dataclass.GameConst
import java.util.ArrayList

class ResultActivityNew : AppCompatActivity() {

    private lateinit var formResult: ResultActivityNewBinding

    private var statisticData = ArrayList<DataClassStatisticResult>()
    private var mType: String = ""
    private var mCaption: String = ""
    private var mCorrects: Int = 0
    private var mErrors: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        formResult = ResultActivityNewBinding.inflate(layoutInflater)
        setContentView(formResult.root)

        getMyIntent()

        val adapter = ResultStatisticAdapter(statisticData, this)
        formResult.rvStatstic.hasFixedSize()
        formResult.rvStatstic.layoutManager = LinearLayoutManager(this@ResultActivityNew)
        formResult.rvStatstic.adapter = adapter

        formResult.tvScore.text = resources.getString(R.string.score_result).format(mCorrects, mErrors)
        //formResult.btnFinish.text = getString(R.string.next)

        // анализ уровня прохождения и награждение
        rewardAnalise()

        formResult.tvQuizName.text = mCaption

        val goodPr = 100 * mCorrects / (mCorrects + mErrors)
        val wrongPr = 100 - goodPr
        formResult.tvGoodPercent.text = goodPr.toString()
        formResult.tvWrongPercent.text = wrongPr.toString()

        // Возврат из активити
        formResult.bFinish.setOnClickListener { finish() }

        formResult.bResultInfo.setOnClickListener {
            if (formResult.bResultInfo.text == resources.getString(R.string.statistic)){
                formResult.rvStatstic.visibility = View.VISIBLE
                formResult.bResultInfo.text = resources.getString(R.string.close)
                formResult.tvScore.visibility = View.GONE
                formResult.layPrecent.visibility = View.GONE
                formResult.tvResultText.visibility = View.GONE
                formResult.bFinish.visibility = View.GONE
            } else {
                formResult.rvStatstic.visibility = View.GONE
                formResult.tvScore.visibility = View.VISIBLE
                formResult.layPrecent.visibility = View.VISIBLE
                formResult.tvResultText.visibility = View.VISIBLE
                formResult.bFinish.visibility = View.VISIBLE
                formResult.bResultInfo.text = resources.getString(R.string.statistic)
            }
        }

    }

    private fun getMyIntent(){
        if (intent != null) {
            mType = intent.getStringExtra(GameConst.TYPE).toString()
            mCaption = intent.getStringExtra(GameConst.CAPTION).toString()
            mCorrects = intent.getIntExtra(GameConst.CORRECT, 0)
            mErrors = intent.getIntExtra(GameConst.WRONG, 0)
            statisticData = intent.getParcelableArrayListExtra<DataClassStatisticResult>(GameConst.STATISTIC)!!
        }
    }

    private fun rewardAnalise(){
            when (mErrors) {
                in 9..< 100 ->{
                    // очень плохо
                    formResult.tvResultText.text = getString(R.string.very_bad_txt)
                    formResult.ivTrophy.setImageResource(R.drawable.result_very_bad)
                }
                in 6..< 9 ->{
                    // плохо
                    formResult.tvResultText.text = getString(R.string.bad_txt)
                    formResult.ivTrophy.setImageResource(R.drawable.result_bad)
                }
                in 3..< 6 ->{
                    // хорошо
                    formResult.tvResultText.text = getString(R.string.good_txt)
                    formResult.ivTrophy.setImageResource(R.drawable.result_good)
                }
                in 1..< 3 ->{
                    // отлично
                    formResult.tvResultText.text = getString(R.string.excelent_txt)
                    formResult.ivTrophy.setImageResource(R.drawable.result_excelent)
                }
                0 ->{
                    // превосходно
                    formResult.tvResultText.text = getString(R.string.briliant_txt)
                    formResult.ivTrophy.setImageResource(R.drawable.result_briliant)
                }
            }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


}


