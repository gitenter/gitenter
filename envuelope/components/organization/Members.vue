<template>
  <div>
    <h3>Members</h3>
    <ul class="user-list">
      <li
        v-for="(user, index) in members"
        :key="index"
      >
        <span class="user">{{ user.displayName }}</span>
      </li>
    </ul>
  </div>
</template>

<script>
export default {
  props: [
    'organizationId'
  ],

  data: function() {
    return {
      members: []
    };
  },

  mounted() {
    console.log(this.organizationId)
    this.$axios.get('/organizations/'+this.organizationId+'/members', {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      console.log(response.data);
      this.members = response.data;
    });
  }
};
</script>
