package cs.hse.edoquebot.webhook.requestObjects;


import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class Parameters {

    private String boxtype;
    private String boxname;
    private String productname;
    private Integer number = 1;
    private Integer cardinal;
    private String from;
    private String to;



}
