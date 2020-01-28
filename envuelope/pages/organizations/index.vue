<template>
  <div>
    <navigationBar />
    <article>
      <managedOrganizations />
      <belongedOrganizations />
    </article>
  </div>
</template>

<router>
  {
    name: 'Organizations'
  }
</router>

<script>
import NavigationBar from '~/components/NavigationBar.vue';
import ManagedOrganizations from '~/components/user/ManagedOrganizations.vue';
import BelongedOrganizations from '~/components/user/BelongedOrganizations.vue';

export default {
  middleware: 'authenticated',
  layout: 'auth',

  components: {
    NavigationBar,
    ManagedOrganizations,
    BelongedOrganizations
  }
};
</script>

<style></style>
