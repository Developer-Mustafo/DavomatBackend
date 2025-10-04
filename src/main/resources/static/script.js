// Static xususiyatlar
const features = [
  "O‘quvchi va xodim davomatini saqlash",
  "Statistika va hisobot chiqarish",
  "Mobil ilova bilan integratsiya",
  "Ma’lumotlar xavfsizligi"
];

const featureList = document.getElementById("feature-list");
if (featureList) {
  features.forEach(f => {
    const li = document.createElement("li");
    li.textContent = f;
    featureList.appendChild(li);
  });
}

// Kontakt formasi
const form = document.getElementById("contact-form");
const formMessage = document.getElementById("form-message");

if (form) {
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const submitButton = form.querySelector('button');
    submitButton.disabled = true;
    formMessage.textContent = "⏳ Yuborilmoqda...";

    const data = {
      name: form.name.value.trim(),
      email: form.email.value.trim(),
      message: form.message.value.trim()
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
        const errText = await response.text();
        formMessage.textContent = `❌ Xato: ${errText || "yuborilmadi."}`;
      }
    } catch (err) {
      console.error("Fetch error:", err);
      formMessage.textContent = "⚠️ Serverga ulanishda xatolik.";
    } finally {
      submitButton.disabled = false;
    }
  });
}

// Dark mode toggle
const darkToggle = document.getElementById("darkToggle");
if (darkToggle) {
  darkToggle.addEventListener("click", () => {
    document.body.classList.toggle("dark");
    // Saqlash localStorage’da dark mode holati
    const isDark = document.body.classList.contains("dark");
    localStorage.setItem("darkMode", isDark);
    darkToggle.textContent = isDark ? "☀️" : "🌙";
  });

  // Sahifa yuklanganda oldingi holatni tiklash
  if (localStorage.getItem("darkMode") === "true") {
    document.body.classList.add("dark");
    darkToggle.textContent = "☀️";
  }
}
