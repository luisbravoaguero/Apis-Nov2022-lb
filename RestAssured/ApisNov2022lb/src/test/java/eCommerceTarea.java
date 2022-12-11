import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import org.apache.http.HttpStatus;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class eCommerceTarea {

    static private String url_base = "webapi.segundamano.mx";
    static private String email = "ventas-lmba1@mailinator.com";
    static private String password = "ventas-lmba1";
    static private String account_id = "";
    static private String access_token = "";
    static private String uuid = "";
    static private String add_id = "";
    static private String account_id_solo = "";
    static private String addressID = "";
    static private String alertID = "";
    public static boolean checkIfDateIsValid(String pattern, String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
    @Test
    @Order(1)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Validar el filtrado de categorias")
    public void obtenerCategoriasFiltrado200() {
        RestAssured.baseURI = String.format("https://%s/urls/v1/public", url_base);
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
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .queryParam("lang", "es")
                        .contentType("application/json")
                        .headers("Accept", "application/json, text/plain, */*")
                        .body(body_request)
                        .post("/ad-listing");


        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el key Precio Resquest sea igual al Precio Response: ");
        //Instancia para convertir body_request string a json y obtener el valor de un campo
        JsonPath jsonObject = new JsonPath(body_request);
        //Instancia para obtener el response
        JsonPath jsonBodyResponse = response.jsonPath();
        //Almacenando los valores del campo data.urls de tipo ArrayList
        ArrayList<String> dataUrlsValue = jsonBodyResponse.get("data.urls");
        //Almancenando el resultado como booleano, preguntamos si el valor 600000 existe en el body_request y en el body_response
        boolean bodyRequestPriceField = jsonObject.getJsonObject("filters[0].price").toString().contains("60000");
        boolean bodyResponsePriceField = dataUrlsValue.get(0).contains("60000");
        //Evaluando la prueba mediante assertEquals
        assertEquals(bodyRequestPriceField, bodyResponsePriceField);
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("url"));
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el Content-Type header response key contenga el valor [application/json]: ");
        String value_headers_response = response.getHeaders().getValue("Content-Type");
        assertTrue(value_headers_response.contains("application/json"));
        //El siguiente codigo obtiene los headers, pero no lo uso porque se lista header que no declaré
        /*Headers allHeaders = response.getHeaders();
        for(Header header : allHeaders) {
            if(header.getValue() == allHeaders.getValue("Content-Type")){
            //System.out.print(header.getName() +" : ");
            System.out.print(header.getValue());
            }
        }*/
        System.out.println(" Passed");

        System.out.print("Validar que el campo urls contenga el valor 60000 en la respuesta: ");
        JsonPath jsonResp = response.jsonPath();
        //Almancenando los valores de data.urls como ArrayList
        ArrayList<String> dataUrlsValues = jsonResp.get("data.urls");
        //Evaluando el primer item del ArrayList
        assertTrue(dataUrlsValues.get(0).contains("60000"));
        //El siguiente código obtiene todos los item almacenados en el ArrayList dataUrlsValues
        /*for (int i = 0; i<dataUrlsValue.size();i++){
            System.out.println("Array-data ["+i+"]: " +dataUrlsValue.get(i));
        }*/
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(2)
    @Severity(SeverityLevel.TRIVIAL)
    @DisplayName("Test case: Validar el listado de Anuncios")
    public void listadoAnuncios200() {
        RestAssured.baseURI = String.format("https://%s/highlights/v1", url_base);
        Response response = given()
                .log()
                .all()
                .filter(new AllureRestAssured())
                .queryParam("prio", "1")
                .queryParam("cat", "2020")
                .queryParam("lim", "3")
                .headers("Accept", "*/*")
                .get("/public/highlights");

        String body_response = response.getBody().prettyPrint();

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("list_id"));
        assertTrue(body_response.contains("thumbnail"));
        assertTrue(body_response.contains("subject"));
        assertTrue(body_response.contains("prices"));
        assertTrue(body_response.contains("code"));
        assertTrue(body_response.contains("currency"));
        assertTrue(body_response.contains("path"));
        assertTrue(body_response.contains("locations"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor de algunos campos del body response sean los esperados: ");
        JsonPath jBodyResponse = response.jsonPath();
        String dataPricesCurrency = jBodyResponse.get("data.items[0].prices[0].currency");
        assertEquals("MXN",dataPricesCurrency);
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");
    }


    @Test
    @Order(3)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case: Obtener informacion del usuario")
    public void obtenerInfoUsuario200() {
        RestAssured.baseURI = String.format("https://%s/nga/api/v1.1/private/accounts", url_base);

        String body_request = "{\"account\":{\"email\":\"" + email + "\"}}";
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .auth().preemptive().basic(email, password)
                .queryParam("lang", "es")
                .body(body_request)
                .post();
        String body_response = response.getBody().prettyPrint();
        assertEquals(200, response.getStatusCode());

        JsonPath jsonResponses = response.jsonPath();

        access_token = jsonResponses.get("access_token");
        System.out.println("Token here: " + access_token);
        account_id = jsonResponses.get("account.account_id");
        System.out.println("Access ID here: " + account_id);
        uuid = jsonResponses.get("account.uuid");
        System.out.println("UUID here: " + uuid);
        String account_id_solo_local = account_id.substring(18);
        account_id_solo = account_id_solo_local;
        System.out.println("Account ID solo here: "+account_id_solo);


        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("access_token"));
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("email_verified"));
        assertTrue(body_response.contains("uuid"));
        assertTrue(body_response.contains("token_type"));
        System.out.println(" Passed");

        System.out.print("Validar que los valores de [access_token], [account_id], [uuid] y [account_id_solo] no sean nulos: ");
        assertNotNull(access_token);
        assertNotNull(account_id);
        assertNotNull(uuid);
        assertNotNull(account_id_solo);
        System.out.println(" Passed");

        System.out.print("Validar que los datos del Body Request sean igual en el Body Response: ");
        //Obteniendo los datos del Body Request
        JsonPath jsonRequestEmail = new JsonPath(body_request);
        String emailRequest = jsonRequestEmail.getJsonObject("account.email");
        //Obteniendo los datos del Body Response
        JsonPath jsonResponse = response.jsonPath();
        String emailResponse = jsonResponse.get("account.email");
        assertEquals(emailRequest,emailResponse);
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");

    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Editar datos del usuario")
    public void editarUsuario201() {
        System.out.println("Access ID here: " + account_id);
        Faker faker = new Faker();
        String firstName = faker.name().firstName();
        String phone_number = faker.phoneNumber().cellPhone();
        String booleanState = faker.random().nextBoolean().toString();

        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s", url_base, account_id);
        String body_request = "{\n" +
                "    \"account\": {\n" +
                "        \"name\": \"" + firstName + "\",\n" +
                "        \"phone\": \"9876543210\",\n" +
                "        \"professional\": " + booleanState + ",\n" +
                "        \"phone_hidden\": " + booleanState + "\n" +
                "    }\n" +
                "}";

        System.out.println("Body Request here: " + body_request);

        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .contentType("application/json")
                .header("Accept", "*/*")
                .header("Connection", "keep-alive")
                .header("Authorization", "tag:scmcoord.com,2013:api " + access_token)
                .queryParam("lang", "es")
                .body(body_request)
                .patch();
        String body_response = response.getBody().prettyPrint();

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("name"));
        assertTrue(body_response.contains("phone"));
        assertTrue(body_response.contains("professional"));
        assertTrue(body_response.contains("phone_hidden"));
        assertTrue(body_response.contains("account_id"));
        assertTrue(body_response.contains("uuid"));
        assertTrue(body_response.contains("email"));
        System.out.println(" Passed");

        System.out.print("Validar que los datos del Body Request sean igual en el Body Response: ");
        //Obteniendo los datos del Body Request
        JsonPath jsonBodyRequest = new JsonPath(body_request);
        String nameRequest = jsonBodyRequest.getJsonObject("account.name");
        String phoneRequest = jsonBodyRequest.getJsonObject("account.phone");
        boolean professionalRequest = jsonBodyRequest.getJsonObject("account.professional");
        boolean phone_hiddenRequest = jsonBodyRequest.getJsonObject("account.phone_hidden");
        //Obteniendo los datos del Body Response
        JsonPath jsonBodyResponse = response.jsonPath();
        String nameResponse = jsonBodyResponse.get("account.name");
        String phoneResponse = jsonBodyResponse.get("account.phone");
        boolean professionalResponse = jsonBodyResponse.get("account.professional");
        boolean phone_hiddenResponse = jsonBodyResponse.get("account.phone_hidden");
        assertEquals(nameRequest,nameResponse);
        assertEquals(phoneRequest,phoneResponse);
        assertEquals(professionalRequest,professionalResponse);
        assertEquals(phone_hiddenRequest,phone_hiddenResponse);
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");

    }

    @Test
    @Order(5)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case: Crear anuncio")
    public void crearUnAnuncio200() {

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
        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up", url_base, uuid);
        Response response = given().log().all()
                .header("x-source", "PHOENIX_DESKTOP")
                .header("Accept", "*/*")
                .header("Content-Type", "application/json")
                .auth().preemptive().basic(uuid, access_token)
                .body(body_request)
                .post();
        String body_response = response.getBody().prettyPrint();
        System.out.println("Body response hereluis: " + body_response);

        JsonPath jsonResponse = response.jsonPath();
        add_id = jsonResponse.get("data.ad.ad_id");
        System.out.println("UUID here: " + add_id);

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3.5 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3500);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("category"));
        assertTrue(body_response.contains("region"));
        assertTrue(body_response.contains("municipality"));
        assertTrue(body_response.contains("area"));
        assertTrue(body_response.contains("price"));
        assertTrue(body_response.contains("thumbnail"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor de [add_id] no sea nulo: ");
        assertNotNull(add_id);
        System.out.println(" Passed");

        System.out.print("Validar que los datos del Body Request sean igual en el Body Response: ");
        //Obteniendo los datos del Body Request
        JsonPath jsonBodyRequest = new JsonPath(body_request);
        String imagesRequest = jsonBodyRequest.getJsonObject("images");
        String categoryRequest = jsonBodyRequest.getJsonObject("category");
        String subjectRequest = jsonBodyRequest.getJsonObject("subject");

        //Obteniendo los datos del Body Response
        JsonPath jsonBodyResponse = response.jsonPath();
        String imagesResponse = jsonBodyResponse.get("data.ad.thumbnail.path");
        String categoryResponse = jsonBodyResponse.get("data.ad.category.code");
        String subjectResponse = jsonBodyResponse.get("data.ad.subject");
        String[] parts = imagesResponse.split("/");
        //Asserts
        assertEquals(imagesRequest,parts[1]);
        assertEquals(categoryRequest,categoryResponse);
        assertEquals(subjectRequest,subjectResponse);
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");



    }

    @Test
    @Order(6)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Ver anuncio creado")
    public void verAnuncio200() {
        System.out.println("account_id_solo here: " + account_id_solo);
        System.out.println("add_id here: " + add_id);

        RestAssured.baseURI = String.format("https://%s/ad-stats/v1/public/accounts/%s/ads/%s", url_base, account_id_solo, add_id);
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .header("Accept", "*/*")
                .get();
        String body_response = response.getBody().prettyPrint();

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 1.5 segundo: "+ timeResponse);
        assertTrue(timeResponse < 1500);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("list_id"));
        assertTrue(body_response.contains("total"));
        assertTrue(body_response.contains("views"));
        assertTrue(body_response.contains("mails"));
        assertTrue(body_response.contains("count"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor de [list_id] no sea nulo: ");
        JsonPath jsonResponse = response.jsonPath();
        String list_idResponse = jsonResponse.get("list_id");
        assertNotNull(list_idResponse);
        System.out.println(" Passed");

        System.out.print("Validar que el valor [list_id] del Body Response sea igual a [add_id]: ");
        //Obteniendo los datos del Body Response
        assertEquals(add_id, list_idResponse);
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");

    }

    @Test
    @Order(7)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Editar anuncio creado")
    public void editarAnuncio200(){
        RestAssured.baseURI = String.format("https://%s/v2/accounts/%s/up/%s",url_base, uuid, add_id);
        String body_request = "{\n" +
                "    \"category\": \"8143\",\n" +
                "    \"subject\": \"Organizamos tu evento y mas\",\n" +
                "    \"body\": \"trabajamos todo tipo de eventos, desde bautizos hasta bodas. Pregunte sin compromiso. Hacemos Cotizaciones\",\n" +
                "    \"region\": \"5\",\n" +
                "    \"municipality\": \"51\",\n" +
                "    \"area\": \"36611\",\n" +
                "    \"price\": \"20000\",\n" +
                "    \"phone_hidden\": \"true\",\n" +
                "    \"show_phone\": \"false\",\n" +
                "    \"contact_phone\": \"76013183\"\n" +
                "}";

        Response response = given()
                .log()
                .all()
                .contentType("application/json")
                .header("Accept","*/*")
                .header("Connection","keep-alive")
                .header("x-source","PHOENIX_DESKTOP")
                .auth().preemptive().basic(uuid, access_token)
                .body(body_request)
                .put();
        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 4 segundo: "+ timeResponse);
        assertTrue(timeResponse < 4000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("ad_id"));
        assertTrue(body_response.contains("subject"));
        assertTrue(body_response.contains("category"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor de [ad_id] no sea nulo: ");
        JsonPath jsonResponse = response.jsonPath();
        String ad_idResponse = jsonResponse.get("data.ad.ad_id");
        assertNotNull(ad_idResponse);
        System.out.println(" Passed");

        System.out.print("Validar que el valor [ad_id] del Body Response sea igual a [add_id]: ");
        //Obteniendo los datos del Body Response
        assertEquals(add_id, ad_idResponse);
        System.out.println(" Passed");

        System.out.print("Validar que los datos del Body Request sean igual en el Body Response: ");
        //Obteniendo los datos del Body Request
        JsonPath jsonBodyRequest = new JsonPath(body_request);
        String categoryRequest = jsonBodyRequest.getJsonObject("category");
        String subjectRequest = jsonBodyRequest.getJsonObject("subject");
        String regionRequest = jsonBodyRequest.getJsonObject("region");

        //Obteniendo los datos del Body Response
        JsonPath jsonBodyResponse = response.jsonPath();
        String categoryResponse = jsonBodyResponse.get("data.ad.category.code");
        String subjectResponse = jsonBodyResponse.get("data.ad.subject");
        String regionResponse = jsonBodyResponse.get("data.ad.region.code");

        //Asserts
        assertEquals(categoryRequest,categoryResponse);
        assertEquals(subjectRequest,subjectResponse);
        assertEquals(regionRequest,regionResponse);
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");

    }

    @Test
    @Order(8)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Ver anuncio creado Despues de Edicion")
    public void VerAnuncioDespuesDeEdicion200(){
        RestAssured.baseURI = String.format("https://%s/ad-stats/v1/public/accounts/%s/ads/%s",url_base, account_id_solo, add_id);

        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .header("Accept","*/*")
                        .get();

        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 1.5 segundo: "+ timeResponse);
        assertTrue(timeResponse < 1500);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("list_id"));
        assertTrue(body_response.contains("total"));
        assertTrue(body_response.contains("views"));
        assertTrue(body_response.contains("mails"));
        assertTrue(body_response.contains("count"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor de [list_id] no sea nulo: ");
        JsonPath jsonResponse = response.jsonPath();
        String list_idResponse = jsonResponse.get("list_id");
        assertNotNull(list_idResponse);
        System.out.println(" Passed");

        System.out.print("Validar que el valor [list_id] del Body Response sea igual a [add_id]: ");
        //Obteniendo los datos del Body Response
        assertEquals(add_id, list_idResponse);
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Borrar anuncio creado")
    public void BorrarAnuncio403(){

        String body_resquest = "{\n" +
                "    \"delete_reason\": {\n" +
                "        \"code\": \"2\"\n" +
                "    }\n" +
                "}";
        RestAssured.baseURI = String.format("https://%s/nga/api/v1%s/klfst/%s",url_base, account_id, add_id);
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .contentType("application/json")
                        .header("Accept","*/*")
                        .header("Authorization", "tag:scmcoord.com,2013:api "+access_token)
                        .body(body_resquest)
                        .delete();
        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 403: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a FORBIDDEN: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("FORBIDDEN"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 1.5 segundo: "+ timeResponse);
        assertTrue(timeResponse < 1500);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("error"));
        assertTrue(body_response.contains("causes"));
        assertTrue(body_response.contains("code"));
        assertTrue(body_response.contains("object"));
        System.out.println(" Passed");

        System.out.print("Validar que en el Response el key [causes] sea igual a [ERROR_AD_ALREADY_DELETED]: ");
        //Obteniendo los datos del Body Response
        JsonPath jsonResponse = response.jsonPath();
        String errorCauseCodeResponse = jsonResponse.get("error.causes[0].code");
        assertEquals("ERROR_AD_ALREADY_DELETED",errorCauseCodeResponse);
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(10)
    @Severity(SeverityLevel.TRIVIAL)
    @DisplayName("Test case: Obtener listado de direcciones")
    public void ObtenerListadoDirecciones200(){

    RestAssured.baseURI = String.format("https://%s/addresses/v1/get",url_base);
    Response response =
            given()
                    .log()
                    .all()
                    .filter(new AllureRestAssured())
                    .auth().preemptive().basic(uuid,access_token)
                    .header("Accept","*/*")
                    .header("Connection","keep-alive")
                    .header("Accept-Language","es-ES,es;q=0.9")
                    .get();
        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 1 segundo: "+ timeResponse);
        assertTrue(timeResponse < 1000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("addresses"));
        assertTrue(body_response.contains("exteriorInfo"));
        assertTrue(body_response.contains("interiorInfo"));
        assertTrue(body_response.contains("region"));
        assertTrue(body_response.contains("municipality"));
        assertTrue(body_response.contains("area"));
        assertTrue(body_response.contains("alias"));
        assertTrue(body_response.contains("contact"));
        assertTrue(body_response.contains("phone"));
        assertTrue(body_response.contains("zipCode"));
        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(11)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Crear una direccion")
    public void CrearDireccion201() {

        RestAssured.baseURI = String.format("https://%s/addresses/v1/create",url_base);

        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .formParam("contact","Agente de ventas")
                        .formParam("phone","9876543210")
                        .formParam("rfc","XAXX010101000")
                        .formParam("zipCode","11011")
                        .formParam("exteriorInfo","Morelia 45")
                        .formParam("interiorInfo","2")
                        .formParam("region","11")
                        .formParam("municipality","300")
                        .formParam("area","8082")
                        .formParam("alias","Alias nuevo en inte")
                        .contentType("application/x-www-form-urlencoded")
                        .header("Accept","*/*")
                        .auth().preemptive().basic(uuid,access_token)
                        .post();

        String body_response = response.getBody().prettyPrint();


        JsonPath jsonResponse = response.jsonPath();
        addressID = jsonResponse.get("addressID");

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 201: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_CREATED, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a Created: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("Created"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 1 segundo: "+ timeResponse);
        assertTrue(timeResponse < 1000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("addressID"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor de [addressID] no sea nulo: ");
        assertNotNull(addressID);
        System.out.println(" Passed");

        System.out.print("Validar que el addressID del Body Response sea igual a la varibale addressID: ");
        String localAddressID = jsonResponse.get("addressID");
        assertEquals(addressID, localAddressID);
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");

    }

    @Test
    @Order(12)
    @Severity(SeverityLevel.TRIVIAL)
    @DisplayName("Test case: Ver direccion creada")
    public void verDireccionCreada200(){

        RestAssured.baseURI = String.format("https://%s/addresses/v1/get/%s",url_base,addressID);
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .contentType("application/json")
                        .auth().preemptive().basic(uuid, access_token)
                        .header("Accept","*/*")
                        .get();
        String body_response = response.getBody().prettyPrint();

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("addresses"));
        assertTrue(body_response.contains(addressID));
        assertTrue(body_response.contains("exteriorInfo"));
        assertTrue(body_response.contains("interiorInfo"));
        assertTrue(body_response.contains("region"));
        assertTrue(body_response.contains("municipality"));
        assertTrue(body_response.contains("area"));
        assertTrue(body_response.contains("alias"));
        assertTrue(body_response.contains("phone"));
        assertTrue(body_response.contains("zipCode"));
        System.out.println(" Passed");

        System.out.print("Validar que el addressID del Body Response sea igual al que se envia: ");
        assertTrue(body_response.contains(addressID));
        System.out.println(" Passed");

        System.out.print("Validar que el KEY addressID del Body Response sea igual al que se envia: ");
        //Almancenado el Body Response como una cadena usando el metodo asString
        String responseBodyAsString = response.asString();

        //Instanciando la clase JsonPath pasandole como parametro la cadena responseBodyAsString
        JsonPath jBodyResponse = new JsonPath(responseBodyAsString);

        //Obteniendo como cadena lo que sigue despues de addresses key
        String value = jBodyResponse.getString( "addresses" );

        //Separando los elementos del array para almacenarlos en spliting
        String[] spliting = value.split(":");

        //Obteniendo el primer elemento del array y obteniendo el substring del elemento desde la posicion 1
        String addressIDKey = spliting[0].substring(1);

        //Asserting
        assertEquals(addressID,addressIDKey);
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(13)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Editar direccion creada")
    public void editarDireccionCreada200() {
        Faker faker = new Faker();
        String alias = faker.dragonBall().character();
        String contact = faker.name().fullName();

        RestAssured.baseURI = String.format("https://%s/addresses/v1/modify/%s",url_base, addressID);
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .formParam("contact",contact)
                        .formParam("phone","9876543210")
                        .formParam("rfc","XAXX010101000")
                        .formParam("zipCode","11011")
                        .formParam("exteriorInfo","Morelia 45")
                        .formParam("interiorInfo","2")
                        .formParam("region","11")
                        .formParam("municipality","300")
                        .formParam("area","8082")
                        .formParam("alias",alias)
                        .contentType("application/x-www-form-urlencoded")
                        .headers("Accept","*/*")
                        .headers("Origin","https://"+url_base)
                        .headers("refe","https:"+url_base)
                        .auth().preemptive().basic(uuid,access_token)
                        .put();
        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el Body Response contenga el key [message]: ");
        assertTrue(body_response.contains("message"));
        System.out.println(" Passed");

        System.out.print("Validar que el Body Response contenga el addressID enviado: ");
        assertTrue(body_response.contains(addressID));
        System.out.println(" Passed");

        System.out.print("Validar que el Body Response contenga el texto [modified correctly]: ");
        assertTrue(body_response.contains("modified correctly"));
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(14)
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test case: Eliminar direccion creada")
    public void EliminarDireccionCreada200(){

        RestAssured.baseURI = String.format("https://%s/addresses/v1/delete/%s",url_base,addressID);
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .auth().preemptive().basic(uuid,access_token)
                        .header("Accept","*/*")
                        .delete();
        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 1 segundo: "+ timeResponse);
        assertTrue(timeResponse < 1000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el Body Response contenga el key [message]: ");
        assertTrue(body_response.contains("message"));
        System.out.println(" Passed");

        System.out.print("Validar que el Body Response contenga el addressID enviado: ");
        assertTrue(body_response.contains(addressID));
        System.out.println(" Passed");

        System.out.print("Validar que el Body Response contenga el texto [deleted correctly]: ");
        assertTrue(body_response.contains("deleted correctly"));
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(15)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Consultar si anuncio fue publicado")
    public void consultarSiAnuncioFuePublicado200(){

        String body_request = "{\n" +
                "    \"ids\": [\n" +
                "        \""+add_id+"\"\n" +
                "    ]\n" +
                "}";
        RestAssured.baseURI = String.format("https://%s/urls/v1/public/ad-view?lang=es",url_base);
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .contentType("application/json")
                        .header("Accept","application/json, text/plain, */*")
                        .body(body_request)
                        .queryParam("lang","es")
                        .post();
        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("urls"));
        assertTrue(body_response.contains("errors"));
        assertTrue(body_response.contains("NotFound"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor del campo [ad_id] del Body Response sea igual a Body Request: ");
        //Primera forma de validar, usando assertTrue, bodyresponse y contains metodo
        assertTrue(body_response.contains(add_id));
        //Segunda form de validar, usando assertEquals, comparando el valor del body request y response
        JsonPath jsonPath = response.jsonPath();
        String localAd_id = jsonPath.get("data.errors.NotFound[0]");
        assertEquals(add_id,localAd_id);
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(16)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Crear Alerta")
    public void crearAlerta200(){
        String body_request = "{\n" +
                "    \"ad_listing_service_filters\": {\n" +
                "        \"region\": \"4\",\n" +
                "        \"municipality\": \"47\",\n" +
                "        \"category_lv0\": \"5000\",\n" +
                "        \"category_lv1\": \"5040\"\n" +
                "    }\n" +
                "}";
        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert",url_base,uuid);
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .contentType("application/json")
                        .header("Accept","application/json, text/plain, */*")
                        .header("authority","webapi.segundamano.mx")
                        .header("Accept-Language","es-ES,es;q=0.9")
                        .header("origin","https://www.segundamano.mx")
                        .header("referer","https://www.segundamano.mx")
                        .auth().preemptive().basic(uuid,access_token)
                        .body(body_request)
                        .post();
        String body_response = response.getBody().prettyPrint();
        System.out.println("Pretty Body Response here: "+body_response);

        JsonPath jsonResponse = response.jsonPath();

        alertID = jsonResponse.get("data.alert.id");
        System.out.println("AlertID here: " + alertID);

        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 3 segundo: "+ timeResponse);
        assertTrue(timeResponse < 3000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el body response contenga algunos campos: ");
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("region"));
        assertTrue(body_response.contains("municipality"));
        assertTrue(body_response.contains("category_lv0"));
        assertTrue(body_response.contains("category_lv1"));
        System.out.println(" Passed");

        System.out.print("Validar que el valor de [alert_id] Response no sea nulo: ");
        assertNotNull(alertID);
        System.out.println(" Passed");

        System.out.print("Validar que los datos del Body Request sean igual en el Body Response: ");
        //Obteniendo los datos del Body Request
        JsonPath jsonBodyRequest = new JsonPath(body_request);
        String regionRequest = jsonBodyRequest.getJsonObject("ad_listing_service_filters.region");
        String municipalityRequest = jsonBodyRequest.getJsonObject("ad_listing_service_filters.municipality");
        String category_lv0Request = jsonBodyRequest.getJsonObject("ad_listing_service_filters.category_lv0");
        String category_lv1Request = jsonBodyRequest.getJsonObject("ad_listing_service_filters.category_lv1");

        //Obteniendo los datos del Body Response
        JsonPath jsonBodyResponse = response.jsonPath();
        String regionResponse = jsonBodyResponse.get("data.alert.ad_listing_service_filters.region");
        String municipalityResponse = jsonBodyResponse.get("data.alert.ad_listing_service_filters.municipality");
        String category_lv0Response = jsonBodyResponse.get("data.alert.ad_listing_service_filters.category_lv0");
        String category_lv1Response = jsonBodyResponse.get("data.alert.ad_listing_service_filters.category_lv1");

        //Asserts
        assertEquals(regionRequest,regionResponse);
        assertEquals(municipalityRequest,municipalityResponse);
        assertEquals(category_lv0Request,category_lv0Response);
        assertEquals(category_lv1Request,category_lv1Response);
        System.out.println(" Passed");

        System.out.print("Validar que el formato de la fecha de creacion de la alerta en Body Response sea correcto: ");
        String patternDate = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS'Z'";
        String created_at = jsonBodyResponse.get("data.alert.created_at").toString();
        System.out.println("date here:  "+created_at);
        boolean result = checkIfDateIsValid(patternDate,created_at);
        assertTrue(result);
        System.out.println(" Passed");

        System.out.println("----------------------End---------------------");
    }

    @Test
    @Order(17)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Borrar Alerta")
    public void borrarAlerta200(){

        RestAssured.baseURI = String.format("https://%s/alerts/v1/private/account/%s/alert/%s",url_base,uuid,alertID);
        Response response =
                given()
                        .log()
                        .all()
                        .filter(new AllureRestAssured())
                        .contentType("application/json")
                        .header("Accept","application/json, text/plain, */*")
                        .header("authority","webapi.segundamano.mx")
                        .header("Accept-Language","es-ES,es;q=0.9")
                        .header("origin","https://www.segundamano.mx")
                        .header("referer","https://www.segundamano.mx")
                        .auth().preemptive().basic(uuid,access_token)
                        .delete();
        String body_response = response.getBody().prettyPrint();
        System.out.println("--------------------Tests---------------------");

        System.out.print("Validar que el codigo de respuesta sea igual a 200: "+ response.getStatusCode());
        //Evaluando el codigo de respuesta, usando  HttpStatus.
        assertEquals(HttpStatus.SC_OK, response.getStatusCode());
        System.out.println(" Passed");

        System.out.print("Validar que el estado de la respuesta sea igual a OK: "+ response.getStatusLine());
        assertTrue(response.getStatusLine().contains("OK"));
        System.out.println(" Passed");

        long timeResponse = response.getTime();
        System.out.print("Validar que el tiempo de respuesta sea menor a 1 segundo: "+ timeResponse);
        assertTrue(timeResponse < 1000);
        System.out.println(" Passed");

        System.out.print("Validar que el body response no venga vacio: ");
        assertNotNull(body_response);
        System.out.println(" Passed");

        System.out.print("Validar que el header response contenga el key Content-Type: ");
        String headers_response = response.getHeaders().toString();
        assertTrue(headers_response.contains("Content-Type"));
        System.out.println(" Passed");

        System.out.print("Validar que el Body Response contenga los keys [data] y [status]: ");
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("status"));
        System.out.println(" Passed");

        System.out.print("Validar que el key status contenga el valor [ok] en el Body Response: ");
        //Primera forma de validar, usando assertTrue con el body_response y contains metodo
        assertTrue(body_response.contains("ok"));
        //Segunda forma de validar, usando assertEquals despues de obtener el valor key status del Body Response
        JsonPath jsonPath = response.jsonPath();
        String okStatus = jsonPath.get("data.status");
        assertEquals("ok",okStatus);
        System.out.println(" Passed");
        System.out.println("----------------------End---------------------");
    }

}
