package net.shadowfacts.clipboard.gui.element

import net.shadowfacts.shadowmc.ui.element.textfield.UITextField
import java.util.function.Consumer

/**
 * @author shadowfacts
 */
class UITaskTextField(text: String, var id: Int, name: String, handler: (UITaskTextField) -> Unit) : UITextField(null, name) {

	init {
		setHandler(handler)
		setText(text)

		setPreferredWidth(114)

		drawBackground = false
	}

	fun setHandler(handler: (UITaskTextField) -> Unit) {
		textChangeHandler = Consumer {
			handler(this)
		}
	}

	override fun setText(text: String) {
		super.setText(text)
		cursorPos = 0
		cursorCounter = 0
		selectionEnd = 0
	}

}