<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="deleteAccount">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td
                id="username"
                class="pre-fill"
              >
                {{ user.username }}
              </td>
            </tr>
            <tr>
              <td>Password</td>
              <td>
                <input
                  id="password"
                  v-model="password"
                  name="password"
                  type="password"
                  value=""
                >
              </td>
            </tr>
            <tr v-if="errorMessage">
              <td />
              <td class="error">
                {{ errorMessage }}
              </td>
            </tr>
            <tr>
              <td />
              <td class="button">
                <input
                  type="submit"
                  value="Delete account"
                >
              </td>
            </tr>
          </table>
        </form>
      </div>
    </article>
  </div>
</template>

<router>
  {
    name: 'Delete account'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue';

const Cookie = process.client ? require('js-cookie') : undefined;

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar,
  },

  data() {
    return {
      user: '',
      password: '',
      errorMessage: '',
    };
  },

  mounted() {
    this.$axios.get('/users/me', {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      this.user = response.data;
    });
  },

  methods: {
    deleteAccount() {
      const request = this.$axios.delete('/users/me?password='+this.password,
      {
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      });
      request.then((response) => {
          console.log(response);
          Cookie.remove('auth');
          this.$store.commit('setAuth', null);
          this.$router.push('/login');
        })
        .catch((error) => {
          this.errorMessage = error.response.data.message;
        });
    }
  }
};
</script>

<style></style>
