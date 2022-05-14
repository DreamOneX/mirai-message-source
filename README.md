# mirai-message-source
A implementation of message-forwarding-client.消息转发客户端的mirai(Tencent-QQ)实现

自用，写死了。
以后可能会改成活的@_@
有相同需要的在这个项目下面搜索1538874738替换成你的qq号就行了
`handler/Send.kt`中第30行
```kotlin
if (sender.id.toString() == "1606959418") return
```
这里的`1606959418`改成你想屏蔽的人
想屏蔽多个多个就加
`|| sender.id.toString() == "xxx"`
应该没人看吧@_@
## 目前能干的事
* 非群主使用命令
* 屏蔽某个人
## 另外
没有Windows和macos构建，有需要的可以自己换
