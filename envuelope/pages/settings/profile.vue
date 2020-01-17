<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="updateProfile">
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
              <td>Display Name</td>
              <td>
                <input
                  id="displayName"
                  v-model="user.displayName"
                  name="displayName"
                  type="text"
                  value=""
                >
                <span
                  v-if="errors.displayName"
                  class="error"
                >{{ errors.displayName }}</span>
              </td>
            </tr>
            <tr>
              <td>Email address</td>
              <td>
                <input
                  id="email"
                  v-model="user.email"
                  name="email"
                  type="email"
                  value=""
                >
                <span
                  v-if="errors.email"
                  class="error"
                >{{ errors.email }}</span>
              </td>
            </tr>
            <tr v-if="successfulMessage">
              <td />
              <td class="success">
                {{ successfulMessage }}
              </td>
            </tr>
            <tr>
              <td />
              <td class="button">
                <input
                  type="submit"
                  value="Update profile"
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
    name: 'Edit profile'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue';

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar,
  },

  data() {
    return {
      user: '',
      errors: {
        username: '',
        password: '',
        displayName: '',
        email: ''
      },
      successfulMessage: '',
    };
  },

  mounted() {
    this.$axios.get('/users/me', {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      console.log(response.data);
      this.user = response.data;
    });
  },

  methods: {
    updateProfile() {
      console.log("Update profile!!");
      const request = this.$axios.put('/users/me', this.user,
      {
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      });
      request.then((response) => {
          console.log(response);
          this.successfulMessage = 'Changes has been saved successfully!';
        })
        .catch((error) => {
          console.log(error);

          this.errors = {
            username: '',
            password: '',
            displayName: '',
            email: ''
          };
          this.successfulMessage = '';

          var attrError;
          for (attrError of error.response.data.errors) {
            this.errors[attrError['field']] = attrError['defaultMessage'];
          }
          console.log(this.errors);
        });
    }
  }
};
</script>

<style></style>
