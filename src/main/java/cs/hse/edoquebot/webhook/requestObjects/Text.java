
package cs.hse.edoquebot.webhook.requestObjects;


import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Text {

    @SerializedName("text")
    @Expose
    private List<String> text = null;

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

}
