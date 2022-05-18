package cs.hse.edoquebot.webhook.boxes;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Product {

    private String name;
    private int weight;
    private String measurementSystem;
}
