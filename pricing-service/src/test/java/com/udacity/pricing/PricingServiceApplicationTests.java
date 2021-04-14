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

}
