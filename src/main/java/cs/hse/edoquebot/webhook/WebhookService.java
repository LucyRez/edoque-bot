package cs.hse.edoquebot.webhook;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import cs.hse.edoquebot.webhook.requestObjects.WebhookRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.json.Json;
import java.io.StringReader;


@Service
@AllArgsConstructor
public class WebhookService {

    public String getResponse(WebhookRequest request){
        String intent = request.getQueryResult().getIntent().getDisplayName();
        if (intent.equals("Test Server Intent")){

            String boxType = request.getQueryResult().getParameters().getBoxType();
            if(boxType != null){
                return resolveBoxIntent(boxType);
            }else{
                return "В нашем ассортименте есть:\n" +
                        " 1. Овощные коробки:\n" +
                        "  – Базовый набор, 2 кг (130 ₽)\n" +
                        "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                        "  – Овощи для салатов, 1.2 кг (900 ₽)\n" +
                        " 2. Фруктовые коробки:\n" +
                        "  – Коробка недели,  1.5 кг (1200 ₽)";
            }
        }
        return "Я не знаю, что на это ответить";
    }

    private String resolveBoxIntent(String boxType){
        if(boxType.equals("овощная")){
            return "Овощные коробки:\n" +
                    "  – Базовый набор, 2 кг (130 ₽)\n" +
                    "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                    "  – Овощи для салатов, 1.2 кг (900 ₽)";
        }

        return "Фруктовые коробки:\n" +
                "  – Коробка недели,  1.5 кг (1200 ₽)";
    }
}
