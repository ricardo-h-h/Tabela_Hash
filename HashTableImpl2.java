public class HashTableImpl2 extends AbstractHashTable {

    public HashTableImpl2(int capacity) {
        super(capacity); // Chama o construtor da classe abstrata
    }

    @Override
    protected int hashFunction(String key) {
        if (key == null) {
            return 0;
        }
        // Utiliza a função hash embutida da String no Java.
        // Ela é geralmente bem distribuída.
        // O método hashCode() da String pode retornar valores negativos.
        // A classe abstrata no método insert já trata isso com Math.abs() antes do módulo.
        return key.hashCode();
    }
}