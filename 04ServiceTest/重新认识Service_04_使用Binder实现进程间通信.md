## 前言

> 上一篇我们讲述了AIDL的使用以及具体的实现细节，本篇我们将不再使用AIDL语言，而是直接使用Binder来实现进程间的通信。

## 一、进程间通信和Binder技术

### 1.1 进程间通信的方式

> 广义的讲，进程间通信（Inter-process communication,简称IPC）是指运行在不同进程中的若干线程间的数据交换。

在操作系统中，每个进程都有一块独立的内存空间。为了保证程序的的安全性，操作系统都会有一套严格的安全机制来禁止进程间的非法访问。
毕竟，如果你的APP能访问到别的APP的运行空间，或者别的APP可以轻而易举的访问到你APP的运行空间，想象一下你是不是崩溃的心都有了。
所以，操作系统层面会对应用进程进行内存隔离，以保证APP的运行安全。 但是很多情况下进程间也是需要相互通信的，例如剪贴板的功能，  
可以从一个程序中复制信息到另一个程序。这就是进程间通信诞生的背景。

操作系统中常见的进程间通信方式有共享内存、管道、UDS以及Binder等。关于这些进程间的通信方式本篇文章我们不做深究，了解即可。

- 共享内存（Shared Memory)
  共享内存方式实现进程间通信依靠的是申请一块内存区域，让后将这块内存映射到进程空间中，这样两个进程都可以直接访问这块内存。在进行进程间通信时，两个进程可以利用这块内存空间进行数据交换。通过这样的方式，减少了数据的赋值操作，因此共享内存实现进程间通信在速度上有明显优势。
- 管道（Pipe） 管道也是操作系统中常见的一种进程间通信方式，Windows系统进程间的通信依赖于此中方式实现。在进行进程间通信时，会在两个进程间建立一根拥有读（read）写(write)
  功能的管道,一个进程写数据，另一个进程可以读取数据，从而实现进程间通信问题。
- UDS（UNIX Domain Socket） UDS也被称为IPC Socket，但它有别于network
  的Socket。UDS的内部实现不依赖于TCP/IP协议，而是基于本机的“安全可靠操作”实现。UDS这种进程间通信方式在Android中用到的也是比较多的。
- Binder Binder是Android中独有的一种进程间通信方式。它底层依靠mmap,只需要一次数据拷贝，把一块物理内存同时映射到内核和目标进程的用户空间。

本篇文章，我们重点就是了解如何使用Binder实现进程间通信。Binder仅仅从名字来看就给人一种很神秘的感觉，就因为这个名字可能就会吓走不少初学者。
但其实Binder本身并没有很神秘，它仅仅是Android系统提供给开发者的一种进程间通信方式。而作为一个Android开发者，Binder是我们必须掌握的知识。
因为它是构架整个Android大厦的钢筋和混凝土，连接了Android各个系统服务和上层应用。 只有了解了Binder机制才能更加深入的理解Android开发和Android Framework。

## 二、使用Binder实现进程间通信

无论是哪种进程间通信，都是需要一个进程提供数据，一个进程获取数据。因此，我们可以把提供数据的一端称为服务端，把获取数据的一端称为客户端。  
从这个角度来看Binder是不是就有点类似于HTTP协议了？所以，你完全可以把Binder当成是一种HTTP协议，客户端通过Binder来获取服务端的数据。  
认识到这一点，再看Binder的使用就会简单很多。   
这里我们继续使用前面几篇文章中出现的例子：

**服务端提供把字符串转化成大写的接口，客户端连接服务端来实现字符串小写转大写的功能，而客户端与服务端的媒介就是Binder。**

### 2.1 服务端的实现

```java
public class MainService extends Service {

    public static final String TAG = "MainService=========";
    public static final int Request_ToUpperCase = 100;

    IBinder mBinder = new Binder() {
        @Override
        protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
            if (Request_ToUpperCase == code) {
                String text = data.readString();
                String newText = text.toUpperCase();
                reply.writeString(newText);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() executed");
        return mBinder;
    }


}
```

```xml

<service android:name="com.afs.rethinkingservice.MainService" android:process=":remote" />
```

