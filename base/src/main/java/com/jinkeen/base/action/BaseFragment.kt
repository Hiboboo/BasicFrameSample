package com.jinkeen.base.action

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        requireActivity().lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event.targetState == Lifecycle.State.RESUMED) {
                    onActivityResume()
                    requireActivity().lifecycle.removeObserver(this)
                }
            }
        })
    }

    private var dataBinding: ViewDataBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val contentView = super.onCreateView(inflater, container, savedInstanceState)
        contentView?.let { dataBinding = if (it.tag is String) DataBindingUtil.bind(it) else null }
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.setupViews(dataBinding)
    }

    /**
     * 实例化页面中的各个控件
     *
     * @param binding 若当前页面布局的根标签是`layout`，则可直接使用参数所表示的`DataBingind`模式进行控件操作
     */
    abstract fun setupViews(binding: ViewDataBinding?)

    /**
     * 当前页面是否为横屏
     *
     * @return `true`表示横屏，`false`为竖屏
     */
    protected open fun isLandscape(): Boolean {
        val orientation = this.resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    /**
     * 获取当前显示页面视图的绑定对象。
     *
     * 如果视图布局的根节点不是`<layout>`，那么就无法设置绑定对象
     *
     * @param T `layoutBinding`对象将被强制转换为目标视图的布局Binding对象
     * @return 返回视图的绑定对象，该对象可以被强转为目标绑定对象。
     * ---
     * *需要注意的是，如果布局根节点不是`<layout>`，那么将返回`null`*
     */
    @Suppress("UNCHECKED_CAST")
    protected open fun <T : ViewDataBinding> getLayoutBinding(): T? = dataBinding?.let { return@let it as T }

    /**
     * 在宿主[AppCompatActivity]的[onResume]生命周期执行后执行
     *
     * 该方法可替换原[onActivityCreated]的使用。
     */
    protected open fun onActivityResume() {}
}