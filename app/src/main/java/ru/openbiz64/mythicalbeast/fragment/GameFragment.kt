package ru.openbiz64.mythicalbeast.fragment


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.fragment.app.activityViewModels
import ru.openbiz64.mythicalbeast.APP_CONTEXT
import ru.openbiz64.mythicalbeast.QuestionActivity
import ru.openbiz64.mythicalbeast.QuestionActivityMyth
import ru.openbiz64.mythicalbeast.R
import ru.openbiz64.mythicalbeast.WebActivity
import ru.openbiz64.mythicalbeast.adapter.ArticleAdapter
import ru.openbiz64.mythicalbeast.adapter.GameAdapter
import ru.openbiz64.mythicalbeast.databinding.FragmentArticleBinding
import ru.openbiz64.mythicalbeast.databinding.FragmentGameBinding
import ru.openbiz64.mythicalbeast.dataclass.ArticleDataClass
import ru.openbiz64.mythicalbeast.dataclass.CommonConst
import ru.openbiz64.mythicalbeast.dataclass.FilterDataClass
import ru.openbiz64.mythicalbeast.dataclass.GameConst
import ru.openbiz64.mythicalbeast.dataclass.GameDataClass
import ru.openbiz64.mythicalbeast.model.MainViewModel


class GameFragment : BaseFragment(), GameAdapter.Listener {

    private lateinit var form: FragmentGameBinding
    private val vm: MainViewModel by activityViewModels()
    private val fileName = "games"
    private lateinit var gameAdapter: GameAdapter
    private var filter: FilterDataClass = FilterDataClass("")


    override fun setSearchString(search: String) {
        vm.setFilter(FilterDataClass(search))
    }

    override fun setFirstItem(search: Char) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //vm = ViewModelProvider(this, MainViewModelFactory(requireContext(), fileName)).get(MainViewModel::class.java)
        gameAdapter = GameAdapter(this, requireContext())

        form.articleRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        form.articleRecyclerView.adapter = gameAdapter//foodAdapter
        //foodAdapter.addAll(foodDummyData)

        vm.gameData.observe(activity as LifecycleOwner) { d ->
            Log.d("MyLog", "Observe")
            if (d.isNotEmpty()) {
                form.ivNoConnection.visibility = View.GONE
                form.bRepeat.visibility = View.GONE
                form.tvNoConnection.visibility = View.GONE
            }
            gameAdapter.submitList(d){
                form.articleRecyclerView.scrollToPosition(0)
            }



        }

        form.bRepeat.setOnClickListener {
            vm.loadGameListFromInternet()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        form = FragmentGameBinding.inflate(inflater, container, false)
        return form.root
    }

    override fun onClickItem(item: GameDataClass) {
        val i = Intent(requireContext(), QuestionActivityMyth::class.java).apply{
                putExtra(GameConst.FILE_QUESTION_NAME, item.slug)
            }
            startActivity(i)
    }

    companion object {
        @JvmStatic
        fun newInstance() = GameFragment()
    }
}