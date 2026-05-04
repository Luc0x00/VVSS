package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductService {

    private final Repository<Integer, Product> productRepo;
    private final ProductValidator validator;

    public ProductService(Repository<Integer, Product> productRepo, ProductValidator validator) {
        this.validator = validator;
        this.productRepo = productRepo;
    }

    public void addProduct(Product p) {
        validator.validate(p);
        productRepo.save(p);
    }

    public void updateProduct(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        Product updated = new Product(id, name, price, categorie, tip);
        validator.validate(updated);
        productRepo.update(updated);
    }

    public void deleteProduct(int id) {
        productRepo.delete(id);
    }

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product findById(int id) {
        return productRepo.findOne(id);
    }

    public List<Product> filterByCategorie(CategorieBautura categorie,
                                           String keyword,
                                           double maxPrice) {
        return filterByCategorie(getAllProducts(), categorie, keyword, maxPrice);
    }

    public List<Product> filterByCategorie(List<Product> products,
                                           CategorieBautura categorie,
                                           String keyword,
                                           double maxPrice) {
        if (products == null || products.isEmpty()) {
            return new ArrayList<>();
        }
        if (categorie == null) {
            return new ArrayList<>();
        }
        List<Product> result = new ArrayList<>();
        for (Product p : products) {
            if (categorie != CategorieBautura.ALL && p.getCategorie() != categorie) {
                continue;
            }
            if (keyword != null && !p.getNume().toLowerCase().contains(keyword.toLowerCase())) {
                continue;
            }
            if (p.getPret() <= maxPrice) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Product> filterByTip(TipBautura tip) {
        if (tip == TipBautura.ALL) return getAllProducts();
        return getAllProducts().stream()
                .filter(p -> p.getTip() == tip)
                .collect(Collectors.toList());
    }
}