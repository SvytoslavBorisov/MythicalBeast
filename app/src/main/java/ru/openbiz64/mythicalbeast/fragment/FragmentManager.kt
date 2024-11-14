package ru.openbiz64.mythicalbeast.fragment

import androidx.appcompat.app.AppCompatActivity
import ru.openbiz64.mythicalbeast.R

object
FragmentManager {
    private var currentFragment: BaseFragment? = null

    fun getCurrentFragment(): BaseFragment? = currentFragment

    fun setFragment(newFragment: BaseFragment, activity:AppCompatActivity){
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder, newFragment)
        transaction.commit()
        currentFragment = newFragment
    }
}