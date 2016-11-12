package net.shadowfacts.clipboard.util

/**
 * @author shadowfacts
 */
interface Clipboard {

	fun getTasks(): MutableList<Task>

	fun setTasks(tasks: MutableList<Task>)

	fun getPage(): Int

	fun setPage(page: Int)

}