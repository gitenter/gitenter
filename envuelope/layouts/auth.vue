<template>
  <div>
    <header>
      <a href="/"><img class="logo" src="/resources/image/logo.png" alt="enterovirus" height="48" width="192"></a>

      <form id="command" action="/logout" method="POST">
        <input type="submit" id="logout" value="Log out" />
      </form>

      <form action="/settings" method="GET">
        <input type="submit" value="Settings" />
      </form>

      <form action="/help/" method="GET">
        <input type="submit" value="Help" />
      </form>

      <div class="text">Logged in as asdf</div>
    </header>

    <nuxt />

    <footer>
      <a href="/about/">About</a> | <a href="/contact/">Contact</a><br>
      Copyright &copy; 2017-2019 GitEnter
    </footer>
  </div>
</template>

<style></style>
