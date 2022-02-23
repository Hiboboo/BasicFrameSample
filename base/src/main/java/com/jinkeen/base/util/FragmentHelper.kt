package com.jinkeen.base.util

import android.content.Context
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.jinkeen.base.R

open class FragmentHelper(private val context: Context) {

    private var currentFragment: Fragment? = null
    private var currentPosition: Int = 0

    /**
     * 从当前页面切换到目标页面。
     * ---
     * 切换过程并不会销毁任何页面，只是将当前页面设为隐藏，将目标页面显示出来。
     *
     * @param containerId 要装入新页面的容器ID
     * @param target 目标页面的类对象
     * @param manager 负责维护`Fragment`碎片的管理对象。*默认将使用[AppCompatActivity.getSupportFragmentManager]*
     * @param data 向目标页面传递的数据集。*默认为`null`*
     * @param position 目标页面的位置。*默认为-1，表示不使用平移动画切换*
     * @param isRemoveBefore 是否在切换到新页面之后，同时再将之前的页面从事务中移除掉
     *
     * @see [androidx.fragment.app.FragmentTransaction.add]
     * @see [androidx.fragment.app.FragmentTransaction.show]
     * @see [androidx.fragment.app.FragmentTransaction.hide]
     */
    @JvmOverloads
    open fun <T : Fragment> switchFragment(
        @IdRes containerId: Int,
        target: Class<T>,
        manager: FragmentManager = (context as AppCompatActivity).supportFragmentManager,
        data: Bundle? = null,
        position: Int = -1,
        isRemoveBefore: Boolean = false
    ) {
        val fragment = manager.findFragmentByTag(target.name) ?: manager.fragmentFactory.instantiate(context.classLoader, target.name)
        fragment.arguments = data
        if (currentFragment != fragment) {
            val transaction = manager.beginTransaction()
            if (position > -1) {
                val openAnim = if (currentPosition < position) R.anim.enter_right else R.anim.enter_left
                val exitAnim = if (currentPosition < position) R.anim.exit_left else R.anim.exit_right
                transaction.setCustomAnimations(openAnim, exitAnim)
                currentPosition = position
            }
            currentFragment?.let {
                if (!fragment.isAdded)
                    transaction.hide(it).add(containerId, fragment, target.name)
                else transaction.hide(it).show(fragment)
                if (isRemoveBefore) transaction.remove(it)
            } ?: transaction.add(containerId, fragment, target.name)
            transaction.commitNowAllowingStateLoss()
            currentFragment = fragment
        }
    }

    /**
     * 获取当前处于栈顶的[Fragment]对象
     *
     * @return 返回正处于当前栈顶或显示中的`Fragment`
     */
    open fun getCurrentFragment(): Fragment? {
        return currentFragment
    }

    /**
     * 获取当前`Fragment`的位置
     *
     * @return 返回当前`Fragment`的位置
     */
    open fun getCurrentPosition(): Int {
        return currentPosition
    }
}