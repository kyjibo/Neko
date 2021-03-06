package eu.kanade.tachiyomi.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import com.google.android.material.button.MaterialButton
import com.mikepenz.iconics.typeface.IIcon
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.util.system.iconicsDrawable
import eu.kanade.tachiyomi.util.view.gone
import eu.kanade.tachiyomi.util.view.visible
import kotlinx.android.synthetic.main.common_view_empty.view.*

class EmptyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    RelativeLayout(context, attrs) {

    init {
        inflate(context, R.layout.common_view_empty, this)
    }

    /**
     * Hide the information view
     */
    fun hide() {
        this.gone()
    }

    /**
     * Show the information view
     * @param textResource text of information view
     */
    fun show(icon: IIcon, @StringRes textResource: Int, actions: List<Action>? = null) {
        show(icon, context.getString(textResource), actions)
    }

    /**
     * Show the information view
     * @param drawable icon of information view
     * @param textResource text of information view
     */

    fun showMedium(icon: IIcon, message: String, actions: List<Action>? = null) {
        image_view.setImageDrawable(
            context.iconicsDrawable(
                icon, color = android.R.attr.textColorHint,
                size = 48
            )
        )
        iconicsAfter(message, actions)
    }

    fun show(icon: IIcon, message: String, actions: List<Action>? = null) {
        image_view.setImageDrawable(
            context.iconicsDrawable(
                icon, color = android.R.attr.textColorHint, size = 128
            )
        )
        iconicsAfter(message, actions)
    }

    fun iconicsAfter(message: String, actions: List<Action>? = null) {
        text_label.text = message

        actions_container.removeAllViews()
        if (!actions.isNullOrEmpty()) {
            actions.forEach {
                val button = (
                    inflate(
                        context,
                        R.layout.material_text_button,
                        null
                    ) as MaterialButton
                    ).apply {
                    setText(it.resId)
                    setOnClickListener(it.listener)
                }

                actions_container.addView(button)
            }
        }

        this.visible()
    }

    data class Action(
        @StringRes val resId: Int,
        val listener: OnClickListener
    )
}
