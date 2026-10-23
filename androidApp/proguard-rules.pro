# Release shrinking (R8). Libraries ship their own rules (kotlinx.serialization,
# Koin, Firebase, LiveKit, Compose); these cover what reflection or optional
# platform classes need on top.

# Contract types are read and written as JSON: keep their serializers.
-keepattributes *Annotation*, InnerClasses, Signature, EnclosingMethod
-keep,includedescriptorclasses class com.kglabs28.yapnplay.**$$serializer { *; }
-keepclassmembers class com.kglabs28.yapnplay.** {
    *** Companion;
}
-keepclasseswithmembers class com.kglabs28.yapnplay.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Ktor client: optional JVM classes it references but Android does not have.
-dontwarn java.lang.management.**
-dontwarn org.slf4j.**
-dontwarn io.ktor.**

# OkHttp optional TLS providers.
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**

# Crash reports stay readable (Crashlytics maps them back with the mapping file).
-keepattributes SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

# Room builds its databases by name (Class.forName("…_Impl")), so R8 must keep
# every generated one. WorkManager (pulled in by AdMob) opens its WorkDatabase
# this way at app start: without this the release build crashes on launch
# ("Failed to create an instance of androidx.work.impl.WorkDatabase").
-keep class * extends androidx.room.RoomDatabase { <init>(); }
-keep class androidx.work.impl.WorkDatabase_Impl { *; }
