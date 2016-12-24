package net.shadowfacts.clipboard.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.shadowfacts.clipboard.Clipboard
import net.shadowfacts.clipboard.gui.GUIClipboard
import net.shadowfacts.clipboard.util.EntityItem
import net.shadowfacts.clipboard.util.Task
import net.shadowfacts.shadowmc.ShadowMC
import net.shadowfacts.shadowmc.block.BlockTE
import net.shadowfacts.shadowmc.network.PacketUpdateTE

/**
 * @author shadowfacts
 */
class BlockClipboard : BlockTE<TileEntityClipboard>(Material.ROCK, "clipboard") {

	companion object {
		val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

		val NORTH_BOX = AxisAlignedBB(3/16.0,	2/16.0,	15/16.0,	13/16.0,	14/16.0,	1.0)
		val SOUTH_BOX = AxisAlignedBB(3/16.0,	2/16.0,	0.0,		13/16.0,	14/16.0,	1/16.0)
		val WEST_BOX = AxisAlignedBB(15/16.0,	2/16.0,	3/16.0,		1.0,		14/16.0,	13/16.0)
		val EAST_BOX = AxisAlignedBB(0.0,		2/16.0,	3/16.0,		1/16.0,		14/16.0,	13/16.0)
	}

	init {
		unlocalizedName = registryName.toString()
	}

	override fun getTileEntityClass(): Class<TileEntityClipboard> {
		return TileEntityClipboard::class.java
	}

	override fun createBlockState(): BlockStateContainer {
		return BlockStateContainer(this, FACING)
	}

	override fun getMetaFromState(state: IBlockState): Int {
		return state.getValue(FACING).ordinal
	}

	@Deprecated("")
	override fun getStateFromMeta(meta: Int): IBlockState {
		return defaultState.withProperty(FACING, EnumFacing.VALUES[meta])
	}

	override fun getStateForPlacement(world: World, pos: BlockPos, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase, hand: EnumHand): IBlockState {
		return defaultState.withProperty(FACING, getDirection(pos, placer))
	}

	private fun getDirection(pos: BlockPos, entity: EntityLivingBase): EnumFacing {
		return EnumFacing.getFacingFromVector(
				entity.posX.toFloat() - pos.x,
				0f,
				entity.posZ.toFloat() - pos.z
		)
	}

	override fun onBlockActivated(world: World, pos: BlockPos, state: IBlockState, player: EntityPlayer, hand: EnumHand, heldItem: EnumFacing, side: Float, hitX: Float, hitY: Float): Boolean {
		if (world.isRemote) {
			openGUI(world, pos)
		}
		return true
	}

	@SideOnly(Side.CLIENT)
	private fun openGUI(world: World, pos: BlockPos) {
		val tile = getTileEntity(world, pos)
		val synchronizer = { tasks: List<Task> ->
			ShadowMC.network.sendToServer(PacketUpdateTE(tile))
		}
		Minecraft.getMinecraft().displayGuiScreen(GUIClipboard.create(tile, synchronizer))
	}

	override fun getPickBlock(state: IBlockState, target: RayTraceResult, world: World, pos: BlockPos, player: EntityPlayer): ItemStack {
		val stack = ItemStack(Clipboard.clipboard)
		stack.tagCompound = getTileEntity(world, pos).writeClipboard(NBTTagCompound())
		return stack
	}

	override fun getDrops(world: IBlockAccess, pos: BlockPos, state: IBlockState, fortune: Int): MutableList<ItemStack> {
		return mutableListOf()
	}

	override fun breakBlock(world: World, pos: BlockPos, state: IBlockState) {
		val stack = ItemStack(Clipboard.clipboard)
		stack.tagCompound = getTileEntity(world, pos).writeClipboard(NBTTagCompound())

		val entity = EntityItem(world, pos.x, pos.y, pos.z, stack)
		world.spawnEntity(entity)

		super.breakBlock(world, pos, state)
	}

	@Deprecated("")
	override fun getBoundingBox(state: IBlockState, source: IBlockAccess, pos: BlockPos): AxisAlignedBB {
		return when (state.getValue(FACING)) {
			EnumFacing.NORTH -> NORTH_BOX
			EnumFacing.SOUTH -> SOUTH_BOX
			EnumFacing.WEST -> WEST_BOX
			EnumFacing.EAST -> EAST_BOX
			else -> Block.FULL_BLOCK_AABB
		}
	}

	@Deprecated("")
	override fun isOpaqueCube(state: IBlockState): Boolean {
		return false
	}

	@Deprecated("")
	override fun isFullCube(state: IBlockState): Boolean {
		return false
	}

	fun canPlace(world: World, pos: BlockPos, side: EnumFacing): Boolean {
		return world.getBlockState(pos).isSideSolid(world, pos, side)
	}

	fun canPlace(world: World, pos: BlockPos, sideHit: EnumFacing, entity: EntityLivingBase): Boolean {
		val facing = getDirection(pos, entity)
		if (sideHit == EnumFacing.DOWN || sideHit == EnumFacing.UP) {
			return canPlace(world, pos.offset(sideHit).offset(facing.opposite), facing)
		} else {
			return canPlace(world, pos, facing)
		}
	}

	@Deprecated("")
	override fun neighborChanged(state: IBlockState, world: World, pos: BlockPos, block: Block, newPos: BlockPos) {
		val facing = state.getValue(FACING)
		if (!canPlace(world, pos.offset(facing.opposite), facing)) {
			dropBlockAsItem(world, pos, state, 0)
			world.setBlockToAir(pos)
		}
	}

}