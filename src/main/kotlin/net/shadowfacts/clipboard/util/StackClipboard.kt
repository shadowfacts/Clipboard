package net.shadowfacts.clipboard.util

import net.minecraft.item.ItemStack

/**
 * @author shadowfacts
 */
class StackClipboard(val stack: ItemStack) : Clipboard {

	override fun getTasks(): MutableList<Task> {
		return stack.getTasks()
	}

	override fun setTasks(tasks: MutableList<Task>) {
		stack.setTasks(tasks)
	}

	override fun getPage(): Int {
		return stack.getPage()
	}

	override fun setPage(page: Int) {
		stack.setPage(page)
	}

}