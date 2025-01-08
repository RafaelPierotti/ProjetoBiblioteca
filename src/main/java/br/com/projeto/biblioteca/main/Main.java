package br.com.projeto.biblioteca.main;

import br.com.projeto.biblioteca.log.LogGenerator;
import br.com.projeto.biblioteca.model.*;
import br.com.projeto.biblioteca.repository.*;
import br.com.projeto.biblioteca.service.api.APIConsumer;
import br.com.projeto.biblioteca.service.ConvertData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main{

    private Scanner scanner = new Scanner(System.in);
    private APIConsumer apiConsumer = new APIConsumer();
    private ConvertData convertData = new ConvertData();
    private List<User> users = new ArrayList<>();
    private User loggedUser;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private SellRepository sellRepository;
    @Autowired
    private RentRepository rentRepository;

    private final String URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final String API_KEY = "AIzaSyB0Rsj0E_cuhvXCT9Od36XvONWznBnLqqw";

    public Main(UserRepository userRepository, ClientRepository clientRepository, BookRepository bookRepository, SellRepository sellRepository, RentRepository rentRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
        this.sellRepository = sellRepository;
        this.rentRepository = rentRepository;
    }

    public void displayMenu() throws IOException{
        var menu = """
                1 - Cadastrar usuário
                2 - Cadastrar cliente
                3 - Cadastrar ou adicionar livro 
                4 - Realizar Venda 
                5 - Relizar Locação
                
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
                case 4:
                    sellBook();
                    break;
                case 5:
                    rentBook();
                    break;
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
                this.loggedUser = foundUser;
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
            LogGenerator.generateLog("Cliente " + name + " cadastrado no sistema");
        }
        displayMenu();
    }

    private void registerBook() throws IOException{
        System.out.println("O produto já está cadastrado no sistema? (Sim/Nao/)");
        var option = scanner.nextLine();

        if (option.toLowerCase().contains("si")){
            registerOldBook();
        } else if (option.toLowerCase().contains("na")){
            registerNewBook();
        } else {
            System.out.println("Opção inválida!!");
        }
    }

    private void registerOldBook() throws IOException{
        System.out.println("Informe o nome do livro: ");
        var title = scanner.nextLine();

        List<Book> booksFound =  bookRepository.findByTitleContainingIgnoreCase(title);

        if (!booksFound.isEmpty()){

            booksFound.forEach(b -> System.out.println("ID: " + b.getId() + " Título: " + b.getTitle()));

            System.out.println("Informe o ID exato do livro: ");
            var id = scanner.nextLong();
            scanner.nextLine();

            System.out.println("Informe a quantidade que você quer adicionar: ");
            var quantity = scanner.nextInt();
            scanner.nextLine();

            bookRepository.addBook(id, quantity);
            LogGenerator.generateLog("Adicionado " + quantity + " livros do ID: " + id);
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

            String firsAuthor = authorsArray.getString(0); // pegando somente o primeiro autor do livro

            BookData bookData = new BookData(
                    volumeInfo.getString("title"),
                    volumeInfo.optString("subtitle", ""),
                    firsAuthor,
                    volumeInfo.optString("publisher", ""),
                    volumeInfo.optString("description", "")
            );

            System.out.println("Qual a quantidade de livros do " + title + " que você vai adicionar? ");
            var quantity = scanner.nextInt();

            System.out.println("Qual o valor de venda desse livro? ");
            var price = scanner.nextDouble();


            Book book = new Book(); // convertendo os dados para a minha classe
            book.setTitle(bookData.title());
            book.setSubtitle(bookData.subtitle());
            book.setAuthors(firsAuthor);
            book.setPublisher(bookData.publisher());
            book.setDescription(bookData.description());
            book.setQuantity(quantity);
            book.setPrice(price);

            System.out.println(book);

            bookRepository.save(book);
            System.out.println("Livro registrado com sucesso!");
            LogGenerator.generateLog(book.getTitle() + "adicionado ao sistema");
            displayMenu();
        } else {
            System.out.println("Livro não encontrado!");
            displayMenu();
        }
    }

    private void sellBook() throws IOException{
        System.out.println("Quantos livros serão vendidos?");
        int numLivros = scanner.nextInt();
        scanner.nextLine();

        List<Book> booksToSell = new ArrayList<>(); // Lista de livros vendidos
        int totalQuantity = 0;
        double total = 0;

        for (int i = 0; i < numLivros; i++) {
            System.out.println("Insira o título do livro " + (i + 1));
            var title = scanner.nextLine();

            List<Book> booksFound = bookRepository.findByTitleContainingIgnoreCase(title);

            if (!booksFound.isEmpty()) {
                booksFound.forEach(b -> System.out.println("ID: " + b.getId() + " Título: " + b.getTitle()));

                System.out.println("Informe o ID do livro: ");
                var id = scanner.nextLong();
                scanner.nextLine();

                Optional<Book> idFound = bookRepository.findByIdEquals(id);

                if (idFound.isPresent()) {

                    System.out.println("Informe a quantidade de livros que serão vendidos do livro " + idFound.get().getTitle() + ":");
                    var quantity = scanner.nextInt();
                    scanner.nextLine();

                    if (quantity > idFound.get().getQuantity()) {
                        System.out.println("Quantidade indisponível para o livro: " + idFound.get().getTitle());
                        System.out.println("\nQuantitade atual: " + idFound.get().getQuantity());
                    } else {
                        double subtotal = idFound.get().getPrice() * quantity;
                        total += subtotal;
                        totalQuantity += quantity;

                        bookRepository.sellBook(id, quantity);
                        booksToSell.add(idFound.get());

                        System.out.println("Subtotal para " + idFound.get().getTitle() + ": " + subtotal);
                    }
                } else {
                    System.out.println("Livro não encontrado!");
                    displayMenu();
                }
            } else {
                System.out.println("Livro não encontrado!");
                displayMenu();
            }
        }

        if (!booksToSell.isEmpty()) {
            System.out.println("O total da venda foi de: " + total);
            System.out.println("\nQual será a forma de pagamento?");
            var methods = """
                - Débito
                - Crédito
                - Dinheiro
                - Pix
                """;

            System.out.println(methods);
            var method = scanner.nextLine().trim();

            PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toLowerCase());

            Sell sell = new Sell(total, totalQuantity, paymentMethod, loggedUser, booksToSell);

            bookRepository.saveAll(booksToSell);
            sellRepository.save(sell);

            System.out.println("Venda realizada com sucesso!");
            LogGenerator.generateLog("Venda realizada pelo usuário:  " + loggedUser.getName() + ", Especificações da venda " + sell);
        } else {
            System.out.println("Nenhum livro foi vendido.");
        }

    }

    private void rentBook() throws IOException{
        System.out.println("Insira o nome do cliente que irá realizar a locação: ");
        var client = scanner.nextLine();

        List<Client> clientFound = clientRepository.findByNameContainingIgnoreCase(client);

        if (clientFound.isEmpty()) {
            System.out.println("Cliente não encontrado!!");
            return;
        }
        clientFound.forEach(c -> System.out.println("ID: " + c.getId() + " Nome: " + c.getName()));

        System.out.println("Informe o ID do cliente: ");
        var clientId = scanner.nextLong();
        scanner.nextLine();

        Optional<Client> foundClient = clientRepository.findByIdEquals(clientId);

        if (foundClient.isEmpty()) {
            System.out.println("Cliente não encontrado!");
            return;
        }
        System.out.println("Informe o nome do livro que " + foundClient.get().getName() + " irá realizar a locação (SOMENTE UM TÍTULO POR PESSOA)");
        var book = scanner.nextLine();

        List<Book> bookFound = bookRepository.findByTitleContainingIgnoreCase(book);

        if (bookFound.isEmpty()) {
            System.out.println("Livro não encontrado!");
            return;
        }

        bookFound.forEach(b -> System.out.println("ID: " + b.getId() + " Nome: " + b.getTitle()));

        System.out.println("Informe o ID do livro: ");
        var bookId = scanner.nextLong();
        scanner.nextLine();

        Optional<Book> idFound = bookRepository.findByIdEquals(bookId);

        if (idFound.isPresent()) {


            Rent rent = new Rent(foundClient.get().getName(), loggedUser, book);
            rentRepository.save(rent);
            System.out.println("Locação realizada com sucesso!");
            LogGenerator.generateLog("Locação realizada com sucesso pelo usuário " + loggedUser.getName() + ", Especificações da locação " + rent);

        } else {
            System.out.println("Livro não encontrado!");
            displayMenu();
        }

    }
}
