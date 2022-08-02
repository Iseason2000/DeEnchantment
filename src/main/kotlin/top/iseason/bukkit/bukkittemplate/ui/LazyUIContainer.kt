package top.iseason.bukkit.bukkittemplate.ui

import org.bukkit.entity.HumanEntity

/**
 * 多页UI，采用懒加载模式，当UI显示时才初始化,UI应该具有空构造函数
 */
open class LazyUIContainer(
    private val pageTypes: List<Class<out Pageable>>,
    /**
     * 是否启用缓存，如果为false则每人的每个UI都是新对象
     */
    private val allowCache: Boolean = true
) : UIContainer(arrayOfNulls(pageTypes.size)) {

    /**
     * 当新的UI被构建时，可以对UI进行修改的回调函数
     */
    open var onUIBuild: (BaseUI.(HumanEntity) -> Unit)? = null

    /**
     * 获取当前的UI，如果不存在则创建一个
     */
    override fun getCurrentPage(player: HumanEntity): BaseUI {
        val index = viewers[player] ?: 0
        if (!allowCache || pages[index] == null) {
            val pageable = pageTypes[index].newInstance() as Pageable
            pageable.getUI().apply { onUIBuild?.invoke(this, player) }.build()
            pageable.container = this
            pages[index] = pageable
        }
        return super.getCurrentPage(player)!!
    }

}
