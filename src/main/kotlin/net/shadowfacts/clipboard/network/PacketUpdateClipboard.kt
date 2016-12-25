package net.shadowfacts.clipboard.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.EnumHand
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.shadowfacts.clipboard.util.Task

/**
 * @author shadowfacts
 */
class PacketUpdateClipboard(var tasks: List<Task>?, var hand: EnumHand?) : IMessage {

	constructor(): this(null, null)

	override fun toBytes(buf: ByteBuf) {
		buf.writeBoolean(hand == EnumHand.MAIN_HAND)
		var tasks = tasks!!
		buf.writeShort(tasks.size)
		for (task in tasks) {
			ByteBufUtils.writeUTF8String(buf, task.task)
			buf.writeBoolean(task.state)
		}
	}

	override fun fromBytes(buf: ByteBuf) {
		hand = if (buf.readBoolean()) EnumHand.MAIN_HAND else EnumHand.OFF_HAND
		var tasks = mutableListOf<Task>()
		var count = buf.readShort()
        while (count --> 0) {
            tasks.add(Task(ByteBufUtils.readUTF8String(buf), buf.readBoolean()))
        }
        this.tasks = tasks
	}

}