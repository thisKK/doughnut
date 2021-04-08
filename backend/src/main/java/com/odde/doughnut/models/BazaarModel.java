package com.odde.doughnut.models;

import com.odde.doughnut.entities.BazaarNotebookEntity;
import com.odde.doughnut.entities.NoteEntity;
import com.odde.doughnut.entities.NotebookEntity;
import com.odde.doughnut.entities.repositories.BazaarNotebookRepository;
import com.odde.doughnut.services.ModelFactoryService;

import java.util.ArrayList;
import java.util.List;

public class BazaarModel {
    final BazaarNotebookRepository bazaarNotebookRepository;

    public BazaarModel(ModelFactoryService modelFactoryService) {
        bazaarNotebookRepository = modelFactoryService.bazaarNotebookRepository;
    }

    public List<NoteEntity> getAllNotes() {
        Iterable<BazaarNotebookEntity> all = bazaarNotebookRepository.findAll();
        List<NoteEntity> notes = new ArrayList<>();
        all.forEach(bn->notes.add(bn.getNotebookEntity().getHeadNoteEntity()));
        return notes;
    }

    public void shareNote(NotebookEntity notebookEntity) {
        BazaarNotebookEntity bazaarNotebookEntity = new BazaarNotebookEntity();
        bazaarNotebookEntity.setNotebookEntity(notebookEntity);
        bazaarNotebookRepository.save(bazaarNotebookEntity);
    }
}
