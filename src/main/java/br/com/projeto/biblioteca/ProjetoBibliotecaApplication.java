package br.com.projeto.biblioteca;

import br.com.projeto.biblioteca.main.Main;
import br.com.projeto.biblioteca.model.Book;
import br.com.projeto.biblioteca.model.User;
import br.com.projeto.biblioteca.repository.BookRepository;
import br.com.projeto.biblioteca.repository.ClientRepository;
import br.com.projeto.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootApplication
public class ProjetoBibliotecaApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private BookRepository bookRepository;

	public static void main(String[] args) {
		SpringApplication.run(ProjetoBibliotecaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		User userAdmin = new User();

		try {
			userAdmin.setName("admin");
			userAdmin.setPassword("admin");
			userAdmin.setId(1L);
			userRepository.save(userAdmin);
		} catch (DataIntegrityViolationException e){}

		Main main = new Main(userRepository, clientRepository, bookRepository);
		main.login();
	}
}
