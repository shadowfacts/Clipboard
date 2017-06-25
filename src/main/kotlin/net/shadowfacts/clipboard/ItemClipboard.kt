package net.shadowfacts.clipboard

import net.minecraft.block.SoundType
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
import net.shadowfacts.clipboard.network.PacketUpdateClipboardItem
import net.shadowfacts.clipboard.util.StackClipboard
import net.shadowfacts.shadowmc.ShadowMC
import net.shadowfacts.shadowmc.item.ItemBase

/**
 * @author shadowfacts
 */
class ItemClipboard : ItemBase("clipboard") {

	init {
		creativeTab = CreativeTabs.MISC
		setMaxStackSize(1)
	}

	override fun initItemModel() {
		ShadowMC.proxy.registerItemModel(this, 0, ResourceLocation(MOD_ID, "clipboard"))
	}

	override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
		val stack = player.getHeldItem(hand)
		if (world.isRemote && !player.isSneaking) {
			openGUI(stack, player, hand)
			return ActionResult(EnumActionResult.SUCCESS, stack)
		}
		return ActionResult(EnumActionResult.PASS, stack)
	}

	@SideOnly(Side.CLIENT)
	private fun openGUI(stack: ItemStack, player: EntityPlayer, hand: EnumHand) {
		val synchronizer = {
			Clipboard.network.sendToServer(PacketUpdateClipboardItem(stack, player, hand))
		}
		Minecraft.getMinecraft().displayGuiScreen(GUIClipboard.create(StackClipboard(stack), synchronizer))
		player.swingArm(hand)
	}

	override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand, side: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
		if (player.isSneaking) {
			val stack = player.getHeldItem(hand)
			if (Clipboard.blockClipboard.canPlace(world, pos, side, player)) {
				world.setBlockState(pos.offset(side), Clipboard.blockClipboard.getStateForPlacement(world, pos, side, hitX, hitY, hitZ, getMetadata(stack), player, hand))
				(world.getTileEntity(pos.offset(side)) as TileEntityClipboard).load(stack)
				player.setHeldItem(hand, ItemStack.EMPTY)

				val sound = SoundType.WOOD
				world.playSound(player, pos.offset(side), sound.placeSound, SoundCategory.BLOCKS, (sound.volume + 1) / 2, sound.pitch * 0.8f)

				return EnumActionResult.SUCCESS
			}
		}
		return EnumActionResult.PASS
	}

	override fun shouldCauseReequipAnimation(oldStack: ItemStack, newStack: ItemStack?, slotChanged: Boolean): Boolean {
		return !oldStack.isItemEqual(newStack)
	}

}