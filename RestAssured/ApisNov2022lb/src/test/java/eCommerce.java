import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Name;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class eCommerce {
    static private String url_base = "webapi.segundamano.mx";
    //https://webapi.segundamano.mx/urls/v1/public/ad-listing?lang=es
    static private String email = "ventas-lmba1@mailinator.com";
    static private String password = "ventas-lmba1";
    static private String account_id = "";
    static private String access_token = "";
    static private String uuid = "";
    static private String add_id = "";

    @Name("Obtener Token")
    private String obtenerToken(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email, password)
                .queryParam("lang","es")
                .body(body_request)
                .post();
        String body_response = response.getBody().prettyPrint();
        System.out.println("Response del Token: "+body_response);

        JsonPath jsonResponse = response.jsonPath();
        access_token = jsonResponse.get("access_token");
        System.out.println("Token: "+ access_token);
        account_id = jsonResponse.get("account.account_id");
        System.out.println("Access ID: "+account_id);

        uuid = jsonResponse.get("account.uuid");
        System.out.println("UUID: "+uuid);
        return access_token;
    }

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
    @Test
    @Order(3)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Crear usuario")
    public void CrearUsuario401(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);
        String new_user = "agente_ventas_test" + (Math.floor(Math.random()*6789)) + "@mailinator.com";
        String new_password = "12345";
        String body_request = "{\"account\":{\"email\":\""+new_user+"\"}}";
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(new_user, new_password)
                .queryParam("lang","es")
                .body(body_request)
                .post();

        assertEquals(401,response.getStatusCode());
        long tiempo = response.getTime();
        assertTrue(tiempo<2000);
    }


    @Test
    @Order(4)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case: Obtener informacion del usuario")
    public void obtenerInfoUsuario200(){
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts",url_base);

        String body_request = "{\"account\":{\"email\":\""+email+"\"}}";
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email, password)
                .queryParam("lang","es")
                .body(body_request)
                .post();
        String body_response = response.getBody().prettyPrint();
        assertEquals(200,response.getStatusCode());
        long tiempo = response.getTime();
        assertTrue(body_response.contains("account_id"));
        //assertTrue(tiempo<00);

        JsonPath jsonResponse = response.jsonPath();

        access_token = jsonResponse.get("access_token");
        System.out.println("Token: "+ access_token);

        account_id = jsonResponse.get("account.account_id");
        System.out.println("Access ID: "+account_id);

        uuid = jsonResponse.get("account.uuid");
        System.out.println("UUID: "+uuid);
    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case: Crear anuncio")
    public void crearUnAnuncio200(){

        //uuid = unique user id
        String token = obtenerToken();
        System.out.println("Token en la prueba crear anuncio: "+token);


        String body_request = "{\n" +
                "    \"images\": \"2240792730.jpg\",\n" +
                "    \"category\": \"4100\",\n" +
                "    \"subject\": \"Compra y venta e intercambioventa e intercambio\",\n" +
                "    \"body\": \"Compra y venta e intercambioventa e intercambioCompra\",\n" +
                "    \"is_new\": \"0\",\n" +
                "    \"region\": \"12\",\n" +
                "    \"municipality\": \"312\",\n" +
                "    \"area\": \"8842\",\n" +
                "    \"price\": \"155\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"807-441-5132\"\n" +
                "}";
        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up",url_base,uuid);
        Response response = given().log().all()
                .header("x-source","PHOENIX_DESKTOP")
                .header("Accept","*/*")
                .header("Content-Type","application/json")
                .auth().preemptive().basic(uuid, token)
                .body(body_request)
                .post();
        String body_response = response.getBody().prettyPrint();

        JsonPath jsonResponse = response.jsonPath();
        //add_id = jsonResponse.get("data.ad.ad_id");

    }
}
