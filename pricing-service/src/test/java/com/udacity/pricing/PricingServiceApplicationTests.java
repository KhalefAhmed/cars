package com.udacity.pricing;

import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// @TODO add tests for error handling
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PricingServiceApplicationTests {

    @LocalServerPort
    int port;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Create a price (via POST).")
    public void testCreatePrice() throws Exception {

        String currency = "DZD";
        double price = 12000.00;
        int vehicleId = 301;

    MockHttpServletRequestBuilder post = post("/prices")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .content("{\"currency\":\"" + currency +
                    "\", \"price\":\"" + price +
                    "\", \"vehicle_id\":\"" + vehicleId +
                    "\"}");

    String content = mockMvc.perform(post)
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();


    JSONObject obj = new JSONObject(content);

    Assertions.assertAll("Create Price",
            () -> Assertions.assertNotNull(obj.get("price_id")),
            () -> Assertions.assertEquals(currency, obj.get("currency")),
            () -> Assertions.assertEquals(price, obj.get("price")),
            () -> Assertions.assertEquals(vehicleId, obj.get("vehicle_id"))
    );

    }

    @Test
    @DisplayName("Delete a price.")
    public void testDeletePrice() throws Exception {

        MockHttpServletRequestBuilder createPost = post("/prices")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"currency\":\"USD\", \"price\":\"12000\", \"vehicle_id\":\"300\"}");

        MvcResult createResult = mockMvc.perform(createPost)
                .andExpect(status().isCreated())
                .andReturn();

        String content = createResult.getResponse().getContentAsString();

        JSONObject obj = new JSONObject(content);

        int priceId = (int) obj.get("price_id");

        mockMvc.perform(delete("/prices/" + priceId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/prices/" + priceId))
                .andExpect(status().isNotFound());

    }

    static class UpdatePriceArgumentsProvider implements ArgumentsProvider {

        public UpdatePriceArgumentsProvider() {}

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("currency", "EUR", "10000.00", "USD", "300"),
                    Arguments.of("vehicle_id", "310", "10000.00", "USD", "301"),
                    Arguments.of("price", "22222.22", "10000.00", "USD", "302")
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(UpdatePriceArgumentsProvider.class)
    @DisplayName("Update a price (via PATCH).")
    public void testUpdatePrice(String parameter,
                                String newValue, String price, String currency, String vehicleId) throws Exception {

        MockHttpServletRequestBuilder createPost = post("/prices")
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"currency\":\"" + currency + "\", \"price\":\"" + price +"\", \"vehicle_id\":\"" + vehicleId + "\"}");


        MvcResult createResult = mockMvc.perform(createPost)
                .andExpect(status().isCreated())
                .andReturn();

        String content = createResult.getResponse().getContentAsString();

        JSONObject obj = new JSONObject(content);
        int priceId = (int) obj.get("price_id");

        MockHttpServletRequestBuilder updatePost = patch("/prices/" + priceId)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"" + parameter + "\":\"" + newValue + "\"}");

        content = mockMvc.perform(updatePost)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        obj = new JSONObject(content);
        Assertions.assertEquals(newValue, obj.get(parameter).toString());

    }





}
