package cs.hse.edoquebot.webhook;

import cs.hse.edoquebot.webhook.requestObjects.OutputContext;
import cs.hse.edoquebot.webhook.requestObjects.Text;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class FulfillmentWithContext {
    private List<Text> fulfillmentMessages;
    private List<OutputContext> outputContexts;
}
