package com.odde.doughnut.entities.json;

import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class TrainingData {
  private @Getter List<TrainingDataMessage> messages;

  public static TrainingData generateTrainingData(
      List<ChatMessage> messages, String rawJsonQuestion) {
    List<TrainingDataMessage> trainingDataMessages =
        messages.stream()
            .map(
                chatMessage ->
                    new TrainingDataMessage(chatMessage.getRole(), chatMessage.getContent()))
            .collect(Collectors.toList());
    trainingDataMessages.add(
        new TrainingDataMessage(ChatMessageRole.ASSISTANT.value(), rawJsonQuestion));
    return new TrainingData(trainingDataMessages);
  }
}
