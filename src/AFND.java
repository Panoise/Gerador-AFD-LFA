import java.util.*;

public class AFND {
    private String estadoInicial = "S";
    private Set<String> estados = new LinkedHashSet<>();
    private Set<String> alfabeto = new LinkedHashSet<>();
    private Set<String> estadosFinais = new LinkedHashSet<>();
    // Origem -> (Simbolo -> Conjunto de Destinos)
    private Map<String, Map<String, Set<String>>> transicoes = new LinkedHashMap<>();
    private int contadorEstados = 0;

    public AFND() {
        estados.add(estadoInicial);
    }

    public String criarNovoEstado() {
        String novo = "q" + (contadorEstados++);
        estados.add(novo);
        return novo;
    }

    public void adicionarTransicao(String origem, String simbolo, String destino) {
        estados.add(origem);
        estados.add(destino);
        if (!simbolo.equals("ε")) {
            alfabeto.add(simbolo);
        }
        transicoes
                .computeIfAbsent(origem, k -> new LinkedHashMap<>())
                .computeIfAbsent(simbolo, k -> new LinkedHashSet<>())
                .add(destino);
    }

    public void marcarFinal(String estado) {
        estadosFinais.add(estado);
    }

    public Set<String> getTransicoes(String origem, String simbolo) {
        return transicoes
                .getOrDefault(origem, Collections.emptyMap())
                .getOrDefault(simbolo, Collections.emptySet());
    }

    public void imprimirTabela() {
        System.out.printf("%-10s", "Estado");
        for (String simbolo : alfabeto) {
            System.out.printf("%-12s", simbolo);
        }
        System.out.println();
        System.out.println("-".repeat(10 + alfabeto.size() * 12));

        for (String estado : estados) {
            String nome = (estadosFinais.contains(estado) ? "*" : "") + estado;
            System.out.printf("%-10s", nome);
            for (String simbolo : alfabeto) {
                Set<String> destinos = getTransicoes(estado, simbolo);
                String texto = destinos.isEmpty() ? "-" : String.join(",", destinos);
                System.out.printf("%-12s", texto);
            }
            System.out.println();
        }
    }

    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstados() { return estados; }
    public Set<String> getAlfabeto() { return alfabeto; }
    public Set<String> getEstadosFinais() { return estadosFinais; }
    public Map<String, Map<String, Set<String>>> getMapaTransicoes() { return transicoes; }
}