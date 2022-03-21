# CMS

标记，**清除-清除**算法的老年代gc回收器，一次CMS gc会算作两次full gc，分别为初始标记和最终标记（算上的时STW次数），但在多次收集后产生的空间碎片如果影响到了对象的分配，也会才用**标记-整理**算法收集一次

清除算法会产生空间碎片，如果cms区预留的空闲内存不能满足新对象的分配，那么会触发**Concurrent Mode Failure**，这时会冻结用户线程，临时启用**Serial Old**收集器重新回收老年代的垃圾，全程STW，耗时很长

* 初始标记(CMS initial mark): STW，仅标记GCRoots对象的下一个可达对象，很快
* 并发标记(CMS concurent mark)
* 重新标记(CMS remark): STW，[解决并发标记时”那些消失的对象](#标记对象时-并发的可达性分析)
* 并发清除(CMS concurrent sweep)

## 1.参数

- **-XX:+UseConcMarkSweepGC**   ： 启用CMS收集器（年轻代默认使用ParNew收集器）

- **–XX:CMSWaitDuration=2000** ： cms后台线程的轮询间隔时间（ms单位)

- **-XX:+UseCMSInitiatingOccupancyOnly** : 使用基于设定的阈值进行CMS gc，值为CMSInitiatingOccupancyFraction

- **-XX:CMSInitiatingOccupancyFraction=80** : 在UseCMSInitiatingOccupancyOnly参数启用后生效。当CMS区（老年代）占比达到80%后，启用CMS垃圾回收。默认为-1，代表不启用，则老年代垃圾回收阈值算法为：**( (100 - MinHeapFreeRatio) + (CMSTriggerRatio * MinHeapFreeRatio) / 100.0) / 100.0** = 92%

- **-XX:ConcGCThreads=2** ：并发gc线程数，默认为（ParallelGCThreads+3）/ 4。ParallelGCThreads为新生代并行GC线程数，当CPU数量小于8时，ParallelGCThreads的值就是CPU的数量，当CPU数量大于8时，ParallelGCThreads的值等于3+5*cpuCount / 8 （可用jstack查看）

## 2.其他

### 2.1cms gc触发条件总结

- [原文](https://club.perfma.com/article/190389)
-  foreground collector  ：空间分配不够触发
- background collector
  - 配置了**ExplicitGCInvokesConcurrent** 参数，且由**System.gc()**调用
  - 根据统计数据动态计算（未配置**UseCMSInitiatingOccupancyOnly**时）
  - 根据 Old Gen 情况判断
  - **jvm的悲观策略**：**Young GC 已经失败或者可能会失败，JVM 就认为需要进行一次 CMS GC**
  - metaspace扩容且**CMSClassUnloadingEnabled** 启用（默认也是启用的）