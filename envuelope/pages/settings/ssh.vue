<template>
  <div>
    <nav>
      <nuxt-link to="/">Home</nuxt-link> &rarr;
      <nuxt-link to="/settings">Settings</nuxt-link> &rarr;
      <span class="nav-current">Manage SSH keys</span>
    </nav>
    <article>
      <div>
        <form @submit.prevent="addANewSshKey">
          <table class="fill-in">
            <tr>
              <td>Previous keys</td>
              <td class="pre-fill" v-if="sshKeys.length">
                <p v-for="sshKey in sshKeys"><code>{{ sshKey.publicKeyAbbr }}</code></p>
              </td>
              <td class="pre-fill" v-else="sshKeys.length">N/A</td>
            </tr>
            <tr>
              <td></td>
              <td class="word">
                <p>The key should be saved in a file named
                <code>id_rsa.pub</code>,
                <code>id_dsa.pub</code>,
                <code>identity.pub</code>,
                <code>id_ecdsa.pub</code>,
                or <code>id_ed25519.pub</code>,
                found under the <code>~/.ssh/</code> folder of your
                home directory. It is a single line begins with the key type
                <code>ssh-rsa</code>,
                <code>ssh-dss</code>,
                <code>ssh-ed25519</code>,
                <code>ecdsa-sha2-nistp521</code>,
                <code>ecdsa-sha2-nistp384</code>,
                or <code>ecdsa-sha2-nistp256</code>,
                followed by the base64-encoded key and (optionally) the comment.</p>
              </td>
            </tr>
            <tr>
              <td>New key</td>
              <td>
                <textarea id="sshKeyValue" v-model="sshKeyField.sshKeyValue" name="sshKeyValue" class="bigger"></textarea>
                <span class="error" v-if="errors.sshKeyValue">{{ errors.sshKeyValue }}</span>
              </td>
            </tr>
            <tr v-if="successfulMessage">
              <td></td>
              <td class="success">{{ successfulMessage }}</td>
            </tr>
            <tr>
              <td></td>
              <td class="button"><input type="submit" value="Add a new SSH key" /></td>
            </tr>
          </table>
        </form>
      </div>
    </article>
  </div>
</template>

<script>
export default {
  middleware: 'authenticated',
  layout: 'auth',

  data() {
    return {
      sshKeys: [],
      sshKeyField: {
        sshKeyValue: ''
      },
      errors: {
        sshKeyValue: ''
      },
      successfulMessage: '',
    }
  },

  mounted() {
    this.$axios.get('/users/me/ssh-keys', {
      headers: {
        'Authorization': "Bearer " + this.$store.state.auth.accessToken
      }
    })
    .then(response => {
      this.sshKeys = response.data
      this.sshKeys.forEach(sshKey => {sshKey['publicKeyAbbr'] = sshKey.publicKey.substring(0, 40)})
    })
  },

  methods: {
    addANewSshKey() {
      const request = this.$axios.post('/users/me/ssh-keys', this.sshKeyField,
      {
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      })
      request.then((response) => {
          console.log(response);

          this.sshKeyField = {
            sshKeyValue: ''
          }

          this.successfulMessage = 'New SSH key has been saved successfully!'

          /*
           * TODO:
           * Any way to not repeat this `mounted` method?
           */
          this.$axios.get('/users/me/ssh-keys', {
            headers: {
              'Authorization': "Bearer " + this.$store.state.auth.accessToken
            }
          })
          .then(response => {
            this.sshKeys = response.data
            this.sshKeys.forEach(sshKey => {sshKey['publicKeyAbbr'] = sshKey.publicKey.substring(0, 40)})
          });
        })
        .catch((error) => {
          console.log(error)

          this.errors = {
            sshKeyValue: ''
          }
          this.successfulMessage = ''

          var attrError;
          for (attrError of error.response.data.errors) {
            this.errors[attrError['field']] = attrError['defaultMessage']
          }
          console.log(this.errors)
        })
    }
  }
}
</script>

<style></style>
