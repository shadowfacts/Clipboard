package net.shadowfacts.clipboard.network

import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.shadowfacts.clipboard.Clipboard
import net.shadowfacts.clipboard.util.setTasks

/**
 * @author shadowfacts
 */
class HandlerUpdateClipboard : IMessageHandler<PacketUpdateClipboard, IMessage> {

	override fun onMessage(message: PacketUpdateClipboard, ctx: MessageContext?): IMessage? {
		val player = ctx!!.serverHandler.playerEntity
		val stack = player.getHeldItem(message.hand)
		if (stack.item == Clipboard.clipboard) {
			stack.setTasks(message.tasks!!)
		}
		return null
	}

}