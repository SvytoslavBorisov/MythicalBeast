package ru.openbiz64.mythicalbeast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import ru.openbiz64.mythicalbeast.databinding.ActivityFilterBinding
import ru.openbiz64.mythicalbeast.dataclass.BeastFilter

class FilterActivity : AppCompatActivity() {
    private lateinit var form: ActivityFilterBinding
    private var allCategory: ArrayList<String> = arrayListOf()
    private lateinit var filter: BeastFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        form = ActivityFilterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(form.root)

        filter = BeastFilter("", resources.getString(R.string.all_category))

        supportActionBar?.title = "Фильтр"
        actionBarSettings()
        getMyIntent()
        initSpinner()

        form.button.setOnClickListener {
            setFilterResult()
        }
    }


    private fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getMyIntent(){

        val sFilter = intent.getSerializableExtra(FILTER_KEY)
        if (sFilter != null){
            filter = sFilter as BeastFilter
        }

        allCategory.clear()
        val dd = intent.getStringArrayExtra(CATEGORY_KEY)
        dd?.forEach { allCategory.add(it)  }
    }

    private fun initSpinner(){

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, allCategory)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        form.spinner.adapter = adapter

        for (i in 0 until form.spinner.count){
            if (form.spinner.getItemAtPosition(i) == filter.mythology){
                form.spinner.setSelection(i)
                break
            }
        }
    }

    private fun setFilterResult(){
        filter.mythology = form.spinner.selectedItem.toString()

        val i = Intent().apply {
            putExtra(FILTER_KEY_RESULT, filter)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.filter_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val FILTER_KEY = "filter_key"
        const val FILTER_KEY_RESULT = "filter_key_result"
        const val CATEGORY_KEY = "category_key"
    }
}