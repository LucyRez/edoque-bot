package cs.hse.edoquebot.webhook.requestObjects;



import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Parameters__1 {

    @SerializedName("param-name")
    @Expose
    private String paramName;

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

}
