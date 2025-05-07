package br.com.erudio.api.controller;

import br.com.erudio.api.proxy.CambioProxy;
import br.com.erudio.domain.model.Book;
import br.com.erudio.domain.repository.BookRepository;
import br.com.erudio.domain.response.Cambio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Tag(name = "Book Endpoint")
@RestController
@RequestMapping("/book-service")
public class BookController {

    @Autowired
    private BookRepository repository;

    @Autowired
    private Environment environment;

    @Autowired
    private CambioProxy proxy;

    @Operation(summary = "Find a specific Book by your ID.")
    @GetMapping("/{id}/{currency}")
    public Book findBook(@PathVariable Long id, @PathVariable String currency) {
        var port = this.environment.getProperty("local.server.port");
        Book book = repository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));

        Cambio cambio = this.proxy.getCambio(book.getPrice(), "USD", currency);

        book.setEnvironment("Book port:" + port + " Cambio port:" + cambio.getEnvironment());
        book.setPrice(cambio.getConvertedValue());

        return book;
    }

//    @GetMapping("/{id}/{currency}")
//    public Book findBook(@PathVariable Long id, @PathVariable String currency) {
//        var port = this.environment.getProperty("local.server.port");
//        Book book = repository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
//
//        HashMap<String, String> params = new HashMap<>();
//        params.put("amount", book.getPrice().toString());
//        params.put("from", "USD");
//        params.put("to", currency);
//        var response = new RestTemplate().getForEntity("http://localhost:8000/cambio-service/{amount}/{from}/{to}", Cambio.class, params);
//        Cambio cambio = response.getBody();
//
//        book.setEnvironment(port);
//        book.setPrice(cambio.getConvertedValue());
//
//        return book;
//    }


}
