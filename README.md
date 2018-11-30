最近遇到了需要播放大量帧动画(100张以上)的需求，使用传统xml帧动画方式，会频繁OOM，为了解决这个问题，进行了几种尝试，目前的解决方案如下：
### 传统帧动画
- 我们需要在drawable目录下创建动画集animalist.xml
  ```xml
  <animation-list xmlns:android="http://schemas.android.com/apk/res/android"
    android:oneshot="false">
    <item
        android:drawable="@mipmap/image_1"
        android:duration="50" />
    <item
        android:drawable="@mipmap/image_2"
        android:duration="50" />
     <!--  省略...  -->
    <item
        android:drawable="@mipmap/image_99"
        android:duration="50" />
    <item
        android:drawable="@mipmap/image_100"
        android:duration="50" />
    </animation-list>


经过尝试，图片不是很大的时候，大概到20几张图片的时候就会OOM

### 解决方案
写了一个处理类，使用方法很简单下边直接上代码

先贴一下调用代码,真的很简单

##### `FrameLoader.into(mImageView).load(mTest).setEndlessLoop(true).setLoopCount(3).play(200);`

直接上代码,懒得看的人可以直接去看Github源码

## [Github地址](https://github.com/Kimger/FrameLoader.git)
#### 这是核心处理类

```java
/**
 * Created with Android Studio.
 *
 * @author Kimger
 * @email kimger@onetos.cc
 * @date 2018-7-13  00:01
 */

public class Process {
    /**
     * 播方动画的相应布局
     */
    private ImageView mImageView;
    /**
     * 播放动画的图片数组
     */
    private int[] mImageRes;
    /**
     * 是否需要停止
     */
    private boolean stop;
    /**
     * 记录播放状态
     */
    private boolean playing;
    /**
     * 当前播放第*张
     */
    private int pImageNo;
    /**
     * 图片刷新频率
     */
    private int pImageFrequency;
    /**
     * 是否无限循环播放
     */
    private boolean endlessLoop = false;
    /**
     * 循环次数，不定义默认为1
     */
    private int loopCount = 1;
    /**
     * 记录当前循环了几次
     */
    private int countCache;

    private String mTag = getClass().getName();

    private static Process mInstance;

    private static List<ImageView> mObjects ;

    private static Map<ImageView, Process> mProcessMap;

    private static Pools.SynchronizedPool<Process> sPool = new Pools.SynchronizedPool<>(20);


    /**
     * 创建新的对象
     *
     * @return
     */
    public static Process build(ImageView tag){
        mInstance = obtain();
        mInstance.into(tag);
        return mInstance;
    }

    public static Process obtain(){
        Process acquire = sPool.acquire();
        return (acquire != null) ? acquire : new Process();
    }

    public void recycle(){
        sPool.release(this);
    }

    /**
     * 设置动画的ImageView
     *
     * @param pImageView
     * @return
     */
    private Process into(ImageView pImageView) {
        stop = true;
        if (mImageView != null) {
            mImageView.removeCallbacks(mRunnable);
        }
        if (pImageView != mImageView) {
            stop();
        }
        this.mImageRes = null;
        this.mImageView = pImageView;
        return mInstance;
    }

    /**
     * 设置动画数组文件
     *
     * @param pImageRes
     * @return
     */
    public Process load(int[] pImageRes) {
        this.mImageRes = pImageRes;
        return mInstance;
    }

    /**
     * 开始播放
     *
     * @param pImageNo
     * @param frequency
     */
    public Process play(int pImageNo, int frequency) {
        stop = false;
        this.pImageNo = pImageNo;
        this.pImageFrequency = frequency;
        mImageView.postDelayed(mRunnable, frequency);
        return mInstance;
    }

    public Process play(int frequency) {
        return play(0, frequency);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (stop) {
                playing = false;
                return;
            } else {
                mImageView.setImageResource(mImageRes[pImageNo]);
                if (pImageNo >= mImageRes.length - 1) {
                    countCache++;
                    if (endlessLoop) {
                        pImageNo = 0;
                        play(pImageNo, pImageFrequency);
                        playing = true;
                    } else {
                        if (countCache >= loopCount) {
                            playing = false;
                            stop();
                            return;
                        } else {
                            pImageNo = 0;
                            play(pImageNo, pImageFrequency);
                            playing = true;
                        }
                    }
                } else {
                    play(pImageNo + 1, pImageFrequency);
                    playing = true;
                }
            }
        }
    };

    public boolean isStop() {
        return stop;
    }

    private void setStop(boolean stop) {
        this.stop = stop;
    }

    /**
     * 停止播放
     */
    public void stop() {
        setStop(true);
        countCache = 0;
        if (mImageView != null) {
            mImageView.removeCallbacks(mRunnable);
            System.gc();
            recycle();
        }
        if (mProcessMap != null) {
            mProcessMap.clear();
        }
        if (mObjects != null) {
            mObjects.clear();
        }

    }

    public boolean isPlaying() {
        return playing;
    }

    /**
     * 是否无限循环
     */
    public Process setEndlessLoop(boolean endlessLoop) {
        this.endlessLoop = endlessLoop;
        return mInstance;
    }

    /**
     * 循环次数
     */
    public Process setLoopCount(int loopCount) {
        this.loopCount = loopCount;
        return mInstance;
    }
}
```

#### Manager类，留给后续添加功能的空间
```java
/**
 * Created with Android Studio.
 *
 * @author Kimger
 * @email kimger@onetos.cc
 * @date 2018-7-13  00:20
 */

public class ProcessManager {
    
    private static ProcessManager mInstance;

    public static ProcessManager get(){
        if (mInstance == null) {
            synchronized (ProcessManager.class){
                if (mInstance == null) {
                    mInstance = new ProcessManager();
                    
                }
            }
        }
        return mInstance;
    }
}
```

#### 这是调用类
```java
/**
 * Create with Android Studio.
 *
 * @author Kimger
 * @email kimger@onetos.cc
 * @date 2018-7-13 00:30
 */

public class FrameLoader {

    public static Process into(ImageView tag){
        return Process.build(tag);
    }
    
    /**
     * 此功能暂时无法使用
     */
    public static Process getProcessByTag(ImageView tag){
        Map<ImageView, Process> imageViewProcessMap = ProcessManager.get().getmProcessMap();
        if (imageViewProcessMap != null) {
            return imageViewProcessMap.get(tag);
        }
        return null;
    }

}
```
然后就可以愉快的去使用帧动画了，亲测二百张也没什么问题。
