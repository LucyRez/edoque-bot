package cs.hse.edoquebot.webhook.requestObjects;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class OutputContext {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lifespanCount")
    @Expose
    private Integer lifespanCount;
    @SerializedName("parameters")
    @Expose
    private Parameters__1 parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLifespanCount() {
        return lifespanCount;
    }

    public void setLifespanCount(Integer lifespanCount) {
        this.lifespanCount = lifespanCount;
    }

    public Parameters__1 getParameters() {
        return parameters;
    }

    public void setParameters(Parameters__1 parameters) {
        this.parameters = parameters;
    }

}
