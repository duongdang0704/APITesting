package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import controllers.General;
import controllers.Logistics;
import data_models.StockBalance;
import data_models.TPack;
import data_models.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.Assert;

import java.io.File;
import java.util.List;

public class GeneralSteps extends BaseSteps{

    TestContext testContext;
    TPack tPack;
    Response response;

    public GeneralSteps(TestContext testContext){
        this.testContext = testContext;
    }
    @And("Use a stock balance that has attribute type {string} and attribute ID {string}")
    public void useTpackThatHasAStockBalanceWithAttributeTypeAndAttributeID(String attrType, String attrID) {
        testContext.setData("AttrType", attrType);
        testContext.setData("AttrID", attrID);
        List<TPack> tPacks = testContext.getTPacks();
        first:
        for (TPack currentTpack: tPacks
             ) {
            List<StockBalance> sbs = currentTpack.getAllStockBalances();
            second:
            for (StockBalance sb: sbs
                 ) {
                String values;
                if (attrType.equals("StockBalance") && sb.AttributesStockBalance.Values.contains(attrID)){
                    testContext.setData("AttrNo", sb.AttributesStockBalance.AttrNo);
                    values = sb.AttributesStockBalance.Values;
                    JsonPath jsonPath = new JsonPath(values);
                    testContext.setData("LastAttrValue", jsonPath.get(attrID));
                    tPack = currentTpack;
                    break first;
                }else if(attrType.equals("Lot") && sb.AttributesLot.Values.contains(attrID)) {
                    testContext.setData("AttrNo", sb.AttributesLot.AttrNo);
                    tPack = currentTpack;
                    values = sb.AttributesLot.Values;
                    JsonPath jsonPath = new JsonPath(values);
                    testContext.setData("LastAttrValue", jsonPath.get(attrID));
                    break first;
                }
            }
        }
    }

    @When("Call API SetAttribute to set attribute {string} value {string}")
    public void callAPISetAttributeToSetAttributeValue(String attrID, String attrValue) {
        testContext.setData("AttrValue", attrValue);
        response = General.setAttribute("", "", (String) testContext.getData("AttrNo"), attrID, attrValue);

    }

    @Then("The attribute is updated")
    public void theAttributeIsUpdated() throws JsonProcessingException {
        Response response = Logistics.getTPackInfo("", "", tPack.Warehouse, tPack.TPackNo, "true", "", "");
        response.then()
                .assertThat()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchema(new File("src/test/resources/schemas/GetTPackInfoSuccess.json")));
        ObjectMapper mapper = new ObjectMapper();
        TPack[] tPacks = mapper.readValue(response.getBody().asString(), TPack[].class);
        TPack currentTPack = tPacks[0];
        StockBalance currentSB = currentTPack.getStockBalance((String) testContext.getData("AttrType"), (String) testContext.getData("AttrNo"));
        String attrIDValue = "\"" + testContext.getData("AttrID") + "\": \"" + testContext.getData("AttrValue") + "\"";
        if(testContext.getData("AttrType").equals("StockBalance")){
            Assert.assertTrue(currentSB.AttributesStockBalance.Values.contains(attrIDValue));
        }else if(testContext.getData("AttrType").equals("Lot")){
            Assert.assertTrue(currentSB.AttributesLot.Values.contains(attrIDValue));
        }
    }
}
