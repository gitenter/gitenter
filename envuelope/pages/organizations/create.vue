<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="createOrganization">
          <table class="fill-in">
            <tr>
              <td>Name</td>
              <td>
                <input
                  id="name"
                  v-model="organization.name"
                  name="name"
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
              <td>Display Name</td>
              <td>
                <input
                  id="displayName"
                  v-model="organization.displayName"
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
                  value="Create Organization"
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
    name: 'Create a New Organization'
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
      organization: {
        name: '',
        displayName: ''
      },
      errors: {
        name: '',
        displayName: ''
      },
      successfulMessage: ''
    };
  },

  methods: {
    createOrganization() {
      this.$axios.post('/organizations', this.organization,
      {
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log(response);
          this.successfulMessage = 'Organization created!';
        })
        .catch((error) => {
          console.log(error);

          this.errors = {
            name: '',
            displayName: ''
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
