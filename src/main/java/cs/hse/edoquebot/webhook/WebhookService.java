package cs.hse.edoquebot.webhook;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import cs.hse.edoquebot.webhook.requestObjects.Text;
import cs.hse.edoquebot.webhook.requestObjects.Text2;
import cs.hse.edoquebot.webhook.requestObjects.WebhookRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.json.Json;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


@Service
@AllArgsConstructor
public class WebhookService {

    public Fulfillment getResponse(WebhookRequest request){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();

        String intent = request.getQueryResult().getIntent().getDisplayName();
        if (intent.equals("Какие есть коробки")){

            String boxType = request.getQueryResult().getParameters().getBoxtype();
            if(!boxType.equals("")){
                return resolveBoxIntent(boxType);
            }else{
                response.add("В нашем ассортименте есть:\n" +
                        " 1. Овощные коробки:\n" +
                        "  – Базовый набор, 2 кг (130 ₽)\n" +
                        "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                        "  – Овощи для салатов, 1.2 кг (900 ₽)\n" +
                        " 2. Фруктовые коробки:\n" +
                        "  – Коробка недели,  1.5 кг (1200 ₽)");
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text);
            }
        }
        response.add("Я не знаю, что на это ответить");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);

    }

    private Fulfillment resolveBoxIntent(String boxType){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();

        if(boxType.equals("овощная")){
            response.add("Овощные коробки:\n" +
                    "  – Базовый набор, 2 кг (130 ₽)\n" +
                    "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                    "  – Овощи для салатов, 1.2 кг (900 ₽)");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }
        response.add("Фруктовые коробки:\n" +
                "  – Коробка недели,  1.5 кг (1200 ₽)");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);
    }

}
