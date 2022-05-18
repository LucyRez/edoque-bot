package cs.hse.edoquebot.webhook;

import com.google.gson.JsonObject;
import cs.hse.edoquebot.webhook.requestObjects.WebhookRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/webhook")
@AllArgsConstructor
public class WebhookController {
    private WebhookService webhookService;

    @PostMapping
    public @ResponseBody Fulfillment webhook(@RequestBody WebhookRequest request){
        return webhookService.getResponse(request);
    }

}
