import java.util.*;

public class AFD {
    private String estadoInicial;
    private Set<String> estados = new LinkedHashSet<>();
    private Set<String> alfabeto = new LinkedHashSet<>();
    private Set<String> estadosFinais = new LinkedHashSet<>();
    // Origem -> (Simbolo -> Destino único)
    private Map<String, Map<String, String>> transicoes = new LinkedHashMap<>();

    public void setEstadoInicial(String estadoInicial) {
        this.estadoInicial = estadoInicial;
        this.estados.add(estadoInicial);
    }

    public void setAlfabeto(Set<String> alfabeto) {
        this.alfabeto.addAll(alfabeto);
    }

    public void adicionarEstado(String estado) {
        estados.add(estado);
    }

    public void adicionarTransicao(String origem, String simbolo, String destino) {
        estados.add(origem);
        estados.add(destino);
        alfabeto.add(simbolo);
        transicoes
                .computeIfAbsent(origem, k -> new LinkedHashMap<>())
                .put(simbolo, destino);
    }

    public void marcarFinal(String estado) {
        estados.add(estado);
        estadosFinais.add(estado);
    }

    public String getTransicao(String origem, String simbolo) {
        return transicoes
                .getOrDefault(origem, Collections.emptyMap())
                .get(simbolo);
    }

    // Remove qualquer estado que não esteja no conjunto de válidos (usado na limpeza)
    public void manterApenasEstados(Set<String> validos) {
        estados.retainAll(validos);
        estadosFinais.retainAll(validos);
        transicoes.keySet().retainAll(validos);
        for (Map<String, String> mapa : transicoes.values()) {
            mapa.values().removeIf(destino -> !validos.contains(destino));
        }
        if (estadoInicial != null && !validos.contains(estadoInicial)) {
            estadoInicial = null;
        }
    }

    public void imprimirTabela() {
        int largura = 10;
        for (String e : estados) {
            largura = Math.max(largura, e.length() + 4);
        }
        String fmt = "%-" + largura + "s";

        System.out.printf(fmt, "Estado");
        for (String simbolo : alfabeto) {
            System.out.printf(fmt, simbolo);
        }
        System.out.println();
        System.out.println("-".repeat(largura * (alfabeto.size() + 1)));

        for (String estado : estados) {
            String prefixo = "";
            if (estado.equals(estadoInicial)) prefixo += "->";
            if (estadosFinais.contains(estado)) prefixo += "*";
            System.out.printf(fmt, prefixo + estado);

            for (String simbolo : alfabeto) {
                String destino = getTransicao(estado, simbolo);
                System.out.printf(fmt, destino == null ? "-" : destino);
            }
            System.out.println();
        }
    }

    public String getEstadoInicial() { return estadoInicial; }
    public Set<String> getEstados() { return estados; }
    public Set<String> getAlfabeto() { return alfabeto; }
    public Set<String> getEstadosFinais() { return estadosFinais; }
    public Map<String, Map<String, String>> getMapaTransicoes() { return transicoes; }

    public boolean reconhecer(String sentenca) {
        if (estadoInicial == null) {
            System.out.println("Automato vazio (sem estado inicial valido).");
            return false;
        }

        String atual = estadoInicial;
        StringBuilder trajeto = new StringBuilder(atual);

        for (int i = 0; i < sentenca.length(); i++) {
            String simbolo = String.valueOf(sentenca.charAt(i));
            String proximo = getTransicao(atual, simbolo);

            if (proximo == null) {
                trajeto.append(" --(").append(simbolo).append(")--> ERRO [simbolo fora do alfabeto]");
                System.out.println("Trajeto: " + trajeto);
                return false;
            }

            atual = proximo;
            trajeto.append(" --(").append(simbolo).append(")--> ").append(atual);
        }

        System.out.println("Trajeto: " + trajeto);
        return estadosFinais.contains(atual) && !atual.equals("ERRO");
    }


}