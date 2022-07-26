package org.meowcat.mesagisto.mirai

import kotlinx.coroutines.launch
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.plugin.PluginManager
import net.mamoe.mirai.console.plugin.id
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.*
import net.mamoe.mirai.message.data.Image
import net.mamoe.mirai.message.data.Image.Key.queryUrl
import org.fusesource.leveldbjni.internal.NativeDB
import org.meowcat.mesagisto.client.*
import org.meowcat.mesagisto.mirai.handlers.MiraiListener
import org.meowcat.mesagisto.mirai.handlers.Receive
import javax.imageio.ImageIO

object Plugin : KotlinPlugin(
  JvmPluginDescription(
    id = "org.meowcat.mesagisto",
    name = "Mesagisto",
    version = "1.0-unknown"
  )
) {
  private val eventChannel = globalEventChannel()
  private val listeners: MutableList<Listener<*>> = arrayListOf()

  override fun onEnable() {
    Config.reload()
    Config.migrate()
    if (!Config.enable) {
      logger.error("Mesagisto信使未启用!")
      return
    }
    // SPI And JNI related things
    switch(jvmPluginClasspath.pluginClassLoader) {
      ImageIO.scanForPlugins()
      NativeDB.LIBRARY.load()
      Result.success(Unit)
    }.getOrThrow()

    Logger.bridgeToMirai(logger)
    MesagistoConfig.builder {
      name = "mirai"
      natsAddress = Config.nats.address
      cipherKey = Config.cipher.key
      proxyEnable = Config.proxy.enable
      proxyUri = Config.proxy.address
      resolvePhotoUrl = { uid, _ ->
        runCatching {
          val image = Image(uid.toString(charset = Charsets.UTF_8))
          image.queryUrl()
        }
      }
    }.apply()
    launch {
      Receive.recover()
    }
    listeners.apply {
      add(eventChannel.subscribeAlways(MiraiListener::handle, EventPriority.LOWEST))
      add(eventChannel.subscribeAlways(MultiBot::handleBotOnline))
      add(eventChannel.subscribeAlways(MultiBot::handleBotJoinGroup))
    }
    CommandManager.registerCommand(Command)
    val service: PermissionService<Permission> = PermissionService.INSTANCE as PermissionService<Permission>
    runCatching {
      service.cancel(AbstractPermitteeId.AnyUser, Plugin.parentPermission, true)
    }
    if (Config.perm.strict) {
      Logger.info { "信使的严格模式已开启, 信使仅对名单内用户指令作出响应" }
      Config.perm.users.forEach { user ->
        service.permit(AbstractPermitteeId.parseFromString("u$user"), Plugin.parentPermission)
      }
    } else {
      Logger.info { "信使的严格模式已关闭, 信使指令可被任意用户调用, 但敏感操作仅允许群组管理员进行." }
      service.permit(AbstractPermitteeId.AnyUser, Plugin.parentPermission)
    }
    if (PluginManager.plugins.find {
      it.id == "net.mamoe.mirai.console.chat-command"
    } == null
    ) {
      Logger.error { "注册指令成功, 但依赖需要 chat-command,否则无法在聊天环境内执行命令" }
    } else {
      Logger.info { "注册指令成功" }
    }
    Logger.info { "Mirai信使已启用" }
  }

  override fun onDisable() {
    listeners.forEach {
      it.complete()
    }
    CommandManager.unregisterCommand(Command)
    Logger.info { "Mirai信使已禁用" }
  }
}
