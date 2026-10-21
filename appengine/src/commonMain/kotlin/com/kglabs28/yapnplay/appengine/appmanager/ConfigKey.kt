package com.kglabs28.yapnplay.appengine.appmanager

/** One remotely controllable value: its Remote Config [name], the [default] until a value arrives, and what it does. */
class ConfigKey<T : Any>(val name: String, val default: T, val about: String)
