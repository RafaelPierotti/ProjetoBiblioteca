package br.com.projeto.biblioteca.main;

import br.com.projeto.biblioteca.log.LogGenerator;
import br.com.projeto.biblioteca.model.Book;
import br.com.projeto.biblioteca.model.BookData;
import br.com.projeto.biblioteca.model.Client;
import br.com.projeto.biblioteca.model.User;
import br.com.projeto.biblioteca.repository.BookRepository;
import br.com.projeto.biblioteca.repository.ClientRepository;
import br.com.projeto.biblioteca.repository.UserRepository;
import br.com.projeto.biblioteca.service.APIConsumer;
import br.com.projeto.biblioteca.service.ConvertData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main{

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

    public void displayMenu() throws IOException{
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

    public void login() throws IOException {
        System.out.println("Informe o seu usuário: ");
        var user = scanner.nextLine();
        System.out.println("Informe a senha do usuário " + user);
        var password = scanner.nextLine();

        Optional<User> userFound = userRepository.findByNameEquals(user);

        if (userFound.isPresent()){
            User foundUser = userFound.get();
            if (foundUser.getPassword().equals(password)){
                System.out.println("Login realizado com o sucesso!");
                LogGenerator.generateLog("Login do usuário " + user + " realizado");
                displayMenu();
            } else {
                System.out.println("Usuário ou senha inválidos!");
                LogGenerator.generateLog("Tentativa de login");
                login();
            }
        }
    }

    private void registerUser() throws IOException {

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
            LogGenerator.generateLog("Usuário " + userName + " cadastrado no sistema");
        }
        displayMenu();
    }

    private void registerClient() throws IOException{
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

    private void registerBook() throws IOException{
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

    private void registerOldBook() throws IOException{
        System.out.println("Informe o nome do livro: ");
        var title = scanner.nextLine();

        Optional<Book> bookFound =  bookRepository.findByTitleContainingIgnoreCase(title);

        if (bookFound.isPresent()){

            System.out.println(bookFound.get().getId() +  bookFound.get().getTitle());

            System.out.println("Informe o ID exato do livro: ");
            var id = scanner.nextLong();

            System.out.println("Informe a quantidade que você quer adicionar: ");
            var quantity = scanner.nextInt();

            bookRepository.updateQuantity(id, quantity);
        } else {
            System.out.println("Livro não encontrado");
        }

    }

    public void registerNewBook() throws IOException{
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("Informe o nome do livro: ");
        var title = scanner.nextLine();

        var json = apiConsumer.getData(URL + title.replace(" ", "+") + "&key=" + API_KEY);

        JSONObject jsonObject = new JSONObject(json);
        if (jsonObject.has("items") && jsonObject.getJSONArray("items").length() > 0) {
            JSONObject volumeInfo = jsonObject.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo");

            JSONArray authorsArray = volumeInfo.getJSONArray("authors");
//            List<String> authors = new ArrayList<>();
//            for (int i = 0; i < authorsArray.length(); i++) {
//                authors.add(authorsArray.getString(i));
//            }

            String firsAuthor = authorsArray.getString(0);

            BookData bookData = new BookData(
                    volumeInfo.getString("title"),
                    volumeInfo.optString("subtitle", ""),
                    firsAuthor,
                    volumeInfo.optString("publisher", ""),
                    volumeInfo.optString("description", "")
            );

            System.out.println("Qual a quantidade de livros do " + title + " que você vai adicionar? ");
            var quantity = scanner.nextInt();


            Book book = new Book();
            book.setTitle(bookData.title());
            book.setSubtitle(bookData.subtitle());
            book.setAuthors(firsAuthor);
            book.setPublisher(bookData.publisher());
            book.setDescription(bookData.description());
            book.setQuantity(quantity);

            System.out.println(book);

            bookRepository.save(book);
            System.out.println("Livro registrado com sucesso!");
            displayMenu();
        } else {
            System.out.println("Livro não encontrado!");
            displayMenu();
        }

    }
}
