import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat.getColor
import com.hailm.mapinvitedemo.base.extension.inflater
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.widget.checkedChanges

val View.inflater: LayoutInflater
    get() = context.inflater

fun TextView.setTextViewDrawableColor(color: Int) {
    for (drawable in this.compoundDrawables) {
        if (drawable != null) {
            drawable.colorFilter =
                PorterDuffColorFilter(getColor(context, color), PorterDuff.Mode.SRC_IN)
        }
    }
}

val SwitchCompat.validateStateFlow: StateFlow<Boolean>
    get() {
        val validateStateFlow by lazy { MutableStateFlow(false) }
        this.checkedChanges()
            .onEach {
                validateStateFlow.emit(it)
            }
            .launchIn(MainScope())
        return validateStateFlow
    }