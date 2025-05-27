public abstract class AbstractHashTable {

    // Classe aninhada estática para representar uma entrada na tabela hash (nó da lista encadeada)
    protected static class Entry {
        String key;
        Entry next;

        public Entry(String key) {
            this.key = key;
            this.next = null;
        }

        public String getKey() {
            return key;
        }

        public Entry getNext() {
            return next;
        }

        public void setNext(Entry next) {
            this.next = next;
        }
    }

    // Classe aninhada estática para encapsular as métricas de sumário da distribuição
    public static class DistributionSummary {
        public final int finalCapacity;
        public final int occupiedPositions;
        public final double occupiedPercentage;
        public final int maxKeysInBucket;
        public final int totalElements;

        public DistributionSummary(int finalCapacity, int occupiedPositions, int maxKeysInBucket, int totalElements) {
            this.finalCapacity = finalCapacity;
            this.occupiedPositions = occupiedPositions;
            this.occupiedPercentage = (finalCapacity > 0) ? ((double) occupiedPositions / finalCapacity * 100.0) : 0.0;
            this.maxKeysInBucket = maxKeysInBucket;
            this.totalElements = totalElements;
        }

        @Override
        public String toString() {
            return String.format(
                    "Capacidade Final da Tabela: %d\n" +
                            "Total de Elementos Inseridos: %d\n" +
                            "Posições Ocupadas: %d de %d (%.2f%%)\n" +
                            "Maior Cluster (máximo de chaves em uma posição): %d",
                    finalCapacity, totalElements, occupiedPositions, finalCapacity, occupiedPercentage, maxKeysInBucket
            );
        }
    }

    protected Entry[] table;
    protected int capacity;       // Capacidade atual da tabela
    protected int size;           // Número atual de elementos na tabela
    protected int collisionCount; // Contador de colisões
    protected double loadFactor;  // Fator de carga para redimensionamento

    // Construtor
    public AbstractHashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Initial capacity must be positive.");
        }
        this.capacity = initialCapacity; // A inicialização da tabela deve ter capacidade de no máximo 32
        this.table = new Entry[this.capacity];
        this.size = 0;
        this.collisionCount = 0;
        this.loadFactor = 0.75; // Fator de carga padrão
    }

    // Método abstrato da função hash a ser implementado pelas subclasses
    protected abstract int hashFunction(String key);

    // Método para redimensionar a tabela quando o fator de carga é excedido
    protected void resize() {
        Entry[] oldTable = this.table;
        int oldCapacity = this.capacity;

        this.capacity = oldCapacity * 2;
        if (this.capacity <= oldCapacity) { // Salvaguarda para overflow ou capacidade inicial muito pequena
            this.capacity = oldCapacity + (oldCapacity / 2 > 0 ? oldCapacity / 2 : 1);
            if (this.capacity <= oldCapacity) this.capacity = oldCapacity + 1;
        }

        // System.out.println("DEBUG: Redimensionando de " + oldCapacity + " para " + this.capacity + ". Tamanho antes: " + this.size);

        this.table = new Entry[this.capacity];
        int oldSize = this.size; // Salva o tamanho antigo para verificar depois do rehash
        this.size = 0;
        this.collisionCount = 0;

        for (int i = 0; i < oldCapacity; i++) {
            Entry current = oldTable[i];
            while (current != null) {
                insert(current.getKey()); // Reinsere usando o método público, que atualiza size e collisionCount
                current = current.getNext();
            }
        }
        // System.out.println("DEBUG: Redimensionamento concluído. Tamanho após rehash: " + this.size + " (esperado: " + oldSize + ")");
        // if (this.size != oldSize) {
        //     System.err.println("ALERTA: Discrepância no tamanho após o rehash!");
        // }
    }

    // Método para inserir um nome na tabela hash
    public void insert(String key) {
        if (key == null) {
            System.err.println("Chave nula não pode ser inserida.");
            return;
        }

        if (((double) (size + 1) / capacity) > this.loadFactor) {
            resize();
        }

        int index = hashFunction(key);
        index = Math.abs(index % capacity);

        Entry newEntry = new Entry(key);

        if (table[index] == null) {
            table[index] = newEntry;
        } else {
            this.collisionCount++;
            Entry current = table[index];
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newEntry);
        }
        this.size++;
    }

    // Método para buscar um nome na tabela hash
    public boolean search(String key) {
        if (key == null) {
            return false;
        }
        int index = hashFunction(key);
        index = Math.abs(index % capacity);

        Entry current = table[index];
        while (current != null) {
            if (current.getKey().equals(key)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    // Retorna o número total de colisões
    public int getCollisionCount() {
        return collisionCount;
    }

    // Retorna o número de elementos na tabela
    public int getSize() {
        return size;
    }

    // Retorna a capacidade da tabela
    public int getCapacity() {
        return capacity;
    }

    // Calcula e retorna um sumário da distribuição das chaves
    public DistributionSummary getDistributionSummary() {
        int occupiedPositions = 0;
        int maxKeysInBucket = 0;

        for (int i = 0; i < capacity; i++) {
            int keysInThisBucket = 0;
            Entry current = table[i];
            while (current != null) {
                keysInThisBucket++;
                current = current.getNext();
            }
            if (keysInThisBucket > 0) {
                occupiedPositions++;
            }
            if (keysInThisBucket > maxKeysInBucket) {
                maxKeysInBucket = keysInThisBucket;
            }
        }
        return new DistributionSummary(capacity, occupiedPositions, maxKeysInBucket, size);
    }


    // Imprime a distribuição das chaves na tabela
    public void printKeyDistribution() {
        System.out.println("--- Distribuição de Chaves (Capacidade: " + capacity + ") ---");
        for (int i = 0; i < capacity; i++) {
            int countInBucket = 0;
            Entry current = table[i];
            while (current != null) {
                countInBucket++;
                current = current.getNext();
            }
            System.out.println("Posição [" + String.format("%02d", i) + "]: " + countInBucket + " chaves");
        }
        System.out.println("--------------------------------------------------");
    }

    // Imprime o número de colisões para cada posição (clusterização)
    public void printCollisionDistributionPerPosition() {
        System.out.println("--- Clusterização (Colisões por Posição) ---");
        for (int i = 0; i < capacity; i++) {
            int keysInBucket = 0;
            Entry current = table[i];
            while (current != null) {
                keysInBucket++;
                current = current.getNext();
            }

            int collisionsInThisBucket = 0;
            if (keysInBucket > 1) {
                collisionsInThisBucket = keysInBucket - 1;
            }
            System.out.println("Posição [" + String.format("%02d", i) + "]: " + keysInBucket + " chaves, " + collisionsInThisBucket + " colisões nesta posição.");
        }
        System.out.println("--------------------------------------------");
    }


    // Reseta a tabela para um novo teste
    // Mantém a capacidade atual, que pode ter sido aumentada pelo redimensionamento.
    public void resetTable() {
        this.table = new Entry[this.capacity];
        this.size = 0;
        this.collisionCount = 0;
    }
}