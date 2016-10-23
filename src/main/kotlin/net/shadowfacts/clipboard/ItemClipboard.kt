package net.shadowfacts.clipboard

import net.minecraft.client.Minecraft
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
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
	}

	override fun initItemModel() {
		ShadowMC.proxy.registerItemModel(this, 0, ResourceLocation(MOD_ID, "clipboard"))
	}

	override fun onItemRightClick(stack: ItemStack, world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
		val synchronizer = {
			Clipboard.network!!.sendToServer(PacketUpdateClipboard(stack, player, hand))
		}
		Minecraft.getMinecraft().displayGuiScreen(GUIClipboard.create(stack, synchronizer))
		player.swingArm(hand)
		return ActionResult(EnumActionResult.SUCCESS, stack)
	}

}