# JVM

## 1.jvm参数整理

* **-Xms64m** ：初始堆大小
* **-Xmx128m** ：最大堆大小
* **-Xmn32m** ：年轻代大小
* **-XX:MaxNewSize=256m** : 最大新生代大小
* **-Xss512k**：栈大小
* **-XX:MetaspaceSize=256M** ：Metaspace扩容时触发FullGC的初始化阈值(并不是元空间的初始化大小，元空间是不断扩容的，当达到这个值时，就会触发full gc，[链接](https://www.jianshu.com/p/b448c21d2e71)）
* **-XX:MaxMetaspaceSize=512M**：Metaspace最大大小
* **-XX:NewRatio=2**：老年代和新生代的比例
* **-XX:SurvivorRatio=8** ：Eden区与一个Survivor区的大小比值（所以**s0:s1:eden=1:1:8**）
* **-XX:MinHeapFreeRatio=40**：空闲堆空间的最小百分比。如果空闲堆空间的比例小于它，则会进行堆扩容
* **-XX:MaxHeapFreeRatio=70**：空闲堆空间的最大百分比。如果空闲堆空间的比例大于它，则会进行堆缩容
* **-XX:-DisableExplicitGC**：禁止显式GC，即禁止程序中System.gc()。个人感觉没必要
* **-XX:+HeapDumpOnOutOfMemoryError**：OOM时导出堆快照到文件
* **-XX:HeapDumpPath=/home/huskie/gc/oom.hprof**：OOM时导出文件路径
* **-Xloggc:**/home/ruidong/gc.log   gc存储日志
* **-XX:OnOutOfMemoryError**：OOM时操作，比如如执行脚本发送邮件
* **-XX:+TraceClassLoading**：打印加载类的详细信息
* **-XX:+PrintGCDetails**：打印GC详细信息
* **-XX:+PrintGCTimeStamps**：打印CG发生的时间戳（相对于项目启动时间）
* **-XX:+PrintGCDateStamps**：打印GC发生的时间
* **-XX:+PrintHeapAtGC**：每一次GC前和GC后，都打印堆信息
* **-XX:+PrintClassHistogram**：按下Ctrl+Break后，打印类的信息
* **-XX:+PrintGCApplicationConcurrentTime** ：打印应用程序的运行时间（许多事情会导致JVM暂停所有线程，停在安全点。gc也只是其中的一种，当暂停之后在重启应用线程，则会刷新这个时间（归0），在重新计数）[链接](http://ifeve.com/logging-stop-the-world-pauses-in-jvm/)
* **-XX:+PrintGCApplicationStoppedTime** ：打印应用线程暂停的时间，显示应用线程被暂停了多久和应用线程暂停到安全点花费的时间
* **-XX:TargetSurvivorRatio=50** ：survivor空间的晋升大小空间百分比（默认为50）
* **-XX:MaxTenuringThreshold=15**  ：年轻代晋升到老年代的最大年龄阈值(tenuring threshold)。默认值为 15[每次GC，增加1岁，到15岁如果还要存活，放入Old区]。**jvm还会动态的计算晋升阈值，方法：依次从年龄为1的对象大小加起来，一直加到大小超过了 [（TargetSurvivorRatio * survivor_capacity）/ 100 ]值，这时加起来的最大年龄大小即为这次晋升的临界阈值**（具体算法在：hotspot\src\share\vm\gc_implementation\shared\ageTable.cpp文件里，方法为compute_tenuring_threshold）
* **-XX:+PrintTenuringDistribution** ：ygc 时打印当前晋升年龄信息

## 2.垃圾收集器

### 2.1 新生代

#### 2.1.1 Serial（hotspot虚拟机在客户端下的默认新生代垃圾收集器）

单线程新生代收集器，复制算法，整个过程STW

优势：内存消耗最小

缺点：不适合大内存多处理器工作，慢

#### 2.1.2 ParNew

多线程并行的新生代收集器，复制算法，整个过程STW

- **-XX:ParallelGCThreads=4** ：并行收集的线程数

#### 2.1.3  Parallel Scavenge

- 吞吐量 = 运行用户代码时间 / ( 运行用户代码时间 + 垃圾收集时间 )

吞吐量优先的新生代并行多线程收集器，复制算法（**标记-复制**算法）

三个重要参数：

- **XX:MaxGCPauseMillis** ：垃圾收集最大停顿时间，大于0的毫秒数
- **-XX:GCTimeRatio**: 大于0小于100的整数（运行用户代码时间比上垃圾回收的时间），默认为99，即允许最大1%的垃圾回收时间
- **-XX:+UseAdaptiveSizePolicy**：开启垃圾收集器的自适应调节策略。虚拟机动态调整新生代，Eden区，Survivor区的比例和晋升大小

### 2.2 老年代

#### 2.2.1 CMS

**标记-清除**算法的老年代收集器


#### 2.2.2 Serial Old

Serial的老年代会收集，**标记-整理**算法

#### 2.2.3 Parallel Scavenge Old

Parallel Scavenge收集器的老年代版本，标记-整理算法

### 2.3 整堆

#### 2.3.1 G1

garbage-frist收集器

### 2.4  垃圾收集器组合

#### 2.4.1 Serial + SerialOld

#### 2.4.2 Serial + CMS (jdk8声明废弃，jdk9已被取消)

#### 2.4.3 ParNew +CMS （使用CMS收集器的默认组合)

#### 2.4.4 ParNew + SerialOld (jdk8声明废弃，jdk9已被取消)

#### 2.4.5 Parallel Scavenge +  SerialOld

#### 2.4.6 Parallel Scavenge + Parallel Scavenge Old（jdk8的默认组合）

#### 2.4.7 G1（jdk9的默认收集器，且CMS被标记为废弃了）



## 3.tips

- **java -XX:+PrintFlagsFinal -version**  ：查看jvm默认参数

## 4. 标记对象时-并发的可达性分析

黑色对象：存活对象，且这个对象的全部引用都扫描过（代表不需要再次扫描了）

灰色对象：存活对象，但这个对象至少存在一个引用未被扫描（它持有的引用没扫描完）

白色对象：不可达对象，不会被扫描到

#### 1.问题：对象消失产生的原因（二者缺一不可）

> 1. 黑色对象重新引用了白色对象
> 2. 灰色对象删除了持有的该白色对象的全部引用
>
> 为什么二者缺一不可：首先第一条，既然是重新引用了白色对象，那么代表这个白色对象一定是被引用在灰色对象上的，且这个灰色对象还没扫描到这个白色对象。第二，考虑到扫描灰色对象的工作还没做完，必须在做完前删除掉引用的这个白色对象

#### 2.解决方式（二选一）

##### 1.增量更新（CMS收集器使用）

gc线程如果已经扫描完成A对象（黑色），用户线程在这期间又将A对象持有了一个应该被回收的B对象引用（白色）。此时需要记录A对象，将其从黑变为灰，在gc线程扫描完成后再扫描一次A对象

##### 2.原始快照

记录删除了白色对象引用的灰色对象，gc线程第一次扫描结束后在扫描这个灰色对象



