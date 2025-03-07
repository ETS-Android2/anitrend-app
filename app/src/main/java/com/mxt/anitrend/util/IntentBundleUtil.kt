package com.mxt.anitrend.util

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.TextUtils
import androidx.core.app.ShareCompat
import androidx.fragment.app.FragmentActivity
import com.mxt.anitrend.util.markdown.RegexUtil
import java.util.regex.Matcher

/**
 * Created by max on 2017/12/01.
 * Intent data formatter and helper methods
 */

class IntentBundleUtil(private val intent: Intent) {

    var sharedIntent: ShareCompat.IntentReader? = null

    private val deepLinkMatcher: Matcher? by lazy(LazyThreadSafetyMode.NONE) {
        RegexUtil.findIntentKeys(intentData?.path)
    }

    private val intentAction: String? = intent.action

    private val intentData: Uri? = intent.data

    private fun hasDepth(key: String?): Array<String>? {
        return if (key?.contains("/") == true)
            key.split("/".toRegex())
                    .dropLastWhile {
                        it.isEmpty()
                    }.toTypedArray()
        else null
    }

    private fun injectIntentParams() {
        deepLinkMatcher?.also {
            val type = it.group(1)
            val groupLimit = it.groupCount()

            var lastKey = it.group(groupLimit)
            val splitKeys = hasDepth(lastKey)

            when (type) {
                KeyUtil.DEEP_LINK_ACTIVITY -> {
                    if (splitKeys != null)
                        intent.putExtra(KeyUtil.arg_id, splitKeys[0].toLong())
                    else
                        intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())
                }
                KeyUtil.DEEP_LINK_USER -> when {
                    TextUtils.isDigitsOnly(lastKey) -> intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())
                    else -> {
                        val keys = lastKey?.split("/", limit = 2)
                        intent.putExtra(KeyUtil.arg_userName, keys?.getOrNull(0))
                        when (keys?.getOrNull(1)?.lowercase()) {
                            "animelist" -> intent.putExtra(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                            "mangalist" -> intent.putExtra(KeyUtil.arg_mediaType, KeyUtil.MANGA)
                        }
                    }
                }
                KeyUtil.DEEP_LINK_MANGA -> {
                    if (splitKeys != null)
                        intent.putExtra(KeyUtil.arg_id, splitKeys[0].toLong())
                    else
                        intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())
                    intent.putExtra(KeyUtil.arg_mediaType, KeyUtil.MANGA)
                }
                KeyUtil.DEEP_LINK_ANIME -> {
                    if (splitKeys != null)
                        intent.putExtra(KeyUtil.arg_id, splitKeys[0].toLong())
                    else
                        intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())
                    intent.putExtra(KeyUtil.arg_mediaType, KeyUtil.ANIME)
                }
                KeyUtil.DEEP_LINK_CHARACTER -> if (splitKeys != null)
                    intent.putExtra(KeyUtil.arg_id, splitKeys[0].toLong())
                else
                    intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())

                KeyUtil.DEEP_LINK_ACTOR -> if (splitKeys != null)
                    intent.putExtra(KeyUtil.arg_id, splitKeys[0].toLong())
                else
                    intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())

                KeyUtil.DEEP_LINK_STAFF -> if (splitKeys != null)
                    intent.putExtra(KeyUtil.arg_id, splitKeys[0].toLong())
                else
                    intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())

                KeyUtil.DEEP_LINK_STUDIO -> if (splitKeys != null)
                    intent.putExtra(KeyUtil.arg_id, splitKeys[0].toLong())
                else
                    intent.putExtra(KeyUtil.arg_id, lastKey?.toLong())
            }
        }
    }

    fun checkIntentData(context: FragmentActivity) {
        if (context.intent?.hasExtra(KeyUtil.arg_shortcut_used) == true && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1)
            ShortcutUtil.reportShortcutUsage(context, context.intent.getIntExtra(KeyUtil.arg_shortcut_used, KeyUtil.SHORTCUT_SEARCH))

        if (!intentAction.isNullOrEmpty() && intentAction == Intent.ACTION_SEND)
            sharedIntent = ShareCompat.IntentReader.from(context)
        else if (deepLinkMatcher != null)
            injectIntentParams()
    }
}

