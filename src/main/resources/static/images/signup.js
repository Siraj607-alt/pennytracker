console.log("Signup JS loaded!");
const popup = document.getElementById("successPopup");

    if (popup) {
        setTimeout(() => {
            window.location.href = "/doLogin";
        }, 2000); // 2 seconds delay
    }
    const existsPopup = document.getElementById("existsPopup");

        if (existsPopup) {
            setTimeout(() => {
                window.location.href = "/doLogin";
            }, 2000);
        }

function showValid(id) {
    document.getElementById("tick-" + id).classList.add("show");
    document.getElementById("ok-" + id).classList.add("show");
    document.getElementById("err-" + id).classList.remove("show");
}

function showInvalid(id) {
    document.getElementById("tick-" + id).classList.remove("show");
    document.getElementById("ok-" + id).classList.remove("show");
    document.getElementById("err-" + id).classList.add("show");
}

function validateGmail(v) {
    return /^[^\s@]+@gmail\.com$/.test(v);
}

function validatePass(v) {
    return v && v.length >= 6;
}

const email = document.getElementById("email");
const password = document.getElementById("password");
const confirmPass = document.getElementById("confirm");

email.addEventListener("input", () => {
    validateGmail(email.value) ? showValid("email") : showInvalid("email");
});

password.addEventListener("input", () => {
    validatePass(password.value) ? showValid("password") : showInvalid("password");
    checkMatch();
});

confirmPass.addEventListener("input", checkMatch);

function checkMatch() {
    if (password.value && confirmPass.value && password.value === confirmPass.value) {
        showValid("confirm");
    } else {
        showInvalid("confirm");
    }
}

document.getElementById("signupForm").addEventListener("submit", (e) => {
    if (!validateGmail(email.value) ||
        !validatePass(password.value) ||
        password.value !== confirmPass.value) {

        e.preventDefault();
        alert("Please fix validation errors");
    }
});
