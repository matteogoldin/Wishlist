#Author: your.email@your.domain.com
#Keywords Summary :
#Feature: List of scenarios.
#Scenario: Business rule through list of steps with arguments.
#Given: Some precondition step
#When: Some key actions
#Then: To observe outcomes or validation
#And,But: To enumerate more Given,When,Then steps
#Scenario Outline: List of steps for data-driven as an Examples and <placeholder>
#Examples: Container for s table
#Background: List of steps run before each of the scenarios
#""" (Doc Strings)
#| (Data Tables)
#@ (Tags/Labels):To group Scenarios
#<> (placeholder)
#""
## (Comments)
#Sample Feature Definition Template

Feature: BDD tests for WishlistSwingApp
  Tests the correct behaviour of the application

  Scenario: Initial state
    Given The database contains the following wishlists
    	| Birthday  | My birthday gifts     |
    	| Christmas | My wish for Christmas |
    And The wishlist "Birthday" contains the following values
    	| Phone  | Samsung Galaxy A52 | 300 |
    	| Wallet | Leather            | 100 |
    When The Wishlist App view is shown
    And The wishlist "Birthday" is selected
    Then The list of wishlist contains 
    	| Birthday  |
    	| Christmas | 
    And The list of item contains
    	| Phone  | 
    	| Wallet |
    	
   Scenario: Add a new wishlist
    Given The Wishlist App view is shown
    When The user click the Add button under the wishlists list
    Then The Add Wishlist view is shown
    #When The user enter the following values in Add Wishlist view: Name: "Birthday", Description: "My birthday gifts"
    #And The user click the Add button in Add Wishlist view
    #Then The Add Wishlist view is closed
    #And The list of wishlist contains 
    	#| Birthday  |
    
