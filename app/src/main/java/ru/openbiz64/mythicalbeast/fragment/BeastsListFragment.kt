package ru.openbiz64.mythicalbeast.fragment


import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.openbiz64.mythicalbeast.APP_CONTEXT
import ru.openbiz64.mythicalbeast.MainApp
import ru.openbiz64.mythicalbeast.R
import ru.openbiz64.mythicalbeast.WebActivity
import ru.openbiz64.mythicalbeast.adapter.BeastAdapter
import ru.openbiz64.mythicalbeast.databinding.FragmentBeastsListBinding
import ru.openbiz64.mythicalbeast.dataclass.BeastDataClass
import ru.openbiz64.mythicalbeast.dataclass.BeastFilter
import ru.openbiz64.mythicalbeast.model.MainViewModel
import ru.openbiz64.mythicalbeast.model.RoomViewModel
import java.io.IOException


class BeastsListFragment : BaseFragment(), BeastAdapter.Listener{

    private lateinit var form: FragmentBeastsListBinding
    private val vm: MainViewModel by activityViewModels()

    private lateinit var beastAdapter: BeastAdapter
    private lateinit var mythAdapter: ArrayAdapter<String>

    private var filter = BeastFilter("", "")

    private val roomViewModel: RoomViewModel by activityViewModels{
        RoomViewModel.RoomViewModelFactory((context?.applicationContext as MainApp).database)
    }

    private lateinit var filterLauncher: ActivityResultLauncher<Intent>
    private fun onFilterResult(){
        filterLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == Activity.RESULT_OK){
//                val sFilter = it.data?.getSerializableExtra(ProductFilterActivity.FILTER_KEY_RESULT)
//                if (sFilter != null){
//                    filter = sFilter as FilterProductData
//                    adapterProduct.update(filter)
//                }
                //Toast.makeText(requireContext().applicationContext, filter.category, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun setSearchString(search: String) {
        filter.title = search
        roomViewModel.findBeastByFilter(filter)
    }

    override fun setFirstItem(search: Char) {
        val list = beastAdapter.currentList
        var i = 0
        for (item in list){
            if (item.title.toString()[0]==search) {
                break
            }
            i++
        }
        form.mineralRecyclerView.scrollToPosition(i)
    }

    override fun onDetach() {
        filter.title = ""
        filter.mythology = ""
        //roomViewModel.allMinerals.removeObservers(activity as LifecycleOwner)
        //roomViewModel.searchBeasts.removeObservers(activity as LifecycleOwner) // Вообще не понятно

        Log.d("MyLog", "================Beast onDetach==========" )
        super.onDetach()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //onFilterResult()
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filter.title = ""
        filter.mythology = ""

        beastAdapter = BeastAdapter(this, requireContext())


        setSpinnerListener(arrayListOf())

        form.mineralRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        form.mineralRecyclerView.adapter = beastAdapter

        roomViewModel.getMythologyList()
        roomViewModel.findBeastByFilter(filter)

        roomViewModel.allMinerals.observe(activity as LifecycleOwner) { d ->
            if ((filter.title.isEmpty()) and (filter.mythology.isEmpty())){
                val tmp = d.sortedBy { it.title }
                beastAdapter.submitList(tmp)
            }  else {
                roomViewModel.findBeastByFilter(filter)
            }
        }

        roomViewModel.searchBeasts.observe(activity as LifecycleOwner, Observer() {item ->
            Log.d("MyLog", "BeastList searchBeasts: Filter: " + filter.title + " = " + filter.mythology)
            item?.let {
                    val tmp = it.sortedBy { i ->
                        i.title
                    }
                    beastAdapter.submitList(tmp)
                }
        })

        roomViewModel.mythologyList.observe(activity as LifecycleOwner) { item ->
            mythAdapter.clear()
            mythAdapter.add(APP_CONTEXT.getString(R.string.all_category))
            mythAdapter.addAll(item)
        }
    }

    private fun setSpinnerListener(list: ArrayList<String>){
        mythAdapter = ArrayAdapter(requireContext(), R.layout.color_spinner_layout, list)
        mythAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
        form.spinnerMain.adapter = mythAdapter

        form.spinnerMain.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                filter.mythology = form.spinnerMain.selectedItem.toString()
                if (form.spinnerMain.selectedItem.toString() == APP_CONTEXT.getString(R.string.all_category)){
                    filter.mythology = ""
                }
                roomViewModel.findBeastByFilter(filter)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        form = FragmentBeastsListBinding.inflate(inflater, container, false)
        return form.root
    }


    companion object {
        @JvmStatic
        fun newInstance() = BeastsListFragment()
    }

    override fun onClickItem(item: BeastDataClass) {
        val i = Intent(requireContext(), WebActivity::class.java).apply{
            putExtra("title", item.title)
            putExtra("url", item.htmlUrl)
            putExtra("favorite", item.isFavorite)
        }
        startActivity(i)
    }

    override fun onClickImage(picUrl: String) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_DeviceDefault_Light_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_item)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.gravity = Gravity.CENTER
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = lp.width//WindowManager.LayoutParams.WRAP_CONTENT

        dialog.window!!.attributes = lp
        dialog.window?.attributes = lp
        dialog.setCancelable(true)

        val img = dialog.findViewById<ImageView>(R.id.ivImage)

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
    }

}