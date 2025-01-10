package br.com.projeto.biblioteca.main;

import br.com.projeto.biblioteca.log.LogGenerator;
import br.com.projeto.biblioteca.model.*;
import br.com.projeto.biblioteca.repository.*;
import br.com.projeto.biblioteca.service.api.APIConsumer;
import br.com.projeto.biblioteca.service.ConvertData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main{

    private Scanner scanner = new Scanner(System.in);
    private APIConsumer apiConsumer = new APIConsumer();
    private ConvertData convertData = new ConvertData();
    private List<User> users = new ArrayList<>();
    private List<Client> clients = new ArrayList<>();
    private List<Book> books = new ArrayList<>();
    private List<Rent> rents = new ArrayList<>();
    private List<Sell> sells = new ArrayList<>();
    private List<Devolution> devolutions = new ArrayList<>();
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
    @Autowired
    private DevolutionRepository devolutionRepository;

    private final String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    private final Double standardFees = 5.0;
    private final String URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final String API_KEY = "AIzaSyB0Rsj0E_cuhvXCT9Od36XvONWznBnLqqw";


    public Main(UserRepository userRepository, ClientRepository clientRepository, BookRepository bookRepository, SellRepository sellRepository, RentRepository rentRepository, DevolutionRepository devolutionRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.bookRepository = bookRepository;
        this.sellRepository = sellRepository;
        this.rentRepository = rentRepository;
        this.devolutionRepository = devolutionRepository;
    }

    public void displayMenu() throws IOException, InterruptedException{
        var menu = """
                1 - Cadastrar usuário
                2 - Cadastrar cliente
                3 - Cadastrar ou adicionar livro 
                4 - Realizar Venda 
                5 - Relizar Locação
                6 - Realizar Devolução
                
                7 - Listar Usuários
                8 - Listar Clientes
                9 - Listar Livros
                10 - Listar Vendas
                11 - Listar Locações
                12 - Listar Devoluções
                
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
                case 6:
                    returnRent();
                    break;
                case 7:
                    listUsers();
                    Thread.sleep(2000);
                    break;
                case 8:
                    listClients();
                    Thread.sleep(2000);
                    break;
                case 9:
                    listBooks();
                    Thread.sleep(2000);
                    break;
                case 10:
                    listSells();
                    Thread.sleep(2000);
                    break;
                case 11:
                    listRents();
                    Thread.sleep(2000);
                    break;
                case 12:
                    listDevolutions();
                    Thread.sleep(2000);
                    break;
                case 0:
                    System.out.println("Saindo...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida!!");
            }

        }
    }

    public void login() throws IOException,  InterruptedException {

       try {
           System.out.println("Informe o seu usuário: ");
           var user = scanner.nextLine();
           System.out.println("Informe a senha do usuário " + user);
           var password = scanner.nextLine();

           Optional<User> userFound = userRepository.findByNameEquals(user);

           if (userFound.isPresent()) {
               User foundUser = userFound.get();
               if (foundUser.getPassword().equals(password)) {
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
       } catch (Exception e){
           LogGenerator.generateLog("Error" + e);
       }
    }

    private void registerUser() throws IOException,  InterruptedException {

        try {
            System.out.println("Informe o nome do usuário: ");
            var userName = scanner.nextLine();
            Optional<User> userFound = userRepository.findByNameEquals(userName);

            if (userFound.isPresent()) {
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
        } catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }
    }

    private void registerClient() throws IOException,  InterruptedException{

        try {
            listClients();
            System.out.println("Informe o CPF do cliente: ");
            var cpf = scanner.nextLine();

            Optional<Client> cpfFound = clientRepository.findByCpfEquals(cpf);

            if (cpfFound.isPresent()) {
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
        } catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }
    }

    private void registerBook() throws IOException, InterruptedException{

        try {

            System.out.println("O produto já está cadastrado no sistema? (Sim/Nao)");
            var option = scanner.nextLine();

            if (option.toLowerCase().contains("si")) {
                registerOldBook();
            } else if (option.toLowerCase().contains("na")) {
                registerNewBook();
            } else {
                System.out.println("Opção inválida!!");
            }
        }catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }
    }

    private void registerOldBook() throws IOException, InterruptedException{

        try {
            listBooks();
            System.out.println("Informe o nome do livro: ");
            var title = scanner.nextLine();

            List<Book> booksFound = bookRepository.findByTitleContainingIgnoreCase(title);

            if (!booksFound.isEmpty()) {

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
        }catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }

    }

    public void registerNewBook() throws IOException, InterruptedException{
        try {

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


                bookRepository.save(book);
                System.out.println("Livro registrado com sucesso!");
                LogGenerator.generateLog(book.getTitle() + "adicionado ao sistema");
                displayMenu();
            } else {
                System.out.println("Livro não encontrado!");
                displayMenu();
            }
        } catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }
    }

    private void sellBook() throws IOException, InterruptedException{
        try {
            System.out.println("Quantos diferentes títulos serão vendidos? ");
            int numBooks = scanner.nextInt();
            scanner.nextLine();

            List<Book> booksSell = new ArrayList<>();
            int totalQuantity = 0;
            double total = 0;

            for (int i = 0; i < numBooks; i++) {
                listBooks();
                System.out.println("Insira o título do livro " + (i + 1));
                var title = scanner.nextLine();

                List<Book> booksFound = bookRepository.findByTitleContainingIgnoreCase(title);

                if (booksFound.isEmpty()) {
                    System.out.println("Livro não encotrado");
                    return;
                }
                booksFound.forEach(b -> System.out.println("ID: " + b.getId() + " Título: " + b.getTitle()));

                System.out.println("Informe o ID do livro: ");
                var id = scanner.nextLong();
                scanner.nextLine();

                Optional<Book> idFound = bookRepository.findByIdEquals(id);

                if (idFound.isEmpty()) {
                    System.out.println("Livro não encontrado");
                    return;
                }

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
                    booksSell.add(idFound.get());

                    System.out.println("Subtotal para " + idFound.get().getTitle() + ": " + subtotal);
                }
            }
            if (!booksSell.isEmpty()) {
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

                PaymentMethod paymentMethod = PaymentMethod.correctWriting(method.toLowerCase());

                Sell sell = new Sell(total, totalQuantity, paymentMethod, loggedUser, booksSell);

                sellRepository.save(sell);

                System.out.println("Venda realizada com sucesso!");
                LogGenerator.generateLog("Venda realizada pelo usuário:  " + loggedUser.getName() + ", Especificações da venda " + sell);
            } else {
                System.out.println("Nenhum livro foi vendido.");
            }
        } catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }

    }

    private void rentBook() throws IOException, InterruptedException{
        try {
            listClients();
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

            System.out.println("Quantos diferentes títulos serão alugados? ");
            int numBooks = scanner.nextInt();
            scanner.nextLine();

            List<Book> booksRent = new ArrayList<>();

            for (int i = 0; i < numBooks; i++) {
                listBooks();
                System.out.println("Insira o título do livro " + (i + 1));
                var title = scanner.nextLine();

                List<Book> bookFound = bookRepository.findByTitleContainingIgnoreCase(title);

                if (bookFound.isEmpty()) {
                    System.out.println("Livro não encontrado!");
                    return;
                }


                bookFound.forEach(b -> System.out.println("ID: " + b.getId() + " Nome: " + b.getTitle()));

                System.out.println("Informe o ID do livro: ");
                var bookId = scanner.nextLong();
                scanner.nextLine();

                Optional<Book> idFound = bookRepository.findByIdEquals(bookId);

                if (idFound.isEmpty()) {
                    System.out.println("Livro não encontrado!");
                    return;
                }

                if (1 > idFound.get().getQuantity()) {
                    System.out.println("Quantidade indisponível para o livro: " + idFound.get().getTitle());
                    System.out.println("\nQuantitade atual: " + idFound.get().getQuantity());
                } else {
                    bookRepository.rentBook(bookId);
                    booksRent.add(idFound.get());
                }
            }
            if (!booksRent.isEmpty()) {

                Rent rent = new Rent(foundClient.get(), loggedUser, booksRent, dateNow);
                rentRepository.save(rent);
                System.out.println("Locação realizada com sucesso!");

                LogGenerator.generateLog("Locação realizada com sucesso pelo usuário " + loggedUser.getName() + ", Especificações da locação " + rent);

            } else {
                System.out.println("Livro não encontrado!");
                displayMenu();
            }
        } catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }
    }

    @Transactional
    private void returnRent() throws IOException, InterruptedException{
        try {
            Double totalFees = 0.0;
            String dateRent = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            listRents();

            System.out.println("Informe o ID da locação: ");
            var rentId = scanner.nextLong();
            scanner.nextLine();

            Optional<Rent> rentFound = rentRepository.findByIdEquals(rentId);

            if (rentFound.isPresent()) {
                Rent rent = rentFound.get();
                System.out.println("Cliente atrasou a devolução? (SIM/NÃO)");
                var answer = scanner.nextLine().toLowerCase();

                if (answer.contains("si")) {
                    System.out.println("Quantos dias? ");
                    var days = scanner.nextInt();
                    scanner.nextLine();
                    totalFees = days * standardFees;
                    System.out.println("Será necessário pagar uma taxas de: " + totalFees);
                } else if (!answer.contains("na")) {
                    System.out.println("Opção inválida");
                    returnRent();
                    return;
                }


                Devolution devolution = new Devolution(loggedUser, rent, dateNow, totalFees);
                List<Long> booksIds = rentRepository.findBookIdsByRentId(rentId);

                booksIds.forEach(b -> bookRepository.devolutionBook(b));

                devolutionRepository.save(devolution);

                System.out.println("Devolução com sucesso!");
                LogGenerator.generateLog("Devolvido com sucesso: " + devolution);
            } else {
                System.out.println("Locação não encontrada!");
                displayMenu();
            }
        } catch (Exception e){
            LogGenerator.generateLog("Error" + e);
        }
    }

    private void listClients(){
        clients = clientRepository.findAll();

        clients.stream()
                .sorted(Comparator.comparing(Client::getId))
                .forEach(c -> System.out.println("ID: " + c.getId() + " CPF: " + c.getCpf() + " Nome: " + c.getName() + " Número de Telefone: " + c.getPhoneNumber()));
    }

    private void listBooks(){
        books = bookRepository.findAll();

        books.stream()
                .sorted(Comparator.comparing(Book::getId))
                .forEach(b -> System.out.println("ID: " + b.getId() + " Título: " + b.getTitle() + " Subtítulo: " + b.getSubtitle() + " Autores: " + b.getAuthors() + " Editora: " + b.getPublisher() + " Quantidade: " + b.getQuantity()));
    }

    private void listSells(){
        sells = sellRepository.findAll();

        sells.stream()
                .sorted(Comparator.comparing(Sell::getId))
                .forEach(s -> System.out.println("ID: " + s.getId() + " Livros: " + s.getBooks() + " Quantidade: " + s.getQuantity() + " Total: R$" + s.getTotal() + " Método de pagamento: " + s.getPaymentMethod() + " Usuário: " + s.getUser()));
    }

    private void listRents(){
        rents = rentRepository.findAll();

        rents.stream()
                .sorted(Comparator.comparing(Rent::getId))
                .forEach(r -> System.out.println("ID: " + r.getId() + " Livros: " + r.getBooks() + " Usuário: " + r.getUser() + " Cliente: " + r.getClient() + " Data: " + r.getRentDate()));
    }

    private void listUsers(){
        users = userRepository.findAll();

        users.stream()
                .sorted(Comparator.comparing(User::getId))
                .forEach(u -> System.out.println("ID: " + u.getId() + " Nome do usuário: " + u.getName()));
    }

    private void listDevolutions(){
        devolutions = devolutionRepository.findAll();

        devolutions.stream()
                .sorted(Comparator.comparing(Devolution::getId))
                .forEach(d -> System.out.println("ID: " + d.getId() + " Usuário: " + d.getUser() + " Locação: " + d.getRent() + " Juros totais: " + d.getTotalFees() + " Data de devolução: " + d.getDevolutionDate()));
    }
}
