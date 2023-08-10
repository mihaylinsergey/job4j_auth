package ru.job4j.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Person;
import ru.job4j.service.PersonService;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private final PersonService persons;
    private BCryptPasswordEncoder encoder;

    @GetMapping("/")
    public List<Person> findAll() {
        return persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<Person>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@RequestBody Person person) {
        Optional<Person> rsl = persons.save(person);
        person.setPassword(encoder.encode(person.getPassword()));
        return new ResponseEntity<Person>(
                rsl.orElse(new Person()),
                rsl.isPresent() ? HttpStatus.CREATED : HttpStatus.CONFLICT
        );
    }

    @PutMapping("/")
    public ResponseEntity<Person> update(@RequestBody Person person) {
        return new ResponseEntity<Person>(person,
                persons.update(person) ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Person> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        return new ResponseEntity<Person>(person,
                persons.delete(person) ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }
}