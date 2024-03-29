# Mysql

## 1 索引相关

### 1.1 b+树

- 每一个节点能存储多个数据，但不存储整行信息，只存储行的索引值（一个节点能存储越多的数据，代表相同数据量下数据高度就越低，在查找时IO次数就越少）

- 非叶子节点不存储数据
- 叶子节点是个链表结构，便于遍历，也存储正真的行数据
- 查找更稳定，按主键搜索行数据，是必须查找到叶子节点的（IO次数就为书的高度）
- 正因为叶子节点时链表结构，可以很好的利用到空间局部性原理（如果一块磁盘被访问到，那么它周围的数据也可能被访问），读取周围部分节点，进行范围查找

### 1.2 b树

- 每个节点也能存储多个数据（就是行数据，就导致每个节点存储的行就越少，和b+树相比存储同样数据量的情况下，树的高度会越高）
- 叶子节点不是链表结构，不利于遍历（就算叶子节点又链表结构，但由于非叶子节点已经存储了部分数据，所以，范围查找时就会漏掉非叶子节点但在这区间的数据）
- 查找不够稳定（数据可能就在顶点，也可能在叶子节点）

### 1.3 b+树和b树高度计算

**b+树**

> ​		默认innodb的页大小为16kb，主键使用bigint为8byte，再加上下一页的指针，一般为6byte，一般表的一行数据大小设为1kb。所以，一个索引值占用14byte，那么，非叶子节点一个页能存储 (16 * 1024 )/14 = 1170条索引值数据，叶子节点一个页能存储 (16 * 1024) / 1 * 1024 = 16条真正的数据。所以，3层高度的b+树能存储 1170 *1170 *16 = 21902400条行数据。综上，3层高度的b+树就能存储2190万条数据左右（一行数据假设为1kb）,所以，可以大致估算出，2200万条数据内进行主键查询时，只需要固定的3次IO，还是很快的。

**b树**

> ​		初始值还是如上，16kb页大小，但b树每个节点存的都是完整的行数据（1kb），一个页能存 (16 / 1) = 16条行数据。（16 * 16 * 16 * 16 * 16 * 16）=1677万，所以，b树6层的高度大概才能存储1677万条数据（一行数据为1kb），也就导致了在磁盘IO时很不确定IO次数，运气好就在根据点，只IO一次。运气不好在叶子节点，要IO六次。况且这种行数据的分散存储在各节点的结构就不好支持范围查找

### 1.4 回表和覆盖索引

**回表：**

> ​		innodb使用主键的聚簇索引来存储数据，非主键索引叶子节点存储的时其对应的主键值，所以还需要用到这个主键索引值取正在的聚簇索引里去查找行数据

**覆盖索引：**

> ​	当mysql查询的列全部数据包含在一个一个索引中，且查询条件也可以命中这个索引，就可以使用覆盖索引，不用回表，一次就能查询出数据
>
> ​	比如表中A有(a,b,c)的聚合索引，查询sql为：SELECT b,c FROM A WHERE a = 1;这时查询可以走到索引，而需要的字段有全部在这个(a,b,c)的索引中，所以，不用回表

## 2 sql优化

从三个层面回答：

- 建表语句
  - 考虑组建联合索引（且区分度越高的放在越左边，因为索引排序是从左到右的，先命中左边，如果左边区分度越高，意味着能过滤掉更多的数据）
- sql本身上
  - 尽量使用覆盖索引，减少回表（也就意味着查询语句不要查询全部数据）
  - 绝不返回不必要的字段
  - 使用最左匹配
  - 不要再索引列直接参与函数计算
  - explain命令查看sql执行计划（查看命中的索引，索引大小，过滤行数的百分比等）
  - 表连接时尽量使小表驱动大表（对于join来说，实在不行可以使用straight join，强制左表驱动右表）
  - 减少表连接的个数（表join的越多，代表msyql就要花更多的时间分析sql，找出查找速度最快的那种连接方式。但考虑到mysql分析执行计划时不应该占用太多时间，所以连接多表时很难找到执行计划最优的那个方式）
- 代码里
  - 在事务内，尽量减少锁暂用时间（比如事务内的update和delete，因为update会触发行锁，索引如果可以就放在最后才执行）

## 3 事务隔离级别相关

### 3.1 READ UNCOMMITTED(读未提交)

​		读取到其他事务还未commit的数据，产生脏读。也就是事物之间还未commit或rollback的数据之间是可见的

### 3.2 READ COMMITED (读已提交)

​		读取到其他事务已经commit的数据，不会产生脏读，但由于能读取到其他事务已经commit的数据，所以可能造成一个事务前后两次读取的数据不一致，不可重复读。

### 3.3 REPEATABLE READ （可重复读，mysql的默认隔离级别）

​		读取不到其他事务已经commit的数据，一个事务执行期间，如果另外一个事务对某个数据做了可修改并且commit了，那第一个事务是读取不到的，所以叫可重复读，MYSQL通过MVCC解决了幻读的问题（指在一个事务内读取到了别的事务插入的数据，导致前后读取不一致。）

### 3.4 SERIALIZABLE(串行化)

​	读写都加互斥锁，最慢但是不会产生脏读，幻读

### 3.5 MVCC

​	mvcc只工作在读已提交和可重复读的隔离界别下。innodb中每行记录后面保存了连个隐藏列，一个是行的创建时间（创建列），一个是行的删除时间（删除列），其实存储的不是时间值，而是系统版本号。每个事务都有自己的版本号，且版本号是自增的

- select ：查找创建列小于或等于当前版本号，而且删除列要么未定义，要么大于当前版本号
- insert：新插入的每一行都将当前版本号作为创建列
- delete：删除的每一行都将当前版本号作为删除列
- update：就是 insert + delete，先新增一行再删除，和上面的一样