package cs.hse.edoquebot.webhook.requestObjects;



import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Parameters {

    private String boxtype;
    private String boxname;
    private String productname;
    private Integer number = 1;
    private Integer cardinal;
    private String from;
    private String to;
    private String status;
    private String address;
    private String deliveryDate;
    private String name;
    private String deliveryZone;
    private String email;
    private String phone;
    private String deliveryTimeInterval;
    private Boolean shouldCall;
    private String comment;
    private Integer tips;

}
