<template>
  <div class="dropup w-100">
    <button
      aria-label="User actions"
      class="user-icon-menu btn d-block w-100 text-start"
      data-bs-toggle="dropdown"
      data-toggle="dropdown"
      aria-haspopup="true"
      aria-expanded="false"
    >
      <SvgMissingAvatar :x="-13" :y="-20" :height="40" />
      {{ user.name }}
    </button>
    <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
      <PopButton btn-class="dropdown-item" title="user settings">
        <template #button_face> Settings for {{ user.name }}</template>
        <!-- prettier-ignore -->
        <template #default="{ closer }">
          <UserProfileDialog
            v-bind="{ user }"
            @user-updated="
              if ($event) {
                $emit('updateUser', $event);
              }
              closer();
            "
          />
        </template>
      </PopButton>
      <PopButton btn-class="dropdown-item" title="user settings">
        <template #button_face>Assessment History</template>
        <AssessmentHistoryDialog />
      </PopButton>
      <a href="#" class="dropdown-item" role="button" @click="logout">Logout</a>
    </div>
  </div>
</template>

<script lang="ts">
import PopButton from "@/components/commons/Popups/PopButton.vue"
import SvgMissingAvatar from "@/components/svgs/SvgMissingAvatar.vue"
import { User } from "@/generated/backend"
import useLoadingApi from "@/managedApi/useLoadingApi"
import { PropType, defineComponent } from "vue"
import UserProfileDialog from "./UserProfileDialog.vue"
import AssessmentHistoryDialog from "../user/AssessmentHistoryDialog.vue"

export default defineComponent({
  setup() {
    return useLoadingApi()
  },
  props: {
    user: { type: Object as PropType<User>, required: true },
  },
  emits: ["updateUser"],
  components: {
    PopButton,
    UserProfileDialog,
    AssessmentHistoryDialog,
    SvgMissingAvatar,
  },
  methods: {
    async logout() {
      await this.managedApi.logout()
      window.location.href = "/bazaar"
    },
  },
})
</script>
