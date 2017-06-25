package net.shadowfacts.clipboard

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.shadowfacts.clipboard.block.BlockClipboard
import net.shadowfacts.clipboard.block.TileEntityClipboard
import net.shadowfacts.clipboard.network.PacketUpdateClipboardBlock
import net.shadowfacts.clipboard.network.PacketUpdateClipboardItem

/**
 * @author shadowfacts
 */
@Mod(modid = MOD_ID, name = NAME, version = VERSION, dependencies = "required-after:shadowmc@[3.5.0,);", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object Clipboard {

	lateinit var network: SimpleNetworkWrapper
		private set

//	Content
	val clipboard = ItemClipboard()
	val blockClipboard = BlockClipboard()

	@Mod.EventHandler
	fun preInit(event: FMLPreInitializationEvent) {
		network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID)
		network.registerMessage(PacketUpdateClipboardItem.Handler::class.java, PacketUpdateClipboardItem::class.java, 0, Side.SERVER)
		network.registerMessage(PacketUpdateClipboardBlock.Handler::class.java, PacketUpdateClipboardBlock::class.java, 1, Side.SERVER)
	}

	@Mod.EventHandler
	@SideOnly(Side.CLIENT)
	fun preInitClient(event: FMLPreInitializationEvent) {
		clipboard.initItemModel()
	}

	@Mod.EventBusSubscriber
	object EventHandler {

		@JvmStatic
		@SubscribeEvent
		fun registerBlocks(event: RegistryEvent.Register<Block>) {
			event.registry.register(blockClipboard)
			GameRegistry.registerTileEntity(TileEntityClipboard::class.java, "$MOD_ID:clipboard")
		}

		@JvmStatic
		@SubscribeEvent
		fun registerItems(event: RegistryEvent.Register<Item>) {
			event.registry.register(clipboard)
		}

	}

}
