package com.digitalmoney.restassure;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.digitalmoney.restassure.model.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.assertj.core.api.Assertions;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RestAssureAccountTests {

	private static String token;

	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	Date expirationDate = dateFormat.parse("2028-09-21");
	String startDate = "2023-06-01";
	String endDate = "2023-06-06";
	LocalDateTime startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
	LocalDateTime endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
	BigDecimal minAmount = BigDecimal.valueOf(10000);
	BigDecimal maxAmount= BigDecimal.valueOf(20000);
	TransactionActivityDTO.TransactionType transactionType1 = TransactionActivityDTO.TransactionType.income;
	TransactionActivityDTO.TransactionType transactionType2 = TransactionActivityDTO.TransactionType.expense;

	private CardDTO cardDTO = new CardDTO(
			"18274928746200220",
			"Test card RestAssure",
			expirationDate,
			"111"
	);

	private final AccountUpdateDTO accountUpdateDTO = new AccountUpdateDTO(
			"account.service.testing"
	);

	private TransferenceRequest transferenceRequest = new TransferenceRequest(
			BigDecimal.valueOf(15000),
			"0000001911100037972519",
			"0000001911100037945871",
			13L
	);

	private TransferenceRequest transferenceRequestFail1 = new TransferenceRequest(
			BigDecimal.valueOf(0),
			"0000001911100037972519",
			"0000001911100037945871",
			13L
	);

	private TransferenceRequest transferenceRequestFail2 = new TransferenceRequest(
			BigDecimal.valueOf(15000),
			"0000001911100037972519",
			"0000001911100037945871",
			1L
	);

	private static Long userId, accountId, cardId, transactionId;
	private static AccountResponseDTO accountResponseDTO, accountUpdateResponseDTO;
	private static CardAddResponseDTO cardAddResponseDTO;
	private static CardResponseDTO cardResponseDTO;
	private static TransactionActivityDTO transactionActivityDTO;
	private static TransactionResponseDTO transactionResponseDTO;
	private static TransactionDetailDTO transactionDetailDTO;

	// Configuración de ExtentReports
	static ExtentSparkReporter spark = new ExtentSparkReporter("src/AccountTestsReport.html");
	static ExtentReports extent;
	ExtentTest test;

	RestAssureAccountTests() throws ParseException {
	}

	@BeforeAll
	public static void setUpReports() {
		extent = new ExtentReports();
		extent.attachReporter(spark);
	}

	@BeforeEach
	public void setUpRestAssure() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 8083;
		RestAssured.basePath = "/accounts";
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
	public void getAccountsDBSuccess200() {
		test = extent.createTest("TC-057: GET Accounts DB - Status Code: 200 - OK")
				.assignCategory("Accounts")
				.assignCategory("Suite: Smoke")
				.assignCategory("Request Method: GET")
				.assignCategory("Status Code: 200 - OK")
				.assignCategory("Sprint: 2")
				.assignAuthor("Laura Panciroli")
				.info("Consulta exitosa de base de datos de cuentas.");

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

		List<AccountResponseDTO> accountList = response.jsonPath().getList("$", AccountResponseDTO.class);
		accountResponseDTO = accountList.stream()
				.filter(account -> account.userId() == 41)
				.findFirst()
				.orElse(null);

		if (accountResponseDTO != null) {
			accountId = accountResponseDTO.id();
			userId = accountResponseDTO.userId();
		}
	}

	@Tag("Smoke")
	@Test
	@Order(2)
	public void getCardsDBSuccess200() {
		test = extent.createTest("TC-058: GET Cards DB - Status Code: 200 - OK")
				.assignCategory("Accounts")
				.assignCategory("Suite: Smoke")
				.assignCategory("Request Method: GET")
				.assignCategory("Status Code: 200 - OK")
				.assignCategory("Sprint: 2")
				.assignAuthor("Laura Panciroli")
				.info("Consulta exitosa de base de datos de tarjetas.");

		given()
				.when()
				.get("/test-db2")
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
	@Order(3)
	public void accountGetByUserIDSuccess200() {
		test = extent.createTest("TC-037A: GET User's account by user's ID - Status Code: 200 - OK")
				.assignCategory("Accounts")
				.assignCategory("Suite: Smoke")
				.assignCategory("Request Method: GET")
				.assignCategory("Status Code: 200 - OK")
				.assignCategory("Sprint: 2")
				.assignAuthor("Laura Panciroli")
				.info("Obtención exitosa de la cuenta de un usuario por su ID.");

		Response response = given()
				.pathParams("userId", userId)
				.when()
				.get("/user/{userId}")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract().response();

		AccountResponseDTO responseDTO = response.getBody().as(AccountResponseDTO.class);
		assertEquals(accountResponseDTO.id(), responseDTO.id());
		assertEquals(accountResponseDTO.userId(), responseDTO.userId());
		assertEquals(accountResponseDTO.cvu(), responseDTO.cvu());
		assertEquals(accountResponseDTO.alias(), responseDTO.alias());
		assertEquals(accountResponseDTO.balance(), responseDTO.balance());

	}

	@Tag("Smoke")
	@Test
	@Order(4)
	public void accountGetByIDSuccess200() {
		test = extent.createTest("TC-037B: GET User's account by ID - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de un usuario por su ID.");

		Response response = given()
				.pathParams("accountId", accountId).
				when().
				get("/{accountId}").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract()
				.response();

		AccountResponseDTO responseDTO = response.getBody().as(AccountResponseDTO.class);
		assertEquals(accountResponseDTO.id(), responseDTO.id());
		assertEquals(accountResponseDTO.userId(), responseDTO.userId());
		assertEquals(accountResponseDTO.cvu(), responseDTO.cvu());
		assertEquals(accountResponseDTO.alias(), responseDTO.alias());
		assertEquals(accountResponseDTO.balance(), responseDTO.balance());
	}

	@Tag("Smoke")
	@Test
	@Order(5)
	public void accountPatchByIDSuccess200() {
		test = extent.createTest("TC-059: PATCH Account by ID - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: PATCH");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Actualización exitosa de una cuenta por su ID.");

		accountUpdateResponseDTO = new AccountResponseDTO(
				accountId,
				userId,
				accountResponseDTO.cvu(),
				accountUpdateDTO.alias(),
				accountResponseDTO.balance()
		);

		JSONObject request = new JSONObject();
		request.put("alias", accountUpdateDTO.alias());

		Response response = given()
				.pathParams("accountId", accountId)
				.contentType(ContentType.JSON)
				.body(request.toJSONString()).
				when().
				patch("/{accountId}").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract()
				.response();

		assertThat(response.getBody().as(AccountResponseDTO.class)).isEqualTo(accountUpdateResponseDTO);
	}

	@Tag("Smoke")
	@Test
	@Order(6)
	public void cardAdditionSuccess200() {
		test = extent.createTest("TC-051: POST Add new Card - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Agregado exitoso de una tarjeta a una cuenta de usuario.");

		Response response = given()
				.pathParams("accountId", accountId)
				.contentType(ContentType.JSON)
				.body(cardDTO)
				.post("/{accountId}/cards")
				.then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract().response();

		// Extraigo atributos necesarios de la respuesta
		JsonPath jsonPath = response.jsonPath();
		cardId = jsonPath.getLong("id");
		String cvvEncoded = jsonPath.getString("cvv");

		cardAddResponseDTO = new CardAddResponseDTO(
				cardId,
				cardDTO.cardNumber(),
				cardDTO.cardHolder(),
				cardDTO.expirationDate(),
				cvvEncoded,
				accountUpdateResponseDTO
		);

		assertThat(response.getBody().as(CardAddResponseDTO.class)).isEqualTo(cardAddResponseDTO);
	}

	@Tag("Smoke")
	@Test
	@Order(7)
	public void cardGetByIDSuccess200() {
		test = extent.createTest("TC-060: GET Card by ID - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de una tarjeta por su ID.");

		Response response = given()
				.pathParams("accountId", accountId)
				.pathParams("cardId", cardId).
				when().
				get("/{accountId}/cards/{cardId}").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract()
				.response();

		cardResponseDTO = new CardResponseDTO(
				cardDTO.cardNumber(),
				cardDTO.cardHolder(),
				cardDTO.expirationDate()
		);

		assertThat(response.getBody().as(CardResponseDTO.class)).isEqualTo(cardResponseDTO);
	}

	@Tag("Smoke")
	@Test
	@Order(8)
	public void cardGetAllByAccountIDSuccess200() {
		test = extent.createTest("TC-048: GET All Cards of the account - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de todas las tarjetas de una cuenta.");

		given()
				.pathParams("accountId", accountId).
				when().
				get("/{accountId}/cards").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType(ContentType.JSON)
				.log().all()
				.extract()
				.response();
	}

	@Tag("Smoke")
	@Test
	@Order(9)
	public void cardGetNoneByAccountIDSuccess200() {
		test = extent.createTest("TC-049: GET No Cards in the account - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Consulta exitosa de una cuenta sin tarjetas.");

		given().
				when().
				get("/2/cards").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.contentType("text/plain;charset=UTF-8")
				.body(equalTo("There are no cards associated with the account."))
				.log().all()
				.extract()
				.response();
	}

	@Tag("Smoke")
	@Test
	@Order(10)
	public void cardDeletionSuccess200() {
		test = extent.createTest("TC-054: DELETE Card by ID - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: DELETE");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Eliminación exitosa de una tarjeta asociada a la cuenta de un usuario.");

		given()
				.pathParams("accountId", accountId)
				.pathParams("cardId", cardId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when().
				delete("/{accountId}/cards/{cardId}").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.body(equalTo("Card deleted successfully"))
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(11)
	public void accountGetTransactionsSuccess200() {
		test = extent.createTest("TC-040: GET Transactions - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 2");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de las últimas 5 transacciones de la cuenta de un usuario existente.");

		Map<String, String> requestBody = new HashMap<>();
		requestBody.put("email", "laup@hotmail.com");
		requestBody.put("password", "123456");

		token  = given()
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(requestBody)
				.when()
				.basePath("/users")
				.post("/login")
				.then()
				.statusCode(200)
				.body("token", notNullValue())
				.log().all()
				.extract().response()
				.jsonPath().get("token");

		given()
				.header("Authorization", "Bearer " + token)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts")
				.get("/{accountId}/transactions").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(12)
	public void accountGetHistoricTransactionsSuccess200() {
		test = extent.createTest("TC-061: GET Historic Transactions - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de todas las transacciones de la cuenta de un usuario existente.");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();

		// Extraigo atributos necesarios de la respuesta
		List<TransactionActivityDTO> transactionsList = response.jsonPath().getList("$", TransactionActivityDTO.class);
		if (!transactionsList.isEmpty()) {
			transactionActivityDTO = transactionsList.get(1);
			transactionId = transactionActivityDTO.getTransactionId();
		}
	}

	@Tag("Smoke")
	@Test
	@Order(13)
	public void accountGetHistoricTransactionsWithLimitSuccess200() {
		test = extent.createTest("TC-062: GET Historic Transactions with limit - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de un número elegido de transacciones de la cuenta de un usuario existente.");

		given()
				.header("Authorization", "Bearer " + token)
				.queryParam("limit",2)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();
	}

	@Tag("Smoke")
	@Test
	@Order(14)
	public void accountGetTransactionDetailsSuccess200() {
		test = extent.createTest("TC-063: GET Transaction details - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de los detalles de una transacción de la cuenta de un usuario existente.");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.pathParams("accountId", accountId)
				.pathParams("transactionId", transactionId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity/{transactionId}").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();

		transactionDetailDTO = response.getBody().as(TransactionDetailDTO.class);
		assertEquals(transactionDetailDTO.getTransactionDate(), transactionActivityDTO.getTransactionDate());
		BigDecimal actualAmount = new BigDecimal(String.valueOf(transactionActivityDTO.getTransactionAmount()));
		int comparisonResult = actualAmount.compareTo(transactionDetailDTO.getAmount());
		assertEquals(0, comparisonResult);
		assertEquals(transactionDetailDTO.getTransactionType(), transactionActivityDTO.getTransactionType());
	}

	@Test
	@Order(15)
	public void accountGetTransactionByAmountSuccess200() {
		test = extent.createTest("TC-064: GET Transactions by amount - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de las transacciones de la cuenta de un usuario existente según el rango de monto indicado.");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.queryParam("minAmount", minAmount)
				.queryParam("maxAmount", maxAmount)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();

		List<TransactionActivityDTO> transactionsList = response.jsonPath().getList("$", TransactionActivityDTO.class);
		if (!transactionsList.isEmpty()) {
			transactionActivityDTO = transactionsList.get(0);
			Assertions.assertThat(transactionActivityDTO.getTransactionAmount())
					.isGreaterThanOrEqualTo(minAmount)
					.isLessThanOrEqualTo(maxAmount);
		}
	}

	@Test
	@Order(16)
	public void accountGetTransactionByType1Success200() {
		test = extent.createTest("TC-065: GET Transactions by type (income) - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de las transacciones de la cuenta de un usuario existente según el tipo de transacción indicado (income).");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.queryParam("transactionType", transactionType1)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();

		List<TransactionActivityDTO> transactionsList = response.jsonPath().getList("$", TransactionActivityDTO.class);
		if (!transactionsList.isEmpty()) {
			transactionActivityDTO = transactionsList.get(0);
			assertEquals(transactionActivityDTO.getTransactionType(), transactionType1);
		}
	}

	@Test
	@Order(17)
	public void accountGetTransactionByType2Success200() {
		test = extent.createTest("TC-066: GET Transactions by type (expense) - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de las transacciones de la cuenta de un usuario existente según el tipo de transacción indicado (expense).");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.queryParam("transactionType", transactionType2)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();

		List<TransactionActivityDTO> transactionsList = response.jsonPath().getList("$", TransactionActivityDTO.class);
		if (!transactionsList.isEmpty()) {
			transactionActivityDTO = transactionsList.get(0);
			assertEquals(transactionActivityDTO.getTransactionType(), transactionType2);
		}
	}

	@Test
	@Order(18)
	public void accountGetTransactionByDateSuccess200() {
		test = extent.createTest("TC-067: GET Transactions by date - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de las transacciones de la cuenta de un usuario existente según el rango de fechas indicado.");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.queryParam("startDate", startDate)
				.queryParam("endDate", endDate)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();

		List<TransactionActivityDTO> transactionsList = response.jsonPath().getList("$", TransactionActivityDTO.class);
		if (!transactionsList.isEmpty()) {
			transactionActivityDTO = transactionsList.get(0);
			Assertions.assertThat(transactionActivityDTO.getTransactionDate())
					.isAfterOrEqualTo(startDateTime)
					.isBeforeOrEqualTo(endDateTime);
		}
	}

	@Test
	@Order(19)
	public void accountGetTransactionByAllSuccess200() {
		test = extent.createTest("TC-068: GET Transactions applying all filters - Status Code: 200 - OK");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: GET");
		test.assignCategory("Status Code: 200 - OK");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Obtención exitosa de las transacciones de la cuenta de un usuario existente aplicando todos los filtros indicados.");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.queryParam("startDate", startDate)
				.queryParam("endDate", endDate)
				.queryParam("minAmount", minAmount)
				.queryParam("maxAmount", maxAmount)
				.queryParam("transactionType", transactionType1)
				.pathParams("accountId", accountId)
				.header("Content-type", "application/json")
				.contentType(ContentType.JSON)
				.when()
				.basePath("/accounts").
				get("/{accountId}/activity").
				then()
				.assertThat()
				.statusCode(200)
				.statusCode(HttpStatus.SC_OK)
				.log().all()
				.extract().response();

		List<TransactionActivityDTO> transactionsList = response.jsonPath().getList("$", TransactionActivityDTO.class);
		if (!transactionsList.isEmpty()) {
			transactionActivityDTO = transactionsList.get(0);
			Assertions.assertThat(transactionActivityDTO.getTransactionDate())
					.isAfterOrEqualTo(startDateTime)
					.isBeforeOrEqualTo(endDateTime);
			assertEquals(transactionActivityDTO.getTransactionType(), transactionType1);
			Assertions.assertThat(transactionActivityDTO.getTransactionAmount())
					.isGreaterThanOrEqualTo(minAmount)
					.isLessThanOrEqualTo(maxAmount);
		}
	}

	@Test
	@Order(20)
	public void accountCreateTransactionSuccess201() {
		test = extent.createTest("TC-069: POST Create Transaction - Status Code: 201 - CREATED");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 201 - CREATED");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Creación exitosa de una transacción en la cuenta de un usuario existente.");

		Response response = given()
				.header("Authorization", "Bearer " + token)
				.pathParams("accountId", accountId)
				.contentType(ContentType.JSON)
				.body(transferenceRequest)
				.when()
				.basePath("/accounts").
				post("/{accountId}/transferences").
				then()
				.assertThat()
				.statusCode(201)
				.statusCode(HttpStatus.SC_CREATED)
				.log().all()
				.extract().response();

		transactionResponseDTO = response.getBody().as(TransactionResponseDTO.class);
		assertEquals(transactionResponseDTO.transactionAmount(), transferenceRequest.transactionAmount());
		assertEquals(transactionResponseDTO.destinationCvu(), transferenceRequest.destinationCvu());
		assertEquals(transactionResponseDTO.originCvu(), transferenceRequest.originCvu());
	}

	@Test
	@Order(21)
	public void accountCreateTransactionFail400() {
		test = extent.createTest("TC-070: POST Create Transaction - Status Code: 400 - BAD REQUEST");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 400 - BAD REQUEST");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Creación fallida de una transacción en la cuenta de un usuario existente por monto erróneo.");

		given()
				.header("Authorization", "Bearer " + token)
				.pathParams("accountId", accountId)
				.contentType(ContentType.JSON)
				.body(transferenceRequestFail1)
				.when()
				.basePath("/accounts").
				post("/{accountId}/transferences").
				then()
				.assertThat()
				.statusCode(400)
				.statusCode(HttpStatus.SC_BAD_REQUEST)
				.body(equalTo("Amount must be greater than zero."))
				.log().all()
				.extract().response();
	}

	@Test
	@Order(22)
	public void accountCreateTransactionFail404() {
		test = extent.createTest("TC-071: POST Create Transaction - Status Code: 404 - NOT FOUND");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 404 - NOT FOUND");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Creación fallida de una transacción en la cuenta de un usuario existente por tarjeta errónea.");

		given()
				.header("Authorization", "Bearer " + token)
				.pathParams("accountId", accountId)
				.contentType(ContentType.JSON)
				.body(transferenceRequestFail2)
				.when()
				.basePath("/accounts").
				post("/{accountId}/transferences").
				then()
				.assertThat()
				.statusCode(404)
				.statusCode(HttpStatus.SC_NOT_FOUND)
				.body(equalTo("Card not found"))
				.log().all()
				.extract().response();
	}

	@Test
	@Order(23)
	public void accountCreateTransactionFail403() {
		test = extent.createTest("TC-072: POST Create Transaction - Status Code: 403 - FORBIDDEN");
		test.assignCategory("Accounts");
		test.assignCategory("Suite: Smoke");
		test.assignCategory("Request Method: POST");
		test.assignCategory("Status Code: 403 - FORBIDDEN");
		test.assignCategory("Sprint: 3");
		test.assignAuthor("Laura Panciroli");
		test.info("Creación fallida de una transacción por cuenta errónea.");

		given()
				.header("Authorization", "Bearer " + token)
				.contentType(ContentType.JSON)
				.body(transferenceRequestFail2)
				.when()
				.basePath("/accounts").
				post("/1/transferences").
				then()
				.assertThat()
				.statusCode(403)
				.statusCode(HttpStatus.SC_FORBIDDEN)
				.body(equalTo("Account does not belong to bearer."))
				.log().all()
				.extract().response();
	}
}
