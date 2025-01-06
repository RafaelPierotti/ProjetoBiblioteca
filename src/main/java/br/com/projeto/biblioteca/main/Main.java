package br.com.projeto.biblioteca.main;

import br.com.projeto.biblioteca.model.Book;
import br.com.projeto.biblioteca.model.BookData;
import br.com.projeto.biblioteca.model.User;
import br.com.projeto.biblioteca.repository.BookRepository;
import br.com.projeto.biblioteca.repository.UserRepository;
import br.com.projeto.biblioteca.service.APIConsumer;
import br.com.projeto.biblioteca.service.ConvertData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
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

    private final String URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private final String API_KEY = "AIzaSyB0Rsj0E_cuhvXCT9Od36XvONWznBnLqqw";

    public Main(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void displayMenu(){
        var menu = """
                1 - Cadastrar usuário
                2 - Cadastrar Livro
                3 - Cadastrar Cliente
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
                    registerBook();
                    break;
//                case 3:
//                    registerClient;
//                    break;
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

        for (User u: users){
            if (u.getName().equals(user) && u.getPassword().equals(password)){
                System.out.println("Acessando...");
                displayMenu();
            } else {
                System.out.println("Usuário ou senha incorreto!!");
            }
        }

    }

    private void registerUser(){
        System.out.println("Informe o nome do usuário: ");
        var userName = scanner.nextLine();
        System.out.println("Informe a senha do usuário: ");
        var password = scanner.nextLine();

        User user = new User(userName, password);

        userRepository.save(user);
    }

    private void registerBook(){
        System.out.println("O produto já está cadastrado no sistema? (Sim/Nao/Nao sei)");
        var option = scanner.nextLine();

        if (option.toLowerCase().contains("si")){
            registerOldBook();
        } else if (option.toLowerCase().contains("na")){

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
