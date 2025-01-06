package br.com.projeto.biblioteca.main;

import br.com.projeto.biblioteca.model.Book;
import br.com.projeto.biblioteca.model.BookData;
import br.com.projeto.biblioteca.model.Client;
import br.com.projeto.biblioteca.model.User;
import br.com.projeto.biblioteca.repository.BookRepository;
import br.com.projeto.biblioteca.repository.ClientRepository;
import br.com.projeto.biblioteca.repository.UserRepository;
import br.com.projeto.biblioteca.service.APIConsumer;
import br.com.projeto.biblioteca.service.ConvertData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private Scanner scanner = new Scanner(System.in);
    private APIConsumer apiConsumer = new APIConsumer();
    private ConvertData convertData = new ConvertData();
    private List<User> users = new ArrayList<>();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ClientRepository clientRepository;

    private final String URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final String API_KEY = "AIzaSyB0Rsj0E_cuhvXCT9Od36XvONWznBnLqqw";

    public Main(UserRepository userRepository, ClientRepository clientRepository, BookRepository bookRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
    }

    public void displayMenu(){
        var menu = """
                1 - Cadastrar usuário
                2 - Cadastrar cliente
                3 - Cadastrar livro
                4 - Realizar Venda ou Aluguem do livro
                
                0 - Sair
                """;

        var option = -1;

        while (option != 0){
            System.out.println(menu);
            option = scanner.nextInt();
            scanner.nextLine();

            switch (option){
                case 1:
                    registerUser();
                    break;
                case 2:
                    registerClient();
                    break;
                case 3:
                    registerBook();
                    break;
//                case 4:
//                    sell();
//                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida!!");
            }
        }
    }

    public void login(){
        System.out.println("Informe o seu usuário: ");
        var user = scanner.nextLine();
        System.out.println("Informe a senha do usuário " + user);
        var password = scanner.nextLine();

        Optional<User> userFound = userRepository.findByNameEquals(user);

        if (userFound.isPresent()){
            User foundUser = userFound.get();
            if (foundUser.getPassword().equals(password)){
                System.out.println("Login realizado com o sucesso!");
                displayMenu();
            } else {
                System.out.println("Usuário ou senha inválidos!");
            }
        }
    }

    private void registerUser(){

        System.out.println("Informe o nome do usuário: ");
        var userName = scanner.nextLine();
        Optional<User> userFound = userRepository.findByNameEquals(userName);

        if (userFound.isPresent()){
            System.out.println("Usuário com o LOGIN " + userName + " já existente no sistema!");
        } else {
            System.out.println("Informe a senha do usuário: ");
            var password = scanner.nextLine();

            User newUser = new User(userName, password);

            userRepository.save(newUser);
            System.out.println("Usuário cadastrado com sucesso!");
        }
        displayMenu();
    }

    private void registerClient(){
        System.out.println("Informe o CPF do cliente: ");
        var cpf = scanner.nextLine();

        Optional<Client> cpfFound = clientRepository.findByCpfEquals(cpf);

        if (cpfFound.isPresent()){
            System.out.println("CPF já cadastrado no sistema!");
        } else {
            System.out.println("Informe o nome completo do cliente: ");
            var name = scanner.nextLine();

            System.out.println("Informe o número de telefone do cliente: ");
            var phoneNumber = scanner.nextLine();

            Client newClient = new Client(name, phoneNumber, cpf);

            clientRepository.save(newClient);

            System.out.println("Cliente cadastrado no sistema!");
        }
        displayMenu();
    }

    private void registerBook(){
        System.out.println("O produto já está cadastrado no sistema? (Sim/Nao/Nao sei)");
        var option = scanner.nextLine();

        if (option.toLowerCase().contains("si")){
            registerOldBook();
        } else if (option.toLowerCase().contains("na")){
            registerNewBook();
        } else if (option.toLowerCase().contains("naos") || option.toLowerCase().contains("nao s")){

        } else {
            System.out.println("Opção inválida!!");
        }
    }

    private void registerOldBook(){
        System.out.println("Informe o nome do livro: ");
        var title = scanner.nextLine();

        bookRepository.findByTitleContainingIgnoreCase(title);

        System.out.println("Informe o ID exato do livro: ");
        var id = scanner.nextLong();

        System.out.println("Informe a quantidade que você quer adicionar: ");
        var quantity = scanner.nextInt();

        bookRepository.updateQuantity(id, quantity);
    }

    public void registerNewBook(){
        System.out.println("Informe o nome do livro: ");
        var title = scanner.nextLine();

        var json = apiConsumer.getData(URL + title.replace(" ", "+") + "&key=" + API_KEY);
        List<BookData> books = convertData.getList(json, BookData.class);

        books.forEach(System.out::println);
    }
}
