<template>
    <div class="test">
        loading
    </div>
</template>
<script>
    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/login');
            } else {
                redirect('/dashboard');
            }
        },
    };
</script>
