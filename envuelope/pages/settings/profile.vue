<template>
  <div>
    <nav>
      <nuxt-link to="/">Home</nuxt-link> &rarr;
      <nuxt-link to="/settings">Settings</nuxt-link> &rarr;
      <span class="nav-current">Edit profile</span>
    </nav>
    <article>
      <div>
        <form @submit.prevent="updateProfile">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td class="pre-fill">{{ user.username }}</td>
            </tr>
            <tr>
              <td>Display Name</td>
              <td>
                <input id="displayName" v-model="user.displayName" name="displayName" type="text" value=""/>
                <span class="error" v-if="errors.displayName">{{ errors.displayName }}</span>
              </td>
            </tr>
            <tr>
              <td>Email address</td>
              <td>
                <input id="email" v-model="user.email" name="email" type="email" value=""/>
                <span class="error" v-if="errors.email">{{ errors.email }}</span>
              </td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Update profile" /></td>
            </tr>
          </table>
        </form>
      </div>
    </article>
  </div>
</template>

<script>
export default {
  middleware: 'authenticated',
  layout: 'auth',

  data() {
    return {
      user: '',
      errors: {
        username: '',
        password: '',
        displayName: '',
        email: ''
      }
    }
  },

  mounted() {
    this.$axios.get('/users/me', {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      console.log(response.data)
      this.user = response.data
    })
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
      })
      request.then((response) => {
          console.log(response);
        })
        .catch((error) => {
          console.log(error)

          this.errors = {
            username: '',
            password: '',
            displayName: '',
            email: ''
          }

          var attrError;
          for (attrError of error.response.data.errors) {
            this.errors[attrError['field']] = attrError['defaultMessage']
          }
          console.log(this.errors)
        })
    }
  }
}
</script>

<style></style>
