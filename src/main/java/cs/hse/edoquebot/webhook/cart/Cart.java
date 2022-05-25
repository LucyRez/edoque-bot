package cs.hse.edoquebot.webhook.cart;

import cs.hse.edoquebot.webhook.boxes.Box;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
public class Cart {
    private String userSession;
    private List<Box> boxes;

    public void addToCart(Box box){
        boxes.add(box);
    }

    public void removeFromCart(Box box){
        boxes.remove(box);
    }

    public Cart(String userSession) {
        this.userSession = userSession;
        boxes = new ArrayList<>();

    }

    public Integer getSum(){
        int sum = 0;
        for (Box box: boxes) {
            sum += box.getPrice();
        }
        return sum;
    }

    @Override
    public String toString() {
        if(boxes.size() == 0){
            return "В вашей корзине пусто";
        }

        StringBuilder allBoxes = new StringBuilder();
        int price = 0;

        Map<String, List<Box>> boxesGrouped =
                boxes.stream().collect(Collectors.groupingBy(box -> box.getBoxName()));


        for (var entry: boxesGrouped.entrySet()) {
            int countInGroup = entry.getValue().size();
            allBoxes.append(" – ").append(entry.getKey()).append(", ").append(countInGroup).append("шт.").append(" \n");
            price += (countInGroup*entry.getValue().get(0).getPrice());
        }

        String priceTag = "Итого: " + price + "₽";

        return "Количество коробок в корзине: " + boxes.size() + "\n" +
                 allBoxes.toString() + priceTag;
    }
}
