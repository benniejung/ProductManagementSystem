package kr.ac.hansung.cse.productmanagementsystem.controller;

import kr.ac.hansung.cse.productmanagementsystem.entity.Product;
import kr.ac.hansung.cse.productmanagementsystem.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.text.DecimalFormat;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Configure for Double values
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
        decimalFormat.setGroupingUsed(false);
        binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, decimalFormat, true));

        // Set required fields to ensure validation is triggered
        binder.setRequiredFields("name", "brand", "madeIn", "price");
    }

    @GetMapping({"", "/"}) // products 또는 /products/ 둘 다 매핑
    public String viewHomePage(Model model) {

        List<Product> listProducts = service.listAll();
        model.addAttribute("listProducts", listProducts);

        return "index";
    }

    @GetMapping("/new")
    public String showNewProductPage(Model model) {

        Product product = new Product();
        model.addAttribute("product", product);

        return "new_product";
    }

    @GetMapping("/edit/{id}")
    public String showEditProductPage(@PathVariable(name = "id") Long id, Model model) {

        Product product = service.get(id);
        model.addAttribute("product", product);

        return "edit_product";
    }

    // @ModelAttribute는  Form data (예: name=Laptop&brand=Samsung&madeIn=Korea&price=1000.00)를 Product 객체
    // @RequestBody는 HTTP 요청 본문에 포함된
    //  JSON 데이터(예: {"name": "Laptop", "brand": "Samsung", "madeIn": "Korea", "price": 1000.00})를 Product 객체에 매핑
    @PostMapping("/save")
    public String saveProduct(@Valid @ModelAttribute("product") Product product, BindingResult bindingResult, Model model) {
        // Log the product data
        System.out.println("Product data: " + product);
        System.out.println("Name: " + product.getName() + ", Brand: " + product.getBrand() + 
                          ", MadeIn: " + product.getMadeIn() + ", Price: " + product.getPrice());

        if (product.getPrice() != null) {
            System.out.println("Price type: " + product.getPrice().getClass().getName());
        } else {
            System.out.println("Price is null");
        }

        if (bindingResult.hasErrors()) {
            // Log validation errors in detail
            System.out.println("Validation errors found: " + bindingResult.getErrorCount());
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println("Error: " + error.getDefaultMessage());
                System.out.println("Field: " + (error.getCode() != null ? error.getCode() : "Unknown"));
            });

            // Check for specific field errors
            if (bindingResult.hasFieldErrors("name")) {
                System.out.println("Name validation failed: " + bindingResult.getFieldError("name").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("brand")) {
                System.out.println("Brand validation failed: " + bindingResult.getFieldError("brand").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("madeIn")) {
                System.out.println("MadeIn validation failed: " + bindingResult.getFieldError("madeIn").getDefaultMessage());
            }
            if (bindingResult.hasFieldErrors("price")) {
                System.out.println("Price validation failed: " + bindingResult.getFieldError("price").getDefaultMessage());
            }

            // Add the errors to the model for debugging
            model.addAttribute("errors", bindingResult.getAllErrors());
            // If there are validation errors, return to the form
            return product.getId() == null ? "new_product" : "edit_product";
        }

        service.save(product);

        return "redirect:/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable(name = "id") Long id) {

        service.delete(id);
        return "redirect:/products";
    }
}
