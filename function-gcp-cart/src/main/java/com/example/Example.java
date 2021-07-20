package com.example;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.BufferedWriter;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.net.HttpURLConnection;
import java.io.PrintWriter;


public class Example implements HttpFunction {

	  private static final Gson gson = new Gson();

  @Override
  public void service(HttpRequest request, HttpResponse response) throws Exception {
    PrintWriter writer = new PrintWriter(response.getWriter());
    response.setContentType("application/json");

  JsonObject body = gson.fromJson(request.getReader(), JsonObject.class);
	JsonObject json = body.get("resource").getAsJsonObject().get("obj").getAsJsonObject();
		System.out.println(json);

		System.out.println("totalPrice:" + json.get("totalPrice"));

		JsonElement totalPriceJsonElement = json.get("totalPrice");
		JsonObject totalPriceJson = totalPriceJsonElement.getAsJsonObject();
		System.out.println("currencyCode:" + totalPriceJson.get("currencyCode"));

		String currencyCode = totalPriceJson.get("currencyCode").getAsString().replace("\"", "");
		System.out.println(currencyCode);
		

		int  quantityvalue = json.getAsJsonArray("lineItems").get(0).getAsJsonObject().get("quantity").getAsInt();
		System.out.println(quantityvalue);
		
		JsonObject obj = new JsonObject();
		JsonArray actions = new JsonArray();
		
		JsonObject setCountryUS = new JsonObject();
		setCountryUS.addProperty("action", "setCountry");
		setCountryUS.addProperty("country", "US");
		
		JsonObject setCountryEU = new JsonObject();
		setCountryEU.addProperty("action", "setCountry");
		setCountryEU.addProperty("country", "EU");

		if (currencyCode.equals("USD") && quantityvalue<=10) {
			actions.add(setCountryUS);
		} else if(currencyCode.equals("EUR") && quantityvalue<=10) {
			actions.add(setCountryEU);
		}
		
		JsonArray errors = new JsonArray();
		JsonObject badrequest = new JsonObject();
		badrequest.addProperty("code", "InvalidJsonInput");
		badrequest.addProperty("message", "You can not put more than 10 items into the cart.");

		if(quantityvalue>10) {
			errors.add(badrequest);
			obj.add("errors", errors);
         writer.print(obj);
			response.setStatusCode(HttpURLConnection.HTTP_BAD_REQUEST);
		} else {
		obj.add("actions", actions);
        writer.print(obj);
		response.setStatusCode(HttpURLConnection.HTTP_OK);
		}
  }
}
