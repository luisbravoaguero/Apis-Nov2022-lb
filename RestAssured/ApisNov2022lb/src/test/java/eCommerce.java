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

public class eCommerce {
    static private String url_base = "webapi.segundamano.mx";
    //https://webapi.segundamano.mx/urls/v1/public/ad-listing?lang=es

    @Test
    @Order(1)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Validar el filtrado de categorias")
    public void obtenerCategoriasFiltrado200(){
        RestAssured.baseURI = String.format("https://%s/urls/v1/public",url_base);
        String body_request = "{\n" +
                "    \"filters\": [\n" +
                "        {\n" +
                "            \"price\": \"-60000\",\n" +
                "            \"category\": \"2020\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"price\": \"60000-80000\",\n" +
                "            \"category\": \"2020\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"price\": \"80000-100000\",\n" +
                "            \"category\": \"2020\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"price\": \"100000-150000\",\n" +
                "            \"category\": \"2020\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"price\": \"150000-\",\n" +
                "            \"category\": \"2020\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .queryParam("lang","es")
                .contentType("application/json")
                .headers("Accept", "application/json, text/plain, */*")
                .body(body_request)
                .post("/ad-listing");


        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: "+body_response);

        //Junit 5 - Pruebas
        System.out.println("Status response: "+response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //VALIDAR EL BODY REPONSE NO VENGA VACIO
        assertNotNull(body_response);

        //vValidar que el body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("url"));

        System.out.println("Tiempo de respuesta: "+response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo<2000);

        String headers_reponse = response.getHeaders().toString();
        assertTrue(headers_reponse.contains("Content-Type"));
        //System.out.println("1er Header: "+response.getHeader("Accept"));
        System.out.println("2do Header: "+response.getHeaders());

    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.TRIVIAL)
    @DisplayName("Test case: Validar el listado de Anuncios")
    public void listadoAnuncios200(){
        RestAssured.baseURI = String.format("https://%s/highlights/v1",url_base);
        Response response = given()
                .log()
                .all()
                .filter(new AllureRestAssured())
                .queryParam("prio","1")
                .queryParam("cat","2020")
                .queryParam("lim","3")
                .headers("Accept", "*/*")
                .get("/public/highlights");

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: "+body_response);

    }


}
