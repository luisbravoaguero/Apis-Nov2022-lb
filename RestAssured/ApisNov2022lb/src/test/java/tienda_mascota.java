import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class tienda_mascota {
    static private String url_base = "petstore.swagger.io";

    @Test
    @Order(1)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case:Adding new pet")
    public void addNewPet200(){
        RestAssured.baseURI = String.format("https://%s/v2",url_base);
        String body_request = "{\n" +
                "  \"id\": 0,\n" +
                "  \"category\": {\n" +
                "    \"id\": 0,\n" +
                "    \"name\": \"string\"\n" +
                "  },\n" +
                "  \"name\": \"mascota\",\n" +
                "  \"photoUrls\": [\n" +
                "    \"string\"\n" +
                "  ],\n" +
                "  \"tags\": [\n" +
                "    {\n" +
                "      \"id\": 0,\n" +
                "      \"name\": \"string\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"status\": \"available\"\n" +
                "}";
        //tomatodo lo que necesario para almacenarlo y alimenta al reporte allure
        //filter(new AllureRestAssured())
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .headers("Accept","*/*")
                .body(body_request)
                .post("/pet");



        //Body response
        String body_response = response.getBody().prettyPrint();
        assertNotNull(body_response);
        assertTrue(body_response.contains("name"));
        assertTrue(body_response.contains("id"));

        //Response status validation
        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);
        assertEquals(200,response.getStatusCode());

        //Header response validation
        String headers_reponse = response.getHeaders().toString();
        assertTrue(headers_reponse.contains("Content-Type"));
        System.out.println("Printing headers: "+ response.getHeaders());
    }
}
