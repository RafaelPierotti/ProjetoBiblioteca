package br.com.projeto.biblioteca;

import br.com.projeto.biblioteca.main.Main;
import br.com.projeto.biblioteca.model.User;
import br.com.projeto.biblioteca.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

@SpringBootApplication
@EnableJpaRepositories
public class ProjetoBibliotecaApplication implements CommandLineRunner {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private SellRepository sellRepository;
	@Autowired
	private RentRepository rentRepository;
	@Autowired
	private DevolutionRepository devolutionRepository;


	public static void main(String[] args) {
		SpringApplication.run(ProjetoBibliotecaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Main main = new Main(userRepository, clientRepository, bookRepository, sellRepository, rentRepository, devolutionRepository);


		var userFound = userRepository.findByNameEquals("admin");

		if (userFound.isEmpty()){
			User userAdmin = new User(); // criando usuário padrão do sistema
			userAdmin.setName("admin");
			userAdmin.setPassword("admin");
			userRepository.save(userAdmin);
			main.login();
		} else {
			main.login();
		}

	}
}
