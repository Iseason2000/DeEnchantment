package top.iseason.bukkittemplate.ui.container

import org.bukkit.entity.HumanEntity

/**
 * 多页UI，采用懒加载模式，当UI显示时才初始化
 */
open class LazyUIContainer(
    protected val pageTypes: Array<Class<out BaseUI>>,
    /**
     * 是否启用缓存，如果为false则每人的每个UI都是新对象
     */
    protected val allowCache: Boolean = true
) : UIContainer(arrayOfNulls(pageTypes.size)) {

    /**
     * 当页面被第一次加载时，将由class生成对象的方法
     */
    open fun onInit(clazz: Class<out BaseUI>): BaseUI {
        return clazz.newInstance()
    }

    /**
     * 当新的UI被构建前，可以对UI进行修改的回调函数
     */
    open fun onUIBuild(ui: BaseUI, player: HumanEntity) {

    }

    /**
     * 获取当前的UI，如果不存在则创建一个
     */
    override fun getCurrentPage(player: HumanEntity): BaseUI {
        val index = viewers[player] ?: 0
        if (!allowCache || pages[index] == null) {
            val ui = onInit(pageTypes[index])
            onUIBuild(ui, player)
            ui.build()
            ui.container = this
            pages[index] = ui
        }
        return super.getCurrentPage(player)!!
    }

}
