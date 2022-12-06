import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class practice_hotel {
    @Test
    @Severity(SeverityLevel.BLOCKER)
    public void create_booking_200(){
        RestAssured.baseURI = String.format("https://restful-booker.herokuapp.com/booking");

        String body_request = "{\n" +
                "    \"firstname\" : \"luis\",\n" +
                "    \"lastname\" : \"bravo\",\n" +
                "    \"totalprice\" : \"342\",\n" +
                "    \"depositpaid\" : \"232\",\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2019-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        Response response = given()
                .log().all()
                .filter(new AllureRestAssured())
                .headers("Content-Type", "application/json")
                .headers("Accept", "*/*")
                .body(body_request)
                .post();

        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: "+body_response);

        //Junit 5 - Pruebas
        System.out.println("Status response: "+response.getStatusCode());
        assertEquals(200,response.getStatusCode());

        //VALIDAR EL BODY REPONSE NO VENGA VACIO
        assertNotNull(body_response);

        //vValidar que el body response contenga algunos campos
        assertTrue(body_response.contains("bookingid"));
        assertTrue(body_response.contains("totalprice"));

        System.out.println("Tiempo de respuesta: "+response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo<2000);

        String headers_reponse = response.getHeaders().toString();
        assertTrue(headers_reponse.contains("Content-Type"));
        //System.out.println("1er Header: "+response.getHeader("Accept"));
        System.out.println("2do Header: "+response.getHeaders());


    }
}
