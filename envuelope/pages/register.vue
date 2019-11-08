<template>
  <div>
    <nav>
      <a href="/">Home</a> &rarr;
      <span class="nav-current">Sign Up</span>
    </nav>
    <article>
      <div>
        <form @submit.prevent="register">
          <table class="fill-in">
            <tr>
              <td>Username</td>
              <td>
                <input id="username" v-model="username" name="username" type="text" value=""/>
              </td>
            </tr>
            <tr>
              <td>Password</td>
              <td>
                <input id="password" v-model="password" name="password" type="password" value=""/>
              </td>
            </tr>
            <tr>
              <td>Display Name</td>
              <td>
                <input id="displayName" v-model="displayName" name="displayName" type="text" value=""/>
              </td>
            </tr>
            <tr>
              <td>Email address</td>
              <td>
                <input id="email" v-model="email" name="email" type="email" value=""/>
              </td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Register" /></td>
            </tr>
          </table>
          <div>
            <input type="hidden" name="_csrf" value="ebe28b79-3705-4e2e-8604-f89336182e35" />
          </div>
        </form>
      </div>
    </article>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  layout: 'unauth',

  data() {
    return {
      username: '',
      password: '',
      displayName: '',
      email: '',
      errors: []
    }
  },

  methods: {
    register() {
      console.log("Register user!!");
      axios.post('http://localhost:8080/api/register', {
        "username": this.username,
        "password": this.password,
        "displayName": this.displayName,
        "email": this.email
      },
      {
        headers: {
          "Content-Type": "application/json"
        }
      })
        .then((Response) => {})
        .catch((err) => {
          console.log(err);
          this.errors.push(err)
        })
    }
  }
}
</script>

<style></style>
