package cs.hse.edoquebot.webhook.requestObjects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class ComplexItems {

    private String boxname;
    private Integer number = 1;

}