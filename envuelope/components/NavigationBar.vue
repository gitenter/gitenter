<template>
  <div>
    <nav>
      <span v-for="inPathRoute in inPathRoutes">
        <nuxt-link :to="inPathRoute.path">{{ inPathRoute.name }}</nuxt-link> &rarr;
      </span>
      <span class="nav-current">{{ currentRoute.name }}</span>
    </nav>
  </div>
</template>

<script>
export default {
  computed: {
    currentRoute: function() {
      return this.$router.history.current
    },

    inPathRoutes: function() {
      /*
       * TODO:
       * Is there a library to do this string operation (especially)
       * when there are corner conditions of `/` when root but no `/`
       * at the end if there's no root.
       */
      var paths = []
      if (this.$router.history.current.path != "/") {
        var pathElements = this.$router.history.current.path.split("/");
        while (pathElements.length > 1) {
          pathElements.pop()
          paths.push(pathElements.join("/"))
        }
        paths.reverse()
        paths[0] = "/"
      }
      var routes = []
      for (var i = 0; i < paths.length; i++) {
        var route = this.$router.resolve(paths[i]).route
        if (route.name) {
          routes.push(this.$router.resolve(paths[i]).route)
        }
      }
      return routes
    }
  }
}
</script>
