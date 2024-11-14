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
import ru.openbiz64.mythicalbeast.databinding.ItemBeastDrkBinding
import ru.openbiz64.mythicalbeast.dataclass.ArticleDataClass
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass
import java.io.IOException

class FavoriteAdapter(private var listener: Listener, private val context: Context): ListAdapter<BeastDataClass, FavoriteAdapter.ItemHolder>(ItemComparator()) {

    class ItemHolder(view: View, private val context: Context): RecyclerView.ViewHolder(view){
        private var binding = ItemBeastDrkBinding.bind(view)

        fun setData(item: BeastDataClass, listener: Listener) = with(binding){
            tvTitle.text = item.title
            tvDescription.text = item.description

            if (item.isFavorite){
                ivFavorite.setImageResource(R.drawable.ic_star_select)
            }else{
                ivFavorite.setImageResource(R.drawable.ic_star_not)
            }

            try {
                val inputStream = context.assets.open("images/" + item.picUrl + ".webp")
                val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
                ivFood.setImageBitmap(bmp)
            } catch (e: IOException) {
                Toast.makeText(context, "File not found", Toast.LENGTH_LONG).show()
            }

            itemView.setOnClickListener {
                listener.onClickItem(item)
            }
            ivFood.setOnClickListener {
                listener.onClickImage(item.picUrl)
            }

            ivFavorite.setOnClickListener{
                listener.onSetFavorite(item)
            }
        }

        companion object{
            fun create(parent: ViewGroup, context: Context): ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_beast_drk, parent, false), context)
            }
        }

    }

    class ItemComparator: DiffUtil.ItemCallback<BeastDataClass>(){
        override fun areItemsTheSame(oldItem: BeastDataClass, newItem: BeastDataClass): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BeastDataClass, newItem: BeastDataClass): Boolean {
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
        fun onClickItem(item: BeastDataClass)
        fun onClickImage(picUrl: String)
        fun onSetFavorite(item: BeastDataClass)
    }
}