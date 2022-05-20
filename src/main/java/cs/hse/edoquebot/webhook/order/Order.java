package cs.hse.edoquebot.webhook.order;

import cs.hse.edoquebot.webhook.requestObjects.Name;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
    private Boolean shouldCall;
    private String comment;
    private Integer tips;

    private Integer calculateDeliveryPrice() {
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

    private String call() {
        if (shouldCall) {
            return "Мы вам перезвоним для подтверждения заказа. \n";
        }
        return "Звонить не будем. \n";
    }

    private String tipsString() {
        if (tips == null) {
            return "Вы можете оставить чаевые курьеру. \n\n";
        }
        return "\n";
    }

    private String commentString() {
        if (comment != null) {
            return "Комментарий: " + comment + ". \n";
        }
        return "Можете добавить комментарий к заказу. \n";
    }

    private String parseDate(){
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyy", Locale.ENGLISH);
        LocalDate date = LocalDate.parse(deliveryDate.split("T")[0], inputFormatter);
        return outputFormatter.format(date);
    }

    private int intTips() {
        if (tips == null) return 0;
        return tips;
    }

    @Override
    public String toString() {
        return "Отлично \n" +
                "\n" +
                "Имя: " + name + "\n" +
                "Телефон: " + phone + "\n" +
                "email: " + email + "\n" +
                "адрес: " + address + "\n" +
                "\n" +
                "Доставим " + parseDate() + ", "+ deliveryTimeInterval + "\n" +
                "Стоимость заказа с учётом доставки: " + (orderSum + calculateDeliveryPrice() + intTips()) + "₽\n" +
                "Оплата производится курьеру картой при получении \n" +
                "\n" +
                call() +
                "\n" +
                commentString() +
                "\n" +
                tipsString() +
                "Оформляем заказ?";
    }
}
