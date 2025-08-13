package com.solutec.desarrollo_web_server.controller;

import com.solutec.desarrollo_web_server.model.Person;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
@CrossOrigin(origins = "*")
public class PersonController{

    // Datos quemados - lista de personas
    private List<Person> personas = new ArrayList<Person>() {{
        add(new Person(1L, "Juan", "Pérez"));
        add(new Person(2L, "María", "García"));
        add(new Person(3L, "Carlos", "López"));
        add(new Person(4L, "Ana", "Martínez"));
        add(new Person(5L, "Luis", "Rodríguez"));
        add(new Person(6L, "Carmen", "Fernández"));
        add(new Person(7L, "Pedro", "González"));
        add(new Person(8L, "Laura", "Sánchez"));
        add(new Person(9L, "Miguel", "Díaz"));
        add(new Person(10L, "Sofía", "Ruiz"));
    }};

    // GET - Obtener todas las personas
    @GetMapping
    public List<Person> getAllPersonas() {
        return personas;
    }

    // GET - Obtener persona por ID
    @GetMapping("/{id}")
    public Person getPersonaById(@PathVariable Long id) {
        return personas.stream()
                .filter(person -> person.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // POST - Agregar nueva persona
    @PostMapping
    public Person addPersona(@RequestBody Person newPerson) {
        // Generar nuevo ID
        Long newId = personas.stream()
                .mapToLong(Person::getId)
                .max()
                .orElse(0L) + 1;
        
        newPerson.setId(newId);
        personas.add(newPerson);
        return newPerson;
    }

    // PUT - Actualizar persona existente
    @PutMapping("/{id}")
    public Person updatePersona(@PathVariable Long id, @RequestBody Person updatedPerson) {
        for (int i = 0; i < personas.size(); i++) {
            Person person = personas.get(i);
            if (person.getId().equals(id)) {
                updatedPerson.setId(id);
                personas.set(i, updatedPerson);
                return updatedPerson;
            }
        }
        return null;
    }

    // DELETE - Eliminar persona
    @DeleteMapping("/{id}")
    public boolean deletePersona(@PathVariable Long id) {
        return personas.removeIf(person -> person.getId().equals(id));
    }

    // GET - Buscar por nombre
    @GetMapping("/buscar")
    public List<Person> buscarPorNombre(@RequestParam String nombre) {
        return personas.stream()
                .filter(person -> person.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .toList();
    }
}