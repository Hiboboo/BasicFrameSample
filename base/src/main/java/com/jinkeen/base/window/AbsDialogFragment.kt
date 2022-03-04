package com.jinkeen.base.window

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

abstract class AbsDialogFragment(@LayoutRes layoutId: Int) : DialogFragment(layoutId) {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.isCancelable = issCancelable()
        this.setStyle(STYLE_NORMAL, this.getStyle())
    }

    protected abstract fun issCancelable(): Boolean

    @StyleRes
    protected abstract fun getStyle(): Int

    /**
     * 在宿主[AppCompatActivity]的[onResume]生命周期执行后执行
     *
     * 该方法可替换原[onActivityCreated]的使用。
     */
    protected open fun onActivityResume() {}

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        return dialog
    }

    private var layoutBinding: ViewDataBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val contentView = super.onCreateView(inflater, container, savedInstanceState)
        contentView?.let { layoutBinding = if (it.tag is String) DataBindingUtil.bind(it) else null }
        return contentView
    }

    protected fun getLayoutBinding(): ViewDataBinding? = layoutBinding

    open fun show(manager: FragmentManager) {
        if (!this.isAdded)
            this.show(manager, this::class.java.name)
    }

    interface OnDismissListener {
        fun onDismiss(dialog: DialogInterface)
    }

    private lateinit var dismissListener: OnDismissListener

    open fun setOnDismissListener(listener: OnDismissListener) {
        this.dismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (this::dismissListener.isInitialized)
            dismissListener.onDismiss(dialog)
    }
}