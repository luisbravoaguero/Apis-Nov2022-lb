import com.github.javafaker.Faker;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

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
        Response response = given().log().all()
                .filter(new AllureRestAssured())
                .queryParam("lang", "es")
                .contentType("application/json")
                .headers("Accept", "application/json, text/plain, */*")
                .body(body_request)
                .post("/ad-listing");


        String body_response = response.getBody().prettyPrint();
        System.out.println("Response: " + body_response);

        //Junit 5 - Pruebas
        System.out.println("Status response: " + response.getStatusCode());
        assertEquals(200, response.getStatusCode());

        //VALIDAR EL BODY REPONSE NO VENGA VACIO
        assertNotNull(body_response);

        //vValidar que el body response contenga algunos campos
        assertTrue(body_response.contains("data"));
        assertTrue(body_response.contains("url"));

        System.out.println("Tiempo de respuesta: " + response.getTime());
        long tiempo = response.getTime();
        assertTrue(tiempo < 2000);

        String headers_reponse = response.getHeaders().toString();
        assertTrue(headers_reponse.contains("Content-Type"));
        //System.out.println("1er Header: "+response.getHeader("Accept"));
        System.out.println("2do Header: " + response.getHeaders());

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
        System.out.println("Response: " + body_response);
        //System.out.println("Token: "+ access_token);


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

        JsonPath jsonResponse = response.jsonPath();

        access_token = jsonResponse.get("access_token");
        System.out.println("Token here: " + access_token);

        account_id = jsonResponse.get("account.account_id");
        System.out.println("Access ID here: " + account_id);

        uuid = jsonResponse.get("account.uuid");
        System.out.println("UUID here: " + uuid);

        String account_id_solo_local = account_id.substring(18);
        account_id_solo = account_id_solo_local;
        System.out.println("Account ID solo here: "+account_id_solo);

    }

    @Test
    @Order(4)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Editar datos del usuario")
    public void editarUsuario201() {

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
        System.out.println("Body response hereluis: " + body_response);
        assertEquals(200, response.getStatusCode());
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
        System.out.println("Response here: " + body_response);
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
        System.out.println("Pretty Body Response here: "+body_response);
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
        System.out.println("Pretty Response Body here: "+body_response);
    }

    @Test
    @Order(9)
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Test case: Borrar anuncio creado")
    public void BorrarAnuncio403(){
        //https://{{url_base}}/nga/api/v1/{{account_id}}/klfst/{{ad_id}}
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
        System.out.println("Pretty Body Response here: " +body_response);
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
        System.out.println("Pretty Body Response: "+body_response);
    }

    @Test
    @Order(11)
    @Severity(SeverityLevel.MINOR)
    @DisplayName("Test case: Crear una direccion")
    public void CrearDireccion200() {

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
        System.out.println("Body pretty: " +body_response);

        JsonPath jsonResponse = response.jsonPath();
        addressID = jsonResponse.get("addressID");

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
        System.out.println("Body response here: "+body_response);
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
                        .header("Accept","*/*")
                        .header("Origin","https://"+url_base)
                        .header("refe","https:"+url_base)
                        .auth().preemptive().basic(uuid,access_token)
                        .put();
        String body_response = response.getBody().prettyPrint();
        System.out.println("Pretty Body Response here: " + body_response);

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
        System.out.println("Pretty Body Response: "+body_response);
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
        System.out.println("Pretty Body Response here: "+body_response);
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
        System.out.println("Pretty Body Response here: "+body_response);
    }

}
