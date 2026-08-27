# Firebase
-keepattributes Signature
-keepclassmembers class * {
  @com.google.firebase.database.IgnoreExtraProperties *;
}

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Hilt
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.AndroidEntryPoint class *
-keep,allowobfuscation,allowshrinking @dagger.hilt.android.HiltAndroidApp class *

# Kotlinx Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,allowcreations,allowoptimization class * {
    @kotlinx.serialization.Serializable *;
}
