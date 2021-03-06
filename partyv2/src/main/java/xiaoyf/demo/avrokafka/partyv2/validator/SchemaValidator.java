package xiaoyf.demo.avrokafka.partyv2.validator;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import org.apache.avro.Schema;
import xiaoyf.demo.avrokafka.model.Event;
import xiaoyf.demo.avrokafka.model.SocialMediaActivity;
import xiaoyf.demo.avrokafka.model.Transaction;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

import static xiaoyf.demo.avrokafka.Constants.*;

public class SchemaValidator {
    public static void main(String[] args) throws Exception {
        SchemaValidator validator = new SchemaValidator();
        validator.validateSchema(TRANSACTION_TOPIC + "-value", Transaction.SCHEMA$);
        validator.validateSchema(EVENT_TOPIC + "-value", Event.SCHEMA$);
        validator.validateSchema(SocialMediaActivity.class.getName(), SocialMediaActivity.SCHEMA$);
        System.out.println("Validated Successfully");
    }

    private void validateSchema(String subject, Schema schema) throws Exception {
        String url = String.format("%s/subjects/%s", SCHEMA_REGISTRY_URL, subject);

        JsonStringEncoder encoder = JsonStringEncoder.getInstance();
        String json = new String(encoder.quoteAsString(schema.toString()));

        String errorResp = post(url, String.format("{\"schema\":\"%s\"}", json));
        if (Objects.nonNull(errorResp)) {
            throw new Exception("Invalid Schema:" + errorResp);
        }
    }

    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private String post(String url, String json) throws IOException {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return response.body().string();
            }

            return null;
        }
    }
}
