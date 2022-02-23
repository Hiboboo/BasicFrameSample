## 基础组件库

提供以下基础通用组件：

- `Activity`和`Fragment`的基础页面风格管理
- 网络请求库`RxHttp`
- 底层驱动统一接口及实例化管理（不包括具体的驱动实现）
- 自定义的`View`
- 通用性的工具型类库

作为私有的`Maven`向队友提供支持，私有的`Gradle`配置如下：

> 1、在工程的根`build.gradle`文件中的`allprojects { repositories { ... } }`节点内增加如下代码：
>
> ```groovy
> maven {
>     allowInsecureProtocol true
>     name = "nexus"
>     url = "http://maven.jinkeen.com/repository/maven-releases/"
>     credentials {
>         username = "私有maven账户名"
>         password = "私有maven密码"
>     }
> }
> ```
>
> 2、在需要的`Module`下的`build.gradle`文件中增加引用代码：
>
> ```groovy
> implementation 'com.jinkeen.base:frame:1.0.1'
> ```
>
> 3、点击`sync`同步按钮即可。

在本库中，已集成多个第三方库，包括：

```groovy
dependencies {

    api 'androidx.core:core-ktx:1.7.0'
    api 'androidx.appcompat:appcompat:1.4.1'
    api 'com.google.android.material:material:1.5.0'
    testApi 'junit:junit:4.13.2'
    androidTestApi 'androidx.test.ext:junit:1.1.3'
    androidTestApi 'androidx.test.espresso:espresso-core:3.4.0'

    api 'androidx.multidex:multidex:2.0.1'

    api 'com.google.code.gson:gson:2.8.9'
    api 'com.jakewharton.rxbinding4:rxbinding:4.0.0'

    // Coil
    api 'io.coil-kt:coil:1.2.0'
    // gif
    api 'pl.droidsonroids.gif:android-gif-drawable:1.2.23'

    api 'com.github.liujingxing.rxhttp:rxhttp:2.8.3'
    api 'com.squareup.okhttp3:okhttp:4.9.1'
    kapt 'com.github.liujingxing.rxhttp:rxhttp-compiler:2.8.3'
    api 'com.github.liujingxing.rxlife:rxlife-coroutine:2.1.0'
    api 'io.reactivex.rxjava3:rxjava:3.1.1'
    api 'io.reactivex.rxjava3:rxandroid:3.0.0'
    api 'com.github.liujingxing.rxlife:rxlife-rxjava3:2.2.1'

    // EventBus
    api 'org.greenrobot:eventbus:3.2.0'
    // 更多样式的Toast
    api 'com.muddzdev:styleabletoast:2.3.0'
}
```