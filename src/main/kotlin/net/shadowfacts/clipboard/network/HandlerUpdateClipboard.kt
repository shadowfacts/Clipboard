package net.shadowfacts.clipboard.network

import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

/**
 * @author shadowfacts
 */
class HandlerUpdateClipboard : IMessageHandler<PacketUpdateClipboard, IMessage> {

	override fun onMessage(message: PacketUpdateClipboard, ctx: MessageContext?): IMessage? {
		val player = FMLCommonHandler.instance().minecraftServerInstance.playerList.getPlayerByUUID(message.player)
		val stack = player.getHeldItem(message.hand)
		stack!!.tagCompound = message.tag
		return null
	}

}