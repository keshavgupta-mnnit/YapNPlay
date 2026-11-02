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
 * badge, the advertiser's icon, the headline (up to two lines, shrunk to
 * fit but never below the small text size) and the call-to-action button,
 * on a glass pill drawn to [look]. Every part follows the width it is
 * given: at the pill's design width they have their full size, narrower
 * the text, icon and button shrink together (down to [MIN_SCALE]), and in a
 * narrow card the icon is left out, so the headline keeps room to be read.
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
        maxLines = 2
        setLineSpacing(0f, LINE_SPACING)
    }
    private val action = TextView(context).apply {
        setTextColor(look.actionText)
        typeface = Typeface.DEFAULT_BOLD
        maxLines = 1
        gravity = Gravity.CENTER
        background = pill(look.actionFill)
    }
    private val badge = TextView(context).apply {
        text = Strings.adBadge
        setTextColor(look.text)
        typeface = Typeface.DEFAULT_BOLD
        gravity = Gravity.CENTER
        background = pill(look.badgeFill)
    }

    init {
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

    private var hasIcon = false
    private var fittedWidth = -1

    init {
        fit(look.fullWidth)
    }

    /** Shows [ad]; a part it doesn't have (no icon, no button) is left out. */
    fun bind(ad: NativeAd) {
        headline.text = ad.headline
        val drawable = ad.icon?.drawable
        icon.setImageDrawable(drawable)
        hasIcon = drawable != null
        fittedWidth = -1
        fit(if (width > 0) width else look.fullWidth)
        action.text = ad.callToAction
        action.visibility = if (ad.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE
        adView.setNativeAd(ad)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // Resizing children during layout must wait for the next frame.
        post { fit(w) }
    }

    /** Sizes the text, icon and button for a pill [width] wide (smaller in a narrow card). */
    private fun fit(width: Int) {
        if (width <= 0 || width == fittedWidth) return
        fittedWidth = width
        val scale = (width.toFloat() / look.fullWidth).coerceIn(MIN_SCALE, 1f)
        val small = look.smallTextSize * scale
        val inset = (look.inset * scale).toInt()
        badge.setTextSize(TypedValue.COMPLEX_UNIT_PX, small)
        badge.setPadding(inset, 0, inset, 0)
        action.setTextSize(TypedValue.COMPLEX_UNIT_PX, small)
        action.setPadding(inset * 2, 0, inset * 2, 0)
        val smallest = small.toInt()
        headline.setAutoSizeTextTypeUniformWithConfiguration(smallest, (look.textSize * scale).toInt().coerceAtLeast(smallest + 1), 1, TypedValue.COMPLEX_UNIT_PX)
        val iconSize = (look.icon * scale).toInt()
        icon.layoutParams = (icon.layoutParams as LinearLayout.LayoutParams).apply {
            this.width = iconSize
            this.height = iconSize
        }
        icon.visibility = if (hasIcon && width >= look.compactBelow) View.VISIBLE else View.GONE
        requestLayout()
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
        /** Two headline lines sit a little closer than usual, to fit the slim pill. */
        const val LINE_SPACING = 0.9f
        /** The smallest the parts shrink to in a narrow card, against their full size (text stays readable). */
        const val MIN_SCALE = 0.8f
    }
}
