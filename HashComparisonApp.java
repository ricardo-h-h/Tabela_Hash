import java.util.List;
import java.util.ArrayList; // Para a lista de nomes a serem buscados

public class HashComparisonApp {

    private static final int TABLE_CAPACITY = 32; // Capacidade inicial da tabela de no máximo 32
    private static final String FILE_PATH = "female_names.txt"; // Caminho para o arquivo de nomes

    // --- CONTROLE DE OUTPUT DETALHADO ---
    // Mude para 'true' para ver a distribuição completa de chaves e colisões por posição.
    // Mude para 'false' para ver apenas um sumário.
    private static final boolean PRINT_FULL_DISTRIBUTION_DETAILS = false;

    public static void main(String[] args) {
        // 1. Leitura de um arquivo TXT contendo uma lista de 5000 nomes
        List<String> names = NameReader.readNamesFromFile(FILE_PATH);

        if (names.isEmpty()) {
            System.out.println("Nenhum nome foi lido do arquivo. Verifique o caminho e o conteúdo do arquivo.");
            return;
        }
        System.out.println(names.size() + " nomes lidos do arquivo.\n");

        // 2. Inicialização das tabelas hash
        AbstractHashTable table1 = new HashTableImpl1(TABLE_CAPACITY);
        AbstractHashTable table2 = new HashTableImpl2(TABLE_CAPACITY);

        // --- Testes para HashTableImpl1 ---
        System.out.println("=== Testando HashTableImpl1 (Função Hash Simples) ===");
        performAndReportTests(table1, names, "HashTableImpl1");

        // --- Testes para HashTableImpl2 ---
        System.out.println("\n=== Testando HashTableImpl2 (Função Hash String.hashCode()) ===");
        performAndReportTests(table2, names, "HashTableImpl2");

        System.out.println("\nComparação concluída.");
    }

    public static void performAndReportTests(AbstractHashTable hashTable, List<String> namesToInsert, String tableName) {
        // 3. Inserção dos dados nas tabelas hash e medição de tempo/colisões
        long startTimeInsert = System.nanoTime();
        for (String name : namesToInsert) {
            hashTable.insert(name);
        }
        long endTimeInsert = System.nanoTime();
        long durationInsert = (endTimeInsert - startTimeInsert) / 1_000_000; // Convertendo para milissegundos

        System.out.println("\n--- Relatório para " + tableName + " ---");
        System.out.println("Tempo total de inserção: " + durationInsert + " ms"); // Tempo total de inserção
        System.out.println("Número total de colisões: " + hashTable.getCollisionCount()); // Número de colisões para cada tabela
        System.out.println("Número de elementos na tabela: " + hashTable.getSize());
        System.out.println("Capacidade final da tabela: " + hashTable.getCapacity()); // Adicionado para sempre mostrar a capacidade final

        // 4. Realização de testes de busca e medição de tempo
        List<String> namesToSearch = new ArrayList<>(namesToInsert);

        long startTimeSearch = System.nanoTime();
        int foundCount = 0;
        for (String name : namesToSearch) {
            if (hashTable.search(name)) {
                foundCount++;
            }
        }
        long endTimeSearch = System.nanoTime();
        long durationSearch = (endTimeSearch - startTimeSearch) / 1_000_000;

        System.out.println("Tempo total de busca (para " + namesToSearch.size() + " nomes): " + durationSearch + " ms"); // Tempo total de busca para cada função hash
        System.out.println(foundCount + " de " + namesToSearch.size() + " nomes foram encontrados na busca.");

        // 5. Verificação da distribuição das chaves nas tabelas
        // e Impressão do número de colisões para cada posição (clusterização)
        if (PRINT_FULL_DISTRIBUTION_DETAILS) {
            System.out.println("\n--- Detalhes da Distribuição e Clusterização (" + tableName + ") ---");
            hashTable.printKeyDistribution();
            hashTable.printCollisionDistributionPerPosition();
        } else {
            System.out.println("\n--- Sumário da Distribuição e Clusterização (" + tableName + ") ---");
            AbstractHashTable.DistributionSummary summary = hashTable.getDistributionSummary();
            System.out.println(summary.toString());
            System.out.println("(Para detalhes completos, altere PRINT_FULL_DISTRIBUTION_DETAILS em HashComparisonApp.java para true)");
        }
        System.out.println("------------------------------------------\n");
    }
}