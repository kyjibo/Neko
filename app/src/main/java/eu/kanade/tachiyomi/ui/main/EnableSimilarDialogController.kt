package eu.kanade.tachiyomi.ui.main

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.data.preference.PreferencesHelper
import eu.kanade.tachiyomi.data.similar.SimilarUpdateJob
import eu.kanade.tachiyomi.ui.base.controller.DialogController
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get

class EnableSimilarDialogController : DialogController() {

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        val activity = activity!!
        val preferences = Injekt.get<PreferencesHelper>()
        return MaterialDialog(activity)
            .title(text = activity.getString(R.string.similar_ask_to_enable_title))
            .message(R.string.similar_ask_to_enable)
            .negativeButton(R.string.similar_ask_to_enable_no, activity.getString(R.string.similar_ask_to_enable_no))
            .positiveButton(R.string.similar_ask_to_enable_yes, activity.getString(R.string.similar_ask_to_enable_yes)) {
                preferences.similarEnabled().set(true)
                SimilarUpdateJob.setupTask()
            }
    }
}
