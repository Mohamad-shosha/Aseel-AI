package com.trace.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class VisionService {

    private static final String API_KEY = "AIzaSyBdCtEa5maceJyPu_-T_g-08iPNR22ZM4Q";
    private static final String VISION_URL = "https://vision.googleapis.com/v1/images:annotate?key=" + API_KEY;

    public String searchImage(MultipartFile file) throws IOException {
        String base64Image = Base64.getEncoder().encodeToString(file.getBytes());

        String jsonRequest = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"image\": {\n" +
                "        \"content\": \"" + base64Image + "\"\n" +
                "      },\n" +
                "      \"features\": [\n" +
                "        {\"type\": \"WEB_DETECTION\"}\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(jsonRequest, MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(VISION_URL)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        if (response.body() == null) {
            return "No response from API";
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.body().string());

        StringBuilder result = new StringBuilder();
        JsonNode webDetection = root.at("/responses/0/webDetection");

        result.append("🔹 Web Entities:\n");
        if (webDetection.has("webEntities")) {
            for (JsonNode entity : webDetection.get("webEntities")) {
                String desc = entity.has("description") ? entity.get("description").asText() : "N/A";
                double score = entity.has("score") ? entity.get("score").asDouble() : 0.0;
                result.append("- ").append(desc).append(" (score: ").append(score).append(")\n");
            }
        }

        result.append("\n🔹 Full Matching Images:\n");
        if (webDetection.has("fullMatchingImages")) {
            for (JsonNode img : webDetection.get("fullMatchingImages")) {
                result.append("- ").append(img.get("url").asText()).append("\n");
            }
        }

        result.append("\n🔹 Visually Similar Images:\n");
        if (webDetection.has("visuallySimilarImages")) {
            for (JsonNode img : webDetection.get("visuallySimilarImages")) {
                result.append("- ").append(img.get("url").asText()).append("\n");
            }
        }

        result.append("\n🔹 Pages With Matching Images:\n");
        if (webDetection.has("pagesWithMatchingImages")) {
            for (JsonNode page : webDetection.get("pagesWithMatchingImages")) {
                String pageUrl = page.has("url") ? page.get("url").asText() : "N/A";
                result.append("- ").append(pageUrl).append("\n");
            }
        }

        result.append("\n🔹 Best Guess Labels:\n");
        if (webDetection.has("bestGuessLabels")) {
            for (JsonNode label : webDetection.get("bestGuessLabels")) {
                result.append("- ").append(label.get("label").asText()).append("\n");
            }
        }

        return result.toString();
    }
}
