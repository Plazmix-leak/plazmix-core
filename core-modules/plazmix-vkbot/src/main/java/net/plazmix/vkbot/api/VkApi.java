package net.plazmix.vkbot.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.NonNull;
import net.plazmix.core.api.utility.JsonUtil;
import net.plazmix.vkbot.api.callback.ResponseCallback;
import net.plazmix.vkbot.api.context.VkCallbackApiContextHandler;
import net.plazmix.vkbot.api.handler.CallbackApiHandler;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Getter
public class VkApi {

    private final List<CallbackApiHandler> callbackApiHandlerList = new ArrayList<>();

    private HttpServer httpServer;


    private static final String API_VERSION                 = "5.85";
    private static final String DEFAULT_URL                 = "https://api.vk.com/api.php";

    public static final ExecutorService EXECUTOR_SERVICE    = Executors.newSingleThreadExecutor();


    private final String accessToken                        = "934cceac7cd249ad5c660ff2aeb546e1666ada1ac94576becafbf1661cc249dda2a8e9b3fd2d2c126f8df",
                         confirmationCode                   = "62257029",
                         secretKey                          = "KriusdhjSuf85ryilgashe";

    private final InetSocketAddress inetSocketAddress       = new InetSocketAddress("135.181.39.144", 8081);


    public void runCallbackApi() throws IOException {
        httpServer = HttpServer.create();
        httpServer.setExecutor(EXECUTOR_SERVICE);

        HttpContext alertContext = httpServer.createContext("/bot");
        alertContext.setHandler(new VkCallbackApiContextHandler(this));

        httpServer.bind(inetSocketAddress, 0);
        httpServer.start();
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public void call(@NonNull String method,
                     @NonNull JsonObject params,
                     @NonNull ResponseCallback callback) {

        EXECUTOR_SERVICE.execute(() -> callSync(method, params, callback));
    }

    public void callSync(@NonNull String method,
                         @NonNull JsonObject params,
                         @NonNull ResponseCallback callback) {

        params.addProperty("access_token", accessToken);
        params.addProperty("v", API_VERSION);
        params.addProperty("method", method);
        params.addProperty("oauth", 1);

        makeRequest(mapToURLParamsQuery(params), callback);
    }

    public void call(String method, JsonObject params) {
        call(method, params, ResponseCallback.EMPTY_CALLBACK);
    }

    public void callSync(String method, JsonObject params) {
        callSync(method, params, ResponseCallback.EMPTY_CALLBACK);
    }

    private void makeRequest(String params, ResponseCallback responseCallback) {
        try {
            URL url = new URL(DEFAULT_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestProperty("Content-Length", String.valueOf(params.length()));
            connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

            connection.setRequestMethod("POST");
            connection.setReadTimeout(2000);
            connection.setDoOutput(true);

            OutputStream stream = connection.getOutputStream();
            stream.write(params.getBytes(StandardCharsets.UTF_8));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            responseCallback.onResponse(reader.lines().collect(Collectors.joining()));
        } catch (Exception exception) {
            responseCallback.onException(exception);
        }
    }

    private static String mapToURLParamsQuery(@NonNull JsonObject jsonObject) {
        StringBuilder answer = new StringBuilder();

        int count = 0;

        Set<Map.Entry<String, JsonElement>> parameters = jsonObject.entrySet();

        for (Map.Entry<String, JsonElement> entry : parameters) {
            answer.append(entry.getKey()).append("=").append(toString(entry.getValue()));

            if (count++ < parameters.size()) {
                answer.append("&");
            }
        }

        return answer.toString();
    }

    public static String toString(@NonNull JsonElement jsonElement) {
        if (jsonElement.isJsonArray()) {
            List<String> elements = new ArrayList<>();

            for (JsonElement arrayElement : jsonElement.getAsJsonArray()) {
                elements.add(arrayElement.getAsString());
            }

            return String.join(",", elements);
        }

        if (jsonElement.isJsonObject()) {
            return JsonUtil.toJson(jsonElement.getAsJsonObject());
        }

        return jsonElement.getAsString();
    }

    /**
     * Add new callback api handler
     *
     * @param callbackApiHandler - callback api handler
     */
    public void addCallbackApiHandler(CallbackApiHandler callbackApiHandler) {
        callbackApiHandlerList.add(callbackApiHandler);
    }

    /**
     * Delete callback api handler
     *
     * @param callbackApiHandler - callback api handler
     */
    public void removeCallbackApiHandler(CallbackApiHandler callbackApiHandler) {
        callbackApiHandlerList.remove(callbackApiHandler);
    }

    /**
     * Get callback api handlers
     *
     * @return - callback api handlers
     */
    public List<CallbackApiHandler> getCallbackApiHandlerList() {
        return Collections.unmodifiableList(callbackApiHandlerList);
    }

}
