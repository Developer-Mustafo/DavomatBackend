// Static xususiyatlar
const features = [
  "O‘quvchi va xodim davomatini saqlash",
  "Statistika va hisobot chiqarish",
  "Mobil ilova bilan integratsiya",
  "Ma’lumotlar xavfsizligi"
];

const featureList = document.getElementById("feature-list");
features.forEach(f => {
  const li = document.createElement("li");
  li.textContent = f;
  featureList.appendChild(li);
});

// Kontakt formasi
const form = document.getElementById("contact-form");
const formMessage = document.getElementById("form-message");

form.addEventListener("submit", async (e) => {
  e.preventDefault();
  formMessage.textContent = "⏳ Yuborilmoqda...";
  form.querySelector('button').disabled = true;

  const data = {
    name: form.name.value,
    email: form.email.value,
    message: form.message.value
  };

  try {
    const response = await fetch("/api/contact/contact", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data)
    });

    if (response.ok) {
      formMessage.textContent = "✅ Xabaringiz yuborildi!";
      form.reset();
    } else {
      formMessage.textContent = "❌ Xato: yuborilmadi.";
    }
  } catch (err) {
    console.error("Fetch error:", err);
    formMessage.textContent = "⚠️ Serverga ulanishda xatolik.";
  } finally {
    form.querySelector('button').disabled = false;
  }
});

// Dark mode toggle
const darkToggle = document.getElementById("darkToggle");
darkToggle.addEventListener("click", () => {
  document.body.classList.toggle("dark");
  darkToggle.textContent = document.body.classList.contains("dark") ? "☀️" : "🌙";
});
