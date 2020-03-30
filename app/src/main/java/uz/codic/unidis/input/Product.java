package uz.codic.unidis.input;

class Product {
    String productId;
    String name;
    String brandName;

    public Product() {

    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getName() {
        return name;
    }

    public String getBrandName() {
        return brandName;
    }
}
