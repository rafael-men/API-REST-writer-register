package net.rafael.api_library.main_project.Controllers;


import net.rafael.api_library.main_project.Dto.AuthorDTO;
import net.rafael.api_library.main_project.Models.Author;
import net.rafael.api_library.main_project.Services.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/author")
public class AuthorController {

    private final AuthorService service;

    public AuthorController(AuthorService service) {
        this.service = service;
    }

    @PostMapping("/new")
    public ResponseEntity<?> saveAuthor(@RequestBody  AuthorDTO author) {

        Author authorEntity = author.mapForAuthor();
        service.save(authorEntity);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("id")
                .buildAndExpand(authorEntity.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDTO> acquireDetails(@PathVariable("id") String id) {
        var AuthorId = UUID.fromString(id);
        Optional<Author> author = service.findById(AuthorId);
        if(author.isPresent()) {
            Author entity = author.get();
            AuthorDTO dto = new AuthorDTO(author.get().getId(), author.get().getName(),author.get().getBirth_date(),author.get().getFrom());
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAuthorById(@PathVariable("id") String id) {
        var idAuthor = UUID.fromString(id);
        Optional<Author> author = service.findById(idAuthor);
        if(author.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteAuthor(author.get());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filtersearch")
    public ResponseEntity<List<AuthorDTO>>  searchAuthorWithFilters(@RequestParam(value = "name",required = false) String name,@RequestParam(value = "from",required = false) String from) {
        List<Author> result = service.searchWithFilters(name, from);
        List<AuthorDTO> list = result.stream().map(author -> new AuthorDTO(author.getId(), author.getName(), author.getBirth_date(),author.getFrom())).collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateAuthor(@PathVariable("id") String id, @RequestBody() AuthorDTO dto) {
        var idAuthor = UUID.fromString(id);
        Optional<Author> author = service.findById(idAuthor);
        if(author.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var authortwo = author.get();
        authortwo.setName(dto.name());
        authortwo.setFrom(dto.from());
        authortwo.setBirth_date(dto.birth_date());

        service.updateAuthor(authortwo);
        return ResponseEntity.noContent().build();
    }

}
