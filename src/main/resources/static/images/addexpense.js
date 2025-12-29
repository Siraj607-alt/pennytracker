console.log("Add Expense JS Loaded!");

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

const amount = document.getElementById("amount");
const category = document.getElementById("category");
const date = document.getElementById("date");

amount.addEventListener("input", () => {
  amount.value > 0 ? showValid("amount") : showInvalid("amount");
});

category.addEventListener("change", () => {
  category.value ? showValid("category") : showInvalid("category");
});

date.addEventListener("change", () => {
  date.value ? showValid("date") : showInvalid("date");
});

document.getElementById("expenseForm").addEventListener("submit", (e) => {
  if (!(amount.value > 0 && category.value && date.value)) {
    e.preventDefault();
    alert("Please enter all required fields!");
  }
});
