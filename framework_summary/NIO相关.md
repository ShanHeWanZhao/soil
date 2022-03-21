# NIO相关

## 一、事件支持

|  Origin | Channel  | OP_ACCEPT | OP_CONNECT | OP_WRITE | OP_READ |
|  :--:  | :--:  |  :--:  | :--:  | :--:  | :--:  |
| client | SocketChannel ||Y|Y|Y|
| server | ServerSocketChannel |Y||||
| server | SocketChannel |||Y|Y|

- `OP_ACCEPT`：当收到一个客户端的连接请求时，该操作就绪。这是`ServerSocketChannel`上唯一有效的操作。

- `OP_CONNECT`：只有客户端`SocketChannel`会注册该操作，当客户端调用`SocketChannel.connect()`时，该操作会就绪。

- `OP_READ`：该操作对客户端和服务端的`SocketChannel`都有效，当OS的读缓冲区中有数据可读时，该操作就绪。

- `OP_WRITE`：该操作对客户端和服务端的`SocketChannel`都有效，当OS的写缓冲区中有空闲的空间时(大部分时候都有)，该操作就绪。

  >  OP_WRITE  事件相对特殊，一般情况，不应该注册`OP_WRITE事件`，**`OP_WRITE`的就绪条件为操作系统内核缓冲区有空闲空间**(`OP_WRITE事件`是在`Socket`发送缓冲区中的可用字节数大于或等于其低水位标记`SO_SNDLOWAT`时发生)，而写缓冲区绝大部分事件都是有空闲空间的，所以当你注册写事件后，写操作一z直就是就绪的，这样会导致`Selector`处理线程会占用整个CPU的资源。所以最佳实践是当你确实有数据写入时再注册`OP_WRITE事件`，并且在写完以后马上取消注册