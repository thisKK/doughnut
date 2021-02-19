package com.odde.doughnut.controllers;

import com.odde.doughnut.models.Link;
import com.odde.doughnut.models.Note;
import com.odde.doughnut.models.User;
import com.odde.doughnut.repositories.NoteRepository;
import com.odde.doughnut.repositories.UserRepository;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.print.attribute.standard.Media;
import java.security.Principal;
import java.util.Date;
import java.util.List;

@RestController
public class NoteController {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteController(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/note")
    public RedirectView createNote(Principal principal, Note note, Model model) throws Exception {
        User currentUser = userRepository.findByExternalIdentifier(principal.getName());
        if (currentUser == null) throw new Exception("User does not exist");
        note.setUser(currentUser);

        noteRepository.save(note);
        return new RedirectView("/review");
    }

    @GetMapping(value="/getNotes", produces=MediaType.APPLICATION_JSON_VALUE)
    public List<Note> getNotes(Principal principal) throws Exception {
        User currentUser = userRepository.findByExternalIdentifier(principal.getName());
        if (currentUser == null) throw new Exception("User does not exist");
        return currentUser.getNotesInDescendingOrder();
    }

    @PostMapping(value = "/linkNote", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public RedirectView linkNote(Integer sourceNoteId, Integer targetNoteId, Model model) throws Exception {
        Note sourceNote = noteRepository.findById(sourceNoteId).get();
        Note targetNote = noteRepository.findById(targetNoteId).get();

        sourceNote.linkToNote(targetNote);
        sourceNote.setUpdatedDatetime(new Date());

        noteRepository.save(sourceNote);
        return new RedirectView("/review");
    }
}
