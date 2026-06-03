# ============================================================
# Kotlin
# ============================================================
-keepattributes *Annotation*, InnerClasses, Signature, SourceFile, LineNumberTable
-dontnote kotlin.**
-keepclassmembers class kotlin.Metadata { *; }

# ============================================================
# Kotlin Coroutines
# ============================================================
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# ============================================================
# kotlinx.serialization
# ============================================================
-keepattributes RuntimeVisibleAnnotations, AnnotationDefault
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Keep @Serializable classes and their companions/serializers
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-if @kotlinx.serialization.Serializable class ** {
    static **$Companion *;
}
-keepclassmembers class <2>$Companion {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class ** {
    public static ** INSTANCE;
}
-keepclassmembers class <1> {
    public static <1> INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# ============================================================
# OkHttp / OkIO
# ============================================================
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
-dontwarn org.codehaus.mojo.animal_sniffer.*
-dontwarn okhttp3.internal.platform.ConscryptPlatform
-keepclassmembers class okhttp3.** { *; }

# ============================================================
# Ktor
# ============================================================
-dontwarn io.ktor.**
-keep class io.ktor.** { *; }

# ============================================================
# Supabase (postgrest-kt, auth-kt, storage-kt, realtime-kt)
# ============================================================
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# ============================================================
# Coil
# ============================================================
-dontwarn coil.**
-keep class coil.** { *; }

# ============================================================
# Koin
# ============================================================
-keepnames class org.koin.** { *; }
-keep class org.koin.core.annotation.** { *; }
-dontwarn org.koin.**

# ============================================================
# Jetpack Compose / AndroidX
# ============================================================
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keep class androidx.lifecycle.** { *; }

# ============================================================
# App classes (activities, application, serializable models)
# ============================================================
-keep class fh.msd.** extends android.app.Application { *; }
-keep class fh.msd.** extends androidx.activity.ComponentActivity { *; }

# ============================================================
# Enums (needed for when-expressions on enums)
# ============================================================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ============================================================
# Parcelable
# ============================================================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}
