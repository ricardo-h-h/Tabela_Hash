import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NameReader {

    public static List<String> readNamesFromFile(String filePath) {
        List<String> names = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Adiciona apenas se a linha não estiver vazia
                    names.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + filePath);
            e.printStackTrace();
        }
        return names;
    }

    // Pequeno teste para verificar se está funcionando
    public static void main(String[] args) {
        // Certifique-se de que o arquivo female_names.txt está na raiz do seu projeto
        // ou forneça o caminho completo para ele.
        List<String> names = readNamesFromFile("female_names.txt"); // [cite: 10]
        System.out.println("Total de nomes lidos: " + names.size());
        if (names.size() > 0) {
            System.out.println("Primeiro nome: " + names.get(0));
            System.out.println("Último nome: " + names.get(names.size() - 1));
        }
    }
}