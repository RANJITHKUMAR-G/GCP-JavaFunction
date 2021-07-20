package com.example;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.net.HttpURLConnection;
import java.io.PrintWriter;


public class Example implements HttpFunction {

	  private static final Gson gson = new Gson();

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
   PrintWriter writer = new PrintWriter(response.getWriter());
        response.setContentType("application/json");
        JsonObject body = gson.fromJson(request.getReader(),JsonObject.class);
        JsonObject json = body.get("resource").getAsJsonObject().get("obj").getAsJsonObject();
        String type = json.get("type").getAsString().replace("\"","");
        
        JsonObject obj = new JsonObject();
        JsonArray actions = new JsonArray();

        JsonObject setCustomerEmail = new JsonObject();
        setCustomerEmail.addProperty("action", "setCustomerEmail");
        setCustomerEmail.addProperty("email", "test@gmail.com");

        if(type.equals("Order")) {
            actions.add(setCustomerEmail);
            obj.add("actions", actions);
            writer.print(obj);
            response.setStatusCode(HttpURLConnection.HTTP_OK);
        }
  }
}
