package com.kglabs28.yapnplay.appengine.core.ui

/** Feedback & support: the list, the new-message form and a conversation. */
object FeedbackStrings {
    const val feedbackSettingsButton = "Feedback & support"
    const val feedbackTitle = "Feedback & Support"
    const val feedbackBack = "Back"
    const val feedbackNew = "New message"
    const val feedbackShare = "Share feedback"
    const val feedbackSeeMine = "See my messages"
    const val feedbackRate = "Rate us on Google Play"
    const val feedbackIntro = "Ask for a new game, suggest a feature, or tell us about an issue or a bug. We read every message and reply here."
    const val feedbackEmpty = "Nothing sent yet. Tap the pencil to write to us."
    const val feedbackLoadFailed = "Couldn't load your messages. Check your connection and try again."
    const val feedbackAnswered = "Replied"
    const val feedbackWaiting = "Sent"

    const val feedbackFormTitle = "Write to us"
    const val feedbackKindPick = "What is it about?"
    const val feedbackHint = "Your message"
    const val feedbackSend = "Send"
    const val feedbackCancel = "Cancel"
    const val feedbackEmptyError = "Write your message"
    const val feedbackKindError = "Choose what it's about"
    const val feedbackConfirmTitle = "Send this message?"
    const val feedbackConfirmEdit = "Edit"
    const val feedbackConfirmSend = "Yes, send"
    fun feedbackTooLong(max: Int) = "Keep it under $max characters"
    const val feedbackSendFailed = "Couldn't send. Check your connection and try again."
    const val feedbackTooMany = "That's a lot for one day. Please try again tomorrow."
    const val feedbackSent = "Thanks! We'll reply here."
    const val feedbackSentTitle = "Message sent"
    const val feedbackSentFormOnly = "Thanks! Our replies show in Settings > See my messages."
    const val feedbackOk = "OK"

    const val feedbackYou = "You"
    const val feedbackTeam = "YapNPlay team"
    const val feedbackNoReplyYet = "We'll reply here soon."
    const val feedbackClose = "Close"

    /** The dropdown's entries, in [com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackKind] order. */
    val feedbackKinds = listOf("Request a new game", "New feature", "Report an issue", "Report a bug", "Share feedback")
}
