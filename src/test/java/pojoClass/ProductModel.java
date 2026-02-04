package pojoClass;

public class ProductModel {
    private String brand;
    private String name;
    private long price;
    private long quantity;

    public ProductModel(String brand, String name, long price, long quantity) {
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // --- GETTERS ---
    public String getBrand() {
        return brand;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public long getQuantity() {
        return quantity;
    }

    // --- SETTERS (Fluent Style) ---
    public ProductModel setBrand(String brand) {
        this.brand = brand;
        return this;
    }

    public ProductModel setName(String name) {
        this.name = name;
        return this;
    }

    public ProductModel setPrice(long price) {
        this.price = price;
        return this;
    }

    public ProductModel setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductModel product = (ProductModel) o;
        return  brand.trim().equalsIgnoreCase(product.brand.trim()) &&
                name.trim().equalsIgnoreCase(product.name.trim()) &&
                price == product.price &&
                quantity == product.quantity;
    }

    @Override
    public String toString() {
        return "Product{brand='" + brand + "',name='" + name + "', price='" + price + "', qty='" + quantity + "'}";
    }
}
