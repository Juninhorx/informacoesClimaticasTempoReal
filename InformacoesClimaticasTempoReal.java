import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;


public class InformacoesClimaticasTempoReal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o nome da cidade: ");
        String cidade = scanner.nextLine();

        try {
            String dadosClimaticos = getDadosClimaticos(cidade); //retorna um JSON
            // Código 1006 significa localização não encontrada
            if (dadosClimaticos.contains("\"code\":1006")) { // representa ("code":1006) -> \" serve para não fechar a string
                System.out.println("Localização não encontrada, por favor tente novamente.");
            } else {
                imprimirDadosClimaticos(dadosClimaticos);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static String getDadosClimaticos(String cidade) throws Exception {
        String apiKey = Files.readString(Paths.get("api-key.txt")).trim(); //trim() -> remove os espaços da string

        String formataNomeCidade = URLEncoder.encode(cidade, StandardCharsets.UTF_8);
        String apiUrl = "http://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=" + formataNomeCidade;
        HttpRequest request = HttpRequest.newBuilder() //Começa a construção de uma nova solicitação HTTP
                .uri(URI.create(apiUrl))// Define o URI da solicitação HTTP
                .build(); // Finaliza a construção da solicitação HTTP
        // Criar objeto enviar solicitações HTTP e receber respostas HTTP, para acessar o site da WeatherAPI

        HttpClient client = HttpClient.newHttpClient();

        // enviar requisições HTTP e receber as respostas HTTP
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return  response.body(); // Retorna os dados da API
    }

        //Imprimir os dados de forma organizada
    public static void imprimirDadosClimaticos(String dados) {

//      System.out.println("Dados originais (json) obtidos da API " + dados);
        JSONObject dadosJson = new JSONObject(dados);
        JSONObject informacoesMeteriologicas = dadosJson.getJSONObject("current");
        // Extrai os dados da localização
        String cidade = dadosJson.getJSONObject("location").getString("name");
        String pais = dadosJson.getJSONObject("location").getString("country");

        // Extrair dados adicionais
        String condicaoTempo = informacoesMeteriologicas.getJSONObject("condition").getString("text");
        int umidade = informacoesMeteriologicas.getInt("humidity");
        float velocidadeVento = informacoesMeteriologicas.getFloat("wind_kph");
        float pressaoAtmosferica = informacoesMeteriologicas.getFloat("pressure_mb");
        float sensacaoTermica = informacoesMeteriologicas.getFloat("feelslike_c");
        float temperaturaAtual = informacoesMeteriologicas.getFloat("temp_c");

        // Extrair a data e hora dos dados
        String dataHoraString = informacoesMeteriologicas.getString("last_updated");

        // Imprime informações atuais
        System.out.println("Informações Meteorológicas para: " + cidade + ", " + pais);
        System.out.println("Data e Hora: " + dataHoraString);
        System.out.println("Temperatura Atual: " + temperaturaAtual + "ºC");
        System.out.println("Sensação Térmica: " + sensacaoTermica + "ºC");
        System.out.println("Condição Tempo: " + condicaoTempo);
        System.out.println("Umidade: " + umidade + "%");
        System.out.println("Velocidade do Vento: " + velocidadeVento + "km/h");
        System.out.println("Pressão Atmosférica: " + pressaoAtmosferica + " mb");




    }

}
