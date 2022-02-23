package com.jinkeen.base.action

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.jinkeen.base.R
import com.jinkeen.base.databinding.ActivityBasicLayoutBinding
import com.jinkeen.base.util.ActivityManager
import com.jinkeen.base.util.setCustomDensity
import kotlin.properties.Delegates

abstract class BaseAppCompatActivity : AppCompatActivity() {

    private lateinit var rootviewBinding: ActivityBasicLayoutBinding
    private var dataBinding: ViewDataBinding? = null

    private var isNeedBaseActionbar: Boolean by Delegates.notNull()
    private var isCustomActionbar: Boolean by Delegates.notNull()
    private var customHomeAsUpIndicator: Int by Delegates.notNull()
    private var isDisplayHomeAsUpEnabled: Boolean by Delegates.notNull()
    private var customActionbarBackgroundColor: Int by Delegates.notNull()
    private var customActionbarTextColor: Int by Delegates.notNull()

    protected open inner class ThemeStyle(private val activity: BaseAppCompatActivity) {

        /** 当前页面是否需要显示标题栏，默认`true` */
        var isNeedBaseActionbar = true

        /** 是否自定义的`Actionbar`样式，默认`false` */
        var isCustomActionbar = false

        /** 获取自定义标题样式的返回按钮，默认黑色箭头 */
        var customHomeAsUpIndicator = R.mipmap.ic_arrow_back_ios

        /** 当前页面是否需要左上角的返回按钮，默认`false` */
        var isDisplayHomeAsUpEnabled = false

        /** 获取自定义标题栏样式的背景颜色值，默认白色 */
        var customActionbarBackgroundColor = R.color.white

        /** 获取自定义标题样式的文字颜色，默认`#333333` */
        var customActionbarTextColor = R.color.actionbar_text_color

        internal fun build() {
            activity.isNeedBaseActionbar = isNeedBaseActionbar
            activity.isCustomActionbar = isCustomActionbar
            activity.customHomeAsUpIndicator = customHomeAsUpIndicator
            activity.isDisplayHomeAsUpEnabled = isDisplayHomeAsUpEnabled
            activity.customActionbarBackgroundColor = customActionbarBackgroundColor
            activity.customActionbarTextColor = customActionbarTextColor
        }
    }

    /**
     * 重写该方法，并传入当前页面的布局`ID`。像官方的[onCreate]方法一样调用`super`关键字。
     *
     * 该方法内除了完成必要的页面初始化功能外，额外已完成基础的通用型功能设置，例如：
     * - 对页面适配的目标分辨率设置
     * - 主动将继承自该类的子类添加进[ActivityManager]池中
     * - 页面风格的默认化设置
     * - 对页面布局是否支持`DataBinding`进行了判断与自动初始化
     *
     * @param savedInstanceState 参考[onCreate]方法中的定义
     * @param layoutRes 目标页面的资源`ID`
     */
    protected open fun onCreate(savedInstanceState: Bundle?, @LayoutRes layoutRes: Int) {
        super.onCreate(savedInstanceState)
        setCustomDensity(this, application, if (isLandscape()) 1920.0f else 1080.0f)
        rootviewBinding = ActivityBasicLayoutBinding.inflate(layoutInflater)
        this.setContentView(rootviewBinding.root)
        ActivityManager.add(this)
        with(ThemeStyle((this))) {
            setupThemeStyle(this)
            build()
        }
        rootviewBinding.rootContainer.removeAllViews()
        this.setupLayoutBinding(layoutRes)
        val toolbar = if (isNeedBaseActionbar) this.initBaseToolbar() else this.initCustomToolbar()
        if (toolbar != null) {
            this.setSupportActionBar(toolbar)
            val actionbar = supportActionBar!!
            actionbar.setDisplayShowTitleEnabled(false)
            actionbar.setHomeAsUpIndicator(if (isCustomActionbar) customHomeAsUpIndicator else R.mipmap.ic_arrow_back_ios)
            actionbar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled)
        }
        this.setupViews(dataBinding)
    }

    /**
     * 获取目标屏幕的宽度，单位(`px`)
     *
     * @return 返回程序运行设备的分辨率宽度数值，单位像素。若有横竖屏区分，可使用[isLandscape]方法进行判断。
     */
    protected abstract fun getScreenWidth(): Float

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
     * 为当前页面设置主题风格
     *
     * @param s 为该主题风格设置具体的属性值
     */
    protected abstract fun setupThemeStyle(s: ThemeStyle)

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

    private fun setupLayoutBinding(layoutRes: Int) {
        val contentView = layoutInflater.inflate(
            layoutRes,
            rootviewBinding.rootContainer,
            false
        )
        rootviewBinding.rootContainer.addView(contentView)
        dataBinding = if (contentView.tag is String) DataBindingUtil.bind(contentView) else null
    }

    /**
     * 实例化当前页面中的各个控件。
     *
     * 该方法跟随[onCreate]生命周期
     *
     * @param binding 若当前页面布局的根标签是`layout`，则可直接使用参数所表示的`DataBingind`模式进行控件操作
     */
    abstract fun setupViews(binding: ViewDataBinding?)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) this.onFinish()
        return super.onOptionsItemSelected(item)
    }

    protected open fun onFinish() {
        this.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        ActivityManager.remove(this)
    }

    private fun initBaseToolbar(): Toolbar {
        rootviewBinding.toolbarLayout.toolbar.setBackgroundColor(
            ContextCompat.getColor(
                this,
                if (isCustomActionbar) customActionbarBackgroundColor else R.color.white
            )
        )
        rootviewBinding.toolbarLayout.toolbarTitle.text = getToolbarTitle()
        rootviewBinding.toolbarLayout.toolbarTitle.setTextColor(
            ContextCompat.getColor(
                this,
                if (isCustomActionbar) customActionbarTextColor else R.color.actionbar_text_color
            )
        )
        return rootviewBinding.toolbarLayout.toolbar
    }

    /**
     * 需要子类重写该方法以实现自定义的标题栏
     *
     * @return 返回自定义的[Toolbar]对象
     */
    protected open fun initCustomToolbar(): Toolbar? {
        rootviewBinding.toolbarLayout.toolbar.visibility = View.GONE
        return null
    }

    /**
     * 获取当前页面的标题
     *
     * @return 当前页面的标题资源
     */
    protected open fun getToolbarTitleRes(): Int = R.string.app_name

    /**
     * 获取当前页面的标题
     *
     * @return 当前页面的标题
     */
    protected open fun getToolbarTitle(): CharSequence = resources.getString(getToolbarTitleRes())
}