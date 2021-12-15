package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.CosmosDBInput;
import com.microsoft.azure.functions.annotation.CosmosDBOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Date;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Ratings {
        /**
         * This function listens at endpoint "/api/CreateRating".
         */
        @FunctionName("CreateRating")
        public HttpResponseMessage createRating(
                        @HttpTrigger(name = "req", methods = {
                                        HttpMethod.POST }, authLevel = AuthorizationLevel.ANONYMOUS, route = "ratings") HttpRequestMessage<Optional<String>> request,
                        @CosmosDBOutput(name = "database", databaseName = "icreamratings", collectionName = "icecreamratingscontainer", connectionStringSetting = "Cosmos_DB_Connection_String") OutputBinding<String> outputItem,
                        final ExecutionContext context) {

                // Parse query parameter

                String body = request.getBody().orElse("");

                if (null != body) {
                        JSONObject jsonObject = new JSONObject(body);
                        String userId = jsonObject.getString("userId");
                        String productId = jsonObject.getString("productId");
                        String locationName = jsonObject.getString("locationName");
                        Integer rating = jsonObject.getInt("rating");
                        String userNotes = jsonObject.getString("userNotes");

                        CloseableHttpClient client = HttpClientBuilder.create().build();
                        try {
                                HttpGet productRequest = new HttpGet(
                                                "https://serverlessohapi.azurewebsites.net/api/GetProduct?productId="
                                                                + productId);

                                CloseableHttpResponse productResponse = client.execute(productRequest);

                                HttpGet userRequest = new HttpGet(
                                                "https://serverlessohapi.azurewebsites.net/api/GetUser?userId="
                                                                + userId);

                                CloseableHttpResponse userResponse = client.execute(userRequest);

                                if (productResponse.getStatusLine().getStatusCode() != 200) {
                                        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                                        .body("Bad Product Id")
                                                        .build();
                                } else if (userResponse.getStatusLine().getStatusCode() != 200) {
                                        return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                                                        .body("Bad User Id")
                                                        .build();
                                } else {
                                        // Generate random ID
                                        final int id = Math.abs(new Random().nextInt());

                                        // Generate document
                                        final String jsonDocument = "{\"id\":\"" + id + "\", " +
                                                        "\"userId\": \"" + userId + "\", " +
                                                        "\"timeStamp\": \"" + new Timestamp(new Date().getTime())
                                                        + "\", " +
                                                        "\"productId\": \"" + productId + "\", " +
                                                        "\"locationName\": \"" + locationName + "\", " +
                                                        "\"rating\": \"" + rating + "\", " +
                                                        "\"userNotes\": \"" + userNotes + "\"}";

                                        context.getLogger().info("Document to be saved: " + jsonDocument);

                                        // Set outputItem's value to the JSON document to be saved
                                        outputItem.setValue(jsonDocument);

                                        // return a different document to the browser or calling client.
                                        return request.createResponseBuilder(HttpStatus.OK)
                                                        .body(jsonDocument)
                                                        .build();
                                }
                        } catch (Exception e) {

                        }
                }
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body("INternal Server Error")
                                .build();
        }

        @FunctionName("GetRating")
        public HttpResponseMessage getRating(
                        @HttpTrigger(name = "req", methods = { HttpMethod.GET
                        }, authLevel = AuthorizationLevel.ANONYMOUS, route = "ratings/{id}") HttpRequestMessage<Optional<String>> request,
                        @CosmosDBInput(name = "database", databaseName = "icreamratings", collectionName = "icecreamratingscontainer", sqlQuery = "select * from icecreamratingscontainer r where r.id = {id}", connectionStringSetting = "Cosmos_DB_Connection_String") Rating[] item,
                        final ExecutionContext context) {

                // Item list
                context.getLogger().info("Parameters are: " + request.getQueryParameters());
                context.getLogger().info("Items from the database are " + item);

                // Convert and display
                if (item == null || item.length == 0) {
                        return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                                        .body("Document not found.")
                                        .build();
                } else {
                        return request.createResponseBuilder(HttpStatus.OK)
                                        .header("Content-Type", "application/json")
                                        .body(item[0])
                                        .build();
                }
        }

        @FunctionName("GetRatings")
        public HttpResponseMessage getRatings(
                        @HttpTrigger(name = "req", methods = { HttpMethod.GET
                        }, authLevel = AuthorizationLevel.ANONYMOUS, route = "ratings/user/{userId}") HttpRequestMessage<Optional<String>> request,
                        @CosmosDBInput(name = "database", databaseName = "icreamratings", collectionName = "icecreamratingscontainer", sqlQuery = "select * from icecreamratingscontainer r where r.userId = {userId}", connectionStringSetting = "Cosmos_DB_Connection_String") Rating[] ratings,
                        final ExecutionContext context) {

                // Item list
                context.getLogger().info("Parameters are: " + request.getQueryParameters());
                context.getLogger().info("Items from the database are " + ratings);

                // Convert and display
                if (null == ratings || ratings.length == 0) {
                        return request.createResponseBuilder(HttpStatus.NOT_FOUND)
                                        .body("Document not found.")
                                        .build();
                } else {
                        return request.createResponseBuilder(HttpStatus.OK)
                                        .header("Content-Type", "application/json")
                                        .body(ratings)
                                        .build();
                }
        }
}
