package ru.openbiz64.mythicalbeast.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.openbiz64.mythicalbeast.R

import ru.openbiz64.mythicalbeast.dataclass.DataClassStatisticResult
import java.io.IOException

class ResultStatisticAdapter(var gpList:ArrayList<DataClassStatisticResult>, context: Context):
    RecyclerView.Adapter<ResultStatisticAdapter.ViewHolder>() {
    val context = context
    private var list = gpList.clone() as ArrayList<DataClassStatisticResult>


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val ivPhotoQuestion = view.findViewById<ImageView>(R.id.ivPhotoQuestion)
        val tvTextQuestion = view.findViewById<TextView>(R.id.tvTextQuestion)
        val tvCorrect = view.findViewById<TextView>(R.id.tvCorrect)
        val tvPlayer = view.findViewById<TextView>(R.id.tvPlayer)
        val ivResultCorrection = view.findViewById<ImageView>(R.id.ivResultCorrection)

        fun setData(listItem: DataClassStatisticResult, context: Context) {

            var bmp: Bitmap? = null
            if (listItem.slugQuestion != "no_pic"){
                ivPhotoQuestion.visibility = View.VISIBLE
                if (listItem.slugQuestion!!.contains("webp")){
                    Picasso.get().load("https://amaranth64.github.io/myth/quiz/images/" + listItem.slugQuestion).placeholder(R.drawable.ic_loading).error(R.drawable.need_internet).noFade().into(ivPhotoQuestion)
                } else {
                    try {
                        val inputStream = context.assets.open("images/" + listItem.slugQuestion + ".webp")
                        bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                        if (bmp != null) ivPhotoQuestion.setImageBitmap(bmp) else ivPhotoQuestion.setImageResource(R.drawable.ic_baseline_help_outline_24)
                    } catch (e: IOException) {
                        Toast.makeText(context, "Обновите приложение", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                ivPhotoQuestion.visibility = View.GONE
            }

            tvTextQuestion.text = listItem.textQuestion.toString()
            tvCorrect.text = listItem.textCorrectAnswer.toString().replace("\n", " ")
            tvPlayer.text = listItem.textPlayerAnswer.toString().replace("\n", " ")


            if (listItem.isCorrectAnswer) {
                ivResultCorrection.setImageResource(R.drawable.greencheck)
            } else {
                ivResultCorrection.setImageResource(R.drawable.redcross)
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(context)
        return ViewHolder(inflater.inflate(R.layout.item_answer_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = list[position]
        holder.setData(listItem, context)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateAdapter(listUpdate: ArrayList<DataClassStatisticResult>) {
        list.clear()
        list.addAll(listUpdate)
        notifyDataSetChanged()
    }

}