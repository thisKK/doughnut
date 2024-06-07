package com.odde.doughnut.controllers;

import com.odde.doughnut.controllers.dto.AnswerDTO;
import com.odde.doughnut.controllers.dto.QuestionSuggestionCreationParams;
import com.odde.doughnut.controllers.dto.QuizQuestionContestResult;
import com.odde.doughnut.entities.*;
import com.odde.doughnut.exceptions.UnexpectedNoAccessRightException;
import com.odde.doughnut.factoryServices.ModelFactoryService;
import com.odde.doughnut.models.AnswerModel;
import com.odde.doughnut.models.UserModel;
import com.odde.doughnut.services.GlobalSettingsService;
import com.odde.doughnut.services.QuizQuestionService;
import com.odde.doughnut.services.ai.AiQuestionGenerator;
import com.odde.doughnut.testability.TestabilitySettings;
import com.theokanning.openai.client.OpenAiApi;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz-questions")
class RestQuizQuestionController {
  private final ModelFactoryService modelFactoryService;
  private final QuizQuestionService quizQuestionService;

  private final UserModel currentUser;

  @Resource(name = "testabilitySettings")
  private final TestabilitySettings testabilitySettings;

  private AiQuestionGenerator aiQuestionGenerator;

  public RestQuizQuestionController(
      @Qualifier("testableOpenAiApi") OpenAiApi openAiApi,
      ModelFactoryService modelFactoryService,
      UserModel currentUser,
      TestabilitySettings testabilitySettings) {
    this.modelFactoryService = modelFactoryService;
    this.currentUser = currentUser;
    this.testabilitySettings = testabilitySettings;
    this.aiQuestionGenerator =
        new AiQuestionGenerator(openAiApi, new GlobalSettingsService(modelFactoryService));
    this.quizQuestionService = new QuizQuestionService(openAiApi, modelFactoryService);
  }

  @PostMapping("/generate-question")
  @Transactional
  public QuizQuestion generateQuestion(
      @RequestParam(value = "note") @Schema(type = "integer") Note note) {
    currentUser.assertLoggedIn();
    return quizQuestionService.generateAIQuestion(note);
  }

  @PostMapping("/{quizQuestion}/contest")
  @Transactional
  public QuizQuestionContestResult contest(
      @PathVariable("quizQuestion") @Schema(type = "integer") QuizQuestion quizQuestion) {
    currentUser.assertLoggedIn();
    return aiQuestionGenerator.getQuizQuestionContestResult(quizQuestion);
  }

  @PostMapping("/{quizQuestion}/regenerate")
  @Transactional
  public QuizQuestion regenerate(
      @PathVariable("quizQuestion") @Schema(type = "integer") QuizQuestion quizQuestion) {
    currentUser.assertLoggedIn();
    return quizQuestionService.generateAIQuestion(quizQuestion.getNote());
  }

  @PostMapping("/{quizQuestion}/answer")
  @Transactional
  public AnsweredQuestion answerQuiz(
      @PathVariable("quizQuestion") @Schema(type = "integer") QuizQuestion quizQuestion,
      @Valid @RequestBody AnswerDTO answerDTO) {
    currentUser.assertLoggedIn();
    Answer answer = new Answer();
    answer.setQuestion(quizQuestion);
    answer.setFromDTO(answerDTO);
    AnswerModel answerModel = modelFactoryService.toAnswerModel(answer);
    answerModel.makeAnswerToQuestion(
        testabilitySettings.getCurrentUTCTimestamp(), currentUser.getEntity());
    return answerModel.getAnswerViewedByUser(currentUser.getEntity());
  }

  @PostMapping("/{quizQuestion}/suggest-fine-tuning")
  @Transactional
  public SuggestedQuestionForFineTuning suggestQuestionForFineTuning(
      @PathVariable("quizQuestion") @Schema(type = "integer") QuizQuestion quizQuestion,
      @Valid @RequestBody QuestionSuggestionCreationParams suggestion) {
    SuggestedQuestionForFineTuning sqft = new SuggestedQuestionForFineTuning();
    var suggestedQuestionForFineTuningService =
        modelFactoryService.toSuggestedQuestionForFineTuningService(sqft);
    return suggestedQuestionForFineTuningService.suggestQuestionForFineTuning(
        quizQuestion,
        suggestion,
        currentUser.getEntity(),
        testabilitySettings.getCurrentUTCTimestamp());
  }

  @GetMapping("/{headNote}/note-book-questions")
  public List<QuizQuestion> getAllQuizQuestionByNoteBook(
      @PathVariable("headNote") @Schema(type = "integer") Note headNote) {
    return modelFactoryService.getQuizQuestionsByHeadNote(headNote);
  }

  @GetMapping("/{note}/note-questions")
  public List<QuizQuestion> getAllQuizQuestionByNote(
      @PathVariable("note") @Schema(type = "integer") Note note) {
    return modelFactoryService.getQuizQuestionsByNote(note);
  }

  @GetMapping("/{headNote}/note-book-pending-questions")
  public List<QuizQuestion> getAllPendingQuizQuestionByNoteBook(
      @PathVariable("headNote") @Schema(type = "integer") @NotNull Note headNote)
      throws UnexpectedNoAccessRightException {
    currentUser.assertAuthorization(headNote.getNotebook());
    return modelFactoryService.getQuizQuestionsByHeadNote(headNote).stream()
        .filter(x -> !x.isApproved() && !x.isReviewed())
        .toList();
  }
}
