require('whatwg-fetch');

export const state = () => ({
    authUser: null,
    collapseMenu: true,
    searchFilterState: true,
});

export const mutations = {
    SET_USER(state, user) {
        state.authUser = user;
    },
    dashboardCollapse: function (state, value) {
        state.collapseMenu = value;
    },
    searchFilter: function (state, value) {
        state.searchFilterState = value;
    },
};
export const getters = {
    ID(state) {
        return state.authUser.ID;
    },
    User(state) {
        return state.authUser;
    },
    collapseMenu(state) {
        return state.collapseMenu;
    },
    searchFilter(state) {
        return state.searchFilterState;
    },
};

export const actions = {
    nuxtServerInit({commit}, {req}) {
        if (req.session.authUser) {
            commit('SET_USER', req.session.authUser);
        }
    },
    errorHandler({state}, message) {
        state.Snotify.error(message);
    },
    async login({commit}, {id, password}) {
        return fetch('/nuxt/user/login', {
            credentials: 'same-origin',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                id,
                password
            }),
        })
            .then((response) => response.json())
            .then((json) => {
                // handle json response
                if (json.result === 'login fail') {
                    throw new Error(json.message);
                } else {
                    return json;
                }
            }).then((authUser) => {
                commit('SET_USER', authUser);
            });
    },
    async logout({commit}) {
        return fetch('/nuxt/user/logout', {
            // Send the client cookies to the server
            credentials: 'same-origin',
            method: 'POST',
        }).then(() => {
            commit('SET_USER', null);
        });
    },
};
