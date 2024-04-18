import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class CurrencyConverter {
    // URL de la API de tasas de cambio
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/7968a6a7f0c5c3b612ac169e/latest/USD";

    public static void main(String[] args) {
        try {
            // Obtener las tasas de cambio de la API
            JSONObject rates = getExchangeRates(API_URL);
            if (rates != null) {
                // Mostrar las tasas de cambio disponibles al usuario
                processExchangeRates(rates);
                // Realizar la conversión de moneda
                convertCurrency(rates);
            } else {
                System.out.println("No se pudo obtener las tasas de cambio.");
            }
        } catch (IOException e) {
            System.out.println("Error al realizar la solicitud a la API.");
            e.printStackTrace();
        }
    }

    // Método para obtener las tasas de cambio de la API
    private static JSONObject getExchangeRates(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            // Leer la respuesta de la API línea por línea
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Crear un objeto JSON a partir de la respuesta de la API
            return new JSONObject(response.toString());
        } else {
            return null;
        }
    }

    // Método para mostrar las tasas de cambio disponibles al usuario
    private static void processExchangeRates(JSONObject rates) {
        System.out.println("Tasas de cambio disponibles:");
        JSONObject conversionRates = rates.getJSONObject("conversion_rates");
        int i = 1;
        for (String currency : conversionRates.keySet()) {
            double rate = conversionRates.getDouble(currency);
            System.out.println(i + ". " + currency + ": " + rate);
            i++;
        }
    }

    // Método para realizar la conversión de moneda
    private static void convertCurrency(JSONObject rates) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nElige la moneda de origen (ingresa el número correspondiente): ");
        int fromOption = Integer.parseInt(reader.readLine()) - 1;

        JSONObject conversionRates = rates.getJSONObject("conversion_rates");
        String[] currencies = conversionRates.keySet().toArray(new String[0]);
        if (fromOption < 0 || fromOption >= currencies.length) {
            System.out.println("Opción no válida.");
            return;
        }
        String fromCurrency = currencies[fromOption];

        System.out.println("Elige la moneda de destino (ingresa el número correspondiente): ");
        int toOption = Integer.parseInt(reader.readLine()) - 1;
        if (toOption < 0 || toOption >= currencies.length) {
            System.out.println("Opción no válida.");
            return;
        }
        String toCurrency = currencies[toOption];

        System.out.println("Ingresa la cantidad a convertir: ");
        double amount = Double.parseDouble(reader.readLine());

        // Obtener las tasas de cambio de las monedas de origen y destino
        double fromRate = conversionRates.getDouble(fromCurrency);
        double toRate = conversionRates.getDouble(toCurrency);

        // Realizar la conversión de moneda
        double convertedAmount = (amount / fromRate) * toRate;
        // Mostrar el resultado de la conversión al usuario
        System.out.println("\n" + amount + " " + fromCurrency + " equivale a " + String.format("%.2f", convertedAmount) + " " + toCurrency);
    }
}


