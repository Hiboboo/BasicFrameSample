#### 燃气充值基础模块

> 作为《生活+》主程序的基础框架部分，主要做基础性功能的支撑。提供底层日志记录与上传，基础页面设计，设备驱动的桥接实现，网络接口定义，网络请求与解析，通用型工具函数以及基础组件库。

#### 引入该模块

1. 在工程根`build.gradle`或`settings.gradle`文件中，添加私有的`maven`信息配置：

   ```groovy
   repositories {
   	maven {
   		allowInsecureProtocol true
   		name = "nexus"
   		url = "http://maven.jinkeen.com/repository/maven-releases/"
   		credentials {
   			username = "" // maven账号
   			password = "" // maven密码
   		}
   	}
   }
   ```

2. 在需要添加的工程`build.gradle`中，添加库的引用：

   ```groovy
   api 'com.jinkeen.base:frame:1.0.10'
   ```

在该模块中，已引入了多个第三方支持库，包括：

- `api 'androidx.multidex:multidex:2.0.1'` 针对多个`dex`文件和`65536`问题的官方解决方案
- `api 'com.google.code.gson:gson:2.8.9'`对`JSON`解析的库
- `api 'com.jakewharton.rxbinding4:rxbinding:4.0.0'`支持对`View`各类事件的优化处理
- `SmartPosLib_V3.1.3_20180727.jar`升腾**C960F**设备的依赖库
- `cloudposApi-1.0.0-release.jar`、`meSdk-3.3.0-SNAPSHOT.jar`新大陆**N910**设备的依赖库
- `api 'io.coil-kt:coil:1.2.0'`图片加载库，`Kotlin`语言专用
- `api 'pl.droidsonroids.gif:android-gif-drawable:1.2.23'` `GIF`图片加载
- `api 'com.github.liujingxing.rxhttp:rxhttp:2.8.3'`、`api 'com.squareup.okhttp3:okhttp:4.9.1'`、`api 'io.reactivex.rxjava3:rxjava:3.1.1'`网络请求库
- `api 'org.greenrobot:eventbus:3.2.0'`事件订阅处理
- `api 'com.muddzdev:styleabletoast:2.3.0'` `Toast`增强版
- `api 'com.github.li-xiaojun:XPopup:2.3.8'`种类丰富的弹窗支持库



***在该模块中，已声明且实现了对于克罗姆卡的`JNI`函数`QwGas`***

***在该模块中，已声明且实现了对于克罗姆卡的`JNI`函数`QwGas`***



##### 1、设备驱动桥接

驱动桥接支持有：

- 现有的所有设备类型自动识别，包括：UniwinM339（VTM）、K2（VTM）、A8，W280P，N910，C960F
- 现有的所有卡片类型常量定义
- 设备连接与卡片操作流方法的接口定义
- 小票打印统一接口的定义

注意：除设备类型自动识别已实现外，***其余接口类的定义都需要由具体项目来实现。*** 

`CardReader`类作为驱动桥接的第一入口，提供有几个必要的方法：

```kotlin
/**
 * 获取当前的设备操作对象
 *
 * @param context 当前运行时的上下文对象
 * @return 返回具体的底层驱动对象，若没有找到与当前设备匹配的驱动实现，则返回`null`
 */
fun getDeviceSystem(context: Context): DeviceSystem? {}
/**
 * 获取对`IC`卡的具体操作流对象
 *
 * @param cardType 指定`IC`卡类型，从[DeviceSystem.recognition]获取
 * @return 返回指定`IC`卡类型对应的具体操作对象，若指定的卡类型不存在或对应的流实现不存在，则返回`null`
 */
fun getCardStream(cardType: CardType): CardStream? {}
/**
 * 重置读卡器
 */
fun reset()
```

`CardReader`是一个单例实现，在`Kotlin`代码中，可直接通过`CardReader()`来获取一个单例对象，并直接调用以上方法来获取设备或卡片操作接口对象。

但要注意的是，`reset()`方法要在程序每一次重启完成后进行一次调用，目的是为了确保桥接实现的正确性。

对于`DeviceSystem`接口的实现，代码参考：

```kotlin
// 在设备的实现类声明之上，要添加@Device注解，其中type的值用于标识当前实现的设备类型，needContext用于确定当前实现中是否需要Context对象的支持。
@Device(type = DeviceType.VTM_K2, needContext = true)
class SunmiK2System private constructor(val context: Context) : DeviceSystem {
    companion object {
        @Volatile
        private var instance: SunmiK2System? = null
        
        // 若实现类是单例模式，要在单例的获取方法之上，添加@SingleInstance注解
        @SingleInstance
        fun getInstance(context: Context): SunmiK2System {
            instance = SunmiK2System(context)
            return instance
        }
    }
}
```

对于`CardStream`接口的实现，代码参考：

```kotlin
// 在具体卡片的流实现类之上，要添加@StreamType注解，其中type的值用于标识当前实现的卡片类型
@StreamType(type = CardType.AT24C01)
class At24c01Stream : CardStream {}
```

提供了小票打印统一接口`JinkeenPrinter`，与`CardReader`非常类似，但该接口内提供了两个重要的静态方法`getInstance()`和`reset()`，前者用于获取打印机对象，后者用于重置打印机。

