# Gerador de Autômato Finito Determinístico (AFD)

Implementação em Java desenvolvida para a disciplina de **Linguagens Formais e Autômatos** (Ciência da Computação — UFFS, Campus Chapecó), ministrada pelo Prof. Braulio Mello.

**Autor:** Marco Antonio Bernardeli da Veiga

## Funcionalidades
- **Carga de Tokens e Gramáticas Regulares (BNF):** leitura de arquivo texto gerando um AFND único sem reuso de estados intermediários (apenas o estado inicial `S` é compartilhado)[cite: 1, 2].
- **Determinização:** conversão de AFND para AFD via construção de subconjuntos[cite: 2].
- **Minimização (Limpeza):** remoção de estados inalcançáveis e estados mortos, sem aplicação de classes de equivalência[cite: 1, 2].
- **Estado de Erro:** inclusão do estado final `*ERRO` para todas as transições não mapeadas[cite: 2].
- **Simulador Léxico:** validação interativa de sentenças sobre o AFD gerado.

## Estrutura do Projeto
- `src/AFND.java`: representação do autômato não determinístico.
- `src/AFD.java`: representação do autômato determinístico e simulador de reconhecimento.
- `src/Carregador.java`: parser de tokens e Gramáticas Regulares em notação BNF[cite: 1, 2].
- `src/ProcessadorAF.java`: algoritmos de determinização, limpeza e estado de erro[cite: 1, 2].
- `src/Main.java`: interface de linha de comando para execução e testes.

## Como Executar
```bash
javac -encoding UTF-8 -d out src/*.java
java -cp out Main