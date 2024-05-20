import { screen } from "@testing-library/vue";
import Sidebar from "@/components/notes/Sidebar.vue";
import { NoteRealm } from "@/generated/backend";
import { flushPromises } from "@vue/test-utils";
import helper from "../helpers";
import makeMe from "../fixtures/makeMe";

function isBefore(node1: Node, node2: Node) {
  return !!(
    // eslint-disable-next-line no-bitwise
    (node1.compareDocumentPosition(node2) & Node.DOCUMENT_POSITION_FOLLOWING)
  );
}

describe("Sidebar", () => {
  const topNoteRealm = makeMe.aNoteRealm.topicConstructor("top").please();
  const firstGeneration = makeMe.aNoteRealm
    .topicConstructor("first gen")
    .under(topNoteRealm)
    .please();
  const firstGenerationSibling = makeMe.aNoteRealm
    .topicConstructor("first gen sibling")
    .under(topNoteRealm)
    .please();
  const secondGeneration = makeMe.aNoteRealm
    .topicConstructor("2nd gen")
    .under(firstGeneration)
    .please();

  const render = (n: NoteRealm) => {
    return helper
      .component(Sidebar)
      .withStorageProps({
        noteRealm: n,
      })
      .render();
  };

  it("should call the api once if top note", async () => {
    helper.managedApi.restNoteController.show1 = vitest
      .fn()
      .mockResolvedValueOnce(topNoteRealm);
    render(topNoteRealm);
    expect(helper.managedApi.restNoteController.show1).toBeCalled();
    await screen.findByText(firstGeneration.note.topic);
  });

  describe("first generation", () => {
    beforeEach(() => {
      helper.managedApi.restNoteController.show1 = vitest
        .fn()
        .mockResolvedValueOnce(topNoteRealm)
        .mockResolvedValueOnce(firstGeneration);
    });

    it("should call the api if not top note", async () => {
      render(firstGeneration);
      await flushPromises();
      expect(helper.managedApi.restNoteController.show1).toBeCalledWith(
        topNoteRealm.id,
      );
      expect(helper.managedApi.restNoteController.show1).toBeCalledWith(
        firstGeneration.id,
      );
    });

    it("should have siblings", async () => {
      render(firstGeneration);
      await flushPromises();
      await flushPromises();
      await screen.findByText(firstGenerationSibling.note.topic);
    });

    it("should have child note of active first gen", async () => {
      render(firstGeneration);
      const secondGen = await screen.findByText(secondGeneration.note.topic);
      const sibling = await screen.findByText(
        firstGenerationSibling.note.topic,
      );
      expect(isBefore(secondGen, sibling)).toBe(true);
    });
  });

  it("should start from notebook top", async () => {
    helper.managedApi.restNoteController.show1 = vitest
      .fn()
      .mockResolvedValueOnce(topNoteRealm)
      .mockResolvedValueOnce(firstGeneration)
      .mockResolvedValueOnce(secondGeneration);
    render(secondGeneration);
    await screen.findByText(firstGeneration.note.topic);
    await screen.findByText(secondGeneration.note.topic);
  });

  it("should disable the menu and keep the content when loading", async () => {
    helper.managedApi.restNoteController.show1 = vitest
      .fn()
      .mockResolvedValueOnce(topNoteRealm);
    const { rerender } = render(topNoteRealm);
    await flushPromises();
    await rerender({ noteRealm: undefined });
    await flushPromises();
    await screen.findByText(firstGeneration.note.topic);
  });
});
