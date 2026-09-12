import "./style.css";

const app = document.querySelector<HTMLDivElement>("#app");

if (!app) {
  throw new Error("App container not found.");
}

const isLoggedIn = (): boolean => {
  return Boolean(localStorage.getItem("token"));
};

const goToDashboardOrLogin = (): void => {
  if (isLoggedIn()) {
    window.location.href = "/dashboard.html";
  } else {
    window.location.href = "/login.html";
  }
};

const goToRegister = (): void => {
  window.location.href = "/register.html";
};

const goToLogin = (): void => {
  window.location.href = "/login.html";
};

app.innerHTML = `
  <!-- ================= NAVBAR ================= -->

  <header class="navbar">

    <a href="/" class="brand">
      <span class="brand-icon">P</span>
      <span>PrepPilot AI</span>
    </a>

    <nav class="nav-links">

      <a href="#features">
        Features
      </a>

      <a href="#how-it-works">
        How it works
      </a>

      <button
        id="navLogin"
        class="nav-login">
        Login
      </button>

      <button
        id="navGetStarted"
        class="nav-get-started">
        Get Started
      </button>

    </nav>

  </header>


  <!-- ================= HERO ================= -->

  <main>

    <section class="hero">

      <div class="hero-content">

	  
        <h1>
          Study Smarter.<br>
          <span>Prepare Better.</span>
        </h1>

        <p class="hero-description">
          Turn your study materials into summaries,
          quizzes and flashcards with the power of AI.
        </p>

        <div class="hero-actions">

          <button
            id="startLearning"
            class="primary-btn">
            Start Learning
            <span>→</span>
          </button>

          <a
            href="#features"
            class="secondary-btn">
            Explore Features
          </a>

        </div>

        <div class="hero-note">
          📚 Upload your notes &nbsp; • &nbsp;
          🤖 Let AI do the preparation &nbsp; • &nbsp;
          🎯 Learn efficiently
        </div>

      </div>


      <!-- ================= DASHBOARD PREVIEW ================= -->

      <div class="dashboard-preview">

        <div class="preview-header">

          <div>

            <span class="preview-label">
              YOUR STUDY DASHBOARD
            </span>

            <h3>
              Good morning, Student 👋
            </h3>

          </div>

          <div class="avatar">
            S
          </div>

        </div>


        <div class="preview-stats">

          <div class="preview-stat">

            <div class="stat-icon">
              📚
            </div>

            <strong>12</strong>

            <span>Documents</span>

          </div>


          <div class="preview-stat">

            <div class="stat-icon">
              🧠
            </div>

            <strong>60</strong>

            <span>Flashcards</span>

          </div>


          <div class="preview-stat">

            <div class="stat-icon">
              🎯
            </div>

            <strong>85%</strong>

            <span>Avg. Score</span>

          </div>

        </div>


        <div class="progress-preview">

          <div class="progress-header">

            <strong>
              Today's Progress
            </strong>

            <span>
              75%
            </span>

          </div>

          <div class="progress-track">

            <div class="progress-fill"></div>

          </div>

          <p>
            Keep going! You're doing great.
          </p>

        </div>

      </div>

    </section>


    <!-- ================= FEATURES ================= -->

    <section
      id="features"
      class="section">

      <div class="section-heading">

        <span class="section-label">
          POWERFUL FEATURES
        </span>

        <h2>
          Everything you need to study
        </h2>

        <p>
          One simple platform to organize your
          study material and prepare smarter.
        </p>

      </div>


      <div class="features-grid">


        <article class="feature-card">

          <div class="feature-icon">
            📄
          </div>

          <h3>
            Smart Document Management
          </h3>

          <p>
            Upload your PDF study materials
            and organize them by subject.
          </p>

        </article>


        <article class="feature-card">

          <div class="feature-icon">
            ✨
          </div>

          <h3>
            AI Summaries
          </h3>

          <p>
            Convert lengthy study material
            into clear and useful summaries.
          </p>

        </article>


        <article class="feature-card">

          <div class="feature-icon">
            🧠
          </div>

          <h3>
            AI Flashcards
          </h3>

          <p>
            Generate flashcards automatically
            and practice using active recall.
          </p>

        </article>


        <article class="feature-card">

          <div class="feature-icon">
            📝
          </div>

          <h3>
            AI Quizzes
          </h3>

          <p>
            Test your understanding with
            AI-generated multiple-choice questions.
          </p>

        </article>


        <article class="feature-card">

          <div class="feature-icon">
            📊
          </div>

          <h3>
            Performance Tracking
          </h3>

          <p>
            Track your quiz scores, attempts
            and overall learning performance.
          </p>

        </article>


        <article class="feature-card">

          <div class="feature-icon">
            🔎
          </div>

          <h3>
            Quick Search
          </h3>

          <p>
            Quickly find your documents and
            generated flashcards.
          </p>

        </article>

      </div>

    </section>


    <!-- ================= HOW IT WORKS ================= -->

    <section
      id="how-it-works"
      class="section how-section">

      <div class="section-heading">

        <span class="section-label">
          HOW IT WORKS
        </span>

        <h2>
          From notes to preparation
        </h2>

        <p>
          Get started in just a few simple steps.
        </p>

      </div>


      <div class="steps">


        <div class="step">

          <div class="step-number">
            01
          </div>

          <div>

            <h3>
              Create your account
            </h3>

            <p>
              Sign up and create your personal
              learning workspace.
            </p>

          </div>

        </div>


        <div class="step">

          <div class="step-number">
            02
          </div>

          <div>

            <h3>
              Upload your study material
            </h3>

            <p>
              Upload PDF notes and organize
              them under your subjects.
            </p>

          </div>

        </div>


        <div class="step">

          <div class="step-number">
            03
          </div>

          <div>

            <h3>
              Generate with AI
            </h3>

            <p>
              Generate summaries, flashcards
              and quizzes from your material.
            </p>

          </div>

        </div>


        <div class="step">

          <div class="step-number">
            04
          </div>

          <div>

            <h3>
              Practice & track
            </h3>

            <p>
              Attempt quizzes and monitor
              your learning performance.
            </p>

          </div>

        </div>

      </div>

    </section>


    <!-- ================= CTA ================= -->

    <section class="cta-section">

      <div class="cta-card">

        <div>

          <span class="section-label">
            READY TO START?
          </span>

          <h2>
            Make your study time smarter.
          </h2>

          <p>
            Organize your notes, learn with AI
            and prepare with confidence.
          </p>

        </div>


        <button
          id="ctaButton"
          class="primary-btn">
          Get Started
          <span>→</span>
        </button>

      </div>

    </section>

  </main>


  <!-- ================= FOOTER ================= -->

  <footer class="footer">

    <div>

      <a href="/" class="brand footer-brand">

        <span class="brand-icon">
          P
        </span>

        <span>
          PrepPilot AI
        </span>

      </a>

      <p>
        AI-powered personal learning assistant.
      </p>

    </div>


    <div class="footer-links">

      <a href="#features">
        Features
      </a>

      <a href="#how-it-works">
        How it works
      </a>

      <button id="footerLogin">
        Login
      </button>

      <button id="footerRegister">
        Register
      </button>

    </div>

  </footer>
`;


