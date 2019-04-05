package net.aldar.cramello.entitie;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;

import net.aldar.cramello.model.response.product.Product;
import net.aldar.cramello.model.response.product.ProductCategory;

import java.util.List;

public class CategoryAndProducts extends ExpandableGroup<Product> {

    private ProductCategory category;
    private List<Product> products;

    public CategoryAndProducts(String name, ProductCategory category, List<Product> products) {
        super(name, products);
        this.category = category;
        this.products = products;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
