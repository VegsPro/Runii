-keepattributes *Annotation*
-keep class com.runii.vegs.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class kotlinx.serialization.** { *; }
