-flattenpackagehierarchy de.ellpeck.rockbottom
-repackageclasses de.ellpeck.rockbottom
-keepattributes Deprecated,*Annotation*,Synthetic
-adaptresourcefilenames **.properties
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF
-libraryjars  <java.home>/lib/rt.jar;build/temp/libs/lib

-keep class de.ellpeck.rockbottom.init.RockBottom {
    *** startGame(...);
}

-keep class de.ellpeck.rockbottom.init.RockBottomServer {
    *** startGame(...);
}

-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-keep public class de.ellpeck.rockbottom.api.** {
    public protected <fields>;
    public protected <methods>;
}

-keepclassmembers enum  * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * extends java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class * {
    public <init>(...);
}

-keepclasseswithmembers,includedescriptorclasses,allowshrinking class * {
    native <methods>;
}
