package application;

import entities.Funcionario;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    private final List<Funcionario> funcionarios;
    private final Map<String, List<Funcionario>> funcionariosMap;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"));
    private static final Month OUTUBRO = Month.OCTOBER;
    private static final Month DEZEMBRO = Month.DECEMBER;

    public Main() {
        funcionarios = new ArrayList<>();
        funcionariosMap = new TreeMap<>();
    }

    public void adicionarFuncionario(Funcionario funcionario) {
        funcionarios.add(funcionario);
    }

    public boolean existeFuncionario() {
        return !funcionarios.isEmpty();
    }

    public void inserirFuncionarios(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String item = br.readLine();

            while (item != null) {
                String[] fields = item.split(",");
                String nome = fields[0];
                String dataNascimentoCsv = fields[1];
                LocalDate dataNascimento = LocalDate.parse(dataNascimentoCsv, DATE_TIME_FORMATTER);
                BigDecimal salario = new BigDecimal(fields[2]);
                String funcao = fields[3];
                Funcionario funcionario = new Funcionario(nome, dataNascimento, salario, funcao);
                adicionarFuncionario(funcionario);
                item = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ERROR ao ler o arquivo " + e.getMessage());
        }
    }

    public void removerFuncionarioPorNome(String nome) {
        funcionarios.removeIf(f -> f.getNome().equalsIgnoreCase(nome));
    }

    public void listarFuncionarios() {
        funcionarios.forEach(funcionario -> System.out.println(funcionario));
    }

    public void aumentarSalario(double porcentagem) {
        porcentagem = porcentagem / 100;
        BigDecimal porcentagemBigDecimal = new BigDecimal(porcentagem);
        if (existeFuncionario()) {
            funcionarios.forEach(f -> f.setSalario(f.getSalario().add(f.getSalario().multiply(porcentagemBigDecimal))));
        }
    }

    public void agruparFuncionariosFuncaoMap() {
        funcionarios.forEach(f -> {
            String funcao = f.getFuncao();
            if (!funcionariosMap.containsKey(funcao)) {
                funcionariosMap.put(funcao, new ArrayList<>());
            }
            funcionariosMap.get(funcao).add(f);
        });
    }

    public void imprimirFuncionariosFuncaoMap() {
        funcionariosMap.forEach((funcao, funcionarios) -> {
            System.out.println("Função: " + funcao);
            funcionarios.forEach(f -> System.out.println("  " + f));
        });
    }

    public void imprimirFuncionariosAniversario() {
        funcionarios.forEach(f -> {
            if (f.getDataNascimento().getMonth().equals(OUTUBRO) || f.getDataNascimento().getMonth().equals(DEZEMBRO)) {
                System.out.println(f);
            }
        });
    }

    public void imprimirFuncionarioMaiorIdade() {
        //nao utilizei lambda pois o escopo das duas proximas variaveis teriam que ser static;
        LocalDate maisVelho = LocalDate.MAX;
        Funcionario funcionarioMaisVelho = null;

        for (Funcionario f : funcionarios) {
            LocalDate dataNascimento = f.getDataNascimento();
            if (dataNascimento.isBefore(maisVelho)) {
                maisVelho = dataNascimento;
                funcionarioMaisVelho = f;
            }
        }

        if (existeFuncionario()) {
            LocalDate hoje = LocalDate.now();
            int idade = hoje.getYear() - maisVelho.getYear();
            if (hoje.getMonthValue() < maisVelho.getMonthValue() || (hoje.getDayOfMonth() < maisVelho.getDayOfMonth() || hoje.getDayOfMonth() == maisVelho.getDayOfMonth())) {
                idade--;
            }
            System.out.println("Nome: " + funcionarioMaisVelho.getNome() + " | " + "Idade: " + idade);
        }
    }

    public void imprimirFuncionariosOrdemAlfabetica() {
        funcionarios.sort((f1, f2) -> f1.getNome().compareTo(f2.getNome()));
        funcionarios.forEach(f -> System.out.println(f));
    }

    public BigDecimal somaTotal() {
        //Nao utilizado expressao lambda pelo mesmo motivo do metodo imprimirFuncionarioMaiorIdade
        BigDecimal soma = BigDecimal.ZERO;
        for (Funcionario f : funcionarios) {
            soma = soma.add(f.getSalario());
        }
        return soma;
    }

    public void imprimirQuantosSalariosMinimos() {
        double salarioMinimo = 1212.00;
        funcionarios.forEach(f -> {
            double qtdSalariosMinimos = f.getSalario().doubleValue() / salarioMinimo;
            System.out.print(f.getNome() + " recebe " + String.format("%.2f", qtdSalariosMinimos) + " salários mínimos.\n");
        });
    }

    public static void main(String[] args) {
        Main main = new Main();

        //3.1
        String path = "/home/arthur/Documentos/testeIniflex/src/funcs.csv";
        main.inserirFuncionarios(path);

        //3.2
        main.removerFuncionarioPorNome("João");


        //3.3
        System.out.println("=============================LISTANDO FUNCIONARIOS=============================");
        main.listarFuncionarios();


//        //3.4
        System.out.println("=============================AUMENTANDO SALARIO 10%=============================");
        main.aumentarSalario(10);
        main.listarFuncionarios();

        //3.5
        main.agruparFuncionariosFuncaoMap();

//        3.6
        System.out.println("=============================FUNCIONARIOS POR FUNCAO=============================");
        main.imprimirFuncionariosFuncaoMap();

        //3.8 (3.7)
        System.out.println("====================Fazem aniversario no mês de OUTUBRO ou DEZEMBRO===============");
        main.imprimirFuncionariosAniversario();

        //3.9 (3.8)
        System.out.println("=============================FUNCIONARIO MAIS VELHO==============================");
        main.imprimirFuncionarioMaiorIdade();

        //3.10 (3.9)
        System.out.println("=================================ORDEM ALFABETICA================================");
        main.imprimirFuncionariosOrdemAlfabetica();


        //3.11 (3.10)
        System.out.println("==========================SALARIO TOTAL DOS FUNCIONARIOS=========================");
        System.out.println(NUMBER_FORMAT.format(main.somaTotal()));

        //3.12 (3.11)
        System.out.println("=============================SALARIOS MINIMOS=============================");
        main.imprimirQuantosSalariosMinimos();
    }


}



