#  Developer Workflow Guide

Welcome to the project! This guide explains **how to work in our GitHub repo**, create branches, and submit your code smoothly.

---

##  Branch Rules

| Rule                  | Meaning                                             |
| --------------------- | --------------------------------------------------- |
| `main` is protected   | Nobody pushes straight to `main`                    |
| No direct commits     | All changes must go through Pull Requests           |
| CODEOWNERS required   | Integration & teammates must review certain changes |
| Module branches exist | Each team has its own working branch                |

---

##  Branch Naming

###  Permanent module branches

| Module      | Branch                      |
| ----------- | --------------------------- |
| Accounts    | `accounts`                  |
| Storage     | `storage`                   |
| Validation  | `validation`                |
| Reports     | `reports`                   |
| Prediction  | `prediction`                |
| Test        | `test`                      |
| Integration | `integration`               |

>  **Never develop directly on these module branches.**
> They are like “parking lots” for features.

---

##  Your Development Workflow

### 1. Clone repo once

```bash
git clone https://github.com/final-project-integration/final-project.git
cd final-project
```

---

### 2. Switch to your team branch

```bash
git checkout accounts   # example: if you're on Accounts team
```

Substitute with your module (`storage`, `reports`, etc.)

---

### 3. Create your own feature branch

Format:

```
<module>/feat-<short-feature-name>
```

Example:

```bash
git checkout -b accounts/feat-register
```

---

### 4. Work → stage → commit → push

```bash
git add .
git commit -m "feat(accounts): implement register workflow"
git push -u origin accounts/feat-register
```

>  Include module name in commit messages:
> `feat(storage): load CSV file`

---

### 5. Open Pull Request (PR)

* **Base branch:** `main`
* **Compare branch:** your feature branch
  (example: `accounts/feat-register`)

---

### 6. Request review

Required:

✅ 1 teammate review
✅ Integration approval if API/signatures change

PR template will guide you.

---

### 7. Merge PR

Once approved, click **Merge Pull Request**.

 Done: your code is now part of the project.

---

##  Keeping your branch updated

If other teams merge work, update before coding:

```bash
git checkout accounts
git pull origin main
```

Then switch back to your feature branch:

```bash
git checkout accounts/feat-register
git merge accounts
```



##  Need Help?

Use these first:

* `docs/ONBOARDING_CHECKLIST.md` — new member checklist
* `docs/API_CONTRACTS.md` — shared method signatures
* Ask your **team lead**
* Tag **@ditto-d** **Darxware** **Aaron-Madou** **Kapil-Tamang for Integration questions

---

##  Golden Rule

> **No one breaks another team’s code.**

If unsure → ask before merging.
We ship as a team.



