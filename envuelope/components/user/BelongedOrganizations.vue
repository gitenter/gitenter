<template>
  <div>
    <h3>Belonged organizations</h3>

    <h5
      id = "belonged-organizations"
      v-for="(organization, index) in belongedOrganizations"
      :key="index"
    >
      <nuxt-link :to="'/organizations/' + organization.id">
        {{ organization.displayName }}
      </nuxt-link>
    </h5>
  </div>
</template>

<script>
export default {
  data: function() {
    return {
      belongedOrganizations: []
    };
  },

  mounted() {
    this.$axios.get('/users/me/organizations', {
      params: {
        "role": "ordinary_member"
      },
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      this.belongedOrganizations = response.data;
    });
  }
};
</script>
