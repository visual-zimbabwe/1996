# Keep Kotlinx Serialization classes used for Archive.org API and bundled JSON
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class app.editorial1996.data.**$$serializer { *; }
-keepclassmembers class app.editorial1996.data.** {
    *** Companion;
}
-keepclasseswithmembers class app.editorial1996.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
