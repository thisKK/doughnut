/// <reference types="cypress" />
/// <reference types="../support" />
// @ts-check

import { Given } from "@badeball/cypress-cucumber-preprocessor"
import pageObjects from "page_objects"

Given(
  "an admin should be able to download the training data with {int} record",
  (count: number) => {
    pageObjects
      .loginAsAdminAndGoToAdminDashboard()
      .downloadAIQuestionTrainingData()
      .expectNumberOfRecords(count)
  },
)

Given(
  "an admin should be able to download the training data with improved question {string}",
  (suggestedQuestion: string) => {
    pageObjects
      .loginAsAdminAndGoToAdminDashboard()
      .downloadAIQuestionTrainingData()
      .expectTxtInDownload(suggestedQuestion)
  },
)

Given(
  "an admin should be able to download the training data with improved choice with {string}",
  (choice: string) => {
    pageObjects
      .loginAsAdminAndGoToAdminDashboard()
      .downloadAIQuestionTrainingData()
      .expectTxtInDownload(choice)
  },
)
