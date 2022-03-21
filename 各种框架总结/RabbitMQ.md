# RabbitMQ（《深入RabbitMQ》书籍）

## AMQP协议：

> 1. 交换机(Exchange)：接受发送到RabbitMQ中的消息
> 2. 队列(Queue)：存储消息的数据结构，位于内存或硬盘中
> 3. 绑定(Binding)：一套规则，用于告诉交换器的消息应该被发送到哪个队列中

1. 声明交换机：Exchange.Declare（客户端发送）  -> Exchange.DeclareOk（服务端响应）

2. 声明队列：Queue.Declare（客户端发送）  -> Queue.DeclareOk（服务端响应）,重复生成相同的队列不会有任何副作用
3. 声明绑定：Queue.Bind（客户端发送）  -> Queue.BindOk（服务端响应）

## 发送消息

客户端发送一条消息到服务器，至少需要发送三个帧。且消息只会保存一个实例，队列中存的是消息的引用，避免相同的消息占用过多的内存。

> 1. Basic.Publish方法帧：
>
>    1. 携带消息的交换器名称和路由键
>    2. mandatory：为true则表示如果当前消息不可路由，服务器则应该通过**Basic.Return**命令将消息返回给发布者
>
> 2. 消息头帧：Basic.Properties
>
>    Basic.Properties字段：
>
>    1. content-type：让消费者知道如何解释消息
>    2. content-encoding：编码格式
>    3. message-id、correlation-id：表示唯一消息标识和消息响应标识，用于在工作流程中实现消息跟踪；
>    4. delivery-mode：1，非持久化。2，持久化到磁盘
>    5. timestamp：消息创建时间
>    6. expiration：消息的过期时间（必须设置为**字符串形式的时间戳**才会有效）
>    7. headers：定义自由格式的属性和实现RabbitMQ路由
>    8. priority：优先级，0-9的整数，数字越小，优先级越高（**但RabbitMQ不支持**）
>    9. 等等

> 1. 消息体帧：

## 接受消息

开启消息消费监听：Basic.Consume（客户端发送） -> Basic.ConsumeOk（服务端响应）

有消息时服务端发送：Basic.Deliver（服务端发送） -> Basic.Ack（客户端响应）

## 消息的可靠投递

### 1.设置mandatory

设置Basic.Publish的mandatory为true，不接受不可路由的笑嘻嘻

### 2.轻量级事务：发布确认

1. Confirm.Select（客户端打开发布确认） -> Confirm.SelectOk（服务端响应开启成功）

2. Basic.Publish（客户端开始发送消息） -> Basic.Ack（投递成功，服务端返回）或Basic.Nack（投递失败，服务端返回）

### 3.备用交换机

javaApi:com.rabbitmq.client.Channel#exchangeDeclare(java.lang.String, java.lang.String, boolean, boolean, boolean, java.util.Map<java.lang.String,java.lang.Object>)

arguments -> alternate-exchange:备用交换机名

如果当前交换机不能路由消息，就会把消息发送给备用交换机，这也会变成正确投递

### 4.事务

1. 开启事务：TX.Select（客户端发送） -> TX.SelectOk（服务端响应）
2. 发布消息：Basic.Publish（客户端开始投递消息） -> Basic.Return.
3. 提交事务：TX.Commit （客户端确认提交）-> TX.CommitOk.

### 5.RabbitMQ集群提供的高可用队列

### 6.设置delivery-mode为2持久化消息到磁盘

如何回收呢？

​	当通过引用追踪到消息不存在于任何队列时（也就是引用消失），RabbitMQ将消息从磁盘中删除。

​	当消息持久化开启时，RabbitMQ除了将其持久化到磁盘外，还会跟踪这个消息存储在所有队列的引用，用来回收。

## 消费消息

### 1.Basic.Get（不推荐）

客户端主动拉取消息，客户端的轮询机制

有待处理的消息：Basic.Get -> Basic.GetOk

无消息：Basic.Get -> Basic.GetEmpty

### 2.Basic.Consume（推荐）

开启服务端的消息推送：Basic.Consume（客户端发送） -> Basic.ConsumeOk（服务端返回）

关闭正在Consume的客户端：Basic.Cancek （客户端发送取消）-> Basic.CancelOk （服务端响应取消成功）

相当于客户端注册了，当服务端有消息时，就会向这个连接推送消息

### 3. 设置no_ack

设置no_ack为fasle，表示每条消息都需要回复ack，服务端才会确认这条消息被成功接受

### 4.设置Qos

Qos：预取数量，表示RabbitMQ会为这个消费者预先非陪一定数量的消息来实现更高效的消息发送。当no_ack为true时，这个就不起作用了。当no_ack为false时，可以支持multiple来确认多条消息

### 5.事务消费消息

不支持运行在已禁用确认的消费者模式下

### 6.拒绝消息

客户端对于不能正常被处理的消息，可以拒绝这个消息，并将其投到**死信队列**中

> Basic.Reject：客户端拒绝一个
>
> Basic.Nack：客户端拒绝多个（RabbitMQ特有的）

## 队列

创建队列时的参数：

> durable：是否持久化（将队列的信息保存到磁盘）
>
> exclusive：当前队列是否只允许存在单个消费者
>
> autoDelete：自动删除（当消费者断开连接后就会删除队列）
>
> arguments：字典结构的参数，制定一些队列创建时的参数，比如：
>
> 	1. x-expires：指定毫秒数后将删除队列
>  	2. x-max-length：队列的最大消息数（容量）
>  	3. x-max-length-bytes：队列可容纳的消息最大字节数
>  	4. 等等