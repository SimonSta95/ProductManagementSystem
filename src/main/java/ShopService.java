import interfaces.OrderRepo;
import records.Order;
import records.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class ShopService {
    private final ProductRepo productRepo = new ProductRepo();
    private final OrderRepo orderRepo = new OrderMapRepo();
    private final IdService idService = new IdService();

    public List<Product> getProducts() {
        return productRepo.getProducts();
    }

    public Product getProduct(String id) {
        return productRepo.getProductById(id);
    }

    public Product addProduct(Product newProduct) {
        return productRepo.addProduct(newProduct);
    }

    public void addProducts(List<Product> newProducts) {
        productRepo.addProducts(newProducts);
    }

    public void removeProduct(String id) {
        productRepo.removeProduct(id);
    }

    public List<Order> getAllOrders() {
        return orderRepo.getOrders();
    }

    public Order getOrder(String id) {
        return orderRepo.getOrderById(id);
    }

    public void removeOrder(String id) {
        orderRepo.removeOrder(id);
    }

    public Order addOrder(List<String> productIds) {
        if(checkStockAndReduce(productIds)) return null;

        List<Product> products = retrieveProducts(productIds);

        final BigDecimal totalPrice = calcTotalPrice(productIds);
        final String orderId = idService.generateId();
        final Order newOrder = new Order(orderId, products, totalPrice);

        return orderRepo.addOrder(newOrder);
    }

    public boolean checkStockAndReduce(List<String> productIds){
        for (String productId : productIds) {
            if(!isPoductAvailable(productId)) {
                System.out.println("Product " + productId + " not in stock");
                return true;
            }
            reduceProductStock(productId);
        }
        return false;
    }

    private boolean isPoductAvailable(String productId) {
        return getProduct(productId).stock() > 0;
    }

    private BigDecimal calcTotalPrice(List<String> productIds) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        List<Product> products = retrieveProducts(productIds);

        for (Product product : products) {
            totalPrice = totalPrice.add(product.price());
        }

        return totalPrice;
    }

    private List<Product> retrieveProducts(List<String> productIds) {
        List<Product> products = new ArrayList<>();

        for (String productId : productIds) {
            Product product = getProduct(productId);
            if(product != null){
                products.add(product);
            } else {
                System.out.println("Product " + productId + " not in stock");
            }
        }
        return products;
    }

    private void reduceProductStock(String productId) {
        Product product = getProduct(productId);
        Product updatedProduct = new Product(product.id(), product.name(), product.price(), product.stock() - 1);
        productRepo.addProduct(updatedProduct);
    }
}
