package nuitinfo.whiletrue.bot

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import java.io.File

class Bot private constructor(private val token: String) : TelegramLongPollingBot() {

    val database: Database
    val mamie: Database

    val ressources = File("src/main/resources/")
    operator fun File.get(name: String) = File(this, name)

    val users = HashMap<Int, Mode>()
    enum class Mode { SARCASTIQUE, MAMIE }
    fun getDBOf(user: Int) = if(users[user] == Mode.SARCASTIQUE) database else mamie

    init {
        println("Analyse des bases de données....")
        database = Database(ressources["db.json"])
        mamie = Database(ressources["mamie.json"])

        println("Lancement du bot, token: '$token'.")
    }

    override fun onUpdateReceived(update: Update) {
        println(update.message?.date.toString() + "> " + update.message?.contact?.lastName + ": " + update.message?.text)

        if (update.message?.text == "/start"){
            SendMessage().apply {
                chatId = update.message?.chatId.toString()
                text = "Enchanté ${update.message?.from?.firstName?:""} !"
                replyMarkup = InlineKeyboardMarkup().apply {
                    keyboard = listOf(listOf(
                        InlineKeyboardButton("Sarcastique").apply {
                            callbackData = "MODE-SARC"
                        },
                        InlineKeyboardButton("Mamie-proof").apply {
                            callbackData = "MODE-MAMIE"
                        }
                    ))
                }
            }.also { execute(it) }
        }

        update.callbackQuery?.let { cb ->
            cb.from?.id?.let { usr ->
                println("Reception d'un callback: ${cb.data}")
                if (cb.data == "MODE-SARC")
                    users[usr] = Mode.SARCASTIQUE
                else
                    users[usr] = Mode.MAMIE

                SendMessage().apply {
                    chatId = cb.message.chatId.toString()
                    text = "Vous êtes maintenant en mode ${users[usr]}."
                }.also { execute(it) }
            }
        }

        update.message?.text?.let { text ->
            update.message?.from?.id?.let { usr ->
                getDBOf(usr)[text](this, update, database)
            }
        }
    }

    override fun getBotUsername(): String = "whiletrueinfobot"

    override fun getBotToken(): String = token

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            println("Configuration....")
            ApiContextInitializer.init()

            if (args.size == 1) {
                try {
                    TelegramBotsApi().registerBot(Bot(args[0].replace("\"", "")))
                } catch (e: TelegramApiRequestException) {
                    throw RuntimeException("Couldn't launch the API...", e)
                }
            } else
                println("Un seul paramètre était attendu, " + args.size + " ont été reçus.")
        }
    }
}
