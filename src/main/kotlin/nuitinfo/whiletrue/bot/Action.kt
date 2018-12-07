package nuitinfo.whiletrue.bot

import com.eclipsesource.json.JsonObject
import com.eclipsesource.json.WriterConfig
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

fun JsonObject.asAction(): Action {
    when(this["type"].asString()){
        "answer" -> return Answer(this)
        "redirect" -> return Redirect(this)
        else -> throw RuntimeException("Unexpected type '${this["type"]}' in: ${toString(WriterConfig.PRETTY_PRINT)}")
    }
}

val errorAction = Answer(JsonObject().add("text", "Je n'ai pas compris."))

abstract class Action {
    abstract operator fun invoke(bot: Bot, update: Update, db: Database)
}

class Answer(json: JsonObject): Action() {

    val text = json["text"]!!.asString()

    override fun invoke(bot: Bot, update: Update, db: Database) {
        val msg = SendMessage().apply {
            chatId = update.message?.chatId.toString()
            text = this@Answer.text
        }

        bot.execute(msg)
    }
}

class Redirect(json: JsonObject): Action() {

    val goto = json["goto"]!!.asString()

    override fun invoke(bot: Bot, update: Update, db: Database) {
        db[goto](bot, update, db)
    }
}