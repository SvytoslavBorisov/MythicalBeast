package ru.openbiz64.mythicalbeast.fragment

import android.R
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.openbiz64.mythicalbeast.MainApp
import ru.openbiz64.mythicalbeast.WebActivity
import ru.openbiz64.mythicalbeast.adapter.BeastAdapter


import ru.openbiz64.mythicalbeast.databinding.FragmentFavoriteBinding
import ru.openbiz64.mythicalbeast.dataclass.CommonConst
import ru.openbiz64.mythicalbeast.dataclass.FilterDataClass
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass
import ru.openbiz64.mythicalbeast.model.RoomViewModel
import java.io.IOException


class FavoriteFragment : BaseFragment(), BeastAdapter.Listener {

    private var layoutId: Int = 0
    private lateinit var form: FragmentFavoriteBinding


    private lateinit var fruitsAdapter: BeastAdapter

    private var filter = FilterDataClass("")


    private val roomViewModel: RoomViewModel by activityViewModels{
        RoomViewModel.RoomViewModelFactory((context?.applicationContext as MainApp).database)
    }

    override fun setSearchString(search: String) {
        filter.searchString = search
        roomViewModel.findFavoriteBeastByName(search)
    }

    override fun setFirstItem(search: Char) {
        val list = fruitsAdapter.currentList
        var i = 0
        for (item in list){
            if (item.title.toString()[0]==search) {
                break
            }
            i++
        }

        form.favoriteRecyclerView.scrollToPosition(i)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        form = FragmentFavoriteBinding.inflate(inflater, container, false)
        return form.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fruitsAdapter = BeastAdapter(this, requireContext())
        form.favoriteRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        form.favoriteRecyclerView.adapter = fruitsAdapter

        roomViewModel.findBeastByName()

        roomViewModel.allFavoriteMinerals.observe(activity as LifecycleOwner) { d ->
            Log.d("MyLog", "Observe")
            if (filter.searchString.isEmpty()){
                val tmp = d.sortedBy { it.title }
                fruitsAdapter.submitList(tmp)
            } else {
                roomViewModel.findFavoriteBeastByName(filter.searchString)
            }
        }

        roomViewModel.searchFavoriteBeasts.observe(activity as LifecycleOwner, Observer() {item ->
            item?.let {
                val tmp = it.sortedBy { i->
                    i.title
                }
                fruitsAdapter.submitList(tmp)
            }
        })

    }

    override fun onDetach() {
        //roomViewModel.allMinerals.removeObservers(activity as LifecycleOwner)
        //roomViewModel.searchBeasts.removeObservers(activity as LifecycleOwner)
        super.onDetach()
    }



    companion object {

        @JvmStatic
        fun newInstance() = FavoriteFragment()
    }

    override fun onClickItem(item: BeastDataClass) {
        val i = Intent(requireContext(), WebActivity::class.java).apply{
            putExtra("title",item.title)
            putExtra("url",item.htmlUrl)
            putExtra("favorite", item.isFavorite)
        }
        startActivity(i)
    }

    override fun onClickImage(picUrl: String) {
        val dialog = Dialog(requireContext(), R.style.Theme_DeviceDefault_Light_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(ru.openbiz64.mythicalbeast.R.layout.dialog_item)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.gravity = Gravity.CENTER
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = lp.width//WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = lp
        dialog.window?.attributes = lp
        dialog.setCancelable(true)

        val img = dialog.findViewById<ImageView>(ru.openbiz64.mythicalbeast.R.id.ivImage)

        try {
            val inputStream = requireContext().assets.open("images/$picUrl.webp")
            val bmp = BitmapDrawable.createFromStream(inputStream, null)?.toBitmap()
            img.setImageBitmap(bmp)
        } catch (e: IOException) {
            Toast.makeText(requireContext(), "File not found", Toast.LENGTH_LONG).show()
        }

        img.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onSetFavorite(item: BeastDataClass) {
        // нужна именно копия, так как если менять item,
        // то DiffUtil не работает корректно
        // (item это ссылка и он меняется в самом адаптере)
        val i = item.copy()  //!!!!!!!!!!!!!!!!!!!!!!!!
        i.isFavorite = !i.isFavorite
        roomViewModel.updateBeast(i)
        //roomViewModel.findMineralByName(filter.searchString)
    }
}