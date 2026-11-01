package com.kglabs28.yapnplay.appengine.core.ads

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Outline
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * The ad pill, inside AdMob's native ad view (its clicks and impressions are
 * counted only on a NativeAdView with its parts registered): the "AD"
 * badge, the advertiser's icon, the headline (shrunk to fit, never cut)
 * and the call-to-action button, on a glass pill drawn to [look].
 */
@SuppressLint("ViewConstructor")
class AdPillView(context: Context, private val look: AdPillLook) : FrameLayout(context) {

    private val adView = NativeAdView(context)

    private val icon = ImageView(context).apply {
        scaleType = ImageView.ScaleType.CENTER_CROP
        outlineProvider = RoundOutline
        clipToOutline = true
    }
    private val headline = TextView(context).apply {
        setTextColor(look.text)
        typeface = Typeface.DEFAULT_BOLD
        maxLines = 1
        setAutoSizeTextTypeUniformWithConfiguration(MIN_TEXT_PX, look.textSize.toInt().coerceAtLeast(MIN_TEXT_PX + 1), 1, TypedValue.COMPLEX_UNIT_PX)
    }
    private val action = TextView(context).apply {
        setTextColor(look.actionText)
        typeface = Typeface.DEFAULT_BOLD
        maxLines = 1
        setTextSize(TypedValue.COMPLEX_UNIT_PX, look.smallTextSize)
        gravity = Gravity.CENTER
        background = pill(look.actionFill)
        setPadding(look.inset * 2, 0, look.inset * 2, 0)
    }

    init {
        val badge = TextView(context).apply {
            text = Strings.adBadge
            setTextColor(look.text)
            typeface = Typeface.DEFAULT_BOLD
            setTextSize(TypedValue.COMPLEX_UNIT_PX, look.smallTextSize)
            gravity = Gravity.CENTER
            background = pill(look.badgeFill)
            setPadding(look.inset, 0, look.inset, 0)
        }
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, intArrayOf(look.fillTop, look.fillBottom)).apply {
                cornerRadius = look.height / 2f
                setStroke(look.rimWidth, look.rim)
            }
            setPadding(look.inset, look.inset / 2, look.inset / 2, look.inset / 2)
        }
        val inner = look.height - look.inset
        row.addView(badge, LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, inner * 2 / 3).apply { marginEnd = look.inset })
        row.addView(icon, LinearLayout.LayoutParams(look.icon, look.icon).apply { marginEnd = look.inset })
        row.addView(headline, LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f).apply { marginEnd = look.inset })
        row.addView(action, LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, inner))
        adView.addView(row, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        adView.headlineView = headline
        adView.iconView = icon
        adView.callToActionView = action
        addView(adView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    /** Shows [ad]; a part it doesn't have (no icon, no button) is left out. */
    fun bind(ad: NativeAd) {
        headline.text = ad.headline
        val drawable = ad.icon?.drawable
        icon.setImageDrawable(drawable)
        icon.visibility = if (drawable == null) View.GONE else View.VISIBLE
        action.text = ad.callToAction
        action.visibility = if (ad.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE
        adView.setNativeAd(ad)
    }

    private fun pill(color: Int) = GradientDrawable().apply {
        setColor(color)
        cornerRadius = look.height.toFloat()
    }

    /** Clips the advertiser's icon to a circle. */
    private object RoundOutline : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) = outline.setOval(0, 0, view.width, view.height)
    }

    private companion object {
        /** The smallest the headline shrinks to (px) before it is simply as small as it gets. */
        const val MIN_TEXT_PX = 14
    }
}
