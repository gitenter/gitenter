<template>
  <div>
    <navigationBar />
    <article>
      <div class="left-narrow">
        <members v-bind:organizationId="organizationId" />
      </div>
      <div class="right-wide">
        <form @submit.prevent="addMember">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td>
                <input
                  id="username"
                  v-model="username"
                  name="username"
                  type="text"
                  value=""
                >
              </td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Add a new member" /></td>
            </tr>
          </table>
        </form>
      </div>
      <div style="clear:both"></div>
    </article>
  </div>
</template>

<router>
  {
    name: 'Manage members'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue';
import Members from '~/components/organization/Members.vue';

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar,
    Members
  },

  data() {
    return {
      organizationId: this.$route.params.organizationId,
      username: ''
    };
  },

  methods: {
    addMember() {
      console.log("Add member!!");
      /*
       * Need the 2nd argument of `this.$axios.post` to be an empty string, otherwise
       * axios will transfer headers array to a string and pass it to API (return 200
       * but no data as the case of without authorization).
       */
      this.$axios.post('/organizations/'+this.organizationId+'/ordinary-members', "", {
        params: {
          'username': this.username
        },
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log("member added");
        })
        .catch((error) => {
          console.log("Add member error");
          console.log(error);
          /*
           * TODO:
           * Error message.
           *
           * TODO:
           * Refresh page, especially `<members>` component after this call.
           */
        });
    }
  }
};
</script>

<style></style>
