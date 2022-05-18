package cs.hse.edoquebot.webhook.boxes;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class Box {

    private String boxName;
    private String description;
    private List<Product> products;
    private double weight;
    private int price;
    private boolean surprise;

    @Override
    public String toString() {
        String descriptionSummary = "\"" + boxName + "\" \n" + description + "\n";
        StringBuilder content = new StringBuilder(" Состав коробки: \n");

        if(!surprise && products.size() != 0){
            for (Product product : products) {
                String productDescription = "  – " +
                        product.getName() +
                        " " +
                        "(" +
                        product.getWeight() +
                        " " +
                        product.getMeasurementSystem() +
                        "),\n";
                content.append(productDescription);
            }
        }
        else if (!surprise) {
            for (Product product : products) {
                String productDescription = "  – " +
                        product.getName() +
                        "\n";
                content.append(productDescription);
            }
        } else{
            content.append("Это коробка-сюрприз. В её составе один крупный фрукт (ананас, дыня,мини- арбуз, кокос или другие)," +
                    " несколько видов сезонных и экзотических фруктов, а также сухофрукты либо сладости. \n");
        }
        descriptionSummary += content;
        descriptionSummary += ("Вес: " + weight + " кг \n");
        descriptionSummary += ("Стоимость: " + price + "₽");

        return descriptionSummary;
    }
}
