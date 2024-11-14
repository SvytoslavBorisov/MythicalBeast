package ru.openbiz64.mythicalbeast.fragment

import androidx.fragment.app.Fragment

abstract class BaseFragment: Fragment() {
    abstract fun setSearchString(search: String)
    abstract fun setFirstItem(search: Char)

}