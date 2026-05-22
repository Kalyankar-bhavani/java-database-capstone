// util.js
window.setRole = function (role) {
    localStorage.setItem("userRole", role);
};

window.getRole = function () {
    return localStorage.getItem("userRole");
};

window.clearRole = function () {
    localStorage.removeItem("userRole");
};