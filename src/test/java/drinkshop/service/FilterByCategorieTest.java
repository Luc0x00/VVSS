package drinkshop.service;

import drinkshop.domain.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterByCategorieTest {

    private ProductService service;

    @BeforeEach
    void setUp() {
        // service-ul poate fi creat fără repo dacă filterByCategorie nu îl folosește
        service = new ProductService(null);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private Product product(int id, String name, double price, CategorieBautura cat) {
        return new Product(id, name, price, cat, TipBautura.BASIC);
    }

    // ── TC01: products = null → returnează listă goală (SC + Cond01=T + P01) ─

    @Test
    @DisplayName("TC01: products=null → empty list")
    void tc01_null_products() {
        List<Product> result = service.filterByCategorie(null, CategorieBautura.TEA, null, 100.0);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── TC02: products = [] → returnează listă goală (Cond02=T + loop 0 iterații) ─

    @Test
    @DisplayName("TC02: products=[] → empty list")
    void tc02_empty_products() {
        List<Product> result = service.filterByCategorie(new ArrayList<>(), CategorieBautura.TEA, null, 100.0);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── TC03: categorie = null → returnează listă goală (Cond03=T + P02) ────

    @Test
    @DisplayName("TC03: categorie=null → empty list")
    void tc03_null_categorie() {
        List<Product> products = List.of(product(1, "Ceai verde", 10.0, CategorieBautura.TEA));
        List<Product> result = service.filterByCategorie(products, null, null, 100.0);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ── TC04: categorie = ALL, keyword = null → toți produsii sub maxPrice (P03) ─

    @Test
    @DisplayName("TC04: ALL categorie, no keyword → all products under maxPrice")
    void tc04_all_categorie_no_keyword() {
        List<Product> products = List.of(
                product(1, "Espresso",   10.0, CategorieBautura.CLASSIC_COFFEE),
                product(2, "Ceai verde", 20.0, CategorieBautura.TEA)
        );
        List<Product> result = service.filterByCategorie(products, CategorieBautura.ALL, null, 25.0);
        assertEquals(2, result.size());
    }

    // ── TC05: categorie greșită → produsul este sărit (Cond04=T + P04) ──────

    @Test
    @DisplayName("TC05: wrong categorie → product skipped")
    void tc05_wrong_categorie() {
        List<Product> products = List.of(product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE));
        List<Product> result = service.filterByCategorie(products, CategorieBautura.TEA, null, 100.0);
        assertTrue(result.isEmpty());
    }

    // ── TC06: keyword nu se potrivește → produsul este sărit (P05) ───────────

    @Test
    @DisplayName("TC06: keyword mismatch → product skipped")
    void tc06_keyword_mismatch() {
        List<Product> products = List.of(product(1, "Espresso", 10.0, CategorieBautura.CLASSIC_COFFEE));
        List<Product> result = service.filterByCategorie(products, CategorieBautura.CLASSIC_COFFEE, "ceai", 100.0);
        assertTrue(result.isEmpty());
    }

    // ── TC07: preț prea mare → produsul este sărit (P06) ────────────────────

    @Test
    @DisplayName("TC07: price exceeds maxPrice → product skipped")
    void tc07_price_too_high() {
        List<Product> products = List.of(product(1, "Espresso", 50.0, CategorieBautura.CLASSIC_COFFEE));
        List<Product> result = service.filterByCategorie(products, CategorieBautura.CLASSIC_COFFEE, null, 30.0);
        assertTrue(result.isEmpty());
    }

    // ── TC08: produs valid → adăugat în rezultat (P07) ──────────────────────

    @Test
    @DisplayName("TC08: valid product → added to result")
    void tc08_valid_product_added() {
        Product p = product(1, "Espresso", 15.0, CategorieBautura.CLASSIC_COFFEE);
        List<Product> result = service.filterByCategorie(List.of(p), CategorieBautura.CLASSIC_COFFEE, null, 20.0);
        assertEquals(1, result.size());
        assertEquals(p, result.get(0));
    }

    // ── TC09: 3 produse → toate ramurile parcurse (LC) ───────────────────────

    @Test
    @DisplayName("TC09: 3 products → loop coverage with mixed outcomes")
    void tc09_three_products_loop_coverage() {
        List<Product> products = List.of(
                product(1, "Espresso",   10.0, CategorieBautura.CLASSIC_COFFEE), // ✓ trece toate filtrele
                product(2, "Ceai verde", 20.0, CategorieBautura.TEA),            // ✗ categorie greșită
                product(3, "Americano",  50.0, CategorieBautura.CLASSIC_COFFEE)  // ✗ preț prea mare
        );
        List<Product> result = service.filterByCategorie(products, CategorieBautura.CLASSIC_COFFEE, null, 30.0);
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
    }
}