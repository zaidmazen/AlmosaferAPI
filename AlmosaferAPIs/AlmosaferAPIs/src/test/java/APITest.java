import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Description;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API Testing")
@Feature("Currency and CMS Page Data APIs")
public class APITest {

    @BeforeAll
    public static void setup() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("./src/test/resources/config.properties"));
            RestAssured.baseURI = prop.getProperty("base.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Story("Fetch Currency List")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Test Currency API")
    @Description("Test to ensure the currency list API returns correct data")
    @Test
    public void testCurrencyApi() {
        Response response = given()
                .header("accept", "application/json, text/javascript")
                .header("accept-language", "en-US,en;q=0.9")
                .when()
                .get("/api/system/currency/list")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(not(isEmptyString()))
                .body("failed", either(is(empty())).or(is(nullValue())))
                .extract()
                .response();

        // Print the response
        //System.out.println("Response: " + response.prettyPrint());
    }

    @Story("Fetch CMS Page Data")
    @DisplayName("Test CMS Page Data API")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test to ensure the CMS page API returns the correct data")
    @Test
    public void testCmsPageDataRetrieval() throws Exception {
        Map<String, String> requestBodyMap = new HashMap<>();
        requestBodyMap.put("template", "top-airports-v2.json");
        requestBodyMap.put("country", "general");
        requestBodyMap.put("media", "mobile");
        requestBodyMap.put("locale", "en_us");

        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        Response response = given()
                .header("accept", "application/json, text/javascript")
                .header("accept-language", "en-US,en;q=0.9")
                .header("content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/cms/page")
                .then()
                .assertThat()
                .statusCode(200)
                .body(not(isEmptyString()))
                .extract()
                .response();

        // Print the response
        //System.out.println("Response: " + response.prettyPrint());
    }
}
