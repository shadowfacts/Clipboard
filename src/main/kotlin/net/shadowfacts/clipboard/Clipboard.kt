package net.shadowfacts.clipboard

import net.minecraft.init.Blocks
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.oredict.ShapelessOreRecipe
import net.shadowfacts.clipboard.block.BlockClipboard
import net.shadowfacts.clipboard.block.TileEntityClipboard
import net.shadowfacts.clipboard.network.HandlerUpdateClipboard
import net.shadowfacts.clipboard.network.PacketUpdateClipboard

/**
 * @author shadowfacts
 */
@Mod(modid = MOD_ID, name = NAME, version = VERSION, dependencies = "required-after:shadowmc@[3.5.0,);", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object Clipboard {

	var network: SimpleNetworkWrapper? = null
		private set

//	Content
	val clipboard = ItemClipboard()
	val blockClipboard = BlockClipboard()

	@Mod.EventHandler
	fun preInit(event: FMLPreInitializationEvent) {
		GameRegistry.register(clipboard)
		GameRegistry.register(blockClipboard)

		GameRegistry.registerTileEntity(TileEntityClipboard::class.java, "$MOD_ID:clipboard")

		GameRegistry.addRecipe(ShapelessOreRecipe(clipboard, "dyeBlack", "feather", "paper", Blocks.WOODEN_PRESSURE_PLATE))

		network = NetworkRegistry.INSTANCE.newSimpleChannel(MOD_ID)
		network!!.registerMessage(HandlerUpdateClipboard::class.java, PacketUpdateClipboard::class.java, 0, Side.SERVER)
	}

	@Mod.EventHandler
	@SideOnly(Side.CLIENT)
	fun preInitClient(event: FMLPreInitializationEvent) {
		clipboard.initItemModel()
	}

}
