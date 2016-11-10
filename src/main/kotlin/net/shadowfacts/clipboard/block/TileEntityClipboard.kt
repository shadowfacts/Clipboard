package net.shadowfacts.clipboard.block

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.shadowfacts.clipboard.util.Task
import net.shadowfacts.clipboard.util.getTasks
import net.shadowfacts.forgelin.extensions.forEach
import net.shadowfacts.shadowmc.tileentity.BaseTileEntity
import org.apache.commons.lang3.tuple.MutablePair

/**
 * @author shadowfacts
 */
class TileEntityClipboard : BaseTileEntity() {

	var tasks: MutableList<Task> = mutableListOf()
		private set

	fun load(stack: ItemStack) {
		tasks = stack.getTasks()
	}

	fun writeTasks(tag: NBTTagCompound): NBTTagCompound {
		val list = NBTTagList()

		tasks.forEach {
			val task = NBTTagCompound()
			task.setString("task", it.task)
			task.setBoolean("state", it.state)
			list.appendTag(task)
		}

		tag.setTag("tasks", list)

		return tag
	}

	override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(tag)
		return writeTasks(tag)
	}

	override fun readFromNBT(tag: NBTTagCompound) {
		super.readFromNBT(tag)

		val list = tag.getTagList("tasks", Constants.NBT.TAG_COMPOUND)

		list.forEach {
			val task = it as NBTTagCompound
			tasks.add(Task(task.getString("item"), task.getBoolean("state")))
		}
	}

}