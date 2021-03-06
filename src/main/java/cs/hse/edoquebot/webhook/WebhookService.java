package cs.hse.edoquebot.webhook;

import cs.hse.edoquebot.webhook.boxes.Box;
import cs.hse.edoquebot.webhook.boxes.Product;
import cs.hse.edoquebot.webhook.cart.Cart;
import cs.hse.edoquebot.webhook.requestObjects.Name;
import cs.hse.edoquebot.webhook.order.Order;
import cs.hse.edoquebot.webhook.requestObjects.*;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
public class WebhookService {

    List<Box> allBoxes;
    List<Product> allProducts;
    List<Cart> allUsersCarts;

    public WebhookService() {
        initBoxes();
        allUsersCarts = new ArrayList<>();
    }

    public Fulfillment getResponse(WebhookRequest request) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();

        String intent = request.getQueryResult().getIntent().getDisplayName();
        List<OutputContext> contexts = request.getQueryResult().getOutputContexts();


        switch (intent) {
            case "Какие есть коробки" -> {
                String boxType = request.getQueryResult().getParameters().getBoxtype();
                String userSession = request.getSession();

                return handleGetAllBoxes(boxType, userSession);
            }
            case "Расскажи про коробку" -> {
                String boxName = request.getQueryResult().getParameters().getBoxname();
                return handleAboutBox(boxName);
            }
            case "Коробки без продукта" -> {
                String productName = request.getQueryResult().getParameters().getProductname();
                String userSession = request.getSession();
                return handleWithoutProduct(productName, userSession);
            }
            case "Коробки с продуктом" -> {
                String productName = request.getQueryResult().getParameters().getProductname();
                String userSession = request.getSession();
                return handleWithProduct(productName, userSession);
            }
            case "Добавь в корзину" -> {
                var complexItems = request.getQueryResult().getParameters().getComplexItems();
                String userSession = request.getSession();

                if (complexItems.size() == 1) {
                    String boxName = complexItems.get(0).getBoxname();
                    Integer quantity = complexItems.get(0).getNumber();
                    return handleAddBoxToCart(boxName, quantity, userSession);
                }
                return handleAddManyToCart(complexItems, userSession);
            }
            case "Удали коробку" -> {
                String boxName = request.getQueryResult().getParameters().getBoxname();
                Integer quantity = request.getQueryResult().getParameters().getNumber();
                String userSession = request.getSession();
                return handleRemoveBoxFromCart(boxName,quantity, userSession);
            }
            case "Удали все коробки одного типа" -> {
                String boxName = request.getQueryResult().getParameters().getBoxname();
                String userSession = request.getSession();
                return handleRemoveBoxAllFromCart(boxName, userSession);
            }
            case "Удали коробку - cancel" -> {
                OutputContext context = request.getQueryResult().getOutputContexts()
                        .stream().filter(x -> x.getName().contains("-followup-8")).findFirst().get();
                String boxName = context.getParameters().getBoxname();
                Integer quantity = context.getParameters().getNumber();
                String userSession = request.getSession();
                return handleRevertRemoveBoxFromCart(boxName, quantity, userSession);
            }
            case "Что в корзине" -> {
                String userSession = request.getSession();
                return handleCheckUserCart(userSession);
            }
            case "Коробки до n рублей" -> {
                Integer price = request.getQueryResult().getParameters().getCardinal();
                String userSession = request.getSession();
                return handleBoxesCheaperThan(price, userSession);
            }
            case "Коробки от n рублей" -> {
                Integer price = request.getQueryResult().getParameters().getCardinal();
                String userSession = request.getSession();
                return handleMoreExpensive(price, userSession);
            }
            case "Замени коробку" -> {
                String from = request.getQueryResult().getParameters().getFrom();
                String to = request.getQueryResult().getParameters().getTo();
                String userSession = request.getSession();
                return handleChangeBox(from, to, userSession);
            }
            case "Замени коробку - cancel" -> {
                OutputContext context = request.getQueryResult().getOutputContexts()
                        .stream().filter(x -> x.getName().contains("-followup-9")).findFirst().get();
                String from = context.getParameters().getFrom();
                String to = context.getParameters().getTo();
                String userSession = request.getSession();
                return handleRevertChangeBox(from, to, userSession);
            }
            case "Очисти корзину" -> {
                String userSession = request.getSession();
                return handleEraseCart(userSession);
            }
            case "Оформи заказ" -> {
                String userSession = request.getSession();
                return handleMakeOrder(userSession, request.getQueryResult().getParameters());
            }
            case "Оформи заказ - order" -> {
                String userSession = request.getSession();
                String address = request.getQueryResult().getParameters().getAddress();
                String deliveryDate = request.getQueryResult().getParameters().getDeliveryDate();
                String name = request.getQueryResult().getParameters().getName();
                String deliveryZone = request.getQueryResult().getParameters().getDeliveryZone();
                String email = request.getQueryResult().getParameters().getEmail();
                String phone = request.getQueryResult().getParameters().getPhone();
                String deliveryTimeInterval = request.getQueryResult().getParameters().getDeliveryTimeInterval();
                return handleConfirmOrder(userSession, address, deliveryDate, name, deliveryZone, email, phone, deliveryTimeInterval);
            }
            case "Оформи заказ - call" -> {
                OutputContext context = request.getQueryResult().getOutputContexts()
                        .stream().filter(x -> x.getName().contains("orderinfofilled")).findFirst().get();
                String userSession = request.getSession();
                String address = context.getParameters().getAddress();
                String deliveryDate = context.getParameters().getDeliveryDate();
                String name = context.getParameters().getName();
                String deliveryZone = context.getParameters().getDeliveryZone();
                String email = context.getParameters().getEmail();
                String phone = context.getParameters().getPhone();
                String deliveryTimeInterval = context.getParameters().getDeliveryTimeInterval();
                String comment = context.getParameters().getComment();
                Integer tips = context.getParameters().getTips();
                return handleOptParams(userSession, address, deliveryDate, name, deliveryZone, email, phone, deliveryTimeInterval, false, comment, tips);
            }
            case "Оформи заказ - comment" -> {
                OutputContext context = request.getQueryResult().getOutputContexts().stream().filter(x -> x.getName().contains("orderinfofilled")).findFirst().get();
                String userSession = request.getSession();
                String address = context.getParameters().getAddress();
                String deliveryDate = context.getParameters().getDeliveryDate();
                String name = context.getParameters().getName();
                String deliveryZone = context.getParameters().getDeliveryZone();
                String email = context.getParameters().getEmail();
                String phone = context.getParameters().getPhone();
                String deliveryTimeInterval = context.getParameters().getDeliveryTimeInterval();
                Boolean shouldCall = context.getParameters().getShouldCall();
                if (shouldCall == null) {
                    shouldCall = true;
                }
                String comment = request.getQueryResult().getParameters().getComment();
                Integer tips = context.getParameters().getTips();
                return handleOptParams(userSession, address, deliveryDate, name, deliveryZone, email, phone, deliveryTimeInterval, shouldCall, comment, tips);
            }
            case "Оформи заказ - tips" -> {
                OutputContext context = request.getQueryResult().getOutputContexts().stream().filter(x -> x.getName().contains("orderinfofilled")).findFirst().get();
                String userSession = request.getSession();
                String address = context.getParameters().getAddress();
                String deliveryDate = context.getParameters().getDeliveryDate();
                String name = context.getParameters().getName();
                String deliveryZone = context.getParameters().getDeliveryZone();
                String email = context.getParameters().getEmail();
                String phone = context.getParameters().getPhone();
                String deliveryTimeInterval = context.getParameters().getDeliveryTimeInterval();
                Boolean shouldCall = context.getParameters().getShouldCall();
                String comment = context.getParameters().getComment();
                Integer tips = request.getQueryResult().getParameters().getTips();
                return handleOptParams(userSession, address, deliveryDate, name, deliveryZone, email, phone, deliveryTimeInterval, shouldCall, comment, tips);
            }
            case "Оформи заказ - confirm" -> {
                String userSession = request.getSession();
                return handleConfirmOrder(userSession);
            }
            case "Расскажи про коробку - add" -> {
                // Просто берём из контекста описания название и кол-во
                OutputContext context = request.getQueryResult().getOutputContexts().
                        stream().filter(x -> x.getName().contains("-followup-4")).findFirst().get();
                String userSession = request.getSession();
                String boxName = context.getParameters().getBoxname();
                Integer quantity = context.getParameters().getNumber();

                return handleAddBoxToCart(boxName, quantity, userSession);
            }
            case "Добавили одну коробку - cancel" -> {
                // Просто берём из контекста описания название и кол-во
                OutputContext context = request.getQueryResult().getOutputContexts().
                        stream().filter(x -> x.getName().contains("added")).findFirst().get();
                String userSession = request.getSession();
                String boxName = context.getParameters().getBoxname();
                Integer quantity = context.getParameters().getNumber();

                return handleRevertAdd(boxName, quantity, userSession);
            }
            case "Добавили много коробок - cancel" -> {
                // Просто берём из контекста описания название и кол-во
                OutputContext context = request.getQueryResult().getOutputContexts().
                        stream().filter(x -> x.getName().contains("madd")).findFirst().get();
                String userSession = request.getSession();
                ArrayList<ComplexItems> complexItems = context.getParameters().getComplexItems();

                return handleRevertAddManyToCart(complexItems, userSession);
            }
            case "Добавили коробки – давай n" -> {
                // Просто берём из контекста описания название и кол-во
                OutputContext context = request.getQueryResult().getOutputContexts().
                        stream().filter(x -> x.getName().contains("additional")).findFirst().get();
                String userSession = request.getSession();
                String boxName = context.getParameters().getBoxname();
                Integer quantity = context.getParameters().getNumber();
                Integer add = context.getParameters().getAdditional();
                String more = context.getParameters().getMore();

                // Добавляем новое кол-во коробок
                return handleAdditional(userSession, boxName, quantity, add, more);
            }
            case "Коробки с продуктом - add", "Коробки без продукта - add", "Какие есть коробки - add",
                    "Коробки до n рублей - add", "Коробки от n рублей - add" -> {
                OutputContext context = request.getQueryResult().getOutputContexts().
                        stream().filter(x -> x.getName().contains("one-item")).findFirst().get();
                String userSession = request.getSession();
                // Название коробки берём из контекста
                String boxName = context.getParameters().getBoxname();
                // Кол-во коробок берём из простых параметров запроса
                Integer quantity = request.getQueryResult().getParameters().getNumber();

                return handleAddBoxToCart(boxName, quantity, userSession);
            }
            case "Коробки с продуктом - description", "Коробки без продукта - description", "Какие есть коробки - description",
                    "Коробки до n рублей - description", "Коробки от n рублей - description" -> {
                OutputContext context = request.getQueryResult().getOutputContexts().
                        stream().filter(x -> x.getName().contains("one-item")).findFirst().get();

                // Название коробки берём из контекста
                String boxName = context.getParameters().getBoxname();

                return handleAboutBox(boxName);
            }


        }
        response.add("Я не знаю, что на это ответить");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleAdditional(String userSession, String boxName,
                                         Integer quantity, Integer add, String more) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("В вашей корзине сейчас пусто. Нечего заменять");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box currentBox = userCart.getBoxes().
                stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (currentBox == null) {
            response.add("Кажется такой коробки нет в вашей корзине. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        if (quantity == null) {
            quantity = 1;
        }

        for (int i = 0; i < quantity; i++) {
            userCart.removeFromCart(currentBox);
        }

        if (Objects.equals(more, "еще")) {
            quantity += add;
        } else {
            quantity = add;
        }

        for (int i = 0; i < quantity; i++) {
            userCart.addToCart(currentBox);
        }

        String boxWord = changeWordForm(quantity);


        Parameters newParams = new Parameters(null, boxName, new ArrayList<>(),null,
                quantity, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
        String contextName = userSession + "/contexts/added";
        contexts.add(new OutputContext(contextName, 1, newParams));

        response.add("Понял. Добавил в итоге " + quantity + boxWord);
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleConfirmOrder(String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("Ваш заказ оформлен, ожидайте курьера.\nБудем рады вам снова!");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        allUsersCarts.remove(userCart);
        allUsersCarts.add(new Cart(userSession));

        response.add("Ваш заказ оформлен, ожидайте курьера.\nБудем рады вам снова!");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleConfirmOrder(String userSession, String address, String deliveryDate,
                                           String name, String deliveryZone, String email, String phone,
                                           String deliveryTimeInterval) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null) {
            response.add("Кажется, в вашей корзине пусто. Не могу продолжить оформление заказа");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Order order = new Order(address, deliveryDate, name, deliveryZone, userCart.getSum(), email,
                phone, deliveryTimeInterval, true, null, null);


        response.add(order.toString());
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);

    }

    private Fulfillment handleOptParams(String userSession, String address, String deliveryDate,
                                        String name, String deliveryZone, String email, String phone,
                                        String deliveryTimeInterval, Boolean shouldCall, String comment, Integer tips) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null) {
            response.add("Кажется, в вашей корзине пусто. Не могу продолжить оформление заказа");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Order order = new Order(address, deliveryDate, name, deliveryZone, userCart.getSum(), email,
                phone, deliveryTimeInterval, shouldCall, comment, tips);


        response.add(order.toString());
        text.add(new Text(new Text2(response)));

        List<OutputContext> outputContexts = new ArrayList<>();
        Parameters newParams = new Parameters(null, null, new ArrayList<>(),null,
                null, null, null, null, null, null, "ok", address,
                deliveryDate, name, deliveryZone, email, phone, deliveryTimeInterval, shouldCall, comment, tips);
        String contextName = userSession + "/contexts/orderinfofilled";
        outputContexts.add(new OutputContext(contextName, 2, newParams));
        outputContexts.add(new OutputContext(userSession + "/contexts/-followup-2", 2, null));

        return new Fulfillment(text, outputContexts);

    }

