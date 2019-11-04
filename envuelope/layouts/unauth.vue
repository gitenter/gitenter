<template>
  <div>
    <header>
      <a href="/"><img class="logo" src="/resources/image/logo.png" alt="enterovirus" height="48" width="192"></a>

      <form action="/help/" method="GET">
        <input type="submit" value="Help" />
      </form>

      <form action="/pricing/" method="GET">
        <input type="submit" value="Pricing" />
      </form>
    </header>

    <nuxt />

    <footer>
      <a href="/about/">About</a> | <a href="/contact/">Contact</a><br>
      Copyright &copy; 2017-2019 GitEnter
    </footer>
  </div>
</template>

<style></style>
