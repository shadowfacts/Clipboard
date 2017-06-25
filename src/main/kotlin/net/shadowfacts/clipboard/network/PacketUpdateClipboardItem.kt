package net.shadowfacts.clipboard.network

import io.netty.buffer.ByteBuf
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumHand
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.shadowfacts.clipboard.Clipboard
import java.util.*

/**
 * @author shadowfacts
 */
class PacketUpdateClipboardItem(): IMessage {

	lateinit var player: UUID
	lateinit var hand: EnumHand
	lateinit var tag: NBTTagCompound

	constructor(stack: ItemStack, player: EntityPlayer, hand: EnumHand): this() {
		this.player = player.uniqueID
		this.hand = hand
		this.tag = stack.tagCompound!!
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeLong(player.mostSignificantBits)
		buf.writeLong(player.leastSignificantBits)
		buf.writeInt(hand.ordinal)
		ByteBufUtils.writeTag(buf, tag)
	}

	override fun fromBytes(buf: ByteBuf) {
		player = UUID(buf.readLong(), buf.readLong())
		hand = EnumHand.values()[buf.readInt()]
		tag = ByteBufUtils.readTag(buf)!!
	}


	class Handler: IMessageHandler<PacketUpdateClipboardItem, IMessage> {

		override fun onMessage(message: PacketUpdateClipboardItem, ctx: MessageContext?): IMessage? {
			val player = FMLCommonHandler.instance().minecraftServerInstance.playerList.getPlayerByUUID(message.player)
			val stack = player.getHeldItem(message.hand)
			if (stack.item == Clipboard.clipboard) {
				stack!!.tagCompound = message.tag
			}
			return null
		}

	}

}