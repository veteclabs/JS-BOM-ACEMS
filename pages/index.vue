<template>
  <div class="test">
    loading
  </div>
</template>
<script>
export default {
  fetch({ store, redirect }) {
    if (!store.state.authUser) {
      return redirect('/login');
    }else {
      let teamId = store.state.authUser.teamId;
      let departmentId = store.state.authUser.departmentId;
      if(teamId === 20) {
        redirect('/dashboard');
      }else {
        redirect(`/dashboard/departmentDashboard?depth=${departmentId}`);
      }
    }
  },
};
</script>
