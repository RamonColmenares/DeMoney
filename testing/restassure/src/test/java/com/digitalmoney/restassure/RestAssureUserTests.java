package com.digitalmoney.restassure;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.digitalmoney.restassure.model.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestAssureUserTests {

	private static String token, refreshToken, hash;
	private static User pendingUser;
	private static UserRegisterDTO userRegisterDTO = new UserRegisterDTO(
			"Lau",
			"P.",
			"41203386",
			"testing.mail@email.com",
			"123654789",
			"123456"
	);
	private static UserRegisterDTO userRegisterFail1DTO = new UserRegisterDTO(
			"",
			"P.",
			"41203370",
			"test.mail@gmail.com",
			"123654789",
			"123456"
	);
	private static UserRegisterDTO userRegisterFail2DTO = new UserRegisterDTO(
			"Lau",
			"P.",
			"30555666",
			"lala@example.com",
			"1234567890",
			"123456"
	);
	private static UserUpdateDTO userUpdateDTO = new UserUpdateDTO(
			"Laura",
			"Panciroli",
			"",
			"",
			"",
			""
	);
	private static UserUpdateResponseDTO userUpdateResponseDTO;
	private static Long id;
	private static AccountDTO account;
	private static UserResponseDTO userResponseDTO;

	// Configuración de ExtentReports
	static ExtentSparkReporter spark = new ExtentSparkReporter("src/UserTestsReport.html");
	static ExtentReports extent;
	ExtentTest test;

	@BeforeAll
	public static void setUpReports() {
		extent = new ExtentReports();
		extent.attachReporter(spark);
	}

	@BeforeEach
	public void setUpRestAssure() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8083;
		RestAssured.basePath = "/users";
	}

	@AfterAll
	public static void tearDownReports() {
		if (extent != null) {
			extent.flush();
		}
	}

	@Tag("Smoke")
	@Test
	@Order(1)
	public void pingUsersMsSuccess200() {
		test = extent.createTest("TC-001: GET Ping Users Microservice - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Ping exitoso al microservicio de usuarios.");

		given()
				.when()
				.get("/ping")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.body(equalTo("UP"))
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(2)
	public void userRegisterSuccess200() {
		test = extent.createTest("TC-004: POST Register User - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Creación exitosa de usuario.");

		Response response = given()
				.contentType(ContentType.JSON)
				.body(userRegisterDTO)
				.post("/register")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract().response();

		// Extraigo atributos necesarios de la respuesta
		JsonPath jsonPath = response.jsonPath();
		id = jsonPath.getLong("id");
		account = jsonPath.getObject("account", AccountDTO.class);

		userResponseDTO = new UserResponseDTO(
				id,
				userRegisterDTO.firstName(),
				userRegisterDTO.lastName(),
				userRegisterDTO.dni(),
				userRegisterDTO.email(),
				userRegisterDTO.phone(),
				account
		);

		assertThat(response.getBody().as(UserResponseDTO.class)).isEqualTo(userResponseDTO);
	}

	@Tag("Smoke")
	@Test
	@Order(3)
	public void userRegisterFail1400() {
		test = extent.createTest("TC-005: POST Register User - Status Code: 400 - BAD REQUEST");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 400 - BAD REQUEST");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Creación fallida de usuario por campo firstName vacío.");

		Response response = given()
				.contentType(ContentType.JSON)
				.body(userRegisterFail1DTO)
				.post("/register")
				.then()
				.assertThat()
				.statusCode(400)
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.contentType(ContentType.JSON)
				.log().all()
				.extract().response();

		List<String> actualMessages = response.getBody().jsonPath().getList("$");
		assertThat(actualMessages, hasItem("First name is mandatory"));
	}

	@Tag("Smoke")
	@Test
	@Order(4)
	public void userRegisterFail2409() {
		test = extent.createTest("TC-016: POST Register User - Status Code: 409 - CONFLICT");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 409 - CONFLICT");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Creación fallida de usuario por utilizar un email ya registrado.");

		given()
				.contentType(ContentType.JSON)
				.body(userRegisterFail2DTO)
				.post("/register")
				.then()
				.assertThat()
				.statusCode(409)
				.statusCode(HttpStatus.SC_CONFLICT)
				.body(equalTo("It seems like this email is already registered."))
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(5)
	public void getUsersDBSuccess200() {
		test = extent.createTest("TC-002: GET Users DB - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Consulta exitosa de base de datos de usuarios.");

		Response response = given()
				.when()
				.get("/test-db")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract().response();

		List<User> usersList = response.jsonPath().getList("$", User.class);

		pendingUser = usersList.stream()
				.filter(account -> account.getId() == id)
				.findFirst()
				.orElse(null);

		hash = pendingUser.getHash();
	}

	@Tag("Smoke")
	@Test
	@Order(6)
	public void getUsersKeycloakSuccess200() {
		test = extent.createTest("TC-003: GET Users Keycloak - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Conexión exitosa del microservicio de usuarios con Keycloak.");

		given()
				.queryParam("username", userRegisterDTO.email()).
				when()
				.get("/test-keycloak")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(7)
	public void userPendingLoginFail401() {
		test = extent.createTest("TC-074: POST Login User - Status Code: 401 - UNAUTHORIZED");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 401 - UNAUTHORIZED");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Login fallido al microservicio de usuarios por falta de activación de cuenta.");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("email", userRegisterDTO.email());
		requestBody.put("password", userRegisterDTO.password());

		given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(requestBody)
				.when()
				.post("/login")
				.then()
				.statusCode(401)
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(8)
	public void userActivationSuccess200() {
		test = extent.createTest("TC-075: POST User activation- Status Code: 204 - NO CONTENT");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 204 - NO CONTENT");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Activación exitosa de usuario.");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("password", userRegisterDTO.password());

		given()
				.contentType(ContentType.JSON)
				.queryParam("hash", hash)
				.body(requestBody)
				.post("/activate")
				.then()
				.assertThat()
				.statusCode(204)
				.statusCode(HttpStatus.SC_NO_CONTENT)
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(9)
	public void userLoginSuccess200() {
		test = extent.createTest("TC-026: POST Login User - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Login exitoso al microservicio de usuarios.");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("email", userRegisterDTO.email() );
		requestBody.put("password", userRegisterDTO.password());

		Response response = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(requestBody)
				.when()
				.post("/login")
				.then()
				.statusCode(200)
				.body("token", notNullValue())
				.body("refreshToken", notNullValue())
				.log().all()
				.extract().response();

		// Extraigo atributos necesarios de la respuesta
		JsonPath jsonPath = response.jsonPath();
		token = jsonPath.get("token");
		refreshToken = jsonPath.get("refreshToken");
	}

	@Tag("Smoke")
	@Test
	@Order(10)
	public void userActiveLoginFail1401() {
		test = extent.createTest("TC-028: POST Login User - Status Code: 401 - UNAUTHORIZED");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 401 - UNAUTHORIZED");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Login fallido al microservicio de usuarios por contraseña incorrecta.");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("email", userRegisterDTO.email());
		requestBody.put("password", "123456789");

		given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(requestBody)
				.when()
				.post("/login")
				.then()
				.statusCode(401)
				.log().all()
				.extract()
				.response();
	}

	@Tag("Smoke")
	@Test
	@Order(11)
	public void userActiveLoginFail2400() {
		test = extent.createTest("TC-029: POST Login User - Status Code: 400 - BAD REQUEST");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 400 - BAD REQUEST");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Login fallido al microservicio de usuarios por campos vacíos.");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("email", "");
		requestBody.put("password", "");

		given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(requestBody)
				.when()
				.post("/login")
				.then()
				.statusCode(400)
				.log().all()
				.extract()
				.response();
	}

	@Tag("Smoke")
	@Test
	@Order(12)
	public void userGetByIDSuccess200() {
		test = extent.createTest("TC-043: GET User by ID - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de un usuario por su ID.");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.pathParams("id", id).
				when().
				get("/{id}").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract()
				.response();

		assertThat(response.getBody().as(UserResponseDTO.class)).isEqualTo(userResponseDTO);
	}

	@Tag("Smoke")
	@Test
	@Order(13)
	public void userPatchByIDSuccess200() {
		test = extent.createTest("TC-046: PATCH User by ID - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: PATCH");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Actualización exitosa de un usuario por su ID.");

		userUpdateResponseDTO = new UserUpdateResponseDTO(
				id,
				userUpdateDTO.firstName(),
				userUpdateDTO.lastName(),
				userRegisterDTO.dni(),
				userRegisterDTO.email(),
				userRegisterDTO.phone()
		);

		JSONObject request = new JSONObject();
		request.put("firstName", userUpdateDTO.firstName());
		request.put("lastName", userUpdateDTO.lastName());

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.pathParams("id", id)
                .contentType(ContentType.JSON)
				.body(request.toJSONString()).
				when().
				patch("/{id}").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract()
				.response();

		assertThat(response.getBody().as(UserUpdateResponseDTO.class)).isEqualTo(userUpdateResponseDTO);
	}

	@Tag("Smoke")
	@Test
	@Order(14)
	public void userSendEmailForPasswordUpdateSuccess200() {
		test = extent.createTest("TC-076: GET Send email for password update - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Envío exitoso de correo para la actualización de contraseña de un usuario.");

		given()
				.header("Authorization", "Bearer " + token)
				.queryParam("email",userRegisterDTO.email()).
				when().
				get("/update-password-email").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.body(equalTo("Email sent for user "+ userRegisterDTO.email()))
				.log().all()
				.extract()
				.response();
	}

	@Tag("Smoke")
	@Test
	@Order(15)
	public void userSendEmailForPasswordUpdateFail1500() {
		test = extent.createTest("TC-077: Send email for password update - Status Code: 500 - INTERNAL SERVER ERROR");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 500 - INTERNAL SERVER ERROR");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Envío fallido de correo para la actualización de contraseña de un usuario por envío de email erróneo.");

		given()
				.header("Authorization", "Bearer " + token)
				.queryParam("email","user@mail.co").
				when().
				get("/update-password-email").
				then()
				.assertThat()
				.statusCode(500)
				.body(equalTo("the user with email user@mail.co was not found"))
				.log().all()
				.extract()
				.response();
	}

	@Tag("Smoke")
	@Test
	@Order(16)
	public void userSendEmailForPasswordUpdateFail2500() {
		test = extent.createTest("TC-078: Send email for password update - Status Code: 500 - INTERNAL SERVER ERROR");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 500 - INTERNAL SERVER ERROR");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Envío fallido de correo para la actualización de contraseña de un usuario por envío de email de otro usuario.");

		given()
				.header("Authorization", "Bearer " + token)
				.queryParam("email","tester.mail.final@gmail.com").
				when().
				get("/update-password-email").
				then()
				.assertThat()
				.statusCode(500)
				.body(equalTo("you can only request your user details"))
				.log().all()
				.extract()
				.response();
	}

	@Tag("Smoke")
	@Test
	@Order(17)
	public void userValidateEmailForPasswordUpdateSuccess200() {
		test = extent.createTest("TC-079: POST Login User - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Validación exitosa de correo para la actualización de contraseña de un usuario.");

		Response response = given()
				.when()
				.get("/test-db")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract().response();

		List<User> usersList = response.jsonPath().getList("$", User.class);

		hash = usersList.stream()
				.filter(account -> account.getId() == id)
				.findFirst()
				.orElse(null).getHash();

		JSONObject request = new JSONObject();
		request.put("email", userRegisterDTO.email());
		request.put("password", "456789");

		given()
				.contentType(ContentType.JSON)
				.queryParam("hash", hash)
				.body(request)
				.post("/update-password")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.body("message", equalTo("Password updated successfully"))
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(18)
	public void userLogoutSuccess200() {
		test = extent.createTest("TC-034: POST Logout User - Status Code: 200 - OK");
		test.assignCategory("Users");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 1");
		test.assignAuthor("Laura Panciroli");
		test.info("Logout exitoso del microservicio de usuarios.");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("refreshToken", refreshToken);

		given()
				.header("Authorization", "Bearer " + token)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(requestBody)
				.when()
				.post("/me/logout")
				.then()
				.statusCode(200)
				.log().all()
				.extract().response();
	}
}
