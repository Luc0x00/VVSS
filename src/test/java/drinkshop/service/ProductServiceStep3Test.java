package drinkshop.service;

import drinkshop.domain.Product;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceStep3Test {

    private Repository<Integer, Product> realRepo;
    private ProductValidator realValidator;
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        realRepo = new AbstractRepository<Integer, Product>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };

        realValidator = new ProductValidator();
        productService = new ProductService(realRepo, realValidator);
    }

    @Test
    public void testAddProduct_FullIntegration_ValidProduct_SuccessfullyAddedAndRetrieved() {
        Product validProduct = new Product(10, "Limonadă", 15.0, null, null);
        productService.addProduct(validProduct);
        List<Product> allProducts = productService.getAllProducts();

        assertEquals(1, allProducts.size());
        assertEquals("Limonadă", allProducts.get(0).getNume());

        Product foundProduct = productService.findById(10);
        assertNotNull(foundProduct);
        assertEquals(15.0, foundProduct.getPret());
    }

    @Test
    public void testAddProduct_FullIntegration_InvalidProduct_NothingSaved() {
        Product invalidProduct = new Product(0, "Cafea invizibilă", 0, null, null);
        assertThrows(ValidationException.class, () -> {
            productService.addProduct(invalidProduct);
        });

        List<Product> allProducts = productService.getAllProducts();
        assertTrue(allProducts.isEmpty(), "Repository-ul ar trebui să fie gol deoarece validarea a picat.");
    }
}