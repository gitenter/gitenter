<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="deleteOrganization">
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
              <td>Please copy name</td>
              <td>
                <input
                  id="copyName"
                  v-model="copyOrganizationName"
                  name="copyName"
                  type="text"
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
                  value="Delete organization"
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
    name: 'Delete organization'
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
      copyOrganizationName: '',
      errorMessage: '',
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
    deleteOrganization() {
      this.$axios.delete('/organizations/'+this.organizationId,
      {
        params: {
          "organization_name": this.copyOrganizationName
        },
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log(response);
          this.$router.push('/');
        })
        .catch((error) => {
          console.log(error.response);
          this.errorMessage = error.response.data.message;
        });
    }
  }
};
</script>

<style></style>
