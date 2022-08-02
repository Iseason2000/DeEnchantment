package top.iseason.bukkit.bukkittemplate.ui

interface Pageable {
    var container: UIContainer?
    fun getUI(): BaseUI
}