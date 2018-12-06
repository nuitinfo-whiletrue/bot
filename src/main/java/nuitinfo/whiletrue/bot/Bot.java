package nuitinfo.whiletrue.bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Bot extends TelegramLongPollingBot {

    private final String token;

    private Bot(String token) {
        this.token = token;
        System.out.println("Lancement du bot, token: '" + token + "'.");
    }

    public static void main(String[] args) {
        System.out.println("Configuration....");
        ApiContextInitializer.init();

        if(args.length == 1) {
            try {
                new TelegramBotsApi().registerBot(new Bot(args[0].replace("\"", "")));
            } catch (TelegramApiRequestException e) {
                throw new RuntimeException("Couldn't launch the API...", e);
            }
        }
        else
            System.out.println("Un seul paramètre était attendu, " + args.length + " ont été reçus.");
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update.toString());
    }

    @Override
    public String getBotUsername() {
        return "whiletrueinfobot";
    }

    @Override
    public String getBotToken() {
        return token;
    }
}
