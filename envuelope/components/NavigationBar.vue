<template>
  <div>
    <nav>
      <span
        v-for="(inPathRoute, index) in inPathRoutes"
        :key="index"
      >
        <nuxt-link :to="inPathRoute.path">{{ inPathRoute.name }}</nuxt-link> &rarr;
      </span>
      <span class="nav-current">{{ currentRoute.name }}</span>
    </nav>
  </div>
</template>

<script>
/*
 * TODO:
 * Pass in parameters so we can optimize e.g. Organization/repository
 * name through `prop` https://vuejs.org/v2/guide/components.html#Passing-Data-to-Child-Components-with-Props
 * However, it is a little bit tricky because the overwritten name
 * may be not `nav-current` but can be any one on the path. Also
 * it is very side that I cannot use `data` in the definition of
 * `<router>` using router-extras-module.
 */
export default {
  computed: {
    currentRoute: function() {
      return this.$route;
    },

    inPathRoutes: function() {
      /*
       * TODO:
       * Is there a library to do this string operation (especially)
       * when there are corner conditions of `/` when root but no `/`
       * at the end if there's no root.
       */
      var paths = [];
      if (this.$route.path != "/") {
        var pathElements = this.$route.path.split("/");
        while (pathElements.length > 1) {
          pathElements.pop();
          paths.push(pathElements.join("/"));
        }
        paths.reverse();
        paths[0] = "/";
      }
      var routes = [];
      for (var i = 0; i < paths.length; i++) {
        var route = this.$router.resolve(paths[i]).route;
        if (route.name) {
          routes.push(this.$router.resolve(paths[i]).route);
        }
      }
      return routes;
    }
  }
};
</script>
