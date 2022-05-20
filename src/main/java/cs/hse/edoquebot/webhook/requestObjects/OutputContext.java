package cs.hse.edoquebot.webhook.requestObjects;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class OutputContext {

    private String name;
    private Integer lifespanCount;
    private Parameters parameters;



}
