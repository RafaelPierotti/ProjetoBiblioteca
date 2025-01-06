package br.com.projeto.biblioteca;

import br.com.projeto.biblioteca.main.Main;
import br.com.projeto.biblioteca.model.User;
import br.com.projeto.biblioteca.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProjetoBibliotecaApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(ProjetoBibliotecaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
//		User userAdmin = new User();
//		userAdmin.setName("admin");
//		userAdmin.setPassword("admin");
//
//		userRepository.save(userAdmin);

		Main main = new Main(userRepository);
		main.registerNewBook();
	}
}
