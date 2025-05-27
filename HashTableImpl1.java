public class HashTableImpl1 extends AbstractHashTable {

    public HashTableImpl1(int capacity) {
        super(capacity); // Chama o construtor da classe abstrata
    }

    @Override
    protected int hashFunction(String key) {
        if (key == null || key.isEmpty()) {
            return 0;
        }
        int hash = 0;
        for (int i = 0; i < key.length(); i++) {
            hash += key.charAt(i); // Soma os valores ASCII dos caracteres
        }
        // O Math.abs e o % capacity já são aplicados no método insert da classe abstrata,
        // mas é uma boa prática garantir que a função hash retorne um valor que,
        // após o módulo, seja um índice válido.
        // Para fins didáticos, podemos deixar o módulo explícito aqui também,
        // embora a classe abstrata já o faça para garantir.
        return hash; // A classe abstrata fará o Math.abs(hash % capacity)
    }
}