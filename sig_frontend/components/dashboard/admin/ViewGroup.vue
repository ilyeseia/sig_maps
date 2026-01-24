<template>
    <div class="modal-card" style="width: 700px;height : 75vh">
      <header class="modal-card-head">
          <h1 class="modal-card-title">Visualisation du groupe</h1>
      </header>
      <section class="modal-card-body">
        <b-field grouped>
            <b-field label="Name:"></b-field>
            <b-field :label="groupToEdit ? groupToEdit.name : ''"></b-field>
        </b-field>
         <b-field grouped>
            <b-field label="Description:"></b-field>
            <b-field :label="groupToEdit ? groupToEdit.description : ''"></b-field>
        </b-field>
        
        <div class="mb-3">
          <template>
            <b-tabs v-model="activeTab">
              <b-tab-item label="Utilisateurs">
                  <b-table :data="users" :columns="userColumns"></b-table>
              </b-tab-item>
            </b-tabs>
          </template>
        </div>
      </section>
       <footer class="right-align modal-card-foot">
        <button id="close-group-form" class="button" type="button" @click="$parent.close()">Fermer</button>
      </footer>
    </div>
</template>
<script>


export default {
  props: ['groupToEdit'],
  data() {
    return {
      activeTab: 0,
      users: [],
      maps: [],
      userColumns: [
        {
          field: 'username',
          label: "Nom d'utlisateur"
        },
        {
          field: 'email',
          label: 'Email'
        }
      ]
    }
  },
  beforeMount() {
    if (this.groupToEdit) {
      this.groupToEdit.users.forEach(member =>
        this.users.push({
          username: member.username,
          email: member.email
        })
      )
    }
  }
}
</script>
<style>
.right-align {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
}
</style>
