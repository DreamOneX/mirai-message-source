# mirai-message-source
A implementation of message-forwarding-client.消息转发客户端的mirai(Tencent-QQ)实现

**自用，写死了。**
~~以后可能会改成活的@_@~~
## 改动
* `handler/Send.kt` 第30行  
```kotlin
if (sender.id.toString() == "1606959418") return
```
`1606959418`: 屏蔽目标QQ号  
屏蔽多个: `|| sender.id.toString() == "xxx"`  
* `handler/Receive.kt`  
代码懒得放了，大概说一下吧  
sync是前缀，when里面比较group.id的是群号，字符串看着改就行  
应该没人看吧@_@
## 目前能干的事
* 非群主使用命令
* 屏蔽某个人
* 特定群使用特定前缀同步消息
## 另外
没有Windows和macos构建，有需要的可以自己换
