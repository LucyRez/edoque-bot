package cs.hse.edoquebot.webhook.requestObjects;



import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class FulfillmentMessage {

    @SerializedName("text")
    @Expose
    private Text text;

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

}
