<template>
  <div>
    <navigationBar />
    <article>
      <div class="left-narrow">
        <h5>Managers</h5>
        <ul class="user-list">
          <ul class="user-list">
            <li
              v-for="(user, index) in managers"
              :key="index"
            >
              <div v-if="user.username === meUsername">
                <span class="user">{{ user.displayName }}</span>
              </div>
              <div v-else>
                <span class="user-deletable">{{ user.displayName }}</span>
                <form @submit.prevent="demoteMember(user.mapId)">
                  <input class="delete" type="submit" value="↓" />
                </form>
              </div>
            </li>
          </ul>
        </ul>
        <h5>Ordinary Members</h5>
        <ul class="user-list">
          <ul class="user-list">
            <ul class="user-list">
              <li
                v-for="(user, index) in ordinaryMembers"
                :key="index"
              >
                <div>
                  <span class="user-deletable">{{ user.displayName }}</span>
                  <form @submit.prevent="promoteMember(user.mapId)">
                    <input class="delete" type="submit" value="↑" />
                  </form>
                </div>
              </li>
            </ul>
          </ul>
        </ul>
      </div>
      <div class="right-wide">
      </div>
      <div style="clear:both"></div>
    </article>
  </div>
</template>

<router>
  {
    name: 'Manage managers'
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
      managers: [],
      ordinaryMembers: [],
      meUsername: this.$store.state.auth.username
    };
  },

  mounted() {
    this.loadManagers();
    this.loadOrdinaryMembers();
  },

  methods: {
    loadManagers() {
      console.log("hello");
      this.$axios.get('/organizations/'+this.organizationId+'/managers', {
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      })
      .then(response => {
        console.log(response.data);
        this.managers = response.data;
      });
    },
    loadOrdinaryMembers() {
      console.log("hello");
      this.$axios.get('/organizations/'+this.organizationId+'/ordinary-members', {
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      })
      .then(response => {
        console.log(response.data);
        this.ordinaryMembers = response.data;
      });
    },
    promoteMember(mapId) {
      console.log("hello");
      console.log(mapId);
      this.$axios.post('/organizations/'+this.organizationId+'/managers/'+mapId, "", {
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log(response);
          console.log("member removed");
          this.loadManagers();
          this.loadOrdinaryMembers();
        })
        .catch((error) => {
          console.log("Remove member error");
          console.log(error);
          /*
           * TODO:
           * Error message.
           */
        });
    },
    demoteMember(mapId) {
      console.log("hello");
      console.log(mapId);
      this.$axios.delete('/organizations/'+this.organizationId+'/managers/'+mapId, {
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log(response);
          console.log("member removed");
          this.loadManagers();
          this.loadOrdinaryMembers();
        })
        .catch((error) => {
          console.log("Remove member error");
          console.log(error);
          /*
           * TODO:
           * Error message.
           */
        });
    }
  }
};
</script>

<style></style>