同样的，接口内定义了几个必要的方法，也需要由觉得项目来实现。

```kotlin
/**
 * 连接打印机
 *
 * @param context 当前运行上下文
 */
fun connect(context: Context)

/**
 * 断开打印机
 *
 * @param context 当前运行上下文
 */
fun disconnect(context: Context)

/**
 * 获取当前打印机状态
 *
 * @return [STATE_OK]或其他状态码
 */
fun getStatus(): Int

/**
 * 执行打印
 * ---
 * 这是一个挂起函数，意味着调用者必须要在协程中进行调用。
 * 因为打印过程可能会很耗时
 *
 * @param styles 要打印数据的具体风格
 * @param block 打印结果回调
 */
suspend fun print(styles: LinkedList<PrintStyle>, block: suspend (code: Int) -> Unit)
```

与`DeviceSystem`和`CardStream`接口实现一样，打印机的实现类也需要添加一个注解，用于标识这是一个打印机的实现类，参考代码：

```kotlin
@Printer(type = DeviceType.VTM_K2)
class SunmiK2Printer private constructor() : JinkeenPrinter {
    companion object {

        private val instance: SunmiK2Printer by lazy { SunmiK2Printer() }

        @SingleInstance
        operator fun invoke(): SunmiK2Printer = instance
    }
}
```

另外，对于读写卡的参数设置和结果获取，该模块中也提供了`CardRequest`和`CardResponse`两个重要的对象，具体可参阅源码注释。

##### 2、网络接口

所有的网络接口都被定义在`ReqApi.kt`文件中，除`BASE_URL`外，其他所有接口均是不可修改的常量定义。

`BASE_URL`定义的是全局的基础域名，默认为华润燃气充值的正式环境域名，若想改成测试环境的，可在主工程的`Application`类中，直接修改其值，参考代码：

**Kotlin**

```kotlin
BASE_URL = "https://test.jinkeen.com/publicServer/" // 测试环境
```

**Java**

```java
ReqApiKt.BASE_URL = "https://test.jinkeen.com/publicServer/"; // 测试环境
```

##### 3、日志记录与上传

在程序运行的所有过程中，都可以将有价值的信息记录于本地，稍后根据设定的条件，将本地已记录的日志信息回传给服务器，用于问题的排查。

在日志记录前，请先做好日志的配置与初始化，参考代码：

```kotlin
class JKApplication : BaseApplication() {
    override fun onCreate() {
        // LogConfig参数可参阅源码及相关注释
        JKLog.init(
            LogConfig(
                filesDir.absolutePath,
                "${getExternalFilesDir(null)?.absolutePath}${File.separator}logan",
                "0123456789012345".toByteArray(),
                "0123456789012345".toByteArray()
            )
        )
    }
}
```

`JKLog`类将作为日志操作的第一入口，提供日志的本地记录，立即上传，周期上传，停止上传等方法，可参阅其源码注释。

在`JKLog`类中的`w()`和`e()`方法，只能记录，无法输出到控制台，若想在记录的同时，在输出台亦可看到相关日志，可使用`utils`类中定义的`loganW()`和`loganE()`两个方法。

##### 4、基础页面与组件介绍

提供创建一个`Activity`或`Fragment`或`DialogFragment`页面的基础风格和通用方法定义。

- `BaseApplication`，已实现`MultiDexApplication`，且内部做了获取全局`Application`方法的声明，继承者参考代码：

  ```kotlin
  class JKApplication : BaseApplication() {
      
      // 将具体的Application对象设置给父类
      override fun getApplication(): Application = this
  }
  ```

- `BaseAppCompatActivity`已继承`AppCompatActivity`，内部做了风格化设置，及其他通用型方法，可参阅源码注释。

- `BaseFragment`已继承`androidx.fragment.app.Fragment`，且做了对`DataBinding`的处理，提供`onActivityResume()`方法来替代原`onActivityCreated()`。

- `AbsDialogFragment`继承自`androidx.fragment.app.DialogFragment`，与`BaseFragment`有着非常类似的实现，可参阅源码注释。

- `FancyButton`，高度自定义的一个按钮组件，除了具有系统默认按钮的功能外，另外增加了更多自定义属性的设置，可参阅源码。

##### 5、其他通用型工具函数

- `ActivityManager`，简单的对`Activity`进行一种集装箱式的管理，无需手动去调用`add`和`remove`方法，因为在`BaseAppCompatActivity`中已自动调用。

- `resource.kt`提供对项目资源文件的一种简单调用

- `SharedPreferenceHelper`提供对本地`shared.xml`文件操作的帮助类

- `SingletonFactory`专门对`Kotlin`语言中，用于创建单例对象的一种帮助类，参考代码：

  ```kotlin
  class FileWorker private constructor(private val config: LogConfig) {
  
      object Instance : SingletonFactory<FileWorker, LogConfig>(::FileWorker)
  }
  ```

  调用者：

  ```kotlin
  private val worker: FileWorker = FileWorker.Instance.get(config)
  ```

- `utils`提供丰富的通用型工具方法，其中包括上边提到的日志输出方法，以及其他各种实用的方法，可参阅其源码注释。