package net.shadowfacts.clipboard

import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.shadowfacts.clipboard.block.TileEntityClipboard
import net.shadowfacts.clipboard.gui.GUIClipboard
import net.shadowfacts.clipboard.network.PacketUpdateClipboard
import net.shadowfacts.shadowmc.ShadowMC
import net.shadowfacts.shadowmc.item.ItemBase

/**
 * @author shadowfacts
 */
class ItemClipboard : ItemBase("clipboard") {

	init {
		unlocalizedName = registryName.toString()
		creativeTab = CreativeTabs.MISC
		setMaxStackSize(1)
	}

	override fun initItemModel() {
		ShadowMC.proxy.registerItemModel(this, 0, ResourceLocation(MOD_ID, "clipboard"))
	}

	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
		if (world.isRemote && !player.isSneaking) {
			openGUI(stack, player, hand)
			return ActionResult(EnumActionResult.SUCCESS, stack)
		}
		return ActionResult(EnumActionResult.PASS, stack)
	}

	@SideOnly(Side.CLIENT)
	private fun openGUI(stack: ItemStack, player: EntityPlayer, hand: EnumHand) {
		val synchronizer = {
			Clipboard.network!!.sendToServer(PacketUpdateClipboard(stack, player, hand))
		}
		Minecraft.getMinecraft().displayGuiScreen(GUIClipboard.create(stack, synchronizer))
		player.swingArm(hand)
	}

	override fun onItemUse(stack: ItemStack, player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
		if (player.isSneaking) {
			world.setBlockState(pos.offset(facing), Clipboard.blockClipboard.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, getMetadata(stack), player, stack))
			(world.getTileEntity(pos.offset(facing)) as TileEntityClipboard).load(stack)
			player.setHeldItem(hand, null)
			return EnumActionResult.SUCCESS
		}
		return EnumActionResult.PASS
	}

}