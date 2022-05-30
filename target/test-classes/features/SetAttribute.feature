Feature: Set Attribute

  @After("@set_attribute")
  Scenario Outline: Set attribute successfully for AttributesStockBalance and AttributesLot
    Given Get TPacks by criteria
      | UserId | UserName | Warehouse | TPackNo | IncludeAttributes | ApiVariant | Location   |  |
      |        |          | PF1       |         | true              |            | 1001001001 |  |
    And Use a stock balance that has attribute type "<AttrType>" and attribute ID "<AttrID>"
    When Call API SetAttribute to set attribute "<AttrID>" value "<AttrValue>"
    Then The attribute is updated
    Examples:
      | AttrID      | AttrValue       | AttrType     |
      | ORDERNUMBER | OOOOOOOOO100001 | StockBalance |