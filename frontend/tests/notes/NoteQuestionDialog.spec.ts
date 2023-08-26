import { flushPromises } from "@vue/test-utils";
import { beforeEach, expect } from "vitest";
import NoteQuestionDialog from "@/components/notes/NoteQuestionDialog.vue";
import makeMe from "../fixtures/makeMe";
import helper from "../helpers";

helper.resetWithApiMock(beforeEach, afterEach);

const note = makeMe.aNoteRealm.please();
const createWrapper = async () => {
  const quizQuestion = makeMe.aQuizQuestion
    .withQuestionType("AI_QUESTION")
    .withQuestionStem("any question?")
    .withChoices([
      { display: "option A", reason: "reason A" },
      { display: "option B", reason: "reason B" },
      { display: "option C", reason: "reason C" },
    ])
    .please();
  helper.apiMock
    .expectingPost(`/api/ai/generate-question?note=${note.id}`)
    .andReturnOnce(quizQuestion);
  const wrapper = helper
    .component(NoteQuestionDialog)
    .withStorageProps({ selectedNote: note.note })
    .mount();
  await flushPromises();
  return wrapper;
};

describe("NoteQuestionDialog", () => {
  it("render the question returned", async () => {
    const wrapper = await createWrapper();
    expect(wrapper.text()).toContain("any question?");
    expect(wrapper.text()).toContain("option A");
    expect(wrapper.text()).toContain("option C");
  });

  it("regenerate question when asked", async () => {
    const wrapper = await createWrapper();

    const quizQuestion = makeMe.aQuizQuestion
      .withQuestionType("AI_QUESTION")
      .withQuestionStem("is it raining?")
      .please();
    helper.apiMock
      .expectingPost(`/api/ai/generate-question?note=${note.id}`)
      .andReturnOnce(quizQuestion);
    wrapper.find("button").trigger("click");
    await flushPromises();
    expect(wrapper.text()).toContain("any question?");
    expect(wrapper.text()).toContain("is it raining?");
  });

  it("When the chat button is clicked, the anwser from AI will be displayed", async () => {
    // Given
    const expected = "I'm ChatGPT";
    const response: Generated.ChatResponse = { assistantMessage: expected };
    // setUp
    helper.apiMock.expectingPost("/api/ai/chat").andReturnOnce(response);

    // When
    const wrapper = await createWrapper();

    await wrapper.find("#chat-input").setValue("What's your name?");
    await wrapper.find("#chat-button").trigger("submit");
    await flushPromises();

    // Then
    wrapper.find(".chat-answer-container").isVisible();
    const actual = wrapper.find("#chat-answer").text();
    expect(actual).toBe(expected);
  });
});
