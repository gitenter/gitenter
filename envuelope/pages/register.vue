<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="register">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td>
                <input
                  id="username"
                  v-model="user.username"
                  name="username"
                  type="text"
                  value=""
                >
                <span
                  v-if="errors.username"
                  class="error"
                >{{ errors.username }}</span>
              </td>
            </tr>
            <tr>
              <td>Password</td>
              <td>
                <input
                  id="password"
                  v-model="user.password"
                  name="password"
                  type="password"
                  value=""
                >
                <span
                  v-if="errors.password"
                  class="error"
                >{{ errors.password }}</span>
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
            <tr>
              <td />
              <td class="button">
                <input
                  type="submit"
                  value="Register"
                >
              </td>
            </tr>
          </table>
          <div />
        </form>
      </div>
    </article>
  </div>
</template>

<router>
  {
    name: 'Sign up'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue';

export default {
  middleware: 'notAuthenticated',
  layout: 'unauth',

  components: {
    NavigationBar,
  },

  data() {
    return {
      user: {
        username: '',
        password: '',
        displayName: '',
        email: ''
      },
      errors: {
        username: '',
        password: '',
        displayName: '',
        email: ''
      }
    };
  },

  methods: {
    register() {
      console.log("Register user!!");
      this.$axios.post('/users', this.user,
      {
        headers: {
          "Content-Type": "application/json"
        }
      }).then((response) => {
          console.log(response);
          this.$router.push('/login');
        })
        .catch((error) => {

          /*
          TODO:
          If there's a way to reset to initial value, rather than define
          it as initial value again.
          */
          this.errors = {
            username: '',
            password: '',
            displayName: '',
            email: ''
          };

          /*
          TODO:
          This ties to Java Validation error output form. Should be more
          general/lightweighted.
          Also, it seems if I use Spring `ExceptionHandler` to wrap/customize
          other error messages (e.g. `ItemNotUniqueException`), it will
          override Java Validation error output.
          */
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
