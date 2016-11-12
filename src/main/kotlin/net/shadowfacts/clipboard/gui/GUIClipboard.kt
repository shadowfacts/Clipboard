package net.shadowfacts.clipboard.gui

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.shadowfacts.clipboard.MOD_ID
import net.shadowfacts.clipboard.gui.element.UITaskCheckbox
import net.shadowfacts.clipboard.gui.element.UITaskTextField
import net.shadowfacts.clipboard.util.*
import net.shadowfacts.shadowmc.ui.UIDimensions
import net.shadowfacts.shadowmc.ui.element.UILabel
import net.shadowfacts.shadowmc.ui.element.button.UIButtonBase
import net.shadowfacts.shadowmc.ui.element.button.UIImage
import net.shadowfacts.shadowmc.ui.element.view.UIFixedView
import net.shadowfacts.shadowmc.ui.element.view.UIStackView
import net.shadowfacts.shadowmc.ui.util.UIBuilder
import net.shadowfacts.shadowmc.ui.util.UIHelper
import net.shadowfacts.shadowmc.util.MouseButton
import org.apache.commons.lang3.tuple.MutablePair
import org.lwjgl.input.Keyboard

/**
 * @author shadowfacts
 */
object GUIClipboard {

	val BG = ResourceLocation("clipboard", "textures/gui/clipboard.png")

	fun create(clipboard: Clipboard, synchronize: () -> Unit): GuiScreen {
		val tasks = clipboard.getTasks()
		val update = {
			clipboard.setTasks(tasks)
			synchronize()
		}

		val root = UIFixedView(192, 192, "root")
		root.add(UIImage(BG, 192, 192, "bg"))

		val stack = UIStackView("stack")

		val items = mutableListOf<Pair<UITaskCheckbox, UITaskTextField>>()

		for (i in 0.until(9)) {
			val checkbox = UITaskCheckbox(false, i, "checkbox$i", {})
			val textfield = UITaskTextField("", i, "textfield$i", {})
			items.add(Pair(checkbox, textfield))

			val innerStack = UIStackView("innerStack$i", "innerStack")
			innerStack.add(checkbox)
			innerStack.add(textfield)
			stack.add(innerStack)
		}

		val pageIndicator = UILabel("", "page")
		root.add(pageIndicator)

		val updateUI = {
			for (i in 0.until(9)) {
				val taskId = clipboard.getPage() * 9 + i
				val (checkbox, textfield) = items[i]
				checkbox.id = taskId
				checkbox.state = if (taskId < tasks.size) tasks[taskId].state else false
				checkbox.setHandler {
					if (it.id < tasks.size) {
						tasks[it.id].state = it.state
					} else {
						tasks.add(Task("", it.state))
						it.id = tasks.size - 1
						textfield.id = it.id
					}
					update()
				}
				textfield.id = taskId
				textfield.text = if (taskId < tasks.size) tasks[taskId].task else ""
				textfield.setHandler {
					if (it.id < tasks.size) {
						tasks[it.id].task = it.text
					} else {
						tasks.add(Task(it.text, false))
						it.id = tasks.size - 1
						checkbox.id = it.id
					}
					update()
				}
			}
			pageIndicator.setText((clipboard.getPage() + 1).toString())
		}

		updateUI()

		val prevPage = object : UIButtonBase("button-page", "prev") {
			init {
				if (clipboard.getPage() == 0) {
					enabled = false
				}
			}

			override fun drawButton(mouseX: Int, mouseY: Int) {
				UIHelper.bindTexture(BG)
				UIHelper.drawTexturedRect(x, y, if (enabled) 26 else 3, 207, dimensions.width, dimensions.height)
			}

			override fun handlePress(mouseX: Int, mouseY: Int, button: MouseButton): Boolean {
				clipboard.setPage(clipboard.getPage() - 1)
				updateUI()
				if (clipboard.getPage() == 0) {
					enabled = false
				}
				return true
			}

			override fun getPreferredDimensions(): UIDimensions = UIDimensions(18, 10)

			override fun getMinDimensions(): UIDimensions = preferredDimensions
		}
		root.add(prevPage)

		val nextPage = object : UIButtonBase("button-page", "next") {
			override fun drawButton(mouseX: Int, mouseY: Int) {
				UIHelper.bindTexture(BG)
				UIHelper.drawTexturedRect(x, y, 26, 194, dimensions.width, dimensions.height)
			}

			override fun handlePress(mouseX: Int, mouseY: Int, button: MouseButton): Boolean {
				clipboard.setPage(clipboard.getPage() + 1)
				updateUI()
				if (clipboard.getPage() != 0) {
					prevPage.setEnabled(true)
				}
				return true
			}

			override fun getPreferredDimensions(): UIDimensions = UIDimensions(18, 10)

			override fun getMinDimensions(): UIDimensions = preferredDimensions
		}
		root.add(nextPage)

		root.add(stack)

		return UIBuilder()
				.add(root)
				.style("$MOD_ID:clipboard")
				.setCloseHandler(update)
				.clearKeyHandlers()
				.addKeyHandler { keyCode, keyChar ->
					if (keyCode == Keyboard.KEY_ESCAPE) Minecraft.getMinecraft().displayGuiScreen(null)
				}
				.setPausesGame(false)
				.createScreen()
	}

}