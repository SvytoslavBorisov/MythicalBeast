package ru.openbiz64.mythicalbeast.adapter

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ru.openbiz64.mythicalbeast.R
import ru.openbiz64.mythicalbeast.databinding.ArticleItemLayoutBinding
import ru.openbiz64.mythicalbeast.dataclass.ArticleDataClass
import java.io.IOException

class ArticleAdapter(private var listener: Listener, private val context: Context): ListAdapter<ArticleDataClass, ArticleAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder(view: View, private val context: Context): RecyclerView.ViewHolder(view){
        private var binding = ArticleItemLayoutBinding.bind(view)

        fun setData(item: ArticleDataClass, listener: Listener) = with(binding){
            tvTitle.text = item.title
            tvCategory.text = item.category
            tvDescription.text = item.description

            try {
                if (item.picURL.contains("http")){
                    Picasso.get().load(item.picURL).into(ivImage)
                } else {
                    val inputStream = context.assets.open("images/" + item.picURL + ".webp")
                    val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                    ivImage.setImageBitmap(bmp)
                }


            } catch (e: IOException) {
                val inputStream = context.assets.open("images/pic1.webp")
                val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                ivImage.setImageBitmap(bmp)
                //Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
            }

            itemView.setOnClickListener {
                listener.onClickItem(item)
            }
        }

        companion object{
            fun create(parent: ViewGroup, context: Context): ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.article_item_layout, parent, false), context)
            }
        }

    }

    class ItemComparator: DiffUtil.ItemCallback<ArticleDataClass>(){
        override fun areItemsTheSame(oldItem: ArticleDataClass, newItem: ArticleDataClass): Boolean {
            return oldItem.picURL == newItem.picURL
        }

        override fun areContentsTheSame(oldItem: ArticleDataClass, newItem: ArticleDataClass): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent, context)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener)
    }

    interface Listener{
        fun onClickItem(item: ArticleDataClass)
    }
}