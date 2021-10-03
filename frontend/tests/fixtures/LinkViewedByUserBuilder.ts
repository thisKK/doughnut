import Builder from "./Builder"
import NoteBuilder from "./NoteBuilder" 
import NotePositionBuilder from "./NotePositionBuilder"

class LinkViewedByUserBuilder extends Builder {
  data: any;

  constructor(parentBuilder?: Builder) {
    super(parentBuilder);
  }

  do(): any {
    return {
      id: 8,
      linkTypeLabel: "a link",
      sourceNoteViewedByUser:  {
        noteItself: new NoteBuilder().do(),
        notePosition: new NotePositionBuilder().do()
      },
      targetNoteViewedByUser: {
        noteItself: new NoteBuilder().do(),
        notePosition: new NotePositionBuilder().do()
      }
    }
  }
}

export default LinkViewedByUserBuilder
