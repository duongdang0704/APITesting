package steps;

import controllers.Logistics;
import data_models.StockBalance;
import data_models.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import utils.Config;

import java.io.File;
import java.util.List;

public class Hook {

    TestContext testContext;
    public Hook(TestContext testContext){
        this.testContext = testContext;
    }
    @Before
    public void startTest(Scenario scenario){
        RestAssured.baseURI = Config.BASE_URL;
        testContext.setScenario(scenario.getName());
    }
    @After
    public void endTest(){

    }

    @After("@split_partial")
    public void mergeSplittedTPack(){
        String toTPackNo = (String)testContext.getData("FromTPackNo");
        String fromTPackNo = (String)testContext.getData("ToTPackNo");
        String warehouse = (String)testContext.getData("Warehouse");
        String itemNo = (String)testContext.getData("ItemNo");
        String lotNo = (String)testContext.getData("LotNo");
        String unitOfMeasure = (String)testContext.getData("UnitOfMeasure");
        String quantityToMove = (String) testContext.getData("QuantityToMove");
        String catchWeightToMove = (String)testContext.getData("CatchWeightToMove");
        List<StockBalance> sbs = (List<StockBalance>) testContext.getData("StockBalances");
        Response response = null;
        if (sbs == null){
             response = Logistics.splitTPack("", "", warehouse, fromTPackNo, toTPackNo, "", "",
                    "false", itemNo, lotNo, quantityToMove, unitOfMeasure, catchWeightToMove);
        }else{
             response = Logistics.splitTPack("", "", warehouse, fromTPackNo, toTPackNo,
                    "", "","false", sbs);
        }
        response.then()
                .assertThat()
                .statusCode(200)
                .body("Result", Matchers.equalTo("OK"))
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/SplitTPackSuccess.json")));

    }
    @After("@set_attribute")
    public void updateAttribute(){

    }
    public void authentication(){

    }
}
