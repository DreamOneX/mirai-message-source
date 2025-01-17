package org.meowcat.mesagisto.mirai

import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueName
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.Group

object Config : AutoSavePluginConfig("config") {
  val nats: NatsConfig by value()
  val cipher: CipherConfig by value()
  val proxy: ProxyConfig by value()
  val enableNudge: Boolean by value(true)
  val blacklist: MutableList<Long> by value(mutableListOf(114514114514))
  val bindings: MutableMap<Long, String> by value()

  @ValueName("disablegroup")
  val disableGroup: MutableList<Long> by value(mutableListOf(114514114514))

  @ValueName("disablechannel")
  val disableChannel: MutableList<String> by value(mutableListOf("114514114514"))

  val perm: PermConfig by value()
  private val targetChannelMapper: MutableMap<Long, String> by value()
  fun mapper(target: Long): String? = bindings[target]
  fun mapper(target: Group): String? = bindings[target.id]
  fun migrate() {
    bindings.putAll(targetChannelMapper)
    targetChannelMapper.clear()
  }
}

@Serializable
data class PermConfig(
  val strict: Boolean = false,
  val users: List<Long> = listOf(123456)
)

@Serializable
data class NatsConfig(
  val address: String = "nats://nats.mesagisto.org:4222"
)

@Serializable
data class ProxyConfig(
  val enable: Boolean = false,
  val address: String = "http://127.0.0.1:7890"
)

@Serializable
data class CipherConfig(
  val key: String = "default"
)
