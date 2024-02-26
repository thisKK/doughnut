package com.odde.doughnut.controllers.json;

import com.odde.doughnut.entities.Note;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class NoteUpdateTopicDTO {
  @Size(min = 1, max = Note.MAX_TITLE_LENGTH)
  @Getter
  @Setter
  @NotNull
  private String topicConstructor = "";
}
