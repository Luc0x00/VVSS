package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.repository.Repository;
import drinkshop.repository.file.FileProductRepository;
import org.junit.jupiter.api.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ProductAddTest {

    private Repository<Integer, Product> repo;
    private ProductService service;
    private final String TEST_FILE = "src/test/resources/test_products.csv";

    @BeforeEach
    void setUp() throws IOException {
        Files.write(Paths.get(TEST_FILE), new byte[0]);
        repo = new FileProductRepository(TEST_FILE);
        service = new ProductService(repo);
    }

    @Test
    @Tag("ECP")
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    @DisplayName("TC1_ECP: Valid product")
    void tc1_ecp_valid() {
        Product p = new Product(10, "Ceai verde", 15.0, CategorieBautura.TEA, TipBautura.BASIC);
        service.addProduct(p);
        assertNotNull(service.findById(10));
    }

    @Test
    @Tag("ECP")
    @DisplayName("TC2_ECP: Invalid ID (-5)")
    void tc2_ecp_invalid_id() {
        Product p = new Product(-5, "Ceai verde", 15.0, CategorieBautura.TEA, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("ECP")
    @DisplayName("TC3_ECP: Invalid Price (-1)")
    void tc3_ecp_invalid_price() {
        Product p = new Product(10, "Ceai verde", -1.0, CategorieBautura.TEA, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("ECP")
    @DisplayName("TC4_ECP: Empty Name")
    void tc4_ecp_empty_name() {
        Product p = new Product(10, "", 15.0, CategorieBautura.TEA, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC1_BVA: ID = 0 (Invalid)")
    void tc5_bva1_id_zero() {
        Product p = new Product(0, "Espresso", 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC2_BVA: ID = -1 (Invalid)")
    void tc6_bva2_id_minus_one() {
        Product p = new Product(-1, "Espresso", 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    @DisplayName("TC3_BVA: ID = 1 (Valid)")
    void tc7_bva3_id_one() {
        Product p = new Product(1, "Espresso", 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        service.addProduct(p);
        assertNotNull(service.findById(1));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC4_BVA: Price = 0.0 (Invalid)")
    void tc8_bva4_price_zero() {
        Product p = new Product(10, "Espresso", 0.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC5_BVA: Price = -0.01 (Invalid)")
    void tc9_bva5_price_negative_limit() {
        Product p = new Product(10, "Espresso", -0.01, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC6_BVA: Price = 0.01 (Valid)")
    void tc10_bva6_price_minimum_valid() {
        Product p = new Product(10, "Espresso", 0.01, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        service.addProduct(p);
        assertEquals(0.01, service.findById(10).getPret());
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC7_BVA: Name empty (Invalid)")
    void tc11_bva7_name_empty() {
        Product p = new Product(10, "", 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC8_BVA: Name length 1 (Valid)")
    void tc12_bva8_name_length_one() {
        Product p = new Product(10, "A", 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        service.addProduct(p);
        assertEquals("A", service.findById(10).getNume());
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC9_BVA: Name length 255 (Valid)")
    void tc13_bva9_name_max_length() {
        String longName = "a".repeat(255);
        Product p = new Product(10, longName, 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        service.addProduct(p);
        assertEquals(255, service.findById(10).getNume().length());
    }

    @Test
    @Tag("BVA")
    @DisplayName("TC10_BVA: Name length 256 (Invalid)")
    void tc14_bva10_name_too_long() {
        String tooLongName = "a".repeat(256);
        Product p = new Product(10, tooLongName, 15.0, CategorieBautura.CLASSIC_COFFEE, TipBautura.BASIC);
        assertThrows(RuntimeException.class, () -> service.addProduct(p));
    }
}