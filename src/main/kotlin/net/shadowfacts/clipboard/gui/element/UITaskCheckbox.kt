package net.shadowfacts.clipboard.gui.element

import net.shadowfacts.shadowmc.ui.UIDimensions
import net.shadowfacts.shadowmc.ui.element.button.UIButtonBase
import net.shadowfacts.shadowmc.ui.element.button.UIButtonToggle
import net.shadowfacts.shadowmc.ui.util.UIHelper
import java.util.function.Consumer

/**
 * @author shadowfacts
 */
class UITaskCheckbox(state: Boolean, var id: Int, name: String, handler: (UITaskCheckbox) -> Unit) : UIButtonToggle(state, Consumer { handler(it as UITaskCheckbox) }, name) {

	fun setHandler(handler: (UITaskCheckbox) -> Unit) {
		callback = Consumer {
			handler(this)
		}
	}

	override fun draw(mouseX: Int, mouseY: Int) {
		UIHelper.bindTexture(UIButtonBase.TEXTURE)
		val u = if (state) 0 else 20
		UIHelper.drawTexturedRect(x - 4, y - 4, u, 0, dimensions.width + 8, dimensions.height + 8)
	}

	override fun getPreferredDimensions(): UIDimensions {
		return UIDimensions(12, 12)
	}

}