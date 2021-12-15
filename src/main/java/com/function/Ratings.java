package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
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
    public HttpResponseMessage run(
            @HttpTrigger(name = "req",
              methods = {HttpMethod.GET, HttpMethod.POST},
              authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            @CosmosDBOutput(name = "database",
              databaseName = "ToDoList",
              collectionName = "Items",
              connectionStringSetting = "Cosmos_DB_Connection_String")
            OutputBinding<String> outputItem,
            final ExecutionContext context) {

        // Parse query parameter
        String query = request.getQueryParameters().get("input");
        String body = request.getBody().orElse(query);

        if(null!=body){

        JSONObject jsonObject = new JSONObject(body);

        String userId = jsonObject.getString("userId");
        String productId = jsonObject.getString("productId");
        String locationName = jsonObject.getString("locationName");
        String rating = jsonObject.getString("rating");
        String userNotes = jsonObject.getString("userNotes");

        // Item list
        context.getLogger().info("Parameters are: " + request.getQueryParameters());

        
        CloseableHttpClient client = HttpClientBuilder.create().build();

        try {
        HttpGet productRequest = new HttpGet("https://serverlessohapi.azurewebsites.net/api/GetProduct?productId="+productId);

        CloseableHttpResponse productResponse = client.execute(productRequest);

        HttpGet userRequest = new HttpGet("https://serverlessohapi.azurewebsites.net/api/GetUser?userId="+userId);

        CloseableHttpResponse userResponse = client.execute(userRequest);

        if(productResponse.getStatusLine().getStatusCode()!=200){
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
            .body("Bad Product Id")
            .build();
        } else if(userResponse.getStatusLine().getStatusCode()!=200){
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
            .body("Bad User Id")
            .build();
        } else{
            // Generate random ID
            final int id = Math.abs(new Random().nextInt());

            // Generate document
            final String jsonDocument = "{\"id\":\"" + id + "\", " +
                                        "\"userId\": \"" + userId + "\", " +
                                        "\"timeStamp\": \"" + new Timestamp(new Date().getTime()) + "\", " +
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
    } catch(Exception e){

    }
}
    return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("INternal Server Error")
                        .build();
    }
}
