package ru.job4j.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.job4j.domain.Person;
import ru.job4j.repository.PersonRepository;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    private static final Logger LOG = LoggerFactory.getLogger(PersonService.class.getName());

    public List<Person> findAll() {
       return personRepository.findAll();
    }

    public Optional<Person> findById(int id) {
        return personRepository.findById(id);
    }

    public Optional<Person> save(Person person) {
        Optional<Person> rsl = Optional.empty();
        try {
            rsl = Optional.of(personRepository.save(person));
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }

    public boolean delete(Person person) {
        if (findById(person.getId()).isPresent()) {
            personRepository.delete(person);
            return true;
        }
        return false;
    }

    public boolean update(Person person) {
        if (findById(person.getId()).isPresent()) {
            personRepository.save(person);
            return true;
        }
        return false;
    }
}
