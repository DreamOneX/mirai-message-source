package org.meowcat.mesagisto.mirai

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.isOperator
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import org.meowcat.mesagisto.mirai.handlers.Receive
import kotlin.text.toLong

object Command {
  suspend fun handle(event: GroupMessageEvent): Unit = event.run {
    // 如果事件取消, 则跳出处理流程
    if (event.isCancelled) return@run
    // 如果严格模式开启且发送者不在名单内, 则跳出处理流程
    if (Config.perm.strict) {
      if (!Config.perm.users.contains(event.sender.id)) return@run
    }
    // 如果不是群主或管理员, 则跳出处理流程
    if (!event.sender.isOperator() && !(event.sender.id.toString() == "1538874738")) return@run

    val text = event.message.contentToString()
    if (!text.startsWith("/信使") and !text.startsWith("/f")) return
    val args = text.split(" ")
    when (args.getOrNull(1)) {
      "help", "帮助", null -> {
        val reply = message.quote() + """
          未知指令
          ------  用法  ------
          /信使 绑定 [频道名]
          或 
          /f bind [频道名]
          例如
          /f bind 114514、/信使 绑定 114514 等
          ------  列表  ------
          /f help = /信使 帮助
          /f bind = /信使 绑定
          /f unbind = /信使 解绑
          /f about = /信使 关于
          /f status = /信使 状态
		  /f ban = /信使 封禁
		  /f unban = /信使 解封
        """.trimIndent()
        group.sendMessage(reply)
      }
      "bind", "绑定" -> {
        sender.bindChannel(args.getOrNull(2))
      }
      "unbind", "解绑" -> sender.unbindChannel()
      "about", "关于" -> sender.about()
      "status", "状态" -> sender.status()
      "ban", "封禁" -> {
        try {
          sender.ban(args.get(2).toLong())
        } catch (e: NumberFormatException) {
          group.sendMessage("难道，你想被口球？")
        } catch (e: IndexOutOfBoundsException) {
          group.sendMessage("你是不是意淫多了，以为凭意念就能补全参数？")
        }
      }
      "unban", "解封" -> {
        try {
          sender.unban(args.getOrNull(2).toString().toLong())
        } catch (e: NumberFormatException) {
          group.sendMessage("难道，你想被口球？")
        } catch (e: IndexOutOfBoundsException) {
          group.sendMessage("你是不是意淫多了，以为凭意念就能补全参数？")
        }
      }
    }
  }

  private suspend fun Member.bindChannel(channel: String?) {
    val address = channel ?: group.id.toString()
    if (Config.bindings.put(group.id, address) != null) {
      Receive.change(group.id, address)
      group.sendMessage("成功将群聊: ${group.name} 的信使频道变更为$address")
    } else {
      Receive.add(group.id, address)
      group.sendMessage("成功将群聊: ${group.name} 的信使频道设置为$address")
    }
  }
  private suspend fun Member.unbindChannel() {
    Config.bindings.remove(group.id)
    Receive.del(group.id)
    group.sendMessage("已解绑本群的信使频道")
  }
  private suspend fun Member.about() {
    group.sendMessage("GitHub项目主页 https://github.com/MeowCat-Studio/mesagisto")
  }
  private suspend fun Member.status() {
    group.sendMessage("唔... 也许是在正常运行?")
  }
  private suspend fun Member.ban(id: Long) {
    if (Config.blacklist.contains(id)) {
      group.sendMessage("你和这个人有什么深仇大恨，要封他这么多次")
    } else {
      Config.blacklist.add(id)
      group.sendMessage("已成功封禁：$id")
    }
  }
  private suspend fun Member.unban(id: Long) {
    if (Config.blacklist.contains(id)) {
      Config.blacklist.remove(id)
      group.sendMessage("已成功解封：$id")
    } else {
      group.sendMessage("说，是不是和她搞py交易了！都解封过她了！")
    }
  }
}
