<template>
  <div>
    <navigationBar />
    <article>
      <div>
        <form @submit.prevent="addANewSshKey">
          <table class="fill-in">
            <tr>
              <td>Previous keys</td>
              <td
                v-if="sshKeys.length"
                class="pre-fill"
              >
                <p
                  v-for="(sshKey, index) in sshKeys"
                  :key="index"
                >
                  <code>{{ sshKey.publicKeyAbbr }}</code>
                </p>
              </td>
              <td
                v-else
                class="pre-fill"
              >
                N/A
              </td>
            </tr>
            <tr>
              <td />
              <td class="word">
                <p>
                  The key should be saved in a file named
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
                  followed by the base64-encoded key and (optionally) the comment.
                </p>
              </td>
            </tr>
            <tr>
              <td>New key</td>
              <td>
                <textarea
                  id="sshKeyValue"
                  v-model="sshKeyField.sshKeyValue"
                  name="sshKeyValue"
                  class="bigger"
                />
                <span
                  v-if="errors.sshKeyValue"
                  class="error"
                >{{ errors.sshKeyValue }}</span>
              </td>
            </tr>
            <tr v-if="successfulMessage">
              <td />
              <td class="success">
                {{ successfulMessage }}
              </td>
            </tr>
            <tr>
              <td />
              <td class="button">
                <input
                  type="submit"
                  value="Add a new SSH key"
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
    name: 'Manage SSH keys'
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
      sshKeys: [],
      sshKeyField: {
        sshKeyValue: ''
      },
      errors: {
        sshKeyValue: ''
      },
      successfulMessage: '',
    };
  },

  mounted() {
    this.loadSshKeys();
  },

  methods: {
    loadSshKeys() {
      this.$axios.get('/users/me/ssh-keys', {
        headers: {
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      })
      .then(response => {
        this.sshKeys = response.data;
        this.sshKeys.forEach(sshKey => {sshKey['publicKeyAbbr'] = sshKey.publicKey.substring(0, 40);});
      });
    },
    addANewSshKey() {
      this.$axios.post('/users/me/ssh-keys', this.sshKeyField,
      {
        headers: {
          "Content-Type": "application/json",
          'Authorization': "Bearer " + this.$store.state.auth.accessToken
        }
      }).then((response) => {
          console.log(response);
          this.loadSshKeys();

          this.sshKeyField = {
            sshKeyValue: ''
          };

          this.errors = {
            sshKeyValue: ''
          };
          this.successfulMessage = 'New SSH key has been saved successfully!';
        })
        .catch((error) => {
          console.log(error);

          this.errors = {
            sshKeyValue: ''
          };
          this.successfulMessage = '';

          var attrError;
          for (attrError of error.response.data.errors) {
            this.errors[attrError['field']] = attrError['defaultMessage'];
          }
          console.log(this.errors);
        });
    }
  }
};
</script>

<style></style>
