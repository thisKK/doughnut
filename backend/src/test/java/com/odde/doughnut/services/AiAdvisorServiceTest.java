package com.odde.doughnut.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.odde.doughnut.entities.json.AiEngagingStory;
import com.odde.doughnut.entities.json.AiSuggestion;
import com.odde.doughnut.exceptions.OpenAiUnauthorizedException;
import com.odde.doughnut.testability.MakeMeWithoutDB;
import com.theokanning.openai.OpenAiService;
import com.theokanning.openai.completion.CompletionResult;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import retrofit2.HttpException;

class AiAdvisorServiceTest {

  private AiAdvisorService aiAdvisorService;
  @Mock private OpenAiService openAiServiceMock;
  MakeMeWithoutDB makeMe = new MakeMeWithoutDB();

  @BeforeEach
  void Setup() {
    MockitoAnnotations.openMocks(this);
    aiAdvisorService = new AiAdvisorService(openAiServiceMock);
  }

  @Test
  void getAiSuggestion_givenAString_returnsAiSuggestionObject() {
    CompletionResult completionResult =
        makeMe.openAiCompletionResult().choice("suggestion_value").please();
    Mockito.when(openAiServiceMock.createCompletion(Mockito.any())).thenReturn(completionResult);
    assertEquals(
        "suggestion_value", aiAdvisorService.getAiSuggestion("suggestion_prompt").suggestion());
  }

  @Test
  void getAiSuggestion_givenAString_whenHttpError_returnsEmptySuggestion() {
    AiSuggestion expected = new AiSuggestion("");
    HttpException httpExceptionMock = Mockito.mock(HttpException.class);
    Mockito.when(httpExceptionMock.code()).thenReturn(400);
    Mockito.when(openAiServiceMock.createCompletion(ArgumentMatchers.any()))
        .thenThrow(httpExceptionMock);

    assertEquals(expected, aiAdvisorService.getAiSuggestion("suggestion_prompt"));

    Mockito.verify(openAiServiceMock).createCompletion(ArgumentMatchers.any());
  }

  @Test
  void getAiEngagingStory_givenAlistOfStrings_returnsAStory() {

    AiEngagingStory expected = new AiEngagingStory("This is an engaging story");

    CompletionResult completionResult =
        makeMe.openAiCompletionResult().choice("This is an engaging story").please();
    Mockito.when(openAiServiceMock.createCompletion(Mockito.any())).thenReturn(completionResult);

    assertEquals(expected, aiAdvisorService.getEngagingStory(Collections.singletonList("title")));
  }

  @Test
  void getAiSuggestion_given_invalidToken_return_401() {
    HttpException httpExceptionMock = Mockito.mock(HttpException.class);
    Mockito.when(httpExceptionMock.code()).thenReturn(401);
    String unauthorized = "Unauthorized";
    Mockito.when(httpExceptionMock.getMessage()).thenReturn(unauthorized);
    Mockito.when(openAiServiceMock.createCompletion(ArgumentMatchers.any()))
        .thenThrow(httpExceptionMock);
    OpenAiUnauthorizedException exception =
        assertThrows(OpenAiUnauthorizedException.class, () -> aiAdvisorService.getAiSuggestion(""));
    assertEquals(unauthorized, exception.getMessage());
  }
}
