@split
Feature: Split Tpack

  @split_partial
  Scenario: Split a portion of quantity of a stock balance of 1 single Tpack to a new Tpack
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "false"
    When Call API SplitTpack where quantityToMove = "0.5" quantity and catchWeightToMove = "0" net weight of stock balance
    Then A new Tpack is generated
    And Stock balance of the splited Tpack is correct, mixed Tpack "false"
    And Stock balance of the new Tpack is correct, mixed Tpack "false"

  Scenario: Split all quantity of a stock balance of 1 single Tpack to a new Tpack
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "false"
    When Call API SplitTpack where quantityToMove = "1" quantity and catchWeightToMove = "0" net weight of stock balance
    Then A new Tpack is generated
    And Stock balance of the new Tpack is correct, mixed Tpack "false"
    And The old Tpack is no longer exist

  @split_partial
  Scenario: Split 1 stock balance of a mixed Tpack to a new Tpack
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "true"
    When Call API SplitTpack where quantityToMove = "0.5" quantity and catchWeightToMove = "0" net weight of stock balance
    Then A new Tpack is generated
    And Stock balance of the new Tpack is correct, mixed Tpack "false"
    And Stock balance of the splited Tpack is correct, mixed Tpack "true"

  @split_partial
  Scenario: Split a mixed Tpack and then it results to non-mixed Tpack
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "true"
    When Call API SplitTpack to split all stock balances except one
    Then A new Tpack is generated
    And Stock balances of the new Tpack is correct
    And There is only a stock balance in the old Tpack, non mixed Tpack

  Scenario: Split all stock balances of 1 Tpack to a new Tpack
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "true"
    When Call API SplitTPack to split all stock balances
    Then A new Tpack is generated
    And Stock balances of the new Tpack is correct, mixed Tpack
    And The old Tpack is no longer exist

  @split_partial
  Scenario: Split catch weight Tpack to a new Tpack
  Given A list of TPack is available on "PF1" and "5601001001"
  And Use Tpack catch weight "true"and mixed Tpack "false"
  When Call API SplitTpack where quantityToMove = "0.5" quantity and catchWeightToMove = "0.5" net weight of stock balance
  Then A new Tpack is generated
  And Stock balance of the new Tpack is correct, mixed Tpack "false"
  And Stock balance of the splited Tpack is correct, mixed Tpack "false"

  @split_partial
  Scenario: Split all catch weight Tpack to a new Tpack
    Given A list of TPack is available on "PF1" and "5601001001"
    And Use Tpack catch weight "true"and mixed Tpack "true"
    When Call API SplitTpack where quantityToMove = "1" quantity and catchWeightToMove = "1" net weight of stock balance
    Then A new Tpack is generated
    And Stock balance of the new Tpack is correct, mixed Tpack "false"
    And The stock balance does not exist in the old Tpack

  Scenario: Split a Tpack to an existing Tpack
    Given A list of TPack is available on "PF1" and "5601001001"
    And Use Tpack catch weight "true"and mixed Tpack "true"
    When Call API SplitTpack where quantityToMove = "1" quantity and catchWeightToMove = "1" net weight of stock balance
    Then A new Tpack is generated
    When Call API SplitTPack to split the new Tpack to the old one
    Then The stock balance does not change

 @split_partial
  Scenario Outline: Split a Tpack to a new Tpack when specifying inputs
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "true"
    When Call API SplitTPack to split a stock balance with input parameters "<ToLocation>" "<PackagingType>" "<PrintLabel>"
    Then A new Tpack is generated
    And Its information is correct
  Examples:
    | ToLocation | PackagingType | PrintLabel |  |
    | 1001001002 | 902           | false      |  |
    |            | 902           |            |  |

  Scenario: Split a Tpack that exceeds quantity

  Scenario: Split a Tpack that has empty quantity

  Scenario Outline: Split a Tpack with 0 or exceed quantity/catch weight
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "true"
    When Call API SplitTpack where quantityToMove = "<Quantity>" quantity and catchWeightToMove = "<CatchWeight>" net weight of stock balance
    Then The error "<Error>" should display
    Examples:
      | Quantity | CatchWeight | Error
      | 0        | 0           | test
      | 2        | 0           | test
      | 1        | 2           | test


  Scenario Outline: Split a Tpack that has invalid quantity
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "<IsCatchWeight>"and mixed Tpack "<IsMixed>"
    When Call API SplitTpack with invalid quantity or catch weight "<Quantity>" "<CatchWeight>"
    Then The error "<Error>" should display
    Examples:
      | Quantity |  | CatchWeight | Error | IsCatchWeight | IsMixed | scenario                    |  |
      | test     |  | 0           | test  | false         | true    | invalid quantity            |  |
      | 1        |  | test        | test  | true          | false   | invalid  catch weight       |  |
      |          |  | 2           | test  | false         | false   | empty quantity              |  |
      |          |  |             | test  | true          | false   | empty quantity/catch weight |  |
      | -1       |  |             | test  | false         | false   | minus quantity              |  |
      |          |  | -1          | test  | true          | false   | minus catch weight          |  |
      |          |  | test        | false | false         | false   | tpack is not catch weight   |  |

  Scenario: Split a Tpack that has invalid ItemNo
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "false"
    When Call API SplitTpack with invalid ItemNo
    Then The error "<Error>" should display

  Scenario: Split a Tpack that has invalid LotNo
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "false"
    When Call API SplitTpack with invalid LotNo
    Then The error "<Error>" should display

  Scenario: Split a Tpack that has invalid UnitOfMeasure
    Given A list of TPack is available on "PF1" and "1001001001"
    And Use Tpack catch weight "false"and mixed Tpack "false"
    When Call API SplitTpack with invalid UnitOfMeasure
    Then The error "<Error>" should display
#
#  Scenario: Split a Tpack that has invalid catch weight
#
#  Scenario: Split a Tpack that exceeds catch weight
#
#  Scenario: Split a Tpack that has empty catch weight

#
#  Scenario: Split a Tpack that has conversion unit of measure

#  Scenario: Split a Tpack that has invalid stock balance

#  Scenario: Split a Tpack that has invalid inputs
