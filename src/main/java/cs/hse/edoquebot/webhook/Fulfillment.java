package cs.hse.edoquebot.webhook;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import cs.hse.edoquebot.webhook.requestObjects.Text;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode
public class Fulfillment {
    private List<Text> fulfillmentMessages;
}
