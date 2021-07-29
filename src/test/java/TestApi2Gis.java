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

    @Description("validate query param '' is not accepted. error message is received")
    @Test
    public void validateErrorMessageForQueryParamsEmpty() {
        given()
                .queryParam("q", "").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("error.message", equalTo("Параметр 'q' должен быть не менее 3 символов"));
    }

    @Description("validate that only query param q='орск' works, second query param " +
            "country_code=ru doesn't work")
    @Test
    public void validateQueryParamsResultsQandCountry() {
        given()
                .queryParam("q", "орск")
                .queryParam("country_code", "ru").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.name", hasItems("Магнитогорск", "Орск", "Усть-Каменогорск"));
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
                .body("items.country.code", hasItems("ru"))
                .body("items.country.code", not(hasItems("kz")))
                .body("items.country.code", not(hasItems("kg")))
                .body("items.country.code", not(hasItems("cz")));
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
                .body("items.country.code", not(hasItems("kg")))
                .body("items.country.code", not(hasItems("ru")))
                .body("items.country.code", not(hasItems("cz")));
    }

    @Description("validate only country = kg are received")
    @Test
    public void validateCountryCodeQueryParamsKg() {
        given()
                .queryParam("country_code", "kg").
                when()
                .get(baseUrl)
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("items.country.code", hasItems("kg"))
                .body("items.country.code", not(hasItems("kz")))
                .body("items.country.code", not(hasItems("ru")))
                .body("items.country.code", not(hasItems("cz")));
    }

    @Description("validate only country = cz are received")
    @Test
    public void validateCountryCodeQueryParamsCz() {
        given()
                .queryParam("country_code", "cz").
                when()
                .get(baseUrl)
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("items.country.code", hasItems("cz"))
                .body("items.country.code", not(hasItems("kz")))
                .body("items.country.code", not(hasItems("ru")))
                .body("items.country.code", not(hasItems("kg")));
    }

    @Description("validate query param country_code='' is not accepted. error message is received")
    @Test
    public void validateCountryCodeQueryParamsEmpty() {
        given()
                .queryParam("country_code", "").
                when()
                .get(baseUrl)
                .then()
                .assertThat()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("error.message", equalTo("Параметр 'country_code' может быть одним из " +
                        "следующих значений: ru, kg, kz, cz"));
    }

    @Description("validate error message received when query param page='' ")
    @Test
    public void validatePagesEmptyQueryParamEmpty() {
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

    @Description("validate error message received when query param page=1.5")
    @Test
    public void validatePagesEmptyQueryParamOnePointFive() {
        given().
                queryParam("page", "1.5").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("error.message", equalTo("Параметр 'page' длжен быть целым числом"));
    }

    @Description("validate error message received when query param page=0")
    @Test
    public void validatePagesEmptyQueryParamZero() {
        given().
                queryParam("page", "0").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("error.message", equalTo("Параметр 'page' длжен быть целым числом"));
    }

    @Description("validate query param page_size by default")
    @Test
    public void validateNumberOfRegionsByDefault() {
        when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.size()", equalTo(15));
    }

    @Description("validate query param page_size=15")
    @Test
    public void validateNumberOfRegionsFifteen() {
        given()
                .queryParam("page_size", "15").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.size()", equalTo(15));
    }

    @Description("validate query param page_size=10")
    @Test
    public void validateNumberOfRegionsTen() {
        given()
                .queryParam("page_size", "10").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.size()", equalTo(10));
    }

    @Description("validate query param page_size=5")
    @Test
    public void validateNumberOfRegionsFive() {
        given()
                .queryParam("page_size", "5").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("items.size()", equalTo(5));
    }

    @Description("validate error message received when query param page_size=15.5")
    @Test
    public void validateNumberOfRegionsFifteenPointFive() {
        given()
                .queryParam("page_size", "15.5").
                when()
                .request("GET", baseUrl)
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("error.message", equalTo("Параметр 'page_size' длжен быть целым числом"));
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
                .body("error.message", equalTo("Параметр 'page_size' может быть одним из следующих " +
                        "значений: 5, 10, 15"));
    }

}
