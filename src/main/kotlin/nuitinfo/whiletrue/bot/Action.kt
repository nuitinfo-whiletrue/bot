package nuitinfo.whiletrue.bot

import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.WriterConfig
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

fun JsonObject.asAction(): Action {
    when(this["type"].asString()){
        "answer" -> return Answer(this)
        else -> throw RuntimeException("Unexpected type '${this["type"]}' in: ${toString(WriterConfig.PRETTY_PRINT)}")
    }
}

abstract class Action {
    abstract operator fun invoke(bot: Bot, update: Update)
}

class Answer(json: JsonObject): Action() {

    val text = json["text"]!!.asString()

    override fun invoke(bot: Bot, update: Update) {
        val msg = SendMessage().apply {
            chatId = update.message?.chatId.toString()
            text = this@Answer.text
        }

        bot.execute(msg)
    }
}