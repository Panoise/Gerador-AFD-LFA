import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Carregador {

    public static AFND carregarArquivo(String caminho) throws IOException {
        AFND afnd = new AFND();
        List<String> linhas = Files.readAllLines(Path.of(caminho), StandardCharsets.UTF_8);

        // Mapeia os não-terminais da GR atual (ex: <A> -> q12) para evitar colisão entre GRs diferentes
        Map<String, String> mapaNaoTerminais = new HashMap<>();
        String inicialGRAtual = null;

        for (String linhaBruta : linhas) {
            String linha = linhaBruta.trim();

            // Linha em branco encerra o bloco da GR atual
            if (linha.isEmpty()) {
                mapaNaoTerminais.clear();
                inicialGRAtual = null;
                continue;
            }

            if (linha.contains("::=")) {
                processarLinhaGR(linha, afnd, mapaNaoTerminais, inicialGRAtual);
                if (inicialGRAtual == null) {
                    inicialGRAtual = linha.split("::=")[0].trim();
                }
            } else {
                // Se veio um token, zera o contexto de GR anterior
                mapaNaoTerminais.clear();
                inicialGRAtual = null;
                processarToken(linha, afnd);
            }
        }

        return afnd;
    }

    private static void processarToken(String token, AFND afnd) {
        String estadoAtual = afnd.getEstadoInicial();

        for (int i = 0; i < token.length(); i++) {
            String simbolo = String.valueOf(token.charAt(i));
            String proximoEstado = afnd.criarNovoEstado();
            afnd.adicionarTransicao(estadoAtual, simbolo, proximoEstado);
            estadoAtual = proximoEstado;
        }

        afnd.marcarFinal(estadoAtual);
    }

    private static void processarLinhaGR(String linha, AFND afnd, Map<String, String> mapaNaoTerminais, String inicialGRAtual) {
        String[] partes = linha.split("::=");
        String lhs = partes[0].trim();
        String rhs = partes[1].trim();

        // Se for o início de uma nova GR (mapa vazio ou repetiu o <S> de uma GR anterior)
        if (mapaNaoTerminais.isEmpty() || lhs.equals(inicialGRAtual)) {
            mapaNaoTerminais.clear();
            mapaNaoTerminais.put(lhs, afnd.getEstadoInicial());
        }

        String estadoOrigem = mapaNaoTerminais.computeIfAbsent(lhs, k -> afnd.criarNovoEstado());

        String[] producoes = rhs.split("\\|");
        for (String prodBruta : producoes) {
            String prod = prodBruta.trim();
            if (prod.isEmpty()) continue;

            // Caso 1: produção vazia (ε, & ou epsilon) -> marca o estado atual como final
            if (prod.equals("ε") || prod.equals("&") || prod.equalsIgnoreCase("epsilon")) {
                afnd.marcarFinal(estadoOrigem);
                continue;
            }

            // Caso 2: terminal seguido de não-terminal no final (ex: a<A> ou <<A>)
            int idxAbre = prod.lastIndexOf('<');
            int idxFecha = prod.lastIndexOf('>');
            if (idxAbre != -1 && idxFecha == prod.length() - 1 && idxFecha > idxAbre + 1) {
                String terminal = prod.substring(0, idxAbre).trim();
                String naoTerminal = prod.substring(idxAbre, idxFecha + 1).trim();
                String estadoDestino = mapaNaoTerminais.computeIfAbsent(naoTerminal, k -> afnd.criarNovoEstado());

                if (!terminal.isEmpty()) {
                    afnd.adicionarTransicao(estadoOrigem, terminal, estadoDestino);
                }
            } else {
                // Caso 3: apenas terminal (ex: a, +, <, <>) -> vai para um novo estado final exclusivo
                String novoFinal = afnd.criarNovoEstado();
                afnd.marcarFinal(novoFinal);
                afnd.adicionarTransicao(estadoOrigem, prod, novoFinal);
            }
        }
    }
}