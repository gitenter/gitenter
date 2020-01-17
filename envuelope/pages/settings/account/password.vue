<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="changePassword">
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
              <td>Old password</td>
              <td>
                <input
                  id="old_password"
                  v-model="changePasswordForm.oldPassword"
                  name="old_password"
                  type="password"
                  value=""
                >
                <span
                  v-if="errors.oldPassword"
                  class="error"
                >{{ errors.oldPassword }}</span>
              </td>
            </tr>
            <tr>
              <td>New password</td>
              <td>
                <input
                  id="password"
                  v-model="changePasswordForm.newPassword"
                  name="password"
                  type="password"
                  value=""
                >
                <span
                  v-if="errors.newPassword"
                  class="error"
                >{{ errors.newPassword }}</span>
              </td>
            </tr>
            <tr v-if="successfulMessage">
              <td />
              <td class="success">
                {{ successfulMessage }}
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
                  value="Change password"
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
    name: 'Change password'
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
      changePasswordForm: {
        oldPassword: '',
        newPassword: ''
      },
      errors: {
        oldPassword: '',
        newPassword: ''
      },
      successfulMessage: '',
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
    changePassword() {
      const request = this.$axios.post('/users/me/password', this.changePasswordForm,
      {
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      });
      request.then((response) => {
          console.log(response);

          this.changePasswordForm = {
            oldPassword: '',
            newPassword: ''
          };

          this.errors = {
            oldPassword: '',
            newPassword: ''
          };

          this.successfulMessage = 'Changes has been saved successfully!';
          this.errorMessage = '';
        })
        .catch((error) => {
          console.log(error.response);

          this.successfulMessage = '';

          this.errors = {
            oldPassword: '',
            newPassword: ''
          };

          var attrError;
          if ("errors" in error.response.data) {
            for (attrError of error.response.data.errors) {
              this.errors[attrError['field']] = attrError['defaultMessage'];
            }
          }
          else {
            this.errorMessage = error.response.data.message;
          }
        });
    }
  }
};
</script>

<style></style>
