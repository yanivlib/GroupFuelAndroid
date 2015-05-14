/*
 * UserService.js - Provides data regarding the current user.
 */

app.service('UserService', function () {
    'use strict';
    this.logged = false;
    var currentUser = Parse.User.current();
    if (currentUser) {
        this.logged = true;
    }

    /*this.doLogin = function (username, password) {
        Parse.User.logIn(username, password, {
            success: function(user) {
                // Close Modal
                console.log("login worked");
                this.logged = true;
                return user;
            },
            error: function(user, error) {
                // clean password field and show error
                console.log("login failed with error: " + error);
                return error;
            }
        });
    }; */

    this.doLogout = function () {
        Parse.User.logOut();
        if (Parse.User.current()) {
            // TODO - logout failed
        }
        this.logged = false;
    };
});
