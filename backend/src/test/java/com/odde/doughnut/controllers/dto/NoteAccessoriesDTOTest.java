package com.odde.doughnut.controllers.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@SpringBootTest
@Transactional
public class NoteAccessoriesDTOTest {

  NoteAccessoriesDTO accessoriesDTO = new NoteAccessoriesDTO();

  @Test
  void validate() throws IOException {
//    Audio audio = accessoriesDTO.fetchAttachAudio(null);
//    assertNull(audio);
  }
}
