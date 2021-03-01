#-dontobfuscate
#-optimizations !code/allocation/variable



#-keep public class * {
#    public protected *;
#}

-keep class com.android.socket.client.sdk**{  public protected *; }
-keep class com.android.socket.client.impl**{  public protected *; }
-keep class com.android.socket.client.core**{ public protected *; }

-keepclassmembers enum * {                                                      # 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {                                # 保持 Parcelable 不被混淆
  public static final android.os.Parcelable$Creator *;
}
-keep public class * implements java.io.Serializable {*;}