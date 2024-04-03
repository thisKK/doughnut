Feature: Convert SRT format to Text
  As a learner, I want to convert the SRT format to text in the Note details.

  Background:
    Given I am logged in as an existing user
    And there are some notes for the current user:
      | topicConstructor | details             |
      | SRT format   | 1\n 00:05:00,400 --> 00:05:15,300\n This is an example of a subtitle. |
  
  Scenario: convert button is dsiplay
    Given I have note with "SRT format"
    When I open the note details "SRT format"
    Then I should see button convert

  @ignore
  Scenario: Convert srt format to text
    Given I have note with SRT format
    When I convert it to text format
    Then I get note with text "This is an example of a subtitle."

  @ignore
  Scenario: Convert srt with wrong format to text
    Given I have note with wrong SRT format
    When I convert it to text format
    Then I get error warning that file is not convertible

  @ignore
  Scenario: Convert srt with text format
    Given I have note with text format
    When I convert it to text format
    Then I get the notification that note is already text format

  
