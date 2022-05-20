package cs.hse.edoquebot.webhook.cart;

import cs.hse.edoquebot.webhook.boxes.Box;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Override
    public String toString() {
        if(boxes.size() == 0){
            return "В вашей корзине пусто";
        }

        StringBuilder allBoxes = new StringBuilder();
        int price = 0;

        for (Box box: boxes) {
            allBoxes.append(" – ").append(box.getBoxName()).append("\n");
            price += box.getPrice();
        }

        String priceTag = "Итого: " + price + "₽";

        return "Количество коробок в корзине: " + boxes.size() + "\n" +
                 allBoxes.toString() + priceTag;
    }
}
