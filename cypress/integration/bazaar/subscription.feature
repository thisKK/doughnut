Feature: Bazaar subscription
  As a learner, I want to subscribe to notes in the Bazaar so that I can
  learn it's content.

  Background:
    Given there are some notes for existing user "another_old_learner"
      | title           | testingParent |
      | Shape           |               |
      | Square          | Shape         |
      | Triangle        | Shape         |
      | Big Square      | Square        |
    And note "Shape" is shared to the Bazaar

  @ignore
  Scenario: Cannot add to learning if not logged in
    Given I haven't login
    When I go to the bazaar
    Then I should not see the "add-to-learning" button on note "Shape"

  @ignore
  Scenario: No "add to learning" button for child note
    Given I've logged in as an existing user
    When I go to the bazaar
    Then I should see the "add-to-learning" button on note "Shape"
    When I open the note "Shape" in the Bazaar
    Then I should not see the "add-to-learning" button on note "Square"

    @ignore
  Scenario: subscribe to a note
    Given I've logged in as an existing user
    And there are some notes for the current user
      | title           |
      | My memo         |
    And my daily new notes to review is set to 2
    When I subscribe to note "Shape" in the bazaar, with target of learning 1 notes per day
    Then On day 1 I repeat old "end                 " and initial review new "Shape, My memo, end  "
    And  On day 2 I repeat old "Shape, My memo, end " and initial review new "Square, Triangle, end"
