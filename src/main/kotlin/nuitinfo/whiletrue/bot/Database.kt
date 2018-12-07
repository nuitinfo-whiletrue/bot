package nuitinfo.whiletrue.bot

import com.eclipsesource.json.Json
import com.eclipsesource.json.JsonObject
import java.io.File
import java.nio.charset.Charset

class Database(val data: Map<String, Action>) {

    constructor(file: File): this(readFromFile(file))

    operator fun get(msg: String): Action = data[msg.toLowerCase()] ?: data["*"]!!

    companion object {
        fun readFromFile(file: File): Map<String, Action> {
            println("Reading ${file.absolutePath}.... [exists=${file.exists()}, canRead=${file.canRead()}]")

            val json: JsonObject = Json.parse(file.reader(Charset.forName("UTF-8")))
                .asObject()

            return json
                .names()
                .map { it to json[it].asObject().asAction() }
                .toMap()
        }
    }
}