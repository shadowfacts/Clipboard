package net.shadowfacts.clipboard.util

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants
import net.shadowfacts.forgelin.extensions.forEach
import org.apache.commons.lang3.tuple.MutablePair

/**
 * @author shadowfacts
 */
fun ItemStack.getTasks(): MutableList<MutablePair<String, Boolean>> {
	if (!hasTagCompound()) return mutableListOf()
	val list = mutableListOf<MutablePair<String, Boolean>>()
	val taglist = tagCompound!!.getTagList("tasks", Constants.NBT.TAG_COMPOUND)
	taglist.forEach {
		val tag = it as NBTTagCompound
		list.add(MutablePair.of(tag.getString("item"), tag.getBoolean("state")))
	}
	return list
}

fun ItemStack.setTasks(tasks: List<MutablePair<String, Boolean>>) {
	if (!hasTagCompound()) tagCompound = NBTTagCompound()
	val taglist = NBTTagList()
	tasks.forEach {
		val tag = NBTTagCompound()
		tag.setString("item", it.left)
		tag.setBoolean("state", it.right)
		taglist.appendTag(tag)
	}
	tagCompound!!.setTag("tasks", taglist)
}

fun ItemStack.getPage(): Int {
	if (!hasTagCompound()) return 0
	return tagCompound!!.getInteger("page")
}

fun ItemStack.setPage(page: Int) {
	if (!hasTagCompound()) tagCompound = NBTTagCompound()
	tagCompound!!.setInteger("page", page)
}

fun EntityItem(world: World, x: Int, y: Int, z: Int, stack: ItemStack): EntityItem {
	return EntityItem(world, x.toDouble(), y.toDouble(), z.toDouble(), stack)
}