import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasItems;

public class TestApi2Gis {

    String baseUrl = "https://regions-test.2gis.com/1.0/regions";

    @Description("validate status code is 200")
    @Test
    public void validateApiResponseStatusCode() {
        when()
                .get(baseUrl)
                .then()
                .contentType(ContentType.JSON)
                .statusCode(200);
    }

    @Description("validate total number of regions")
    @Test
    public void validateTotalNumberOfRegions() {
        when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("total", equalTo(22))
                .body("items.size()", equalTo(22));
        ;
    }

    @Description("validate query param 'рск'")
    @Test
    public void validateQueryParamsResults() {
        given()
                .queryParam("q", "рск").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.name", hasItems("Красноярск", "Магнитогорск", "Новосибирск"));
    }

    @Description("validate query param 'оРсК' are not case sensitive")
    @Test
    public void validateQueryParamsDifCaseResults() {
        given()
                .queryParam("q", "оРсК").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.name", hasItems("Магнитогорск", "Орск", "Усть-Каменогорск"));
    }

    @Description("validate query param 'ск' is not accepted. error message is received")
    @Test
    public void validateErrorMessageForQueryParams() {
        given()
                .queryParam("q", "ск").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("error.message", equalTo("Параметр 'q' должен быть не менее 3 символов"));
    }

    @Description("validate error message received when number of pages is not 5, 10 or 15")
    @Test
    public void validatePagesQueryParam() {
        given().
                queryParam("page_size", "20").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих значений: 5, 10, 15"));
    }

    @Description("validate error message received when number of pages is 0 or null")
    @Test
    public void validatePagesEmptyQueryParam() {
        given().
                queryParam("page", "").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("error.message", equalTo("Параметр 'page' длжен быть целым числом"));
    }


    @Description("validate only country = ru are received")
    @Test
    public void validateCountryCodeQueryParamsRu() {
        given()
                .queryParam("country_code", "ru").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.country.code", hasItems("ru"));
    }

    @Description("validate only country = kz are received")
    @Test
    public void validateCountryCodeQueryParamsKz() {
        given()
                .queryParam("country_code", "kz").
                when()
                .get(baseUrl)
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("items.country.code", hasItems("kz"))
                .body("items.country.code", not(hasItems("kg")));
    }

    @Description("")
    @Test
    public void validateCountryCodeQuery() throws UnirestException {
        Unirest.get(baseUrl)
                .queryString("country_code", "kz")
                .asString()
                .getBody();

    }

}
