# Team Onboarding — How to Work in This Repo ✅

## Branch Rules
- `main` is protected ✅
- No direct commits to `main`
- Every change must go through a Pull Request (PR)
- CODEOWNERS system auto-assigns reviewers

## Your Workflow
1) Clone once:
   git clone https://github.com/final-project-integration/final-project.git
   cd final-project

2) Switch to your module branch:
   git checkout <module-branch>

   **Module branches:**
   - accounts
   - storage
   - validation
   - reports
   - prediction
   - test
   - integration (lead)

3) Create a feature branch:
   git checkout -b <module>/feat-<feature-name>
   Example:
   git checkout -b accounts/feat-register

4) Commit & push:
   git add .
   git commit -m "feat(accounts): implement register function"
   git push -u origin accounts/feat-register

5) Open PR → base: `main`
6) CODEOWNERS reviewer approves ✅
7) Merge PR via GitHub button 🔒

## CSV + Budget Object Rules
- Shared format lives in `docs/API_CONTRACTS.md`
- **If your change affects another team**, update that doc in the same PR

## Reference
- Integration entry: `integration/src/Menu.java`
- Docs: `docs/`
- Onboarding: `docs/TEAM_ONBOARDING.md`

Welcome to a real software engineering workflow 🚀

