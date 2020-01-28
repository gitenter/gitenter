<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="updateProfile">
          <table class="fill-in">
            <tr>
              <td>Name</td>
              <td
                id="name"
                class="pre-fill"
              >
                {{ organization.name }}
              </td>
            </tr>
            <tr>
              <td>Display name</td>
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
    name: 'Edit repository profile'
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
      organizationId: this.$route.params.organizationId,
      organization: '',
      errors: {
        name: '',
        displayName: ''
      },
      successfulMessage: '',
    };
  },

  mounted() {
    this.$axios.get('/organizations/'+this.organizationId, {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      console.log(response.data);
      this.organization = response.data;
    });
  },

  methods: {
    updateProfile() {
      console.log("Update profile!!");
      this.$axios.put('/organizations/'+this.organizationId, this.organization,
      {
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log(response);
          this.successfulMessage = 'Changes has been saved successfully!';
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
