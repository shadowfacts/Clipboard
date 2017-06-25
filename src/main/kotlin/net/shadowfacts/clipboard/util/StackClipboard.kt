package net.shadowfacts.clipboard.util

import net.minecraft.item.ItemStack

/**
 * @author shadowfacts
 */
class StackClipboard(val stack: ItemStack) : Clipboard {

	override var tasks
		get() = stack.getTasks()
		set(value) { stack.setTasks(value) }

	override var page
		get() = stack.getPage()
		set(value) { stack.setPage(value) }

}