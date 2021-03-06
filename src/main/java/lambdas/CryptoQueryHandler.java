package lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.crypto_currency.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CryptoQueryHandler {

    private final String apiKey = System.getenv("API_KEY");
    private final Logger logger = LogManager.getLogger(CryptoQueryHandler.class);

    public APIGatewayProxyResponseEvent handleRequest(Map<String, Object> input, Context context) {
        logger.info("Incoming map size {}", input.size());
        int topCount = 20;

        if (input.containsKey("top") && input.get("top") instanceof Integer) {
            topCount = (int) input.get("top");
        }

        String uri = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("start", "1"));
        params.add(new BasicNameValuePair("limit", "" + topCount));
        params.add(new BasicNameValuePair("convert", "USD"));

        try {
            String result = makeAPICall(uri, params);
            ObjectMapper mapper = new ObjectMapper();
            CryptoRequest parsed = mapper.readValue(result, CryptoRequest.class);
            List<CryptoDTO> cryptoReturn = new ArrayList<>();
            for (CryptoData cryptoData : parsed.getData()) {
                CryptoDTO cryptoDTO = new CryptoDTO(cryptoData.getId(), cryptoData.getName(), cryptoData.getSymbol(), cryptoData.getLastUpdated());
                for (Map.Entry<String, ValueModel> quoteEntry : cryptoData.getQuote().entrySet()) {
                    CryptoExchangeDTO cryptoExchangeDTO = new CryptoExchangeDTO(quoteEntry.getKey(), quoteEntry.getValue().getPrice(),
                            quoteEntry.getValue().getPercentChange1H(),
                            quoteEntry.getValue().getPercentChange24H());
                    cryptoDTO.getExchanges().add(cryptoExchangeDTO);
                }
                cryptoReturn.add(cryptoDTO);
            }

            return new APIGatewayProxyResponseEvent().withStatusCode(200)
                    .withBody(mapper.writeValueAsString(cryptoReturn))
                    .withIsBase64Encoded(false);
        } catch (IOException e) {
            logger.error("Error: cannot access content - {}", e.toString());
        }

        return new APIGatewayProxyResponseEvent().withStatusCode(400).withBody("").withIsBase64Encoded(false);
    }

    public String makeAPICall(String uri, List<NameValuePair> parameters) {
        String response_content = "";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            URIBuilder query = new URIBuilder(uri);

            query.addParameters(parameters);

            HttpGet request = new HttpGet(query.build());

            request.setHeader(HttpHeaders.ACCEPT, "application/json");
            request.addHeader("X-CMC_PRO_API_KEY", apiKey);

            CloseableHttpResponse response = client.execute(request);
            logger.info(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            response_content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
        } catch (IOException e) {
            logger.error("Error while making an API call; {}", e.toString());
        } catch (URISyntaxException e) {
            logger.error("Error: cannot create the URI - {}", e.toString());
        }

        return response_content;
    }

}

