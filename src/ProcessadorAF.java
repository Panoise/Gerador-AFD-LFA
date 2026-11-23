import java.util.*;

public class ProcessadorAF {

    // Etapa 1: Teorema da Determinização (construção de subconjuntos)
    public static AFD determinizar(AFND afnd) {
        AFD afd = new AFD();
        afd.setAlfabeto(afnd.getAlfabeto());

        Queue<Set<String>> fila = new LinkedList<>();
        Map<Set<String>, String> nomes = new LinkedHashMap<>();

        Set<String> inicial = new TreeSet<>();
        inicial.add(afnd.getEstadoInicial());

        String nomeInicial = formatarConjunto(inicial);
        nomes.put(inicial, nomeInicial);
        afd.setEstadoInicial(nomeInicial);
        fila.add(inicial);

        while (!fila.isEmpty()) {
            Set<String> conjuntoAtual = fila.poll();
            String nomeAtual = nomes.get(conjuntoAtual);

            // Se qualquer estado do conjunto for final no AFND, o estado composto é final no AFD
            for (String e : conjuntoAtual) {
                if (afnd.getEstadosFinais().contains(e)) {
                    afd.marcarFinal(nomeAtual);
                    break;
                }
            }

            // Calcula a transição para cada símbolo do alfabeto
            for (String simbolo : afnd.getAlfabeto()) {
                Set<String> destino = new TreeSet<>();
                for (String e : conjuntoAtual) {
                    destino.addAll(afnd.getTransicoes(e, simbolo));
                }

                if (!destino.isEmpty()) {
                    String nomeDestino = nomes.get(destino);
                    if (nomeDestino == null) {
                        nomeDestino = formatarConjunto(destino);
                        nomes.put(destino, nomeDestino);
                        afd.adicionarEstado(nomeDestino);
                        fila.add(destino);
                    }
                    afd.adicionarTransicao(nomeAtual, simbolo, nomeDestino);
                }
            }
        }

        return afd;
    }

    // Etapa 2a: Remove estados inalcançáveis (busca em largura a partir do inicial)
    public static void removerInalcancaveis(AFD afd) {
        if (afd.getEstadoInicial() == null) return;

        Set<String> alcancaveis = new LinkedHashSet<>();
        Queue<String> fila = new LinkedList<>();

        alcancaveis.add(afd.getEstadoInicial());
        fila.add(afd.getEstadoInicial());

        while (!fila.isEmpty()) {
            String atual = fila.poll();
            for (String simbolo : afd.getAlfabeto()) {
                String destino = afd.getTransicao(atual, simbolo);
                if (destino != null && alcancaveis.add(destino)) {
                    fila.add(destino);
                }
            }
        }

        Set<String> removidos = new LinkedHashSet<>(afd.getEstados());
        removidos.removeAll(alcancaveis);
        System.out.println("Estados inalcancaveis removidos no AFD: " + (removidos.isEmpty() ? "nenhum" : removidos));

        afd.manterApenasEstados(alcancaveis);
    }

    // Etapa 2b: Remove estados mortos (quem não consegue chegar em nenhum estado final)
    public static void removerMortos(AFD afd) {
        Set<String> vivos = new LinkedHashSet<>(afd.getEstadosFinais());
        boolean mudou = true;

        while (mudou) {
            mudou = false;
            for (String estado : afd.getEstados()) {
                if (!vivos.contains(estado)) {
                    for (String simbolo : afd.getAlfabeto()) {
                        String destino = afd.getTransicao(estado, simbolo);
                        if (destino != null && vivos.contains(destino)) {
                            vivos.add(estado);
                            mudou = true;
                            break;
                        }
                    }
                }
            }
        }

        Set<String> removidos = new LinkedHashSet<>(afd.getEstados());
        removidos.removeAll(vivos);
        System.out.println("Estados mortos removidos no AFD: " + (removidos.isEmpty() ? "nenhum" : removidos));

        afd.manterApenasEstados(vivos);
    }

    // Etapa 3: Adiciona o estado de erro e preenche todas as células vazias
    public static void adicionarEstadoErro(AFD afd) {
        String erro = "ERRO";
        afd.adicionarEstado(erro);
        afd.marcarFinal(erro); // Enunciado: "acrescentar um último estado final. Este será o estado de erro."

        for (String estado : new ArrayList<>(afd.getEstados())) {
            for (String simbolo : afd.getAlfabeto()) {
                if (afd.getTransicao(estado, simbolo) == null) {
                    afd.adicionarTransicao(estado, simbolo, erro);
                }
            }
        }
    }

    private static String formatarConjunto(Set<String> conjunto) {
        return "{" + String.join(",", conjunto) + "}";
    }
}