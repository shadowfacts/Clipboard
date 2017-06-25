package net.shadowfacts.clipboard.network

import io.netty.buffer.ByteBuf
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.ByteBufUtils
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.shadowfacts.clipboard.block.TileEntityClipboard

/**
 * @author shadowfacts
 */
class PacketUpdateClipboardBlock(): IMessage {

	var dim: Int = 0
	lateinit var pos: BlockPos
	lateinit var tag: NBTTagCompound

	constructor(te: TileEntityClipboard): this() {
		this.dim = te.world.provider.dimension
		this.pos = te.pos
		this.tag = te.writeToNBT(NBTTagCompound())
	}

	override fun toBytes(buf: ByteBuf) {
		buf.writeInt(dim)
		buf.writeLong(pos.toLong())
		ByteBufUtils.writeTag(buf, tag)
	}

	override fun fromBytes(buf: ByteBuf) {
		dim = buf.readInt()
		pos = BlockPos.fromLong(buf.readLong())
		tag = ByteBufUtils.readTag(buf)!!
	}

	class Handler: IMessageHandler<PacketUpdateClipboardBlock, IMessage> {

		override fun onMessage(message: PacketUpdateClipboardBlock, ctx: MessageContext): IMessage? {
			val world = FMLCommonHandler.instance().minecraftServerInstance.getWorld(message.dim)
			val te = world.getTileEntity(message.pos)
			if (te is TileEntityClipboard) {
				te.readFromNBT(message.tag)
			}
			return null
		}

	}

}