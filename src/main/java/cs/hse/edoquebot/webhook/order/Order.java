package cs.hse.edoquebot.webhook.order;

import cs.hse.edoquebot.webhook.requestObjects.Name;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Order {
    private String address;
    private String deliveryDate;
    private String name;
    private String deliveryZone;
    private Integer orderSum;
    private String email;
    private String phone;
    private String deliveryTimeInterval;


    private Integer calculateDeliveryPrice(){
        switch (deliveryZone) {
            case "Москва в пределах МКАД" -> {
                if (orderSum >= 1500) {
                    return 0;
                }
                return 350;
            }
            case "До 5 км от МКАД" -> {
                if (orderSum >= 1500) {
                    return 100;
                }
                return 450;
            }
            case "5-10 км от МКАД" -> {
                if (orderSum >= 1500) {
                    return 200;
                }
                return 550;
            }
            case "10-15 км от МКАД" -> {
                if (orderSum >= 1500) {
                    return 300;
                }
                return 650;
            }
            case "15-20 км от МКАД" -> {
                if (orderSum >= 1500) {
                    return 400;
                }
                return 750;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return "Отлично \n" +
                "\n" +
                "Имя: " + name + "\n" +
                "Телефон: " + phone +"\n" +
                "email: " + email + "\n" +
                "адрес: " + address + "\n" +
                "\n" +
                "Доставим " + deliveryDate + "\n" +
                "Стоимость заказа с учётом доставки: " + (orderSum + calculateDeliveryPrice())+ "₽\n" +
                "Оплата производится курьеру картой при получении \n" +
                "\n" +
                "Мы вам перезвоним для подтверждения заказа. \n" +
                "\n" +
                "Оформляем заказ?";
    }
}
