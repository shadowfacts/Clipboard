package net.shadowfacts.clipboard.block

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.common.util.Constants
import net.shadowfacts.clipboard.util.Clipboard
import net.shadowfacts.clipboard.util.Task
import net.shadowfacts.clipboard.util.getTasks
import net.shadowfacts.forgelin.extensions.forEach
import net.shadowfacts.shadowmc.ShadowMC
import net.shadowfacts.shadowmc.network.PacketRequestTEUpdate
import net.shadowfacts.shadowmc.tileentity.BaseTileEntity

/**
 * @author shadowfacts
 */
class TileEntityClipboard : BaseTileEntity(), Clipboard {

	private var tasks: MutableList<Task> = mutableListOf()
	private var page: Int = 0

	fun load(stack: ItemStack) {
		tasks = stack.getTasks()
	}

	fun writeClipboard(tag: NBTTagCompound): NBTTagCompound {
		val list = NBTTagList()

		tasks.forEach {
			val task = NBTTagCompound()
			task.setString("task", it.task)
			task.setBoolean("state", it.state)
			list.appendTag(task)
		}

		tag.setTag("tasks", list)
		tag.setInteger("page", page)

		return tag
	}

	override fun writeToNBT(tag: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(tag)
		return writeClipboard(tag)
	}

	override fun readFromNBT(tag: NBTTagCompound) {
		super.readFromNBT(tag)

		tasks.clear()
		val list = tag.getTagList("tasks", Constants.NBT.TAG_COMPOUND)
		list.forEach {
			val task = it as NBTTagCompound
			tasks.add(Task(task.getString("task"), task.getBoolean("state")))
		}

		page = tag.getInteger("page")
	}

	override fun onLoad() {
		if (world.isRemote) {
			ShadowMC.network.sendToServer(PacketRequestTEUpdate(this))
		}
	}

	override fun getTasks(): MutableList<Task> {
		return tasks
	}

	override fun setTasks(tasks: MutableList<Task>) {
		this.tasks = tasks
	}

	override fun getPage(): Int {
		return page
	}

	override fun setPage(page: Int) {
		this.page = page
	}

}