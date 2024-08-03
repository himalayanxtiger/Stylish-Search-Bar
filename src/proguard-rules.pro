# Add any ProGuard configurations specific to this
# extension here.

-keep public class com.xtiger.stylishsearchbar.StylishSearchbar {
    public *;
 }
-keeppackagenames gnu.kawa**, gnu.expr**

-optimizationpasses 4
-allowaccessmodification
-mergeinterfacesaggressively

-repackageclasses 'com/xtiger/stylishsearchbar/repack'
-flattenpackagehierarchy
-dontpreverify
