<template>
  <div>
    <navigationBar />
    <article>
      <div class="left-narrow">
        <h3>Members</h3>
        <ul class="user-list">
          <li
            v-for="(user, index) in members"
            :key="index"
          >
            <div v-if="user.username === managerUsername">
              <span class="user">{{ user.displayName }}</span>
            </div>
            <div v-else>
              <span class="user-deletable">{{ user.displayName }}</span>
              <form @submit.prevent="removeMember(user.mapId)">
                <input class="delete" type="submit" value="x" />
              </form>
            </div>
          </li>
        </ul>
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
const Cookie = process.client ? require('js-cookie') : undefined;

import NavigationBar from '~/components/NavigationBar.vue';

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar
  },

  data() {
    return {
      organizationId: this.$route.params.organizationId,
      members: [],
      username: '',
      managerUsername: this.$store.state.auth.username
    };
  },

  mounted() {
    this.loadMembers();
  },

  methods: {
    loadMembers() {
      console.log("hello");
      this.$axios.get('/organizations/'+this.organizationId+'/members', {
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      })
      .then(response => {
        console.log(response.data);
        this.members = response.data;
      });
    },
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
          this.loadMembers();
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
    },

    removeMember(mapId) {
      console.log("hello");
      console.log(mapId);
      this.$axios.delete('/organizations/'+this.organizationId+'/members/'+mapId, {
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log(response);
          console.log("member removed");
          this.loadMembers();
        })
        .catch((error) => {
          console.log("Remove member error");
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