    private Fulfillment handleGetAllBoxes(String boxType, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> outputContexts = new ArrayList<>();


        if (!boxType.equals("")) {
            return resolveBoxesIntent(boxType, userSession);
        } else {
            response.add("В нашем ассортименте есть:\n" +
                    " 1. Овощные коробки\n" +
                    "  – Базовый набор, 2 кг (130 ₽)\n" +
                    "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                    "  – Овощи для салатов, 1.2 кг (900 ₽)\n" +
                    " 2. Фруктовые коробки\n" +
                    "  – Коробка недели,  1.5 кг (1200 ₽)\n" +
                    " 3. Лучший выбор\n" +
                    "  – Демо-коробка, 4.4 кг (4000 ₽)\n" +
                    "  – Весенняя коробка, 1.8 кг (1600 ₽)\n" +
                    "  – Не болей, 2.7 кг (1850 ₽)");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, outputContexts);
        }
    }

    private Fulfillment handleAboutBox(String boxName) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        if (!boxName.equals("")) {
            return resolveBoxIntent(boxName);
        } else {
            response.add("В нашем ассортименте есть:\n" +
                    " 1. Овощные коробки\n" +
                    "  – Базовый набор, 2 кг (130 ₽)\n" +
                    "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                    "  – Овощи для салатов, 1.2 кг (900 ₽)\n" +
                    " 2. Фруктовые коробки\n" +
                    "  – Коробка недели,  1.5 кг (1200 ₽)\n" +
                    " 3. Лучший выбор\n" +
                    "  – Демо-коробка, 4.4 кг (4000 ₽)\n" +
                    "  – Весенняя коробка, 1.8 кг (1600 ₽)\n" +
                    "  – Не болей, 2.7 кг (1850 ₽)\n" +
                    "Про какую коробку хотите узнать подробнее?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }
    }

    private Fulfillment handleWithoutProduct(String productName, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> outputContexts = new ArrayList<>();

        List<Box> withoutProduct = allBoxes.stream().filter(box -> box.getProducts()
                        .stream().noneMatch(product -> product.getName().equals(productName)))
                .collect(Collectors.toList());

        StringBuilder summary = new StringBuilder("Можем предложить следующие коробки: \n");
        for (Box box : withoutProduct) {
            summary.append(" – ").append(box.getBoxName()).append("\n");
        }
        response.add(summary.toString());
        text.add(new Text(new Text2(response)));

        if (withoutProduct.size() == 1) {
            Box box = withoutProduct.get(0);
            Parameters newParams = new Parameters(null, box.getBoxName(), new ArrayList<>(),null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);
            String contextName = userSession + "/contexts/one-item";
            outputContexts.add(new OutputContext(contextName, 1, newParams));
        }

        return new Fulfillment(text, outputContexts);
    }

    private Fulfillment handleWithProduct(String productName, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> outputContexts = new ArrayList<>();

        List<Box> withProduct = allBoxes.stream().filter(box -> box.getProducts()
                        .stream().anyMatch(product -> product.getName().equals(productName)))
                .collect(Collectors.toList());

        if (withProduct.size() == 0) {
            response.add("С этим продуктом ничего нет");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, outputContexts);
        }

        StringBuilder summary = new StringBuilder("Можем предложить следующие коробки: \n");
        for (Box box : withProduct) {
            summary.append(" – ").append(box.getBoxName()).append("\n");
        }
        response.add(summary.toString());
        text.add(new Text(new Text2(response)));

        if (withProduct.size() == 1) {
            Box box = withProduct.get(0);
            Parameters newParams = new Parameters(null, box.getBoxName(), new ArrayList<>(),null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null, null, null, null, null, null);
            String contextName = userSession + "/contexts/one-item";
            outputContexts.add(new OutputContext(contextName, 1, newParams));
        }

        return new Fulfillment(text, outputContexts);
    }

    private Fulfillment handleRevertAdd(String boxName, Integer quantity, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("В вашей корзине сейчас пусто. Нечего убирать");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box removedBox = userCart.getBoxes().
                stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (removedBox == null) {
            response.add("Не получилось отменить ваше действие. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        if (quantity == null) {
            quantity = 1;
        }

        for (int i = 0; i < quantity; i++) {
            userCart.removeFromCart(removedBox);
        }

        response.add("Убрал из корзины");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private void remove(Box box, Integer quantity, String userSession) {


        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null) {
            userCart = new Cart(userSession);
            allUsersCarts.add(userCart);
        }

        if (quantity == null) {
            quantity = 1;
        }

        for (int i = 0; i < quantity; i++) {
            userCart.removeFromCart(box);
        }

    }

    private Fulfillment handleRevertAddManyToCart(ArrayList<ComplexItems> complexItems, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        ArrayList<Box> boxes = new ArrayList<>();

        for (var item: complexItems) {

            Box addedBox = allBoxes.stream().filter(box -> box.getBoxName().equals(item.getBoxname())).findFirst().orElse(null);
            if (addedBox == null) {
                response.add("Не получилось отменить ваше действие. Можете сказать по другому?\"");

                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            boxes.add(addedBox);

        }

        for (int i = 0; i < complexItems.size(); i++) {
            Integer num = complexItems.get(i).getNumber();
            if (num == null) { num = 1; }
            remove(boxes.get(i), num, userSession);
        }

        response.add("Убрал из корзины");

        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);

    }

    private String add(String boxName, Integer quantity, String userSession) {
        Box addedBox = allBoxes.stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (addedBox == null) {
            return "Кажется коробки "+ "\""+boxName+ "\""+" нет в ассортименте. Можете сказать по другому?";
        }

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null) {
            userCart = new Cart(userSession);
            allUsersCarts.add(userCart);
        }

        if (quantity == null) {
            quantity = 1;
        }

        for (int i = 0; i < quantity; i++) {
            userCart.addToCart(addedBox);
        }

        if (quantity == 1) {
            return "–"+ boxName;
        } else {
            String boxWord = changeWordForm(quantity);
            return "–" + quantity + boxWord + "\""+boxName+ "\"";
        }
    }

    private Fulfillment handleAddManyToCart(ArrayList<ComplexItems> complexItems, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        List<String> res = new ArrayList<>();
        res.add("В результате добавили:");
        for (var item: complexItems) {
            Integer num = item.getNumber();
            if (num == null) { num = 1; }

            res.add(add(item.getBoxname(), num, userSession));
        }

        response.add(String.join( "\n", res));

        Parameters addedParams = new Parameters(null, null, complexItems,null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
        String addedContextName = userSession + "/contexts/madd";
        contexts.add(new OutputContext(addedContextName, 1, addedParams));

        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);

    }

    private Fulfillment handleAddBoxToCart(String boxName, Integer quantity, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Box addedBox = allBoxes.stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (addedBox == null) {
            response.add("Кажется такой коробки нет в ассортименте. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null) {
            userCart = new Cart(userSession);
            allUsersCarts.add(userCart);
        }

        if (quantity == null) {
            quantity = 1;
        }


        for (int i = 0; i < quantity; i++) {
            userCart.addToCart(addedBox);
        }

        if (quantity == 1) {
            response.add("Добавил в корзину");
        } else {
            String boxWord = changeWordForm(quantity);
            response.add("Добавил " + quantity + boxWord + "в корзину");
        }

        Parameters addedParams = new Parameters(null, boxName, new ArrayList<>(),null,
                quantity, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
        String addedContextName = userSession + "/contexts/added";
        contexts.add(new OutputContext(addedContextName, 1, addedParams));

        Parameters additionalParams = new Parameters(null, boxName, new ArrayList<>(),null,
                quantity, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
        String additionalContextName = userSession + "/contexts/additional";
        contexts.add(new OutputContext(additionalContextName, 1, additionalParams));

        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);

    }

    private Fulfillment handleRemoveBoxAllFromCart(String boxName, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("В вашей корзине сейчас пусто. Нечего убирать");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box removedBox = userCart.getBoxes().
                stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (removedBox == null) {
            response.add("Кажется такой коробки нет в вашей корзине. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        int i = 0;

        while(removedBox!= null) {
            i++;
            userCart.removeFromCart(removedBox);

            removedBox = userCart.getBoxes().
                    stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        }

        Parameters newParams = new Parameters(null, boxName, new ArrayList<>(),null,
                i, null, null, null, null, null, null, null,
                null, null, null, null,
                null, null, null, null, null);
        String contextName = userSession + "/contexts/-followup-8";
        contexts.add(new OutputContext(contextName, 1, newParams));

        response.add("Убрал из корзины");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleRemoveBoxFromCart(String boxName, Integer quantity, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("В вашей корзине сейчас пусто. Нечего убирать");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box removedBox = userCart.getBoxes().
                stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (removedBox == null) {
            response.add("Кажется такой коробки нет в вашей корзине. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }
        userCart.removeFromCart(removedBox);

        if (quantity == null) {
            quantity = 1;
        }
        quantity--;

        for (int i = 0; i < quantity; i++) {
            Box removedBox2 = userCart.getBoxes().
                    stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
            if (removedBox2 == null) {
                response.add("Убрал из корзины");
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            userCart.removeFromCart(removedBox2);
        }


        response.add("Убрал из корзины");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleRevertRemoveBoxFromCart(String boxName, Integer quantity, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Box addedBox = allBoxes.stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (addedBox == null) {
            response.add("Кажется такой коробки нет в ассортименте. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null) {
            userCart = new Cart(userSession);
            allUsersCarts.add(userCart);
        }

        if (quantity == null) {
            quantity = 1;
        }
        for (int i = 0; i < quantity; i++) {
            userCart.addToCart(addedBox);
        }

        response.add("Вернул в корзину эту коробку");

        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);

    }

    private Fulfillment handleCheckUserCart(String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null) {
            userCart = new Cart(userSession);
            allUsersCarts.add(userCart);
        }

        response.add(userCart.toString());
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleBoxesCheaperThan(Integer price, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> outputContexts = new ArrayList<>();

        List<Box> cheaper = allBoxes.stream().filter(box -> box.getPrice() < price)
                .collect(Collectors.toList());

        if (cheaper.size() == 0) {
            response.add("Не нашлось коробок дешевле " + price + " ₽:\n");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, outputContexts);
        }

        StringBuilder summary = new StringBuilder("Коробки дешевле " + price + " ₽:\n");
        for (Box box : cheaper) {
            summary.append(" – ").append(box.getBoxName()).append("\n");
        }

        response.add(summary.toString());
        text.add(new Text(new Text2(response)));

        if (cheaper.size() == 1) {
            Box box = cheaper.get(0);
            Parameters newParams = new Parameters(null, box.getBoxName(), new ArrayList<>(),null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null,
                    null, null, null, null, null);
            String contextName = userSession + "/contexts/one-item";
            outputContexts.add(new OutputContext(contextName, 1, newParams));
        }

        return new Fulfillment(text, outputContexts);
    }

    private Fulfillment handleMoreExpensive(Integer price, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> outputContexts = new ArrayList<>();

        List<Box> cheaper = allBoxes.stream().filter(box -> box.getPrice() > price)
                .collect(Collectors.toList());

        if (cheaper.size() == 0) {
            response.add("Не нашлось коробок дороже " + price + " ₽:\n");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, outputContexts);
        }

        StringBuilder summary = new StringBuilder("Коробки дороже " + price + " ₽:\n");
        for (Box box : cheaper) {
            summary.append(" – ").append(box.getBoxName()).append("\n");
        }
        response.add(summary.toString());
        text.add(new Text(new Text2(response)));

        if (cheaper.size() == 1) {
            Box box = cheaper.get(0);
            Parameters newParams = new Parameters(null, box.getBoxName(), new ArrayList<>(),null,
                    null, null, null, null, null, null, null, null,
                    null, null, null, null,
                    null, null, null, null, null);
            String contextName = userSession + "/contexts/one-item";
            outputContexts.add(new OutputContext(contextName, 1, newParams));
        }

        return new Fulfillment(text, outputContexts);
    }

    private Fulfillment handleChangeBox(String from, String to, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("В вашей корзине сейчас пусто. Нечего заменять");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box changedBox = userCart.getBoxes().
                stream().filter(box -> box.getBoxName().equals(from)).findFirst().orElse(null);
        if (changedBox == null) {
            response.add("Кажется такой коробки нет в вашей корзине. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box addedBox = allBoxes.stream().filter(box -> box.getBoxName().equals(to)).findFirst().orElse(null);
        if (addedBox == null) {
            response.add("Кажется, коробки на которую вы хотите заменить, нет в ассортименте. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        userCart.removeFromCart(changedBox);
        userCart.addToCart(addedBox);

        response.add("Заменил коробку. Вместо неё теперь: " + addedBox.getBoxName());
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleRevertChangeBox(String to, String from, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("В вашей корзине сейчас пусто. Нечего заменять");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box changedBox = userCart.getBoxes().
                stream().filter(box -> box.getBoxName().equals(from)).findFirst().orElse(null);
        if (changedBox == null) {
            response.add("Кажется такой коробки нет в вашей корзине. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        Box addedBox = allBoxes.stream().filter(box -> box.getBoxName().equals(to)).findFirst().orElse(null);
        if (addedBox == null) {
            response.add("Кажется, коробки на которую вы хотите заменить, нет в ассортименте. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        userCart.removeFromCart(changedBox);
        userCart.addToCart(addedBox);

        response.add("Вернул все как было. В корзине теперь, как и раньше:" + addedBox.getBoxName());
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleEraseCart(String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("Очистил корзину, хотя там и так ничего не было");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        allUsersCarts.remove(userCart);
        allUsersCarts.add(new Cart(userSession));

        response.add("Очистил корзину");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private Fulfillment handleMakeOrder(String userSession, Parameters params) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();


        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null || userCart.getBoxes().size() == 0) {
            response.add("В вашей корзине пусто");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, contexts);
        }

        response.add(userCart.toString() + "\n" + "Можем перейти к оформлению?");
        text.add(new Text(new Text2(response)));
        List<OutputContext> outputContexts = new ArrayList<>();
        Parameters newParams = new Parameters(params.getBoxtype(), params.getBoxname(), new ArrayList<>(),params.getProductname(),
                params.getNumber(), null, null, params.getCardinal(), params.getFrom(), params.getTo(), "ok", "",
                "", null, "", "", "", "", null, null, null);
        String contextName = userSession + "/contexts/orderinfo";
        outputContexts.add(new OutputContext(contextName, 1, newParams));
        return new Fulfillment(text, outputContexts);
    }

    private Fulfillment resolveBoxesIntent(String boxType, String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> outputContexts = new ArrayList<>();
        String contextName = userSession + "/contexts/one-item";


        if (boxType.equals("овощная")) {
            response.add("Овощные коробки:\n" +
                    "  – Базовый набор, 2 кг (130 ₽)\n" +
                    "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                    "  – Овощи для салатов, 1.2 кг (900 ₽)");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, outputContexts);
        } else if (boxType.equals("лучший выбор")) {
            response.add("Лучший выбор\n" +
                    "  – Демо-коробка, 4.4 кг (4000 ₽)\n" +
                    "  – Весенняя коробка, 1.8 кг (1600 ₽)\n" +
                    "  – Не болей, 2.7 кг (1850 ₽)");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text, outputContexts);
        }

        response.add("Фруктовые коробки:\n" +
                "  – Коробка недели,  1.5 кг (1200 ₽)");
        text.add(new Text(new Text2(response)));

        Parameters newParams = new Parameters(null, "Коробка недели", new ArrayList<>(),null,
                null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null);
        outputContexts.add(new OutputContext(contextName, 1, newParams));

        return new Fulfillment(text, outputContexts);

    }

    private Fulfillment resolveBoxIntent(String boxName) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        List<OutputContext> contexts = new ArrayList<>();

        switch (boxName) {
            case "Коробка недели" -> {
                response.add(allBoxes.get(0).toString());
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            case "Базовый набор" -> {
                response.add(allBoxes.get(1).toString());
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            case "Овощи для готовки" -> {
                response.add(allBoxes.get(2).toString());
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            case "Овощи для салатов" -> {
                response.add(allBoxes.get(3).toString());
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            case "Демо-коробка" -> {
                response.add(allBoxes.get(4).toString());
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            case "Весенняя коробка" -> {
                response.add(allBoxes.get(5).toString());
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
            case "Не болей" -> {
                response.add(allBoxes.get(6).toString());
                text.add(new Text(new Text2(response)));
                return new Fulfillment(text, contexts);
            }
        }
        response.add("Можем порекомендовать следующие коробки:\n" +
                "  – Демо-коробка, 4.4 кг (4000 ₽)\n" +
                "  – Весенняя коробка, 1.8 кг (1600 ₽)\n" +
                "  – Не болей, 2.7 кг (1850 ₽)\n" +
                "Про какую коробку хотите узнать подробнее?");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text, contexts);
    }

    private void initProducts() {
        allProducts = List.of(
                new Product("Клубника", 250, "г"),
                new Product("Виноград", 500, "г"),
                new Product("Мандарины", 500, "г"),
                new Product("Груши Пакхам", 450, "г"),
                new Product("Киви", 2, "шт"),
                new Product("Картофель", 1, "кг"),
                new Product("Морковь", 500, "г"),
                new Product("Репчатый лук", 500, "г"),
                new Product("Шампиньоны", 300, "г"),
                new Product("Брокколи", 1, "шт"),
                new Product("Болгарский перец", 1, "шт"),
                new Product("Огурцы", 500, "г"),
                new Product("Помидоры Черри", 250, "г"),
                new Product("Салат Руккола", 125, "г"),
                new Product("Букет зелени", 100, "г"),
                new Product("Авокадо", 0, "г"),
                new Product("Салат Лолло Бионда", 0, "г"),
                new Product("Виноград Томпсон", 0, "г"),
                new Product("Яблоки Гренни Смит", 0, "г"),
                new Product("Яблоки", 0, "г"),
                new Product("Лимон", 0, "г"),
                new Product("Грейпфрут", 0, "г"),
                new Product("Фиточай", 0, "г"),
                new Product("Мёд", 0, "г"),
                new Product("Медовое помело", 0, "г"),
                new Product("Корица", 0, "г"),
                new Product("Корень имбиря", 0, "г")
        );

    }

    private void initBoxes() {
        initProducts();
        allBoxes = List.of(
                new Box("Коробка недели", "Семейный набор фруктов. Обновляем состав каждую неделю.",
                        List.of(allProducts.get(0), allProducts.get(1), allProducts.get(2), allProducts.get(3), allProducts.get(4)),
                        1.5, 1400, false),
                new Box("Базовый набор", "Лучший семейный набор!",
                        List.of(allProducts.get(5), allProducts.get(6), allProducts.get(7)),
                        2, 220, false),
                new Box("Овощи для готовки", "Овощи для супов и горячих блюд. Обновляем состав каждую неделю.",
                        List.of(allProducts.get(8), allProducts.get(9), allProducts.get(6), allProducts.get(7),
                                allProducts.get(10)),
                        1.5, 750, false),
                new Box("Овощи для салатов", "Овощи для супов и горячих блюд. Обновляем состав каждую неделю.",
                        List.of(allProducts.get(11), allProducts.get(10), allProducts.get(12), allProducts.get(13),
                                allProducts.get(14)),
                        1.2, 900, false),
                new Box("Демо-коробка", "Микс из сезонных и экзотических фруктов",
                        List.of(),
                        4.4, 4000, true),
                new Box("Весенняя коробка", "Из состава этой коробки можно приготовить не один вкусный завтрак," +
                        " обед или ужин и даже взять что-то с собой на перекус.",
                        List.of(allProducts.get(15), allProducts.get(11), allProducts.get(9), allProducts.get(16),
                                allProducts.get(17), allProducts.get(4), allProducts.get(18)),
                        1.8, 1600, false),
                new Box("Не болей", "Коробка с продуктами для поддержки иммунитета." +
                        " Что может быть лучше чая с лимоном и мёдом, когда забота о здоровье особенно важна.",
                        List.of(allProducts.get(19), allProducts.get(20), allProducts.get(21), allProducts.get(2),
                                allProducts.get(22), allProducts.get(23), allProducts.get(24), allProducts.get(25),
                                allProducts.get(26)),
                        2.7, 1850, false)

        );

    }

    private String changeWordForm(int quantity){
        int n = Math.abs(quantity);

        n %= 100;
        if (n >= 5 && n <= 20) {
            return " коробок ";
        }
        n %= 10;
        if (n >= 2 && n <= 4) {
            return  " коробки ";
        }

        return " коробок ";
    }


}
