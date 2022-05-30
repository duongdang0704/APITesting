package data_models;

import io.cucumber.gherkin.internal.com.eclipsesource.json.Json;
import io.cucumber.gherkin.internal.com.eclipsesource.json.JsonArray;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import org.testng.Assert;

import java.util.LinkedHashMap;

import static io.restassured.RestAssured.given;

public class Authentication {

    public static String TOKEN = getToken();
    public static String getToken(){
        Response response = given()
                .contentType("application/x-www-form-urlencoded; charset=utf-8")
                .formParam("client_id", "66219bf3-30ce-473c-91eb-c1fbfa85c705")
                .formParam("scope", "66219bf3-30ce-473c-91eb-c1fbfa85c705/.default")
                .formParam("client_secret", "C0R7Q~QjUBXVIpXkbT9F6CSFjNgBc0cFv0tbU")
                .formParam("grant_type", "client_credentials")
                .when()
                .post("https://login.microsoftonline.com/bee816db-e2fb-4f03-9f91-445a05517f0d/oauth2/v2.0/token");
        JsonPath token = response.jsonPath();
        LinkedHashMap<String, String> authenObj = token.get();
        return authenObj.get("access_token");
    }
}
