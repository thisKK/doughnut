import GlobalBar from "@/components/toolbars/GlobalBar.vue"
import { User } from "@/generated/backend/models/User"
import ManagedApi from "@/managedApi/ManagedApi"
import NoteEditingHistory from "@/store/NoteEditingHistory"
import createNoteStorage, { StorageAccessor } from "@/store/createNoteStorage"
import { screen } from "@testing-library/vue"
import { flushPromises } from "@vue/test-utils"
import makeMe from "../fixtures/makeMe"
import helper from "../helpers"

describe("global bar", () => {
  let noteEditingHistory: NoteEditingHistory
  let histories: StorageAccessor
  let user: User

  beforeEach(() => {
    helper.managedApi.restCircleController.index = vitest
      .fn()
      .mockResolvedValue([])
    user = makeMe.aUser().please()
    noteEditingHistory = new NoteEditingHistory()
    histories = createNoteStorage(
      new ManagedApi({ states: [], errors: [] }),
      noteEditingHistory
    )
  })

  it("opens the circles selection", async () => {
    const wrapper = helper
      .component(GlobalBar)
      .withProps({
        storageAccessor: histories,
        user,
        apiStatus: { states: [] },
      })
      .mount()
    wrapper.find("[role='button']").trigger("click")
    await flushPromises()
    expect(helper.managedApi.restCircleController.index).toBeCalled()
  })

  it("fetch API to be called ONCE", async () => {
    helper
      .component(GlobalBar)
      .withProps({
        storageAccessor: histories,
        user,
        apiStatus: { states: [] },
      })
      .render()

    expect(screen.queryByTitle("undo")).toBeNull()
  })

  it("show undo when there is something to undo", async () => {
    const notebook = makeMe.aNotebook.please()
    noteEditingHistory.deleteNote(notebook.headNote.id)
    helper
      .component(GlobalBar)
      .withProps({
        storageAccessor: histories,
        user,
        apiStatus: { states: [] },
      })
      .render()

    expect(await screen.findByTitle("undo delete note")).not.toBeDisabled()
  })
})
