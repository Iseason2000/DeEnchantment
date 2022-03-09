/*
 * Description:
 * @Author: Iseason2000
 * @Date: 2022/1/28 下午8:35
 *
 */

package top.iseason.deenchantment.utils.kparser

class Stack<T> {
    private val stack = arrayListOf<T>()
    private var top = -1
    fun push(item: T) {
        stack.add(item)
        top++
    }

    fun pop(): T = stack.removeAt(top--)

    fun peek(): T = stack[top]

    fun isEmpty() = stack.isEmpty()

    fun size() = top + 1

    fun display() = println(stack)

    fun clear() {
        stack.clear()
        top = -1
    }
}