/* =====================================================
   NAVIGATION EVENTS
   ===================================================== */

document
  .querySelector<HTMLButtonElement>("#navLogin")
  ?.addEventListener("click", goToLogin);


document
  .querySelector<HTMLButtonElement>("#navGetStarted")
  ?.addEventListener("click", goToRegister);


document
  .querySelector<HTMLButtonElement>("#startLearning")
  ?.addEventListener(
    "click",
    goToDashboardOrLogin
  );


document
  .querySelector<HTMLButtonElement>("#ctaButton")
  ?.addEventListener("click", goToRegister);


document
  .querySelector<HTMLButtonElement>("#footerLogin")
  ?.addEventListener("click", goToLogin);


document
  .querySelector<HTMLButtonElement>("#footerRegister")
  ?.addEventListener("click", goToRegister);


/* =====================================================
   SMOOTH SCROLL
   ===================================================== */

document
  .querySelectorAll<HTMLAnchorElement>(
    'a[href^="#"]'
  )
  .forEach((link) => {

    link.addEventListener(
      "click",
      (event) => {

        const targetId =
          link.getAttribute("href");

        if (!targetId || targetId === "#") {
          return;
        }

        const target =
          document.querySelector(
            targetId
          );

        if (!target) {
          return;
        }

        event.preventDefault();

        target.scrollIntoView({
          behavior: "smooth",
          block: "start"
        });

        history.replaceState(
          null,
          "",
          targetId
        );

      }
    );

  });