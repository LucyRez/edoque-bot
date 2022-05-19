package cs.hse.edoquebot.webhook;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import cs.hse.edoquebot.webhook.boxes.Box;
import cs.hse.edoquebot.webhook.boxes.Product;
import cs.hse.edoquebot.webhook.cart.Cart;
import cs.hse.edoquebot.webhook.requestObjects.Text;
import cs.hse.edoquebot.webhook.requestObjects.Text2;
import cs.hse.edoquebot.webhook.requestObjects.WebhookRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.json.Json;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public Fulfillment getResponse(WebhookRequest request){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();

        String intent = request.getQueryResult().getIntent().getDisplayName();
        if (intent.equals("Какие есть коробки")){

            String boxType = request.getQueryResult().getParameters().getBoxtype();
            return handleGetAllBoxes(boxType);
        }else if(intent.equals("Расскажи про коробку")){
            String boxName = request.getQueryResult().getParameters().getBoxname();
            return handleAboutBox(boxName);
        }else if(intent.equals("Коробки без продукта")){
            String productName = request.getQueryResult().getParameters().getProductname();
            return handleWithoutProduct(productName);
        }else if(intent.equals("Коробки с продуктом")){
            String productName = request.getQueryResult().getParameters().getProductname();
            return handleWithProduct(productName);
        }else if(intent.equals("Добавь в корзину")){
            String boxName = request.getQueryResult().getParameters().getBoxname();
            Integer quantity = request.getQueryResult().getParameters().getNumber();
            String userSession = request.getSession();
            return handleAddBoxToCart(boxName, quantity, userSession);
        }else if(intent.equals("Что в корзине")){
            String userSession = request.getSession();
            return handleCheckUserCart(userSession);
        }
        response.add("Я не знаю, что на это ответить");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);

    }

    private Fulfillment handleGetAllBoxes(String boxType){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        if(!boxType.equals("")){
            return resolveBoxesIntent(boxType);
        }else{
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
            return new Fulfillment(text);
        }
    }

    private Fulfillment handleAboutBox(String boxName){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        if(!boxName.equals("")){
            return resolveBoxIntent(boxName);
        }else{
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
            return new Fulfillment(text);
        }
    }

    private Fulfillment handleWithoutProduct(String productName){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();


        List<Box> withoutProduct = allBoxes.stream().filter(box -> box.getProducts()
                        .stream().noneMatch(product -> product.getName().equals(productName)))
                .collect(Collectors.toList());

        StringBuilder summary = new StringBuilder("Можем предложить следующие коробки: \n");
        for (Box box: withoutProduct) {
            summary.append(" – ").append(box.getBoxName()).append("\n");
        }
        response.add(summary.toString());
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);
    }

    private Fulfillment handleWithProduct(String productName){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();


        List<Box> withoutProduct = allBoxes.stream().filter(box -> box.getProducts()
                        .stream().anyMatch(product -> product.getName().equals(productName)))
                .collect(Collectors.toList());

        StringBuilder summary = new StringBuilder("Можем предложить следующие коробки: \n");
        for (Box box: withoutProduct) {
            summary.append(" – ").append(box.getBoxName()).append("\n");
        }
        response.add(summary.toString());
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);
    }

    private Fulfillment handleAddBoxToCart(String boxName, Integer quantity, String userSession){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();
        Box addedBox = allBoxes.stream().filter(box -> box.getBoxName().equals(boxName)).findFirst().orElse(null);
        if (addedBox == null){
            response.add("Кажется такой коробки нет в ассортименте. Можете сказать по другому?");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null){
            userCart = new Cart(userSession);
            allUsersCarts.add(userCart);
        }

        if (quantity == null){
            quantity = 1;
        }
        
        for (int i = 0; i < quantity; i++) {
            userCart.addToCart(addedBox);
        }

        if(quantity == 1){
            response.add("Добавил в корзину");

        }else {
            response.add("Добавил " + quantity + " коробки в корзину");
        }

        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);

    }

    private Fulfillment handleCheckUserCart(String userSession) {
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();

        Cart userCart = allUsersCarts.stream().filter(cart -> cart.getUserSession().equals(userSession))
                .findFirst().orElse(null);

        if (userCart == null){
            userCart = new Cart(userSession);
            allUsersCarts.add(userCart);
        }

        response.add(userCart.toString());
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);
    }

    private Fulfillment resolveBoxesIntent(String boxType){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();

        if(boxType.equals("овощная")){
            response.add("Овощные коробки:\n" +
                    "  – Базовый набор, 2 кг (130 ₽)\n" +
                    "  – Овощи для готовки, 1.5 кг (750 ₽)\n" +
                    "  – Овощи для салатов, 1.2 кг (900 ₽)");
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }
        response.add("Фруктовые коробки:\n" +
                "  – Коробка недели,  1.5 кг (1200 ₽)");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);
    }

    private Fulfillment resolveBoxIntent(String boxName){
        List<Text> text = new ArrayList<>();
        List<String> response = new ArrayList<>();

        if(boxName.equals("Коробка недели")){
            response.add(allBoxes.get(0).toString());
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }else if(boxName.equals("Базовый набор")){
            response.add(allBoxes.get(1).toString());
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }else if(boxName.equals("Овощи для готовки")){
            response.add(allBoxes.get(2).toString());
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }else if(boxName.equals("Овощи для салатов")){
            response.add(allBoxes.get(3).toString());
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }else if(boxName.equals("Демо-коробка")){
            response.add(allBoxes.get(4).toString());
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }else if(boxName.equals("Весенняя коробка")){
            response.add(allBoxes.get(5).toString());
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }else if(boxName.equals("Не болей")){
            response.add(allBoxes.get(6).toString());
            text.add(new Text(new Text2(response)));
            return new Fulfillment(text);
        }
        response.add("Можем порекомендовать следующие коробки:\n" +
                "  – Демо-коробка, 4.4 кг (4000 ₽)\n" +
                "  – Весенняя коробка, 1.8 кг (1600 ₽)\n" +
                "  – Не болей, 2.7 кг (1850 ₽)\n" +
                "Про какую коробку хотите узнать подробнее?");
        text.add(new Text(new Text2(response)));
        return new Fulfillment(text);
    }

    private void initProducts(){
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

    private void initBoxes(){
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
                                allProducts.get(17),  allProducts.get(4),  allProducts.get(18)),
                        1.8, 1600, false),
                new Box("Не болей", "Коробка с продуктами для поддержки иммунитета." +
                        " Что может быть лучше чая с лимоном и мёдом, когда забота о здоровье особенно важна.",
                        List.of(allProducts.get(19), allProducts.get(20), allProducts.get(21), allProducts.get(2),
                                allProducts.get(22),  allProducts.get(23),  allProducts.get(24), allProducts.get(25),
                                allProducts.get(26)),
                        2.7, 1850, false)

        );

    }



}
