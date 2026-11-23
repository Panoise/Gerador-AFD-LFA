import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Caminho do arquivo (Enter para 'entrada.txt'): ");
        String caminho = sc.nextLine().trim();
        if (caminho.isEmpty()) {
            caminho = "entrada.txt";
        }

        try {
            AFND afnd = Carregador.carregarArquivo(caminho);
            System.out.println("\n=== 1. AFND GERADO ===");
            afnd.imprimirTabela();

            AFD afd = ProcessadorAF.determinizar(afnd);
            System.out.println("\n=== 2. AFD DETERMINIZADO ===");
            afd.imprimirTabela();

            System.out.println("\n=== 3. MINIMIZACAO (LIMPEZA) ===");
            ProcessadorAF.removerInalcancaveis(afd);
            ProcessadorAF.removerMortos(afd);
            afd.imprimirTabela();

            ProcessadorAF.adicionarEstadoErro(afd);
            System.out.println("\n=== 4. AFD FINAL COM ESTADO DE ERRO ===");
            afd.imprimirTabela();

            System.out.println("\n=== SIMULADOR DE RECONHECIMENTO (digite 'sair' para encerrar) ===");
            while (true) {
                System.out.print("\nSentenca para testar: ");
                String sentenca = sc.nextLine();
                if (sentenca.equalsIgnoreCase("sair")) break;

                boolean aceita = afd.reconhecer(sentenca);
                System.out.println("Resultado: " + (aceita ? "ACEITA" : "REJEITADA (ERRO)"));
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar arquivo: " + e.getMessage());
        }

        sc.close();
    }
}