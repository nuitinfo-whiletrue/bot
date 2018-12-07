package nuitinfo.whiletrue.bot

import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import java.io.File

class Bot private constructor(private val token: String) : TelegramLongPollingBot() {

    val database: Database
    val mamie: Database

    val ressources = File("src/main/resources/")
    operator fun File.get(name: String) = File(this, name)

    init {
        println("Analyse des bases de données....")
        database = Database(ressources["db.json"])
        mamie = Database(ressources["mamie.json"])

        println("Lancement du bot, token: '$token'.")
    }

    override fun onUpdateReceived(update: Update) {
        println(update.message?.date.toString() + "> " + update.message?.contact?.lastName + ": " + update.message?.text)

        update.message?.text?.let {
            database[it](this, update, database)
        }
    }

    override fun getBotUsername(): String {
        return "whiletrueinfobot"
    }

    override fun getBotToken(): String {
        return token
    }

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
