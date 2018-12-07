package nuitinfo.whiletrue.bot

import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.WriterConfig
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.random.Random

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

    val value = json["text"]

    val _text = if (value.isString) value.asString() else null
    val _texts = if (value.isArray) value.asArray() else null

    override fun invoke(bot: Bot, update: Update) {
        val msg = SendMessage().apply {
            chatId = update.message?.chatId.toString()

            if (_text != null)
                text = _text
            else _texts!!.let {
                text = it.values().randomize().asString()
            }
        }

        bot.execute(msg)
    }

    fun <T> List<T>.randomize() = this[Random.nextInt(size)]
}