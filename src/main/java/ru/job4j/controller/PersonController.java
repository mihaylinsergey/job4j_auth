package ru.job4j.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.job4j.domain.Person;
import ru.job4j.dto.PersonDto;
import ru.job4j.service.PersonService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/person")
@AllArgsConstructor
public class PersonController {
    private final PersonService persons;
    private final BCryptPasswordEncoder encoder;
    private final ObjectMapper objectMapper;
    private static final Logger LOGGER = LoggerFactory.getLogger(PersonController.class.getSimpleName());

    @GetMapping("/")
    public List<Person> findAll() {
        return persons.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> findById(@Valid @PathVariable int id) {
        var person = this.persons.findById(id);
        return new ResponseEntity<Person>(
                person.orElse(new Person()),
                person.isPresent() ? HttpStatus.OK : HttpStatus.NOT_FOUND
        );
    }

    @PostMapping("/")
    public ResponseEntity<Person> create(@Valid @RequestBody Person person) throws SQLException {
        validateForNull(person);
        person.setPassword(encoder.encode(person.getPassword()));
        Optional<Person> rsl = persons.save(person);
        if (rsl.isEmpty()) {
            throw new SQLException("Error!");
        }
        return new ResponseEntity<Person>(
                rsl.orElse(new Person()),
                rsl.isPresent() ? HttpStatus.CREATED : HttpStatus.CONFLICT
        );
    }

    @PutMapping("/")
    public ResponseEntity<Person> update(@Valid @RequestBody Person person) {
        validateForNull(person);
        return new ResponseEntity<Person>(person,
                persons.update(person) ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PersonDto> updatePassword(@Valid @RequestBody PersonDto personDto) {
        if (personDto.getId() == 0 || personDto.getPassword() == null) {
            throw new NullPointerException("Number and password mustn't be empty");
        }
        var optionalPerson = persons.findById(personDto.getId());
        if (optionalPerson.isEmpty()) {
            return new ResponseEntity<>(personDto, HttpStatus.NO_CONTENT);
        }
        Person updatePerson = optionalPerson.get();
        updatePerson.setPassword(encoder.encode(personDto.getPassword()));
        persons.update(updatePerson);
        return new ResponseEntity<>(personDto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Person> delete(@PathVariable int id) {
        Person person = new Person();
        person.setId(id);
        return new ResponseEntity<Person>(person,
                persons.delete(person) ? HttpStatus.OK : HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(value = { SQLException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", "This login already exists");
            put("type", e.getClass());
        }}));
        LOGGER.error(e.getLocalizedMessage());
    }

    private void validateForNull(Person person) {
        if (person.getLogin() == null || person.getPassword() == null) {
            throw new NullPointerException("Login and password mustn't be empty");
        }
        if (person.getPassword().length() < 6) {
            throw new IllegalArgumentException("Invalid password");
        }
    }
}