服务端的代码就是返回一个Binder实例，重写onTransact()，当code=100的时候，读取data中的字符串，  
然后调用该字符串的toUpperCase方法获取大写字符串，再把字符串写到reply中。

### 2.2 客户端的实现

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools" android:layout_width="match_parent"
    android:layout_height="match_parent" android:orientation="vertical"
    tools:context=".MainActivity">

    <Button android:id="@+id/btn_bind_service" android:layout_width="match_parent"
        android:layout_height="wrap_content" android:text="绑定服务" />

    <Button android:id="@+id/change_text_to_uppercase" android:layout_width="match_parent"
        android:layout_height="wrap_content" android:text="修改字符串" />

</LinearLayout>
```

```java
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity=========";

    private IBinder mBinder;// 远程服务的Binder

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() executed");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() executed");
            mBinder = service;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_bind_service).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        });
        findViewById(R.id.change_text_to_uppercase).setOnClickListener(v -> {
            try {
                String text = "aaabbbcccddd";
                Parcel parcel = Parcel.obtain();
                Parcel replay = Parcel.obtain();
                parcel.writeString(text);
                mBinder.transact(MainService.Request_ToUpperCase, parcel, replay, 0);
                String newText = replay.readString();
                Log.d(TAG, "newText === " + newText);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

}
```

客户端的代码就是通过绑定远程服务，然后获取到IBinder实例，再调用IBinder的transact方法，把需要转化为大写的字符串aaabbbcccddd写入到parcel中，
等待transact方法执行完毕，再从replay中读取字符串数据，而这个字符串就是AAABBBCCCDDD。

### 2.3 验证结果

启动app，点击绑定服务，再点击修改字符串的按钮，日志信息如下：

```cmd
2022-04-01 10:00:50.091 29125-29125/com.afs.rethinkingservice04 D/MainService=========: onBind() executed
2022-04-01 10:00:50.093 29077-29077/com.afs.rethinkingservice04 D/MainActivity=========: onServiceConnected() executed
2022-04-01 10:00:52.991 29077-29077/com.afs.rethinkingservice04 D/MainActivity=========: newText === AAABBBCCCDDD
```

可见，使用Binder实现进程间通信是非常简单的，可以说简单的有点出乎所料。

## 三、优化Binder进程通信

上面我们虽然很轻易的用Binder实现了进程间通信，但其实这个方案还可以继续进行优化。

### 3.1 定义一个小写转大写的接口：

```java
public interface ChangeStringInterface {
    /**
     * 把小写字符串转化成大写字符串
     *
     * @param str
     * @return
     */
    public String toUpperCase(String str);
}
```

### 3.2 定义一个ChangeStringInterface的接口实现类，且这个类继承了Binder：

```java
public class ChangeStringImpl extends Binder implements ChangeStringInterface {
    public static final int Request_ToUpperCase = 100;

    @Override
    public String toUpperCase(String text) {
        return text.toUpperCase();
    }

    @Override
    protected boolean onTransact(int code, @NonNull Parcel data, @Nullable Parcel reply, int flags) throws RemoteException {
        if (Request_ToUpperCase == code) {
            String text = data.readString();
            String newText = toUpperCase(text);
            reply.writeString(newText);
            return true;
        }
        return super.onTransact(code, data, reply, flags);
    }
}
```

我们重写Binder的onTransact，并在该方法内部调用toUpperCase()方法来实现字符串小写转大写的功能。

### 3.3 修改MainService中的代码

```java
public class MainService extends Service {

    public static final String TAG = "MainService=========";

    IBinder mBinder = new ChangeStringImpl();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind() executed");
        return mBinder;
    }

}
```

我们使用IBinder mBinder = new ChangeStringImpl()来替换到之前的IBinder mBinder = new Binder();

### 3.4 优化客户端的代码

我们来创建一个ChangeStringProxy类。

```java
public class ChangeStringProxy implements ChangeStringInterface {
    public static final String TAG = "Proxy=========";

    private IBinder mBinder;

    //构造方法私有化，只能被asInterface调用
    private ChangeStringProxy(IBinder binder) {
        this.mBinder = binder;
    }

    // 通过Binde把小写字符串转化为大写字符串
    @Override
    public String toUpperCase(String str) {
        try {
            Parcel parcel = Parcel.obtain();
            Parcel replay = Parcel.obtain();
            parcel.writeString(str);
            mBinder.transact(ChangeStringImpl.Request_ToUpperCase, parcel, replay, 0);
            return replay.readString();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 实例化Binder代理类的对象
    public static ChangeStringInterface asInterface(IBinder binder) {
        if (binder == null) {
            return null;
        }
        if (binder instanceof ChangeStringInterface) {
            Log.d(TAG, "当前进程");
            // 如果是同一个进程的请求，则直接返回Binder
            return (ChangeStringInterface) binder;
        } else {
            Log.d(TAG, "远程进程");
            // 如果是跨进程查询则返回Binder的代理对象
            return new ChangeStringProxy(binder);
        }
    }

}
```

ChangeStringProxy也实现ChangeStringInterface接口，，在toUpperCase中调用Binder实现Binder通信，同时构造方法需要传入Binder对象实例。
另外我们把构造方法设置成了private，同时提供了一个asInterface方法，这个方法通过判断Binder是不是IGradeInterface类型从而确定是不是跨进程的通信。  
如果不是跨进程通信，则返回当前这个Binder，其实就是我们在MainService的OnBinder方法中返回的ChangeStringImpl实例对象，否则就返回ChangeStringProxy这个IBinder的代理类。

然后修改MainActivity中的代码：

```java
public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity=========";

    private ChangeStringInterface mBinder;// 远程服务的Binder

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected() executed");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected() executed");
            mBinder = ChangeStringProxy.asInterface(service);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_bind_service).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MainService.class);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
        });
        findViewById(R.id.change_text_to_uppercase).setOnClickListener(v -> {
            String text = "aaabbbcccddd";
            String newText = mBinder.toUpperCase(text);
            Log.d(TAG, "newString === " + newText);
        });
    }
}
```

在ServiceConnection的onServiceConnected方法中，调用ChangeStringProxy.asInterface方法对mBinder对象实例化。  
然后就可以调用mBinder.toUpperCase实现小写字符串转大写的功能了。

**有看过前面几篇文章的小伙伴一看应该就明白了，我们手动构建服务端和客户端的这个过程，其实就是之前篇章中，aidl帮我们做的事情。

类比上篇文章我们可以看出来：

- ChangeStringInterface 对应 MainAidlService
- ChangeStringProxy 对应 Proxy
- ChangeStringImpl 对应 Stub对象

## 四、验证结果

最后来验证一下我们是否真的实现了字符串小写转大写的功能。

### 4.1 远程进程通信结果

启动app，点击修改字符串，日志信息如下所示：

```cmd
2022-04-01 10:46:28.284 30400-30400/com.afs.rethinkingservice04 D/MainService=========: onBind() executed
2022-04-01 10:46:28.286 30344-30344/com.afs.rethinkingservice04 D/MainActivity=========: onServiceConnected() executed
2022-04-01 10:46:28.286 30344-30344/com.afs.rethinkingservice04 D/Proxy=========: 远程进程
2022-04-01 10:46:31.458 30344-30344/com.afs.rethinkingservice04 D/MainActivity=========: newString === AAABBBCCCDDD
```

### 4.2 本地进程通信结果

去掉AndroidManifest.xml中**android:process=":remote"**字段，然后重新编译运行app，点击修改字符串，日志信息如下所示：

```cmd
2022-04-01 11:07:20.448 31438-31438/com.afs.rethinkingservice04 D/MainService=========: onBind() executed
2022-04-01 11:07:20.454 31438-31438/com.afs.rethinkingservice04 D/MainActivity=========: onServiceConnected() executed
2022-04-01 11:07:20.454 31438-31438/com.afs.rethinkingservice04 D/Proxy=========: 当前进程
2022-04-01 11:07:27.348 31438-31438/com.afs.rethinkingservice04 D/MainActivity=========: newString === AAABBBCCCDDD
```

本篇文章主要带大家认识了进程间通信和Binder与AIDL的使用。通过本篇文章的学习可以发现Binder与AIDL其实是非常简单的。  
在了解了Binder之后，我们就可以去更加深入的学习Android Framework层的知识了。